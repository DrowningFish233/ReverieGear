package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.Utils.GearCombatUtils;
import com.drowningfish233.reveriegear.Utils.gear.RFGearCombatStats;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModAttribute;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.github.L_Ender.cataclysm.items.The_Annihilator;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearAnnihilatorItem extends The_Annihilator implements GearWeapon {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;

    public RFGearAnnihilatorItem(Supplier<GearType> gearType, Properties properties) {
        super(GearHelper.getBaseItemProperties()
                .fireResistant()
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
        return EquipmentSlot.MAINHAND.getName().equalsIgnoreCase(slot)
                || EquipmentSlot.OFFHAND.getName().equalsIgnoreCase(slot);
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        return getGearType().armorDurabilityMultiplier();
    }

    @Override
    public ItemColor getItemColors() {
        return ColorHandlers::getShieldColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        GearClientHelper.addInformation(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.reveriegear.annihilator.desc").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(Component.translatable("item.reveriegear.annihilator.desc2").withStyle(ChatFormatting.DARK_GREEN));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder, true);

        builder.add(ModAttribute.ADDITIONAL_CRITICAL_DAMAGE,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("reveriegear", "annihilator_crit_damage"),
                        75f, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        return builder.build();
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
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(getDurabilityStat());
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        GearHelper.inventoryTick(stack, level, entity, slot, selected);
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
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
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int i = this.getUseDuration(stack, entity) - timeLeft;
            if (i >= 40) {
                float actualDamage = GearCombatUtils.getActualWeaponDamage(entity, stack);
                int cooldown = getCooldown(entity, stack);

                unleashAttack(level, player, actualDamage, cooldown);
                if (!level.isClientSide) {
                    player.getCooldowns().addCooldown(this, cooldown);
                }
            }
        }
    }

    private void unleashAttack(Level level, Player player, float damage, int cooldown) {
        double radius = getAttackRadius(player.getMainHandItem());
        float finalDamage = damage * 2.0F;

        ScreenShake_Entity.ScreenShake(level, player.position(), 30, 0.1f, 0, 30);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.EXPLOSION.get(), SoundSource.PLAYERS,
                1.5f, 1.0F / (player.getRandom().nextFloat() * 0.4F + 0.8F));

        List<Entity> list = level.getEntities(player, player.getBoundingBox().inflate(radius, radius, radius));
        for (Entity entity : list) {
            if (entity instanceof LivingEntity && entity != player) {
                entity.hurt(level.damageSources().mobAttack(player), finalDamage);
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.0D, 2.0D, 0.0D));
            }
        }
    }

    private double getAttackRadius(ItemStack stack) {
        float attackDamage = GearData.getProperties(stack).getNumber(GearProperties.ATTACK_DAMAGE, 1.0F);
        return 4.0F + attackDamage * 0.2F;
    }

    private int getCooldown(LivingEntity entity, ItemStack stack) {
        return RFGearCombatStats.scaleMeleeCooldown(entity, stack, 100, 1.6F);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack otherStack = player.getItemInHand(otherHand);

        if (otherStack.getItem() == this && !player.getCooldowns().isOnCooldown(this)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            float attackStrength = player.getAttackStrengthScale(0.5F);
            boolean isCritical = attackStrength > 0.9F
                    && player.fallDistance > 0.0F
                    && !player.onGround()
                    && !player.onClimbable()
                    && !player.isInWater()
                    && !player.hasEffect(MobEffects.BLINDNESS)
                    && !player.isPassenger()
                    && !player.isSprinting();

            if (isCritical) {
                float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float finalDamage = baseDamage * 2.25F;
                target.hurt(target.damageSources().playerAttack(player), finalDamage);
                target.playSound(ModSounds.PARRY.get(), 0.5F, 1.1F);
                GearHelper.attemptDamage(stack, 1, attacker, EquipmentSlot.MAINHAND);
                return true;
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        GearHelper.postHurtEnemy(stack, target, attacker);
    }

}
