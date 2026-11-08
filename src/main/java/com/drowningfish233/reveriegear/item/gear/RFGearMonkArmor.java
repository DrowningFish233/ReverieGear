package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.ReverieGear;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.silentchaos512.gear.api.item.GearType;

import java.util.function.Supplier;

public class RFGearMonkArmor extends RFGearSetArmorItem {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public RFGearMonkArmor(Supplier<GearType> gearType, Type armorType) {
        super(gearType, armorType);
    }

    @Override
    public Object getRenderProvider() {
        return geoRenderProvider(
                ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/legionnaire_ruler_armor.geo.json"),
                ReverieGear.getId("textures/models/armor/monk_geo.png"),
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json"));
    }

    @Override
    protected float getArmorMultiplier() {
        return 1.0f;
    }

    @Override
    protected float getToughnessMultiplier() {
        return 1.0f;
    }

    @Override
    protected void addSetAttributeModifiers(ItemStack stack, ItemAttributeModifiers.Builder builder,
                                            EquipmentSlotGroup equipmentSlotGroup) {
    }

    public static boolean isWearingAny(Player player) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (player.getItemBySlot(slot).getItem() instanceof RFGearMonkArmor) {
                return true;
            }
        }
        return false;
    }
}
