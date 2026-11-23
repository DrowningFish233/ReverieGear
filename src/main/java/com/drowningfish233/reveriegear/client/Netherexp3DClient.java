package com.drowningfish233.reveriegear.client;

import com.drowningfish233.reveriegear.client.renderer.NetherexpGearRenderer;
import com.drowningfish233.reveriegear.registry.RFItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

final class Netherexp3DClient {
    private Netherexp3DClient() {
    }

    static void register(RegisterClientExtensionsEvent event) {
        IClientItemExtensions extensions = new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new NetherexpGearRenderer();
            }
        };

        event.registerItem(extensions, RFItems.PUMP_CHARGE_SHOTGUN.gearItem());
    }
}
