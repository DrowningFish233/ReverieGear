package com.drowningfish233.reveriegear.compat.irons;

import com.drowningfish233.reveriegear.item.gear.RFGearAutoloaderCrossbow;
import com.drowningfish233.reveriegear.item.gear.RFGearLightningSpike;
import com.drowningfish233.reveriegear.item.gear.RFGearSpellMageRobe;
import com.drowningfish233.reveriegear.item.gear.RFGearStaffItem;
import com.drowningfish233.reveriegear.item.gear.RFGearStarBladeItem;
import com.drowningfish233.reveriegear.registry.GearSet;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;
import net.minecraft.world.item.ArmorItem;


public final class IronsGearSets {
    private IronsGearSets() {
    }

    public static GearSet staff() {
        return new GearSet(RFGearTypes.STAFF, "staff_head",
                () -> new RFGearStaffItem(RFGearTypes.STAFF));
    }


    public static GearSet autoloaderCrossbow() {
        return new GearSet(RFGearTypes.AUTOLOADER_CROSSBOW, "autoloader_crossbow_limb",
                () -> new RFGearAutoloaderCrossbow(RFGearTypes.AUTOLOADER_CROSSBOW));
    }

    public static GearSet starBlade() {
        return new GearSet(RFGearTypes.TIANSHA_STAR_BLADE, "tiansha_star_blade_blade",
                () -> new RFGearStarBladeItem(RFGearTypes.TIANSHA_STAR_BLADE));
    }

    public static GearSet lightningSpike() {
        return new GearSet(RFGearTypes.LIGHTNING_SPIKE, "lightning_spike_blade",
                () -> new RFGearLightningSpike(RFGearTypes.LIGHTNING_SPIKE));
    }



    public static GearSet robeHelmet() {
        return new GearSet(RFGearTypes.ROBE_HELMET, "robe_helmet_fabric",
                () -> new RFGearSpellMageRobe(RFGearTypes.ROBE_HELMET, ArmorItem.Type.HELMET));
    }

    public static GearSet robeChestplate() {
        return new GearSet(RFGearTypes.ROBE_CHESTPLATE, "robe_chestplate_fabric",
                () -> new RFGearSpellMageRobe(RFGearTypes.ROBE_CHESTPLATE, ArmorItem.Type.CHESTPLATE));
    }

    public static GearSet robeLeggings() {
        return new GearSet(RFGearTypes.ROBE_LEGGINGS, "robe_leggings_fabric",
                () -> new RFGearSpellMageRobe(RFGearTypes.ROBE_LEGGINGS, ArmorItem.Type.LEGGINGS));
    }

    public static GearSet robeBoots() {
        return new GearSet(RFGearTypes.ROBE_BOOTS, "robe_boots_fabric",
                () -> new RFGearSpellMageRobe(RFGearTypes.ROBE_BOOTS, ArmorItem.Type.BOOTS));
    }
}
