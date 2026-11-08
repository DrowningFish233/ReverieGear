package com.drowningfish233.reveriegear.api;

import com.drowningfish233.reveriegear.ReverieGear;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public enum RFTextureType {
    DEFAULT("default"),
    SPELL("spell"),
    MALIGNANT("malignant");

    private final String name;
    private final ResourceLocation armorTexture1;
    private final ResourceLocation armorTexture2;

    RFTextureType(String name) {
        this.name = name;
        this.armorTexture1 = ReverieGear.getId("textures/models/armor/main_layer_" + name + "_1.png");
        this.armorTexture2 = ReverieGear.getId("textures/models/armor/main_layer_" + name + "_2.png");
    }

    public ResourceLocation getArmorTexture(boolean innerModel) {
        return innerModel ? this.armorTexture2 : this.armorTexture1;
    }

    @Nullable
    public static RFTextureType fromString(String str) {
        for (RFTextureType type : values()) {
            if (str.equalsIgnoreCase(type.name) || str.equalsIgnoreCase(type.name())) {
                return type;
            }
        }
        return null;
    }

    public static final Codec<RFTextureType> CODEC = Codec.STRING
            .comapFlatMap(
                    str -> {
                        var type = fromString(str);
                        return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown texture type: " + str);
                    },
                    RFTextureType::name
            );

    public static final StreamCodec<FriendlyByteBuf, RFTextureType> STREAM_CODEC = StreamCodec.of(
            (buf, val) -> buf.writeEnum(val),
            buf -> buf.readEnum(RFTextureType.class)
    );
}
