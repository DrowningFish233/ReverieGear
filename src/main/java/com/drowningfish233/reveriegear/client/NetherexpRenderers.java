package com.drowningfish233.reveriegear.client;

import com.drowningfish233.reveriegear.client.renderer.ShotgunPellet.GearPhasmoPelletRenderer;
import com.drowningfish233.reveriegear.client.renderer.ShotgunPellet.GearShotgunPelletRenderer;
import com.drowningfish233.reveriegear.client.renderer.ShotgunPellet.GearSlugPelletRenderer;
import com.drowningfish233.reveriegear.registry.RFEntityRegistry;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

final class NetherexpRenderers {
    private NetherexpRenderers() {
    }

    static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RFEntityRegistry.GEAR_SHOTGUN_PELLET.get(), GearShotgunPelletRenderer::new);
        event.registerEntityRenderer(RFEntityRegistry.GEAR_SLUG_PELLET.get(), GearSlugPelletRenderer::new);
        event.registerEntityRenderer(RFEntityRegistry.GEAR_PHASMO_PELLET.get(), GearPhasmoPelletRenderer::new);
    }
}
