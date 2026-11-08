package com.drowningfish233.reveriegear.compat.cataclysm;

import com.drowningfish233.reveriegear.item.gear.RFGearAncientSpearItem;
import com.drowningfish233.reveriegear.item.gear.RFGearAnnihilatorItem;
import com.drowningfish233.reveriegear.item.gear.RFGearAstrapeItem;
import com.drowningfish233.reveriegear.item.gear.RFGearBrontesItem;
import com.drowningfish233.reveriegear.item.gear.RFGearCeraunusItem;
import com.drowningfish233.reveriegear.item.gear.RFGearCursedBowItem;
import com.drowningfish233.reveriegear.item.gear.RFGearIncineratorItem;
import com.drowningfish233.reveriegear.item.gear.RFGearInfernalForgeItem;
import com.drowningfish233.reveriegear.item.gear.RFGearMeatShredderItem;
import com.drowningfish233.reveriegear.item.gear.RFGearSeekingBowItem;
import com.drowningfish233.reveriegear.item.gear.RFGearShieldItem;
import com.drowningfish233.reveriegear.item.gear.RFGearSoulSpearItem;
import com.drowningfish233.reveriegear.item.gear.RFGearTidalClawsItem;
import com.drowningfish233.reveriegear.item.gear.RFGVoidAssaultShoulderWeaponItem;
import com.drowningfish233.reveriegear.registry.GearSet;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;
import net.minecraft.world.item.Item;


public final class CataclysmGearSets {
    private CataclysmGearSets() {
    }

    public static GearSet voidAssault() {
        return new GearSet(RFGearTypes.VOID_ASSAULT_SHOULDER_WEAPON, "void_assault_shoulder_weapon_head",
                () -> new RFGVoidAssaultShoulderWeaponItem(RFGearTypes.VOID_ASSAULT_SHOULDER_WEAPON));
    }

    public static GearSet flameBulwark() {
        return new GearSet(RFGearTypes.FLAME_BULWARK, "flame_bulwark_plate",
                () -> new RFGearShieldItem(RFGearTypes.FLAME_BULWARK));
    }

    public static GearSet infernalForge() {
        return new GearSet(RFGearTypes.INFERNAL_FORGE, "infernal_forge_head",
                () -> new RFGearInfernalForgeItem(RFGearTypes.INFERNAL_FORGE,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }

    public static GearSet soulSpear() {
        return new GearSet(RFGearTypes.SOUL_SPEAR, "soul_spear_blade",
                () -> new RFGearSoulSpearItem(RFGearTypes.SOUL_SPEAR,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }

    public static GearSet cursedBow() {
        return new GearSet(RFGearTypes.CURSED_BOW, "cursed_bow_limb",
                () -> new RFGearCursedBowItem(RFGearTypes.CURSED_BOW,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }

    public static GearSet incinerator() {
        return new GearSet(RFGearTypes.INCINERATOR, "incinerator_blade",
                () -> new RFGearIncineratorItem(RFGearTypes.INCINERATOR,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }

    public static GearSet brontes() {
        return new GearSet(RFGearTypes.BRONTES, "brontes_head",
                () -> new RFGearBrontesItem(RFGearTypes.BRONTES,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }

    public static GearSet ancientSpear() {
        return new GearSet(RFGearTypes.ANCIENT_SPEAR, "ancient_spear_head",
                () -> new RFGearAncientSpearItem(RFGearTypes.ANCIENT_SPEAR,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }

    public static GearSet ceraunus() {
        return new GearSet(RFGearTypes.CERAUNUS, "ceraunus_blade",
                () -> new RFGearCeraunusItem(RFGearTypes.CERAUNUS,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }

    public static GearSet annihilator() {
        return new GearSet(RFGearTypes.ANNIHILATOR, "annihilator_blade",
                () -> new RFGearAnnihilatorItem(RFGearTypes.ANNIHILATOR,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }

    public static GearSet meatShredder() {
        return new GearSet(RFGearTypes.MEAT_SHREDDER, "meat_shredder_blade",
                () -> new RFGearMeatShredderItem(RFGearTypes.MEAT_SHREDDER,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }

    public static GearSet seekingBow() {
        return new GearSet(RFGearTypes.SEEKING_BOW, "seeking_bow_limb",
                () -> new RFGearSeekingBowItem(RFGearTypes.SEEKING_BOW,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }

    public static GearSet astrape() {
        return new GearSet(RFGearTypes.ASTRAPE, "astrape_blade",
                () -> new RFGearAstrapeItem(RFGearTypes.ASTRAPE,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }

    public static GearSet tidalClaws() {
        return new GearSet(RFGearTypes.TIDAL_CLAWS, "tidal_claws_blade",
                () -> new RFGearTidalClawsItem(RFGearTypes.TIDAL_CLAWS,
                        new Item.Properties().stacksTo(1).fireResistant()));
    }
}
