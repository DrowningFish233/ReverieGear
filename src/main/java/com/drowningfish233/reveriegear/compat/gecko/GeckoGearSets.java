package com.drowningfish233.reveriegear.compat.gecko;

import com.drowningfish233.reveriegear.item.gear.RFGearMonkArmor;
import com.drowningfish233.reveriegear.item.gear.RFGearTravelerArmor;
import com.drowningfish233.reveriegear.registry.GearSet;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;
import net.minecraft.world.item.ArmorItem;


public final class GeckoGearSets {
    private GeckoGearSets() {
    }


    public static GearSet monkHelmet() {
        return new GearSet(RFGearTypes.MONK_HELMET, "monk_helmet_plate",
                () -> new RFGearMonkArmor(RFGearTypes.MONK_HELMET, ArmorItem.Type.HELMET));
    }

    public static GearSet monkChestplate() {
        return new GearSet(RFGearTypes.MONK_CHESTPLATE, "monk_chestplate_plate",
                () -> new RFGearMonkArmor(RFGearTypes.MONK_CHESTPLATE, ArmorItem.Type.CHESTPLATE));
    }

    public static GearSet monkLeggings() {
        return new GearSet(RFGearTypes.MONK_LEGGINGS, "monk_leggings_plate",
                () -> new RFGearMonkArmor(RFGearTypes.MONK_LEGGINGS, ArmorItem.Type.LEGGINGS));
    }

    public static GearSet monkBoots() {
        return new GearSet(RFGearTypes.MONK_BOOTS, "monk_boots_plate",
                () -> new RFGearMonkArmor(RFGearTypes.MONK_BOOTS, ArmorItem.Type.BOOTS));
    }


    public static GearSet travelerHelmet() {
        return new GearSet(RFGearTypes.TRAVELER_HELMET, "traveler_helmet_plate",
                () -> new RFGearTravelerArmor(RFGearTypes.TRAVELER_HELMET, ArmorItem.Type.HELMET));
    }

    public static GearSet travelerChestplate() {
        return new GearSet(RFGearTypes.TRAVELER_CHESTPLATE, "traveler_chestplate_plate",
                () -> new RFGearTravelerArmor(RFGearTypes.TRAVELER_CHESTPLATE, ArmorItem.Type.CHESTPLATE));
    }

    public static GearSet travelerLeggings() {
        return new GearSet(RFGearTypes.TRAVELER_LEGGINGS, "traveler_leggings_plate",
                () -> new RFGearTravelerArmor(RFGearTypes.TRAVELER_LEGGINGS, ArmorItem.Type.LEGGINGS));
    }

    public static GearSet travelerBoots() {
        return new GearSet(RFGearTypes.TRAVELER_BOOTS, "traveler_boots_plate",
                () -> new RFGearTravelerArmor(RFGearTypes.TRAVELER_BOOTS, ArmorItem.Type.BOOTS));
    }
}
