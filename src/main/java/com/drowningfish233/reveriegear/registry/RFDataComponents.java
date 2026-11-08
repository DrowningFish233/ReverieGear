package com.drowningfish233.reveriegear.registry;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.core.data.MiningModeData;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class RFDataComponents {
    public static final DeferredRegister.DataComponents REGISTRAR =
            DeferredRegister.DataComponents.createDataComponents(Registries.DATA_COMPONENT_TYPE, ReverieGear.MODID);

    public static final Supplier<DataComponentType<MiningModeData>> MINING_MODE =
            REGISTRAR.registerComponentType(
                    "mining_mode",
                    builder -> builder
                            .persistent(MiningModeData.CODEC)
                            .networkSynchronized(MiningModeData.STREAM_CODEC)
            );

    private RFDataComponents() {
    }

    public static void register(IEventBus modEventBus) {
        REGISTRAR.register(modEventBus);
    }
}
