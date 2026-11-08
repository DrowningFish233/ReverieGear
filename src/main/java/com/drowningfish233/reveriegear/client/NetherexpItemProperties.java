package com.drowningfish233.reveriegear.client;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.registry.RFItems;
import net.jadenxgamer.netherexp.core.item.PumpChargeShotgunItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

final class NetherexpItemProperties {
    private NetherexpItemProperties() {
    }

    static void register() {
        ItemProperties.register(
                RFItems.PUMP_CHARGE_SHOTGUN.gearItem(),
                ResourceLocation.fromNamespaceAndPath(ReverieGear.MODID, "pumps"),
                (stack, level, entity, seed) -> {
                    int pumps = PumpChargeShotgunItem.getPumps(stack);
                    return (float) Math.max(0, Math.min(3, pumps - 1));
                }
        );
    }
}
