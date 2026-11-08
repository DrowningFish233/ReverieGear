package com.drowningfish233.reveriegear.item.gear;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;

import java.util.function.Supplier;


public class PlaceholderGearItem extends Item implements GearItem {
    private final Supplier<GearType> gearType;

    public PlaceholderGearItem(Supplier<GearType> gearType) {
        super(new Item.Properties().stacksTo(1));
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public boolean isValidSlot(String slot) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}
