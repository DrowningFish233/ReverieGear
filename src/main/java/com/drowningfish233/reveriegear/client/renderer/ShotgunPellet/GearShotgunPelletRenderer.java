package com.drowningfish233.reveriegear.client.renderer.ShotgunPellet;

import net.jadenxgamer.netherexp.client.rendering.entity.ShotgunPelletRenderer;
import net.jadenxgamer.netherexp.core.entity.ShotgunPellet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GearShotgunPelletRenderer extends ShotgunPelletRenderer {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "netherexp",
            "textures/entity/projectiles/shotgun_pellet.png"
    );

    public GearShotgunPelletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ShotgunPellet entity) {
        return TEXTURE;
    }
}
