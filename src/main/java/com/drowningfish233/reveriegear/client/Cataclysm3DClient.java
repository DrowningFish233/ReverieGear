package com.drowningfish233.reveriegear.client;

import com.drowningfish233.reveriegear.client.renderer.CataclysmGearRenderer;
import com.drowningfish233.reveriegear.registry.RFItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

final class Cataclysm3DClient {
    private Cataclysm3DClient() {
    }

    static void register(RegisterClientExtensionsEvent event) {
        IClientItemExtensions extensions = new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new CataclysmGearRenderer();
            }
        };

        event.registerItem(extensions,
                RFItems.INFERNAL_FORGE.gearItem(),
                RFItems.MEAT_SHREDDER.gearItem(),
                RFItems.CURSED_BOW.gearItem(),
                RFItems.SEEKING_BOW.gearItem(),
                RFItems.VOID_ASSAULT_SHOULDER_WEAPON.gearItem(),
                RFItems.ANCIENT_SPEAR.gearItem(),
                RFItems.INCINERATOR.gearItem(),
                RFItems.FLAME_BULWARK.gearItem(),
                RFItems.ANNIHILATOR.gearItem(),
                RFItems.ASTRAPE.gearItem(),
                RFItems.CERAUNUS.gearItem(),
                RFItems.TIDAL_CLAWS.gearItem(),
                RFItems.BRONTES.gearItem()
        );
    }
}
