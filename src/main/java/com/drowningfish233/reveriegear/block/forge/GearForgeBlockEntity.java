package com.drowningfish233.reveriegear.block.forge;

import com.drowningfish233.reveriegear.registry.RFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GearForgeBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {


    private static final String LAYOUT_TAG = "RFForgeBlueprintLayout";
    private static final int LAYOUT_VERSION = 2;

    private NonNullList<ItemStack> items = NonNullList.withSize(GearForgeLogic.CONTAINER_SIZE, ItemStack.EMPTY);

    public GearForgeBlockEntity(BlockPos pos, BlockState state) {
        super(RFBlocks.GEAR_FORGE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) {
            setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{GearForgeLogic.OUTPUT_SLOT};
        }
        int[] slots = new int[GearForgeLogic.REQUIREMENT_COUNT];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = GearForgeLogic.REQUIREMENT_START + i;
        }
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return slot >= GearForgeLogic.REQUIREMENT_START
                && slot < GearForgeLogic.REQUIREMENT_START + GearForgeLogic.REQUIREMENT_COUNT;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == GearForgeLogic.OUTPUT_SLOT;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(LAYOUT_TAG, LAYOUT_VERSION);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (!tag.contains(LAYOUT_TAG)) {
            migrateLegacyLayout(tag);
        }
        items = NonNullList.withSize(GearForgeLogic.CONTAINER_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    private static void migrateLegacyLayout(CompoundTag tag) {
        ListTag entries = tag.getList("Items", Tag.TAG_COMPOUND);
        int shift = GearForgeLogic.WORKPIECE_SLOT - GearForgeLogic.LEGACY_WORKPIECE_SLOT;
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            int slot = entry.getByte("Slot") & 0xFF;
            if (slot >= GearForgeLogic.LEGACY_WORKPIECE_SLOT && slot < GearForgeLogic.LEGACY_CONTAINER_SIZE) {
                entry.putByte("Slot", (byte) (slot + shift));
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.reveriegear.gear_forge");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new GearForgeMenu(containerId, inventory, this);
    }

    public List<ItemStack> snapshot() {
        List<ItemStack> list = new ArrayList<>(items.size());
        for (ItemStack stack : items) {
            list.add(stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        return list;
    }

    public List<ItemStack> blueprintSnapshot() {
        return List.copyOf(items.subList(
                GearForgeLogic.BLUEPRINT_START,
                GearForgeLogic.BLUEPRINT_START + GearForgeLogic.BLUEPRINT_COUNT));
    }
}
