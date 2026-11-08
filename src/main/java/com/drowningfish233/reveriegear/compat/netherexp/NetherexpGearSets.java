package com.drowningfish233.reveriegear.compat.netherexp;

import com.drowningfish233.reveriegear.item.gear.RFGearPumpChargeShotgunItem;
import com.drowningfish233.reveriegear.registry.GearSet;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;


public final class NetherexpGearSets {
    private NetherexpGearSets() {
    }

    public static GearSet pumpChargeShotgun() {
        return new GearSet(RFGearTypes.PUMP_CHARGE_SHOTGUN, "pump_charge_shotgun_barrel",
                () -> new RFGearPumpChargeShotgunItem(RFGearTypes.PUMP_CHARGE_SHOTGUN));
    }
}
