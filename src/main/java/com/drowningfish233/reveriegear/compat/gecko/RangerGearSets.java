package com.drowningfish233.reveriegear.compat.gecko;

import com.drowningfish233.reveriegear.item.gear.RFGearRangerArmor;
import com.drowningfish233.reveriegear.registry.GearSet;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;
import net.minecraft.world.item.ArmorItem;


public final class RangerGearSets {
    private RangerGearSets() {
    }

    public static GearSet helmet() {
        return new GearSet(RFGearTypes.RANGER_HELMET, "ranger_helmet_plate",
                () -> new RFGearRangerArmor(RFGearTypes.RANGER_HELMET, ArmorItem.Type.HELMET));
    }

    public static GearSet chestplate() {
        return new GearSet(RFGearTypes.RANGER_CHESTPLATE, "ranger_chestplate_plate",
                () -> new RFGearRangerArmor(RFGearTypes.RANGER_CHESTPLATE, ArmorItem.Type.CHESTPLATE));
    }

    public static GearSet leggings() {
        return new GearSet(RFGearTypes.RANGER_LEGGINGS, "ranger_leggings_plate",
                () -> new RFGearRangerArmor(RFGearTypes.RANGER_LEGGINGS, ArmorItem.Type.LEGGINGS));
    }

    public static GearSet boots() {
        return new GearSet(RFGearTypes.RANGER_BOOTS, "ranger_boots_plate",
                () -> new RFGearRangerArmor(RFGearTypes.RANGER_BOOTS, ArmorItem.Type.BOOTS));
    }
}
