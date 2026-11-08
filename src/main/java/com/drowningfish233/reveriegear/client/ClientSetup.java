package com.drowningfish233.reveriegear.client;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.block.forge.client.GearForgeScreen;
import com.drowningfish233.reveriegear.compat.ModCompat;
import com.drowningfish233.reveriegear.registry.RFEntityRegistry;
import com.drowningfish233.reveriegear.registry.RFItems;
import com.drowningfish233.reveriegear.registry.RFMenus;
import com.drowningfish233.reveriegear.client.renderer.MalignantStrongholdExtensions;
import com.drowningfish233.reveriegear.client.renderer.SoulHunterExtensions;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ReverieGear.MODID, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(RFMenus.GEAR_FORGE.get(), GearForgeScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (ModCompat.TERRA_ENTITY && RFEntityRegistry.RF_SUMMON_SWORD != null) {
            TerraEntityRenderers.register(event);
        }
        if (ModCompat.NETHEREXP) {
            NetherexpRenderers.register(event);
        }
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        if (ModCompat.MALUM) {
            MalignantStrongholdExtensions.register(event);
            SoulHunterExtensions.register(event);
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerBuiltinModelProperties();

            if (ModCompat.IRONS_SPELLBOOKS) {
                IronsItemProperties.register();
            }
            if (ModCompat.NETHEREXP) {
                NetherexpItemProperties.register();
            }
        });
    }

    private static void registerBuiltinModelProperties() {
        Item bulwark = RFItems.FLAME_BULWARK.gearItem();
        ItemProperties.register(bulwark, ResourceLocation.parse("blocking"),
                (stack, level, livingEntity, seed) ->
                        livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == stack ? 1.0F : 0.0F);

        Item shredder = RFItems.MEAT_SHREDDER.gearItem();
        ItemProperties.register(shredder, ResourceLocation.parse("using"),
                (stack, level, livingEntity, seed) ->
                        livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == stack ? 1.0F : 0.0F);
    }
}
