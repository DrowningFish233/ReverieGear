package com.drowningfish233.reveriegear;

import com.drowningfish233.reveriegear.attachment.RFAttachments;
import com.drowningfish233.reveriegear.registry.RFAttributesRegistry;
import com.drowningfish233.reveriegear.registry.RFBlocks;
import com.drowningfish233.reveriegear.registry.RFDataComponents;
import com.drowningfish233.reveriegear.registry.RFEntityRegistry;
import com.drowningfish233.reveriegear.registry.RFItems;
import com.drowningfish233.reveriegear.registry.RFMenus;
import com.drowningfish233.reveriegear.registry.gear.RFGearProperties;
import com.drowningfish233.reveriegear.registry.gear.RFGearTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(ReverieGear.MODID)
public class ReverieGear {
    public static final String MODID = "reveriegear";

    public static ResourceLocation getId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public ReverieGear(IEventBus modEventBus, ModContainer modContainer) {
        RFGearTypes.REGISTRAR.register(modEventBus);
        RFGearProperties.REGISTRAR.register(modEventBus);
        RFAttachments.register(modEventBus);

        RFBlocks.BLOCKS.register(modEventBus);
        RFBlocks.BLOCK_ENTITIES.register(modEventBus);
        RFMenus.MENUS.register(modEventBus);

        RFEntityRegistry.register(modEventBus);
        RFDataComponents.register(modEventBus);
        RFAttributesRegistry.register(modEventBus);

        modEventBus.addListener(RFEntityRegistry::registerAttributes);

        RFItems.ITEMS.register(modEventBus);
        RFItems.CREATIVE_MODE_TABS.register(modEventBus);
        RFItems.register();
    }
}
