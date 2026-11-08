package com.drowningfish233.reveriegear.compat.irons;

import com.drowningfish233.reveriegear.item.gear.RFGearSpellBookItem;
import com.drowningfish233.reveriegear.registry.GearSet;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;
import net.minecraft.world.item.Item;


public final class IronsCuriosGearSets {
    private IronsCuriosGearSets() {
    }

    public static GearSet spellBook() {
        return new GearSet(RFGearTypes.SPELLBOOK, "spellbook_cover",
                () -> new RFGearSpellBookItem(RFGearTypes.SPELLBOOK, "spellbook",
                        new Item.Properties().stacksTo(1).setNoRepair()));
    }
}
