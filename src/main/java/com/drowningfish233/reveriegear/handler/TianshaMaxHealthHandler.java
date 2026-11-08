package com.drowningfish233.reveriegear.handler;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.attachment.RFAttachments;
import com.drowningfish233.reveriegear.registry.RFDamageTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = ReverieGear.MODID)
public class TianshaMaxHealthHandler {

    private static final int MAX_HEALTH_REDUCTION_TIMEOUT_TICKS = 1200;
    private static final ResourceLocation MAX_HEALTH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "tiansha_max_health_reduction");

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        var source = event.getSource();
        var target = event.getEntity();
        float newDamage = event.getNewDamage();

        if (source.is(RFDamageTypes.TIANSHA_STRIKE)) {
            Player attacker = null;
            if (source.getEntity() instanceof Player player) {
                attacker = player;
            }

            float originalMaxHealth = getOriginalMaxHealth(target);
            float healthRatio = target.getHealth() / originalMaxHealth;

            if (healthRatio > 0.5f) {
                event.setNewDamage(0);
                reduceMaxHealth(target, newDamage, attacker);
                refreshTimer(target);

                if (attacker != null) {
                    ItemStack stack = attacker.getMainHandItem();
                    GearHelper.attemptDamage(stack, 1, attacker, InteractionHand.MAIN_HAND);
                }
                return;
            }

            if (healthRatio >= 0.1f) {
                reduceMaxHealth(target, newDamage, attacker);
                refreshTimer(target);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        var server = event.getServer();
        long gameTime = server.overworld().getGameTime();

        if (gameTime % 600 == 0) {
            for (var level : server.getAllLevels()) {
                for (var entity : level.getAllEntities()) {
                    if (entity instanceof LivingEntity living) {
                        checkAndRemoveReduction(living);
                    }
                }
            }
        }
    }

    public static float detonateReduction(LivingEntity target, LivingEntity attacker) {
        if (target == null || target.level().isClientSide()) return 0f;

        AttributeInstance maxHealthAttr = target.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr == null) return 0f;

        float currentReduction = target.getData(RFAttachments.HYPERIUS_MAX_HEALTH_REDUCTION);
        if (currentReduction <= 0.01f) return 0f;

        maxHealthAttr.removeModifier(MAX_HEALTH_MODIFIER_ID);
        target.setData(RFAttachments.HYPERIUS_MAX_HEALTH_REDUCTION, 0.0f);

        refreshTimer(target);

        float damage = currentReduction * 0.2f;
        if (damage > 0 && attacker != null) {
            target.hurt(RFDamageTypes.causeTianshaRelease(attacker), damage);
        }

        return currentReduction;
    }

    public static float getCurrentReduction(LivingEntity target) {
        if (target == null) return 0f;
        return target.getData(RFAttachments.HYPERIUS_MAX_HEALTH_REDUCTION);
    }

    private static void refreshTimer(LivingEntity target) {
        if (target.level() instanceof ServerLevel serverLevel) {
            target.setData(RFAttachments.HYPERIUS_LAST_DAMAGE_TIME, serverLevel.getGameTime());
        }
    }

    private static void checkAndRemoveReduction(LivingEntity target) {
        if (target.level().isClientSide()) return;

        long lastDamageTime = target.getData(RFAttachments.HYPERIUS_LAST_DAMAGE_TIME);
        long currentTime = target.level().getGameTime();

        if (currentTime - lastDamageTime > MAX_HEALTH_REDUCTION_TIMEOUT_TICKS) {
            removeMaxHealthReduction(target);
        }
    }

    private static void removeMaxHealthReduction(LivingEntity target) {
        AttributeInstance maxHealthAttr = target.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr == null) return;

        float currentReduction = target.getData(RFAttachments.HYPERIUS_MAX_HEALTH_REDUCTION);
        if (currentReduction <= 0.01f) return;

        maxHealthAttr.removeModifier(MAX_HEALTH_MODIFIER_ID);
        target.setData(RFAttachments.HYPERIUS_MAX_HEALTH_REDUCTION, 0.0f);

        spawnRecoveryEffect(target);
    }

    public static void reduceMaxHealth(LivingEntity target, float reductionAmount, @Nullable Player attacker) {
        if (reductionAmount <= 0) return;
        if (target.level().isClientSide()) return;

        AttributeInstance maxHealthAttr = target.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr == null) return;

        float originalMaxHealth = getOriginalMaxHealth(target);
        float currentHealth = target.getHealth();
        float healthRatio = currentHealth / originalMaxHealth;

        if (healthRatio < 0.1f) {
            return;
        }

        float currentReduction = target.getData(RFAttachments.HYPERIUS_MAX_HEALTH_REDUCTION);
        float newReduction = currentReduction + reductionAmount;

        float maxAllowedReduction = originalMaxHealth * 0.9f;
        newReduction = Math.min(newReduction, maxAllowedReduction);

        maxHealthAttr.removeModifier(MAX_HEALTH_MODIFIER_ID);

        if (newReduction > 0.01f) {
            AttributeModifier modifier = new AttributeModifier(
                    MAX_HEALTH_MODIFIER_ID,
                    -newReduction,
                    AttributeModifier.Operation.ADD_VALUE
            );
            maxHealthAttr.addTransientModifier(modifier);

            target.setData(RFAttachments.HYPERIUS_MAX_HEALTH_REDUCTION, newReduction);

            spawnCutEffect(target, reductionAmount);
        }
    }

    private static float getOriginalMaxHealth(LivingEntity target) {
        AttributeInstance maxHealthAttr = target.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr == null) return target.getMaxHealth();

        maxHealthAttr.removeModifier(MAX_HEALTH_MODIFIER_ID);
        float original = (float) maxHealthAttr.getValue();

        float currentReduction = target.getData(RFAttachments.HYPERIUS_MAX_HEALTH_REDUCTION);
        if (currentReduction > 0.01f) {
            AttributeModifier modifier = new AttributeModifier(
                    MAX_HEALTH_MODIFIER_ID,
                    -currentReduction,
                    AttributeModifier.Operation.ADD_VALUE
            );
            maxHealthAttr.addTransientModifier(modifier);
        }

        return original;
    }

    public static void spawnCutEffect(LivingEntity target, float amount) {
        if (!(target.level() instanceof ServerLevel level)) return;

        int particleCount = Math.min(20, Math.max(5, (int) (amount / 2)));

        for (int i = 0; i < particleCount; i++) {
            double offsetX = (target.getRandom().nextDouble() - 0.5) * 1.5;
            double offsetY = target.getRandom().nextDouble() * 2.0;
            double offsetZ = (target.getRandom().nextDouble() - 0.5) * 1.5;

            if (i % 2 == 0) {
                level.sendParticles(ParticleTypes.END_ROD,
                        target.getX() + offsetX,
                        target.getY() + offsetY + 0.5,
                        target.getZ() + offsetZ,
                        1, 0.0, 0.0, 0.0, 0.0);
            } else {
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        target.getX() + offsetX * 0.5,
                        target.getY() + offsetY + 0.5,
                        target.getZ() + offsetZ * 0.5,
                        1, 0.0, 0.1, 0.0, 0.0);
            }
        }
    }

    private static void spawnRecoveryEffect(LivingEntity target) {
        if (!(target.level() instanceof ServerLevel level)) return;

        for (int i = 0; i < 20; i++) {
            double offsetX = (target.getRandom().nextDouble() - 0.5) * 2;
            double offsetY = target.getRandom().nextDouble() * 2;
            double offsetZ = (target.getRandom().nextDouble() - 0.5) * 2;

            level.sendParticles(ParticleTypes.HEART,
                    target.getX() + offsetX,
                    target.getY() + offsetY + 0.5,
                    target.getZ() + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
