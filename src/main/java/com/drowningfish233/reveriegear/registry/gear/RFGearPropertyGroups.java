package com.drowningfish233.reveriegear.registry.gear;

import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyGroup;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.lib.util.Color;

import java.util.LinkedHashSet;
import java.util.Set;

public class RFGearPropertyGroups {

    public static final GearPropertyGroup SPELL = new GearPropertyGroup() {
        @Override
        public String getName() {
            return "spell";
        }

        @Override
        public Color getColor() {
            return Color.LIGHTPINK;
        }

        @Override
        public Set<GearProperty<?, ?>> getProperties() {
            Set<GearProperty<?, ?>> set = new LinkedHashSet<>();
            for (GearProperty<?, ?> property : SgRegistries.GEAR_PROPERTY) {
                if (property.getGroup() == this) {
                    set.add(property);
                }
            }
            return set;
        }
    };

    private RFGearPropertyGroups() {}
}
