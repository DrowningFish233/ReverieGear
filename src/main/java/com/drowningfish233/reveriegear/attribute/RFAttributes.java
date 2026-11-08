package com.drowningfish233.reveriegear.attribute;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public final class RFAttributes {

    public static final Attribute ARCANE_SCYTHE_CONVERSION = new RangedAttribute(
            "attribute.reveriegear.arcane_scythe_conversion",
            0.0,
            0.0,
            1.0
    ).setSyncable(true);

    private RFAttributes() {
    }
}
