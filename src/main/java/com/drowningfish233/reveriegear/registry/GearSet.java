package com.drowningfish233.reveriegear.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.item.MainPartItem;
import net.silentchaos512.gear.item.blueprint.BlueprintType;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;

import java.util.function.Supplier;


public final class GearSet {
    private final DeferredHolder<GearType, GearType> type;
    private final String name;
    private final String partName;
    private final Supplier<? extends Item> gearSupplier;

    private DeferredItem<Item> gearItem;
    private DeferredItem<MainPartItem> mainPart;
    private DeferredItem<GearBlueprintItem> blueprint;
    private DeferredItem<GearBlueprintItem> template;

    public GearSet(DeferredHolder<GearType, GearType> type, String partName, Supplier<? extends Item> gearSupplier) {
        this.type = type;
        this.name = type.getId().getPath();
        this.partName = partName;
        this.gearSupplier = gearSupplier;
    }

    public String name() {
        return this.name;
    }

    public DeferredHolder<GearType, GearType> type() {
        return this.type;
    }

    public void register(DeferredRegister.Items items) {
        this.gearItem = items.register(this.name, () -> this.gearSupplier.get());
        this.mainPart = items.register(this.partName,
                () -> new MainPartItem(this.type, new Item.Properties().stacksTo(1).setNoRepair()));
        this.blueprint = items.register(this.name + "_blueprint",
                () -> new GearBlueprintItem(this.type, BlueprintType.BLUEPRINT, new Item.Properties()));
        this.template = items.register(this.name + "_template",
                () -> new GearBlueprintItem(this.type, BlueprintType.TEMPLATE, new Item.Properties()));
    }

    public Item gearItem() {
        return this.gearItem.get();
    }

    public Item mainPart() {
        return this.mainPart.get();
    }

    public Item blueprint() {
        return this.blueprint.get();
    }

    public Item template() {
        return this.template.get();
    }

    public DeferredItem<Item> gearItemHolder() {
        return this.gearItem;
    }
}
