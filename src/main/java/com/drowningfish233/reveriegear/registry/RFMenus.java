package com.drowningfish233.reveriegear.registry;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.block.forge.GearForgeMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RFMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, ReverieGear.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<GearForgeMenu>> GEAR_FORGE =
            MENUS.register("gear_forge", () -> IMenuTypeExtension.create(
                    (windowId, inventory, data) -> new GearForgeMenu(windowId, inventory)));

    private RFMenus() {
    }
}
