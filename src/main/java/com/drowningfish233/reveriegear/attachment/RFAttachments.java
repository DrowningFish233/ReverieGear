package com.drowningfish233.reveriegear.attachment;

import com.drowningfish233.reveriegear.ReverieGear;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class RFAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ReverieGear.MODID);

    public static final Supplier<AttachmentType<Float>> HYPERIUS_MAX_HEALTH_REDUCTION =
            ATTACHMENT_TYPES.register(
                    "hyperius_max_health_reduction",
                    () -> AttachmentType.builder(() -> 0.0F)
                            .serialize(Codec.FLOAT)
                            .build()
            );

    public static final Supplier<AttachmentType<Long>> HYPERIUS_LAST_DAMAGE_TIME =
            ATTACHMENT_TYPES.register(
                    "hyperius_last_damage_time",
                    () -> AttachmentType.builder(() -> 0L)
                            .serialize(Codec.LONG)
                            .build()
            );

    public static final Supplier<AttachmentType<Boolean>> SPINNING =
            ATTACHMENT_TYPES.register(
                    "spinning",
                    () -> AttachmentType.builder(() -> false)
                            .serialize(Codec.BOOL)
                            .sync(ByteBufCodecs.BOOL)
                            .build()
            );

    private RFAttachments() {
    }

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }
}
