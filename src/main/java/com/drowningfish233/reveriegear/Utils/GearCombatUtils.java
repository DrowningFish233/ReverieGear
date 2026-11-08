package com.drowningfish233.reveriegear.Utils;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


public final class GearCombatUtils {
    private GearCombatUtils() {
    }

    public static float getActualWeaponDamage(LivingEntity living, ItemStack itemStack) {
        double totalDamage = living.getAttributeValue(Attributes.ATTACK_DAMAGE);

        if (living.getMainHandItem() == itemStack) {
            return (float) totalDamage;
        }
        ItemStack mainHandStack = living.getMainHandItem();
        if (!mainHandStack.isEmpty()) {
            ItemAttributeModifiers modifiers = mainHandStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                if (entry.attribute().is(Attributes.ATTACK_DAMAGE) && entry.slot().test(EquipmentSlot.MAINHAND)) {
                    if (entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE) {
                        totalDamage -= entry.modifier().amount();
                    }
                }
            }
        }
        ItemAttributeModifiers weaponModifiers = itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        for (ItemAttributeModifiers.Entry entry : weaponModifiers.modifiers()) {
            if (entry.attribute().is(Attributes.ATTACK_DAMAGE) && entry.slot().test(EquipmentSlot.MAINHAND)) {
                if (entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE) {
                    totalDamage += entry.modifier().amount();
                }
            }
        }

        return (float) totalDamage;
    }

    public static boolean applyRandomDebuff(LivingEntity target, int duration, int amplifier) {
        if (target == null || target.level().isClientSide()) {
            return false;
        }

        List<Holder<MobEffect>> harmfulEffects = BuiltInRegistries.MOB_EFFECT.holders()
                .filter(holder -> holder.value().getCategory() == MobEffectCategory.HARMFUL)
                .collect(Collectors.toList());

        if (harmfulEffects.isEmpty()) {
            return false;
        }

        Holder<MobEffect> debuff = harmfulEffects.get(ThreadLocalRandom.current().nextInt(harmfulEffects.size()));
        target.addEffect(new MobEffectInstance(debuff, duration, amplifier));
        return true;
    }

    public static boolean applyRandomBuff(LivingEntity target, int duration, int amplifier) {
        if (target == null || target.level().isClientSide()) {
            return false;
        }

        List<Holder<MobEffect>> beneficialEffects = BuiltInRegistries.MOB_EFFECT.holders()
                .filter(holder -> holder.value().getCategory() == MobEffectCategory.BENEFICIAL)
                .collect(Collectors.toList());

        if (beneficialEffects.isEmpty()) {
            return false;
        }

        Holder<MobEffect> buff = beneficialEffects.get(ThreadLocalRandom.current().nextInt(beneficialEffects.size()));
        target.addEffect(new MobEffectInstance(buff, duration, amplifier));
        return true;
    }
}
