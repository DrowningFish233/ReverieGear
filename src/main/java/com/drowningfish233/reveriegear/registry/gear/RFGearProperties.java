package com.drowningfish233.reveriegear.registry.gear;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyGroups;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.function.Supplier;

public class RFGearProperties {
    public static final DeferredRegister<GearProperty<?, ?>> REGISTRAR =
            DeferredRegister.create(SgRegistries.GEAR_PROPERTY,  "silentgear");

    public static final Supplier<NumberProperty> SPELL_SLOTS = REGISTRAR.register(
            "spell_slots",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, 100f)
                            .group(RFGearPropertyGroups.SPELL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );

    public static final Supplier<NumberProperty> SPELL_POWER = REGISTRAR.register(
            "spell_power",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.PERCENTAGE,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(RFGearPropertyGroups.SPELL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );

    public static final Supplier<NumberProperty> MAX_MANA = REGISTRAR.register(
            "max_mana",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, 10000f)
                            .group(RFGearPropertyGroups.SPELL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );

    public static final Supplier<NumberProperty> SPELL_RESIST = REGISTRAR.register(
            "spell_resist",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.PERCENTAGE,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(RFGearPropertyGroups.SPELL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );

    public static final Supplier<NumberProperty> MANA_REGEN = REGISTRAR.register(
            "mana_regen",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.PERCENTAGE,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, 1000f)
                            .group(RFGearPropertyGroups.SPELL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );

    public static final Supplier<NumberProperty> GEAS_LIMIT = REGISTRAR.register(
            "geas_limit",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, 6f)
                            .group(RFGearPropertyGroups.SPELL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );

    public static final Supplier<NumberProperty> HEALING_MULTIPLIER = REGISTRAR.register(
            "healing_received",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.PERCENTAGE,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, 500f)
                            .group(GearPropertyGroups.SPECIAL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );


}
