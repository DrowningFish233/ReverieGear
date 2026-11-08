package com.drowningfish233.reveriegear.compat.lodestone;

import com.drowningfish233.reveriegear.item.gear.RFGearCaneItem;
import com.drowningfish233.reveriegear.registry.GearSet;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;


public final class LodestoneGearSets {
    private LodestoneGearSets() {
    }

    public static GearSet cane() {
        return new GearSet(RFGearTypes.CANE, "cane_head",
                () -> new RFGearCaneItem(RFGearTypes.CANE));
    }
}
