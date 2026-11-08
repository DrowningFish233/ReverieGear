package com.drowningfish233.reveriegear.compat.irons;

import com.drowningfish233.reveriegear.item.gear.RFGearFuriosoItem;
import com.drowningfish233.reveriegear.registry.GearSet;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;


public final class IronsMalumGearSets {
    private IronsMalumGearSets() {
    }

    public static GearSet furioso() {
        return new GearSet(RFGearTypes.FURIOSO, "furioso_blade",
                () -> new RFGearFuriosoItem(RFGearTypes.FURIOSO));
    }




}
