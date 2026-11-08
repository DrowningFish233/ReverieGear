package com.drowningfish233.reveriegear.item.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import team.lodestar.lodestone.systems.item.LodestoneItemProperties;

public class CaneItem extends Item {

    public static final ResourceLocation MOVEMENT_SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("reveriegear", "cane_speed_boost");

    public CaneItem(LodestoneItemProperties properties) {
        super(properties);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        return builder.build();
    }
}
