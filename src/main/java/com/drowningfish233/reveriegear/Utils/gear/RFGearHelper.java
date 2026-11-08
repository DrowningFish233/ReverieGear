package com.drowningfish233.reveriegear.Utils.gear;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.registry.gear.RFGearProperties;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import static net.silentchaos512.gear.util.GearData.getProperties;

public class RFGearHelper {

    public static float getMaxMana(ItemStack stack) {
        if (!GearHelper.isGear(stack)) {
            return 0;
        }
        return GearData.getProperties(stack).getNumber(RFGearProperties.MAX_MANA);
    }

    public static float getGeasLimitBonus(ItemStack gear) {
        if (GearHelper.isBroken(gear)) return 0;

        float value = getProperties(gear).getNumber(RFGearProperties.GEAS_LIMIT);
        return Math.round(value * 100.0f) / 100.0f;
    }

    public static float getHealingReceivedMultiplier(ItemStack gear) {
        if (GearHelper.isBroken(gear)) return 0.0f;
        return getProperties(gear).getNumber(RFGearProperties.HEALING_MULTIPLIER);
    }

    public static void addAttributeModifiersBothHands(ItemStack stack, ItemAttributeModifiers.Builder builder) {
        if (GearHelper.isBroken(stack)) return;

        var properties = getProperties(stack);

        GearHelper.addAttributeModifiers(stack, builder);

        float spellPower = properties.getNumber(RFGearProperties.SPELL_POWER);
        if (spellPower != 0) {
            builder.add(
                    AttributeRegistry.SPELL_POWER,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "spell_power"),
                            spellPower,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.HAND
            );
        }

        float spellResist = properties.getNumber(RFGearProperties.SPELL_RESIST);
        if (spellResist != 0) {
            builder.add(
                    AttributeRegistry.SPELL_RESIST,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "spell_resist"),
                            spellResist,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.ARMOR
            );
        }

        float manaRegen = properties.getNumber(RFGearProperties.MANA_REGEN);
        if (manaRegen != 0) {
            builder.add(
                    AttributeRegistry.MANA_REGEN,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "mana_regen"),
                            manaRegen,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.HAND
            );
        }
    }

    public static float getSpellPower(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0f;
        return getProperties(stack).getNumber(RFGearProperties.SPELL_POWER);
    }

    public static float getSpellResist(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0f;
        return getProperties(stack).getNumber(RFGearProperties.SPELL_RESIST);
    }

    public static float getManaRegen(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0f;
        return getProperties(stack).getNumber(RFGearProperties.MANA_REGEN);
    }

    public static void addSpellAttributes(ItemStack stack, ItemAttributeModifiers.Builder builder, EquipmentSlotGroup slotGroup) {
        if (GearHelper.isBroken(stack)) return;

        var properties = getProperties(stack);

        float spellPower = properties.getNumber(RFGearProperties.SPELL_POWER);
        if (spellPower != 0) {
            builder.add(
                    AttributeRegistry.SPELL_POWER,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "spell_power"),
                            spellPower,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    slotGroup
            );
        }

        float spellResist = properties.getNumber(RFGearProperties.SPELL_RESIST);
        if (spellResist != 0) {
            builder.add(
                    AttributeRegistry.SPELL_RESIST,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "spell_resist"),
                            spellResist,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    slotGroup
            );
        }

        float manaRegen = properties.getNumber(RFGearProperties.MANA_REGEN);
        if (manaRegen != 0) {
            builder.add(
                    AttributeRegistry.MANA_REGEN,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "mana_regen"),
                            manaRegen,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    slotGroup
            );
        }
    }
}
