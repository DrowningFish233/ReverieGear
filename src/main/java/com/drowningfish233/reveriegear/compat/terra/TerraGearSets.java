package com.drowningfish233.reveriegear.compat.terra;

import com.drowningfish233.reveriegear.item.gear.RFGearBoomerangItem;
import com.drowningfish233.reveriegear.item.gear.RFGearSummonSword;
import com.drowningfish233.reveriegear.item.gear.RFGearWhipItem;
import com.drowningfish233.reveriegear.item.gear.RFGearYoyosItem;
import com.drowningfish233.reveriegear.registry.GearSet;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;


public final class TerraGearSets {
    private TerraGearSets() {
    }

    public static GearSet boomerang() {
        return new GearSet(RFGearTypes.BOOMERANG, "boomerang_blade",
                () -> new RFGearBoomerangItem(RFGearTypes.BOOMERANG));
    }

    public static GearSet whip() {
        return new GearSet(RFGearTypes.WHIP, "whip_head",
                () -> new RFGearWhipItem(RFGearTypes.WHIP));
    }

    public static GearSet yoyos() {
        return new GearSet(RFGearTypes.YOYOS, "yoyos_head",
                () -> new RFGearYoyosItem(RFGearTypes.YOYOS));
    }

    public static GearSet summonSword() {
        return new GearSet(RFGearTypes.SUMMON_SWORD, "summon_sword_blade",
                () -> new RFGearSummonSword(RFGearTypes.SUMMON_SWORD));
    }


}
