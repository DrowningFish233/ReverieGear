package com.drowningfish233.reveriegear.client.renderer.ShotgunPellet;

import com.drowningfish233.reveriegear.entity.shotgunpellet.GearSlugPellet;
import net.jadenxgamer.netherexp.client.rendering.entity.PelletRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GearSlugPelletRenderer extends PelletRenderer<GearSlugPellet> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "netherexp",
            "textures/entity/projectiles/slug_pellet.png"
    );

    public GearSlugPelletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(GearSlugPellet entity) {
        return TEXTURE;
    }
}
