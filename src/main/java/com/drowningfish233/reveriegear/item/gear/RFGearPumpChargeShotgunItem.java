package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.entity.shotgunpellet.GearSlugPellet;
import com.drowningfish233.reveriegear.entity.shotgunpellet.GearPhasmoPellet;
import com.drowningfish233.reveriegear.entity.shotgunpellet.GearShotgunPellet;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.jadenxgamer.elysium_api.api.client.screen_flash.ScreenFlash;
import net.jadenxgamer.elysium_api.api.util.ClientItemData;
import net.jadenxgamer.netherexp.config.JNEConfigs;
import net.jadenxgamer.netherexp.core.item.PumpChargeShotgunItem;
import net.jadenxgamer.netherexp.core.item.ShotgunFistItem;
import net.jadenxgamer.netherexp.core.item.ShotgunShellItem;
import net.jadenxgamer.netherexp.core.keys.JNEDamageSources;
import net.jadenxgamer.netherexp.core.keys.JNEEnchantments;
import net.jadenxgamer.netherexp.registry.JNEDataComponents;
import net.jadenxgamer.netherexp.registry.JNEItems;
import net.jadenxgamer.netherexp.registry.JNEParticleTypes;
import net.jadenxgamer.netherexp.registry.JNESoundEvents;
import net.jadenxgamer.netherexp.util.HolderHelper;
import net.jadenxgamer.netherexp.util.VFXHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.systems.easing.Easing;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearPumpChargeShotgunItem extends PumpChargeShotgunItem implements GearWeapon {

    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;
    private static final float BASE_PELLET_DAMAGE = 1.0f;

    public RFGearPumpChargeShotgunItem(Supplier<GearType> gearType) {
        super(GearHelper.getBaseItemProperties()
                .fireResistant()
                .stacksTo(1)
                .component(JNEDataComponents.PUMPS, 1)
        );
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    @Override
    public boolean isValidSlot(String slot) {
        return "mainhand".equalsIgnoreCase(slot) || "offhand".equalsIgnoreCase(slot);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return ItemAttributeModifiers.EMPTY;
    }

    private float getRangedDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumber(GearProperties.RANGED_DAMAGE, 1.0F);
    }

    private float getProjectileSpeed(ItemStack stack) {
        return GearData.getProperties(stack).getNumber(GearProperties.PROJECTILE_SPEED, 1.0F);
    }

    private float getProjectileAccuracy(ItemStack stack) {
        return GearData.getProperties(stack).getNumber(GearProperties.PROJECTILE_ACCURACY, 1.0F);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack shotgun = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            setPumps(shotgun, getPumps(shotgun) + 1);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    JNESoundEvents.SHOTGUN_LOAD.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            if (level.isClientSide()) {
                pump.stop(player);
                pump.startIfStopped(player.tickCount, player);
            }
            return InteractionResultHolder.pass(shotgun);
        }

        if (GearHelper.isBroken(shotgun)) {
            return InteractionResultHolder.pass(shotgun);
        }

        ItemStack projectileStack = player.getProjectile(shotgun);
        List<ItemStack> draw = draw(shotgun, projectileStack, player);

        boolean hasAmmo = !projectileStack.isEmpty() || player.getAbilities().instabuild;

        int pumps = getPumps(shotgun);

        if (pumps > 3) {
            this.overpumpExplosion(level, player, shotgun, draw);
            if (!player.getAbilities().instabuild) {
                GearHelper.attemptDamage(shotgun, 5, player, hand);
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    JNESoundEvents.SHOTGUN_USE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            setPumps(shotgun, 1);
            return InteractionResultHolder.pass(shotgun);
        }

        if (!hasAmmo) {
            float baseVelocity = 1.5f;
            float baseInaccuracy = 7 + (pumps * 5);
            this.shoot(level, player, shotgun, List.of(), baseVelocity, baseInaccuracy);

            if (!player.getAbilities().instabuild) {
                GearHelper.attemptDamage(shotgun, pumps, player, hand);
            }
        } else {
            float baseVelocity = 1.5f;
            float baseInaccuracy = 7 + (pumps * 5);
            this.shoot(level, player, shotgun, draw, baseVelocity, baseInaccuracy);

            if (!player.getAbilities().instabuild && !projectileStack.isEmpty()) {
                projectileStack.shrink(1);
                if (projectileStack.isEmpty()) {
                    player.getInventory().removeItem(projectileStack);
                }
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                JNESoundEvents.SHOTGUN_USE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        setPumps(shotgun, 1);

        return InteractionResultHolder.pass(shotgun);
    }

    @Override
    protected void shoot(Level level, LivingEntity shooter, ItemStack shotgun, List<ItemStack> projectileItems, float velocity, float inaccuracy) {
        float damageMultiplier = getRangedDamage(shotgun);
        float speedMultiplier = getProjectileSpeed(shotgun);
        float accuracyMultiplier = getProjectileAccuracy(shotgun);

        float finalVelocity = velocity * Math.max(speedMultiplier, 0.1f);
        float finalInaccuracy = inaccuracy / Math.max(accuracyMultiplier, 0.1f);
        float finalDamage = BASE_PELLET_DAMAGE * damageMultiplier;

        int count = calculateCount(shotgun);
        int cooldown = calculateCooldown(shotgun);
        double selfRecoil = calculateSelfRecoil(shooter, shotgun);

        boolean isBlank = false;
        if (!projectileItems.isEmpty()) {
            isBlank = projectileItems.getFirst().is(JNEItems.BLANK_SHOTGUN_SHELL.get());
        }

        if (level.isClientSide()) {
            VFXHelper.shotgunScreenShake(shooter.position(), 8.0f, Easing.LINEAR);
            fire.start(shooter.tickCount, shooter);
            ClientItemData.getOrCreate(shotgun).put("shootFlash", true);
        } else {
            if (!projectileItems.isEmpty() && !isBlank) {
                Item shellItem = projectileItems.getFirst().getItem();

                for (int i = 0; i < count; i++) {
                    Vec3 look = shooter.getLookAngle();
                    Projectile pellet;

                    if (shellItem == JNEItems.SHOTGUN_SHELL.get()) {
                        pellet = new GearShotgunPellet(
                                shooter.getX(), shooter.getY() + 1.0, shooter.getZ(),
                                level, shooter, finalDamage
                        );
                    } else if (shellItem == JNEItems.SLUG_SHOTGUN_SHELL.get()) {
                        pellet = new GearSlugPellet(
                                shooter.getX(), shooter.getY() + 1.0, shooter.getZ(),
                                level, shooter, finalDamage
                        );
                    } else if (shellItem == JNEItems.PHASMO_SHOTGUN_SHELL.get()) {
                        pellet = new GearPhasmoPellet(
                                shooter.getX(), shooter.getY() + 1.0, shooter.getZ(),
                                level, shooter, finalDamage
                        );
                    } else {
                        if (shellItem instanceof ShotgunShellItem shotgunShellItem) {
                            pellet = shotgunShellItem.createPellet(level, shooter,
                                    shooter.getX(), shooter.getY() + 1.0, shooter.getZ()
                            );
                        } else {
                            pellet = new GearShotgunPellet(
                                    shooter.getX(), shooter.getY() + 1.0, shooter.getZ(),
                                    level, shooter, finalDamage
                            );
                        }
                    }

                    pellet.shoot(look.x, look.y, look.z, finalVelocity, finalInaccuracy);
                    level.addFreshEntity(pellet);
                }
            } else {
                for (int i = 0; i < count; i++) {
                    Vec3 look = shooter.getLookAngle();
                    Projectile pellet = new GearShotgunPellet(
                            shooter.getX(), shooter.getY() + 1.0, shooter.getZ(),
                            level, shooter, finalDamage
                    );
                    pellet.shoot(look.x, look.y, look.z, finalVelocity, finalInaccuracy);
                    level.addFreshEntity(pellet);
                }
            }
        }

        applyRecoil(shooter, shotgun, cooldown, selfRecoil);
    }

    @Override
    protected void overpumpExplosion(Level level, LivingEntity shooter, ItemStack shotgun, List<ItemStack> projectileItems) {
        int cooldown = 80;
        float damage = 5.0f;

        if (level.isClientSide()) {
            VFXHelper.explosionScreenShake(shooter.position(), 16.0f, Easing.LINEAR);
            explode.start(shooter.tickCount, shooter);
            ClientItemData.getOrCreate(shotgun).put("shootFlash", true);
        } else {
            level.explode(shooter, shooter.getX(), shooter.getY(), shooter.getZ(), 4, Level.ExplosionInteraction.NONE);
            ((ServerLevel) level).sendParticles(
                    JNEParticleTypes.RED_EXPLOSION_EMITTER.get(),
                    shooter.getX(), shooter.getY(), shooter.getZ(),
                    1, 0.0, 0.0, 0.0, 0);
            damage = 10.0f;
        }

        applyRecoil(shooter, shotgun, cooldown, 1.5);

        int counterforce = HolderHelper.getEnchantmentLevel(JNEEnchantments.COUNTERFORCE, shotgun);
        boolean dashCancel = false;
        if (counterforce > 0 && !shooter.onGround() && (shooter.fallDistance <= 0.0 || shooter.isInFluidType())) {
            dashCancel = true;
        }
        if (!dashCancel) {
            shooter.hurt(level.damageSources().source(JNEDamageSources.SHOTGUN_EXPLOSION, shooter), damage);
        }
    }

    private void applyRecoil(LivingEntity shooter, ItemStack shotgun, int cooldown, double recoil) {
        int counterforce = HolderHelper.getEnchantmentLevel(JNEEnchantments.COUNTERFORCE, shotgun);
        Vec3 pushBack = new Vec3(-shooter.getLookAngle().x, -shooter.getLookAngle().y, -shooter.getLookAngle().z).normalize();

        if (counterforce > 0 && !shooter.onGround() && (shooter.fallDistance <= 0.0 || shooter.isInFluidType())) {
            pushBack = new Vec3(shooter.getLookAngle().x, shooter.getLookAngle().y, shooter.getLookAngle().z).normalize();
            if (JNEConfigs.COUNTERFORCE_IFRAMES.get() > 0) {
                shooter.invulnerableTime = JNEConfigs.COUNTERFORCE_IFRAMES.get();
                shooter.level().playSound(null, shooter.blockPosition(),
                        JNESoundEvents.SHOTGUN_COUNTERFORCE.get(),
                        SoundSource.PLAYERS, 1.0f, 1.0f);
                ScreenFlash.triggerScreenFlash(5, JNEConfigs.COUNTERFORCE_IFRAMES.get() / 4,
                        5, 0x33FF0000, false, true);
            }
        }

        if (shooter instanceof Player player) {
            player.getCooldowns().addCooldown(this, cooldown);
        }
        shooter.push(pushBack.scale(recoil));
    }

    private int calculateCount(ItemStack shotgun) {
        int base = 10;
        int volley = HolderHelper.getEnchantmentLevel(JNEEnchantments.VOLLEY, shotgun);
        int quickCharge = HolderHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, shotgun);
        if (volley > 0) base += (volley * 2);
        else if (quickCharge > 0) base -= (quickCharge * 2);
        float charge = GearData.getProperties(shotgun).getNumber(GearProperties.CHARGING_VALUE, 0.0F);
        int count = (int)(base * charge * getPumps(shotgun));
        return Math.max(3, Math.min(30, count));
    }

    private int calculateCooldown(ItemStack shotgun) {
        int base = JNEConfigs.PUMP_CHARGE_SHOTGUN_COOLDOWN.get();
        int volley = HolderHelper.getEnchantmentLevel(JNEEnchantments.VOLLEY, shotgun);
        int quickCharge = HolderHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, shotgun);

        if (volley > 0) base += (volley * 10);
        else if (quickCharge > 0) base -= (quickCharge * 4);
        return base;
    }

    private double calculateSelfRecoil(LivingEntity shooter, ItemStack shotgun) {
        double base = JNEConfigs.SHOTGUN_SELF_RECOIL.get();
        int recoil = HolderHelper.getEnchantmentLevel(JNEEnchantments.RECOIL, shotgun);
        if (recoil > 0) base += ((double) recoil / 10);

        Vec3 raycastStart = shooter.getEyePosition(1.0F);
        Vec3 raycastEnd = raycastStart.add(shooter.getViewVector(1.0F).scale(JNEConfigs.POINT_BLANK_SELF_RECOIL_DISTANCE.get()));
        AABB pointBlankBoundingBox = new AABB(raycastStart, raycastEnd);

        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                shooter.level(), shooter, raycastStart, raycastEnd, pointBlankBoundingBox,
                victim -> victim instanceof LivingEntity && victim != shooter);
        if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity living && living.isAlive()) {
            base += JNEConfigs.POINT_BLANK_SELF_RECOIL_BONUS.get();
            if (shooter.level().isClientSide) ShotgunFistItem.Client.pointBlankParticle(shooter.level(),
                    raycastStart.add(shooter.getViewVector(1.0F).scale(1)));
        }

        return base + ((double) getPumps(shotgun) / 10);
    }

    private Projectile createGearPellet(Level level, LivingEntity shooter, double x, double y, double z, float damage) {
        return new GearShotgunPellet(x, y, z, level, shooter, damage);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        GearHelper.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(GearProperties.DURABILITY);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        GearClientHelper.addInformation(stack, context, tooltip, flag);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return GearHelper.getEnchantmentValue(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return GearHelper.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        GearHelper.postHurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        return GearHelper.onBlockDestroyed(stack, level, state, pos, entity);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return GearHelper.getDestroySpeed(stack, state);
    }
}
