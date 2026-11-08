package com.drowningfish233.reveriegear.compat.malum;

import com.drowningfish233.reveriegear.item.gear.RFGearEdgeOfDeliveranceItem;
import com.drowningfish233.reveriegear.item.gear.RFGearErosionScepterItem;
import com.drowningfish233.reveriegear.item.gear.RFGearHexStaffItem;
import com.drowningfish233.reveriegear.item.gear.RFGearMagicScytheItem;
import com.drowningfish233.reveriegear.item.gear.RFGearMalignantStrongholdArmor;
import com.drowningfish233.reveriegear.item.gear.RFGearScytheItem;
import com.drowningfish233.reveriegear.item.gear.RFGearSoulHunterArmor;
import com.drowningfish233.reveriegear.item.gear.RFGearSunderingAnchorItem;
import com.drowningfish233.reveriegear.item.gear.RFGearTyrvingItem;
import com.drowningfish233.reveriegear.item.gear.RFGearUnwindingChaosStaffItem;
import com.drowningfish233.reveriegear.item.gear.RFGearWeightOfWorldsItem;
import com.drowningfish233.reveriegear.registry.GearSet;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;
import com.sammy.malum.registry.common.magic.MalumSpiritTypes;
import net.minecraft.world.item.ArmorItem;


public final class MalumGearSets {
    private MalumGearSets() {
    }

    public static GearSet erosionScepter() {
        return new GearSet(RFGearTypes.EROSION_SCEPTER, "erosion_scepter_head",
                () -> new RFGearErosionScepterItem(RFGearTypes.EROSION_SCEPTER, MalumSpiritTypes.UMBRAL_SPIRIT));
    }

    public static GearSet sunderingAnchor() {
        return new GearSet(RFGearTypes.SUNDERING_ANCHOR, "sundering_anchor_blade",
                () -> new RFGearSunderingAnchorItem(RFGearTypes.SUNDERING_ANCHOR));
    }

    public static GearSet scythe() {
        return new GearSet(RFGearTypes.SCYTHE, "scythe_blade",
                () -> new RFGearScytheItem(RFGearTypes.SCYTHE, MalumSpiritTypes.WICKED_SPIRIT));
    }

    public static GearSet magicScythe() {
        return new GearSet(RFGearTypes.MAGIC_SCYTHE, "magic_scythe_blade",
                () -> new RFGearMagicScytheItem(RFGearTypes.MAGIC_SCYTHE, MalumSpiritTypes.WICKED_SPIRIT));
    }

    public static GearSet deliveranceScythe() {
        return new GearSet(RFGearTypes.DELIVERANCE_SCYTHE, "deliverance_scythe_blade",
                () -> new RFGearEdgeOfDeliveranceItem(RFGearTypes.DELIVERANCE_SCYTHE, MalumSpiritTypes.WICKED_SPIRIT));
    }

    public static GearSet tyrving() {
        return new GearSet(RFGearTypes.TYRVING, "tyrving_blade",
                () -> new RFGearTyrvingItem(RFGearTypes.TYRVING, MalumSpiritTypes.WICKED_SPIRIT));
    }

    public static GearSet hexStaff() {
        return new GearSet(RFGearTypes.HEX_STAFF, "hex_staff_head",
                () -> new RFGearHexStaffItem(RFGearTypes.HEX_STAFF, MalumSpiritTypes.WICKED_SPIRIT));
    }

    public static GearSet unwindingChaosStaff() {
        return new GearSet(RFGearTypes.UNWINDING_CHAOS_STAFF, "unwinding_chaos_staff_head",
                () -> new RFGearUnwindingChaosStaffItem(RFGearTypes.UNWINDING_CHAOS_STAFF, MalumSpiritTypes.INFERNAL_SPIRIT));
    }

    public static GearSet weightOfWorlds() {
        return new GearSet(RFGearTypes.WEIGHT_OF_WORLDS, "weight_of_worlds_blade",
                () -> new RFGearWeightOfWorldsItem(RFGearTypes.WEIGHT_OF_WORLDS));
    }

    public static GearSet malignantHelmet() {
        return new GearSet(RFGearTypes.MALIGNANT_STRONGHOLD_HELMET, "malignant_stronghold_helmet_plate",
                () -> new RFGearMalignantStrongholdArmor(RFGearTypes.MALIGNANT_STRONGHOLD_HELMET, ArmorItem.Type.HELMET));
    }

    public static GearSet malignantChestplate() {
        return new GearSet(RFGearTypes.MALIGNANT_STRONGHOLD_CHESTPLATE, "malignant_stronghold_chestplate_plate",
                () -> new RFGearMalignantStrongholdArmor(RFGearTypes.MALIGNANT_STRONGHOLD_CHESTPLATE, ArmorItem.Type.CHESTPLATE));
    }

    public static GearSet malignantLeggings() {
        return new GearSet(RFGearTypes.MALIGNANT_STRONGHOLD_LEGGINGS, "malignant_stronghold_leggings_plate",
                () -> new RFGearMalignantStrongholdArmor(RFGearTypes.MALIGNANT_STRONGHOLD_LEGGINGS, ArmorItem.Type.LEGGINGS));
    }

    public static GearSet malignantBoots() {
        return new GearSet(RFGearTypes.MALIGNANT_STRONGHOLD_BOOTS, "malignant_stronghold_boots_plate",
                () -> new RFGearMalignantStrongholdArmor(RFGearTypes.MALIGNANT_STRONGHOLD_BOOTS, ArmorItem.Type.BOOTS));
    }

    public static GearSet soulHunterHelmet() {
        return new GearSet(RFGearTypes.SOUL_HUNTER_HELMET, "soul_hunter_helmet_plate",
                () -> new RFGearSoulHunterArmor(RFGearTypes.SOUL_HUNTER_HELMET, ArmorItem.Type.HELMET));
    }

    public static GearSet soulHunterChestplate() {
        return new GearSet(RFGearTypes.SOUL_HUNTER_CHESTPLATE, "soul_hunter_chestplate_plate",
                () -> new RFGearSoulHunterArmor(RFGearTypes.SOUL_HUNTER_CHESTPLATE, ArmorItem.Type.CHESTPLATE));
    }

    public static GearSet soulHunterLeggings() {
        return new GearSet(RFGearTypes.SOUL_HUNTER_LEGGINGS, "soul_hunter_leggings_plate",
                () -> new RFGearSoulHunterArmor(RFGearTypes.SOUL_HUNTER_LEGGINGS, ArmorItem.Type.LEGGINGS));
    }

    public static GearSet soulHunterBoots() {
        return new GearSet(RFGearTypes.SOUL_HUNTER_BOOTS, "soul_hunter_boots_plate",
                () -> new RFGearSoulHunterArmor(RFGearTypes.SOUL_HUNTER_BOOTS, ArmorItem.Type.BOOTS));
    }
}
