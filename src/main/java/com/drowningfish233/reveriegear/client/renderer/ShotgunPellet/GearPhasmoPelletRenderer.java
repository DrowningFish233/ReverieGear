package com.drowningfish233.reveriegear.client.renderer.ShotgunPellet;

import com.drowningfish233.reveriegear.entity.shotgunpellet.GearPhasmoPellet;
import net.jadenxgamer.netherexp.client.rendering.entity.PelletRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GearPhasmoPelletRenderer extends PelletRenderer<GearPhasmoPellet> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "netherexp",
            "textures/entity/projectiles/phasmo_pellet.png"
    );

    public GearPhasmoPelletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(GearPhasmoPellet entity) {
        return TEXTURE;
    }
}
