package com.drowningfish233.reveriegear.registry;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.attribute.RFAttributes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class RFAttributesRegistry {

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, ReverieGear.MODID);

    public static final Supplier<Attribute> ARCANE_SCYTHE_CONVERSION =
            ATTRIBUTES.register("arcane_scythe_conversion", () -> RFAttributes.ARCANE_SCYTHE_CONVERSION);

    private RFAttributesRegistry() {
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
