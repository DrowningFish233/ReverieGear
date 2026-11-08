package com.drowningfish233.reveriegear.core.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MiningModeData(String mode) {
    public static final MiningModeData DEFAULT = new MiningModeData("small_tunnel");

    public static final Codec<MiningModeData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("mode").forGetter(MiningModeData::mode)
            ).apply(instance, MiningModeData::new)
    );

    public static final StreamCodec<ByteBuf, MiningModeData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MiningModeData::mode,
            MiningModeData::new
    );
}
