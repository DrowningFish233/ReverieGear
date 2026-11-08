package com.drowningfish233.reveriegear.client;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.registry.RFItems;
import io.redspace.ironsspellbooks.item.weapons.AutoloaderCrossbow;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;

final class IronsItemProperties {
    private IronsItemProperties() {
    }

    static void register() {
        Item crossbow = RFItems.AUTOLOADER_CROSSBOW.gearItem();

        ItemProperties.register(crossbow, ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) ->
                        AutoloaderCrossbow.isLoading(stack) && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);

        ItemProperties.register(crossbow, ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (CrossbowItem.isCharged(stack)) {
                        return 0.0F;
                    }
                    int chargeDuration = AutoloaderCrossbow.getChargeDuration(stack, entity);
                    if (chargeDuration <= 0) {
                        return 0.0F;
                    }
                    return Mth.clamp((float) AutoloaderCrossbow.getLoadingTicks(stack) / (float) chargeDuration, 0.0F, 1.0F);
                });

        ItemProperties.register(crossbow, ResourceLocation.withDefaultNamespace("charged"),
                (stack, level, entity, seed) -> CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);

        ItemProperties.register(crossbow, ResourceLocation.withDefaultNamespace("firework"),
                (stack, level, entity, seed) -> {
                    ChargedProjectiles projectiles = stack.get(DataComponents.CHARGED_PROJECTILES);
                    return projectiles != null && projectiles.contains(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
                });
    }
}
