package com.drowningfish233.reveriegear.client;

import com.drowningfish233.reveriegear.registry.RFEntityRegistry;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.confluence.terraentity.client.entity.renderer.mob.SummonSwordRenderer;

final class TerraEntityRenderers {
    private TerraEntityRenderers() {
    }

    static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RFEntityRegistry.RF_SUMMON_SWORD.get(), SummonSwordRenderer::new);
    }
}
