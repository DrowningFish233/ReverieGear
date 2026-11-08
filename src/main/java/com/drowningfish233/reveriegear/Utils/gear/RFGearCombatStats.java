package com.drowningfish233.reveriegear.Utils.gear;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public final class RFGearCombatStats {
    private static final float MIN_ATTACK_SPEED = 0.1F;

    private RFGearCombatStats() {
    }

    public static float getAttackSpeed(ItemStack stack, float fallback) {
        if (stack.isEmpty() || !GearHelper.isGear(stack)) {
            return fallback;
        }
        float value = GearData.getProperties(stack).getNumber(GearProperties.ATTACK_SPEED, fallback);
        return value > 0.0F ? value : fallback;
    }

    public static float getDrawSpeedMultiplier(ItemStack stack) {
        if (stack.isEmpty() || !GearHelper.isGear(stack)) {
            return 1.0F;
        }
        float value = GearData.getProperties(stack).getNumber(GearProperties.DRAW_SPEED, 0.0F);
        return 1.0F + Math.max(0.0F, value);
    }

    public static float getRangedDamage(ItemStack stack, float fallback) {
        if (stack.isEmpty() || !GearHelper.isGear(stack)) {
            return fallback;
        }
        float value = GearData.getProperties(stack).getNumber(GearProperties.RANGED_DAMAGE, fallback);
        return value > 0.0F ? value : fallback;
    }

    public static int scaleMeleeCooldown(LivingEntity entity, ItemStack stack, int baseCooldownTicks, float baseAttackSpeed) {
        float currentAttackSpeed = getAttackSpeed(stack, baseAttackSpeed);
        if (entity != null && entity.getMainHandItem() == stack) {
            double attributeValue = entity.getAttributeValue(Attributes.ATTACK_SPEED);
            if (attributeValue > 0.0D) {
                currentAttackSpeed = (float) attributeValue;
            }
        }
        float ratio = baseAttackSpeed / Math.max(MIN_ATTACK_SPEED, currentAttackSpeed);
        return clampCooldown(baseCooldownTicks, ratio, 0.25F, 2.5F);
    }

    public static int scaleMeleeCooldown(ItemStack stack, int baseCooldownTicks, float baseAttackSpeed) {
        return scaleMeleeCooldown(null, stack, baseCooldownTicks, baseAttackSpeed);
    }

    public static int scaleRangedCooldown(ItemStack stack, int baseCooldownTicks) {
        return clampCooldown(baseCooldownTicks, 1.0F / getDrawSpeedMultiplier(stack), 0.4F, 2.0F);
    }

    public static int scaleDrawTicks(ItemStack stack, int elapsedTicks) {
        return Math.max(0, Math.round(elapsedTicks * getDrawSpeedMultiplier(stack)));
    }

    private static int clampCooldown(int baseCooldownTicks, float ratio, float minFactor, float maxFactor) {
        int min = Math.max(2, Math.round(baseCooldownTicks * minFactor));
        int max = Math.max(min, Math.round(baseCooldownTicks * maxFactor));
        return Mth.clamp(Math.round(baseCooldownTicks * ratio), min, max);
    }
}
