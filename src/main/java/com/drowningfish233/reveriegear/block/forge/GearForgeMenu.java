package com.drowningfish233.reveriegear.block.forge;

import com.drowningfish233.reveriegear.registry.RFMenus;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GearForgeMenu extends AbstractContainerMenu implements ContainerData {

    public static final int IMAGE_WIDTH = 320;
    public static final int IMAGE_HEIGHT = 240;

    public static final int LABEL_Y = 18;

    public static final int LIB_X = 8;
    public static final int LIB_Y = 30;
    public static final int LIB_W = GearForgeLogic.BLUEPRINT_COLS * 18;
    public static final int LIB_H = GearForgeLogic.BLUEPRINT_VISIBLE_ROWS * 18;
    public static final int SCROLLBAR_X = LIB_X + LIB_W + 2;
    public static final int SCROLLBAR_W = 4;

    public static final int CAPTION_Y = LIB_Y + LIB_H + 4;

    public static final int FRAME_X = 70;
    public static final int FRAME_Y = 30;
    public static final int FRAME_SIZE = 56;
    public static final int WORKPIECE_X = FRAME_X + (FRAME_SIZE - 16) / 2;
    public static final int WORKPIECE_Y = FRAME_Y + (FRAME_SIZE - 16) / 2;
    public static final int WORKPIECE_NAME_Y = FRAME_Y + FRAME_SIZE + 4;
    public static final int PART_LIST_X = FRAME_X;
    public static final int PART_LIST_Y = WORKPIECE_NAME_Y + 8;
    public static final int PART_ROW_H = 9;
    public static final int PART_ROWS = 4;

    public static final int PART_LIST_W = 54;

    public static final int MAT_X = 134;
    public static final int MAT_Y = 30;
    public static final int MAT_COLS = 3;
    public static final int OUTPUT_X = MAT_X;
    public static final int OUTPUT_Y = 96;
    public static final int FORGE_X = MAT_X;
    public static final int FORGE_Y = OUTPUT_Y + 22;
    public static final int FORGE_W = 58;
    public static final int FORGE_H = 16;

    public static final int CAND_X = 196;
    public static final int CAND_Y = 30;
    public static final int CAND_W = 116;
    public static final int CAND_ROW_H = 16;
    public static final int CAND_ROWS = 6;
    public static final int ACTION_SCROLLBAR_X = CAND_X + CAND_W + 2;
    public static final int ACTION_SCROLLBAR_W = 2;

    public static final int HINT_X = 8;
    public static final int HINT_Y = 136;
    public static final int HINT_LINE_H = 10;
    public static final int HINT_LINES = 1;
    public static final int HINT_WIDTH = IMAGE_WIDTH - 16;

    public static final int INV_LABEL_Y = 149;
    public static final int INV_X = 79;
    public static final int INV_Y = 159;
    public static final int HOTBAR_Y = 217;

    private static final int DATA_MODE = 0;
    private static final int DATA_SELECTED = 1;
    private static final int DATA_ACTION_COUNT = 2;
    private static final int DATA_REQ_COUNT = 3;
    private static final int DATA_SCROLL = 4;
    private static final int DATA_OUTPUT_VALID = 5;
    private static final int DATA_HINT = 6;
    private static final int DATA_ACTIVE_BLUEPRINT = 7;
    private static final int DATA_ACTION_SCROLL = 8;
    private static final int DATA_COUNT = 9;

    public static final int HINT_NONE = 0;
    public static final int HINT_NO_BLUEPRINT = 1;
    public static final int HINT_NO_ACTION = 2;
    public static final int HINT_MISSING_BLUEPRINT = 3;
    public static final int HINT_NEED_MATERIALS = 4;
    public static final int HINT_MIXED_MATERIAL = 5;
    public static final int HINT_UNUSABLE = 6;

    public static final int BUTTON_ACTION_BASE = 0;
    public static final int BUTTON_SCROLL_UP = 10;
    public static final int BUTTON_SCROLL_DOWN = 11;
    public static final int BUTTON_FORGE = 12;
    public static final int BUTTON_ACTION_SCROLL_UP = 13;
    public static final int BUTTON_ACTION_SCROLL_DOWN = 14;
    public static final int BUTTON_BLUEPRINT_BASE = 16;
    public static final int BUTTON_SCROLL_PAGE_UP = 32;
    public static final int BUTTON_SCROLL_PAGE_DOWN = 33;

    public static final int BUTTON_SCROLL_TO_BASE = 40;

    private final Container container;
    @Nullable
    private final GearForgeBlockEntity blockEntity;
    private final Player player;
    private final Level level;

    private int mode;
    private int selectedIndex;
    private int actionCount;
    private int requirementCount;
    private int scroll;

    private int actionScroll;
    private int outputValid;
    private int hint;

    private int activeBlueprint = -1;

    private List<ItemStack> cachedBlueprints = List.of();
    private ItemStack cachedWorkpiece = ItemStack.EMPTY;
    private int cachedActiveBlueprint = Integer.MIN_VALUE;
    private List<ForgeAction> cachedActions = List.of();
    private List<ItemStack> lastSnapshot = List.of();
    private boolean dirty = true;

    public GearForgeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(GearForgeLogic.CONTAINER_SIZE), null);
    }

    public GearForgeMenu(int containerId, Inventory playerInventory, GearForgeBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity);
    }

    private GearForgeMenu(int containerId, Inventory playerInventory, Container container,
                          @Nullable GearForgeBlockEntity blockEntity) {
        super(RFMenus.GEAR_FORGE.get(), containerId);
        this.container = container;
        this.blockEntity = blockEntity;
        this.player = playerInventory.player;
        this.level = this.player.level();

        for (int i = 0; i < GearForgeLogic.BLUEPRINT_VISIBLE; i++) {
            this.addSlot(new BlueprintSlot(container, i, -1,
                    LIB_X + (i % GearForgeLogic.BLUEPRINT_COLS) * 18,
                    LIB_Y + (i / GearForgeLogic.BLUEPRINT_COLS) * 18));
        }
        for (int i = 0; i < GearForgeLogic.BLUEPRINT_TAIL_COUNT; i++) {
            this.addSlot(new BlueprintSlot(container, -1, i, -1000, -1000));
        }
        this.addSlot(new WorkpieceSlot(container, GearForgeLogic.WORKPIECE_SLOT, WORKPIECE_X, WORKPIECE_Y));
        for (int i = 0; i < GearForgeLogic.REQUIREMENT_COUNT; i++) {
            this.addSlot(new RequirementSlot(container, GearForgeLogic.REQUIREMENT_START + i,
                    MAT_X + (i % MAT_COLS) * 18, MAT_Y + (i / MAT_COLS) * 18, i));
        }
        this.addSlot(new OutputSlot(container, GearForgeLogic.OUTPUT_SLOT, OUTPUT_X, OUTPUT_Y));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        INV_X + col * 18, INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, INV_X + col * 18, HOTBAR_Y));
        }

        this.addDataSlots(this);
    }

    public GearForgeLogic.Mode mode() {
        GearForgeLogic.Mode[] values = GearForgeLogic.Mode.values();
        return values[Math.max(0, Math.min(mode, values.length - 1))];
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public int actionCount() {
        return actionCount;
    }

    public int requirementCount() {
        return requirementCount;
    }

    public int scroll() {
        return scroll;
    }

    public boolean outputValid() {
        return outputValid != 0;
    }

    public int hint() {
        return hint;
    }

    public int activeBlueprint() {
        return activeBlueprint;
    }

    public int activeBlueprintWindowPos() {
        int pos = activeBlueprint - scroll * GearForgeLogic.BLUEPRINT_COLS;
        return pos >= 0 && pos < GearForgeLogic.BLUEPRINT_VISIBLE ? pos : -1;
    }

    public int actionScroll() {
        return actionScroll;
    }

    public int maxActionScroll() {
        return Math.max(0, actionCount - CAND_ROWS);
    }

    public int actionIndexAtRow(int row) {
        int index = actionScroll + row;
        return index >= 0 && index < actionCount ? index : -1;
    }

    public ItemStack workpiece() {
        return container.getItem(GearForgeLogic.WORKPIECE_SLOT);
    }

    private int blueprintContainerIndex(int index) {
        if (!level.isClientSide) {
            return GearForgeLogic.BLUEPRINT_START + index;
        }
        int base = scroll * GearForgeLogic.BLUEPRINT_COLS;
        if (index >= base && index < base + GearForgeLogic.BLUEPRINT_VISIBLE) {
            return GearForgeLogic.BLUEPRINT_WINDOW_MENU_START + (index - base);
        }
        int tailPos = index < base ? index : index - GearForgeLogic.BLUEPRINT_VISIBLE;
        return GearForgeLogic.BLUEPRINT_TAIL_MENU_START + tailPos;
    }

    public List<ItemStack> blueprints() {
        List<ItemStack> list = new ArrayList<>(GearForgeLogic.BLUEPRINT_COUNT);
        for (int i = 0; i < GearForgeLogic.BLUEPRINT_COUNT; i++) {
            list.add(container.getItem(blueprintContainerIndex(i)));
        }
        return list;
    }

    public int maxScroll() {

        return GearForgeLogic.BLUEPRINT_MAX_SCROLL;
    }

    public int firstVisibleRow() {
        return clamp(scroll, 0, maxScroll());
    }

    public int lastVisibleRow() {
        return firstVisibleRow() + GearForgeLogic.BLUEPRINT_VISIBLE_ROWS - 1;
    }

    public int storedBlueprintCount() {
        int count = 0;
        for (int i = 0; i < GearForgeLogic.BLUEPRINT_COUNT; i++) {
            if (!container.getItem(blueprintContainerIndex(i)).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public ItemStack activeBlueprintStack() {
        resolveActiveBlueprint();
        if (activeBlueprint < 0 || activeBlueprint >= GearForgeLogic.BLUEPRINT_COUNT) {
            return ItemStack.EMPTY;
        }
        return container.getItem(blueprintContainerIndex(activeBlueprint));
    }

    public int firstBlueprintIndex() {
        for (int i = 0; i < GearForgeLogic.BLUEPRINT_COUNT; i++) {
            ItemStack stack = container.getItem(blueprintContainerIndex(i));
            if (!stack.isEmpty() && stack.getItem() instanceof IBlueprint) {
                return i;
            }
        }
        return -1;
    }

    public List<ItemStack> snapshot() {
        List<ItemStack> list = new ArrayList<>(GearForgeLogic.CONTAINER_SIZE);
        for (int i = 0; i < GearForgeLogic.CONTAINER_SIZE; i++) {
            ItemStack stack = container.getItem(i);
            list.add(stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        return list;
    }

    public List<ForgeAction> actions() {
        resolveActiveBlueprint();
        List<ItemStack> library = blueprints();
        ItemStack workpiece = workpiece();
        if (activeBlueprint == cachedActiveBlueprint
                && ItemStack.matches(cachedWorkpiece, workpiece)
                && sameStacks(cachedBlueprints, library)) {
            return cachedActions;
        }
        cachedActiveBlueprint = activeBlueprint;
        cachedWorkpiece = workpiece.copy();
        cachedBlueprints = copy(library);
        cachedActions = GearForgeLogic.actions(level, library, workpiece, activeBlueprint);
        return cachedActions;
    }

    @Nullable
    public ForgeAction currentAction() {
        List<ForgeAction> list = actions();
        if (list.isEmpty() || selectedIndex < 0 || selectedIndex >= list.size()) {
            return null;
        }
        return list.get(selectedIndex);
    }

    public List<ForgeRequirement> requirements() {
        ForgeAction action = currentAction();
        return action == null ? List.of() : action.requirements;
    }

    public List<ItemStack> requirementStacks() {
        List<ItemStack> list = new ArrayList<>(GearForgeLogic.REQUIREMENT_COUNT);
        for (int i = 0; i < GearForgeLogic.REQUIREMENT_COUNT; i++) {
            list.add(container.getItem(GearForgeLogic.REQUIREMENT_START + i));
        }
        return list;
    }

    @Override
    public void broadcastChanges() {
        if (!level.isClientSide) {
            serverTick();
        }
        super.broadcastChanges();
    }

    private void serverTick() {

        scroll = clamp(scroll, 0, maxScroll());

        List<ItemStack> snapshot = snapshot();
        if (!dirty && sameStacks(snapshot, lastSnapshot)) {
            return;
        }
        dirty = false;

        refreshActions();
        ejectRejected();
        refreshOutput();
        refreshHint();

        lastSnapshot = snapshot();
    }

    private void resolveActiveBlueprint() {
        ItemStack selected = activeBlueprint >= 0 && activeBlueprint < GearForgeLogic.BLUEPRINT_COUNT
                ? container.getItem(blueprintContainerIndex(activeBlueprint))
                : ItemStack.EMPTY;
        if (selected.isEmpty() || !(selected.getItem() instanceof IBlueprint)) {
            activeBlueprint = firstBlueprintIndex();
        }
    }

    private void refreshActions() {
        List<ForgeAction> list = actions();
        actionCount = list.size();
        mode = GearForgeLogic.modeOf(workpiece()).ordinal();
        selectedIndex = clamp(selectedIndex, 0, Math.max(0, actionCount - 1));
        actionScroll = clamp(actionScroll, 0, maxActionScroll());
        requirementCount = Math.min(requirements().size(), GearForgeLogic.REQUIREMENT_COUNT);
        outputValid = 0;
    }

    private void scrollSelectionIntoView() {
        if (selectedIndex < actionScroll) {
            actionScroll = selectedIndex;
        } else if (selectedIndex >= actionScroll + CAND_ROWS) {
            actionScroll = selectedIndex - CAND_ROWS + 1;
        }
        actionScroll = clamp(actionScroll, 0, maxActionScroll());
    }

    private boolean scrollTo(int row) {
        int next = clamp(row, 0, maxScroll());
        if (next == scroll) {
            return false;
        }
        scroll = next;
        return true;
    }


    private void revealBlueprintRow(int index) {
        int row = index / GearForgeLogic.BLUEPRINT_COLS;
        if (row < scroll) {
            scroll = row;
        } else if (row >= scroll + GearForgeLogic.BLUEPRINT_VISIBLE_ROWS) {
            scroll = row - GearForgeLogic.BLUEPRINT_VISIBLE_ROWS + 1;
        }
        scroll = clamp(scroll, 0, maxScroll());
    }


    private boolean storeBlueprintInLibrary(ItemStack stack) {
        if (stack.isEmpty() || level.isClientSide) {
            return false;
        }
        int base = firstVisibleRow() * GearForgeLogic.BLUEPRINT_COLS;
        for (int step = 0; step < GearForgeLogic.BLUEPRINT_COUNT; step++) {
            int index = (base + step) % GearForgeLogic.BLUEPRINT_COUNT;
            if (!container.getItem(index).isEmpty()) {
                continue;
            }
            container.setItem(index, stack.copy());
            stack.setCount(0);
            revealBlueprintRow(index);
            dirty = true;
            return true;
        }
        return false;
    }

    private void ejectRejected() {
        List<ForgeRequirement> requirements = requirements();
        for (int i = 0; i < GearForgeLogic.REQUIREMENT_COUNT; i++) {
            Slot slot = this.getSlot(GearForgeLogic.REQUIREMENT_MENU_START + i);
            ItemStack stack = slot.getItem();
            if (stack.isEmpty()) {
                continue;
            }
            boolean accepted = i < requirementCount && i < requirements.size() && requirements.get(i).test(stack);
            if (accepted) {
                continue;
            }
            giveBack(slot.remove(stack.getCount()));
        }
    }

    private void giveBack(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (player.getInventory().add(stack)) {
            return;
        }
        if (blockEntity != null && blockEntity.getLevel() != null) {
            Containers.dropItemStack(blockEntity.getLevel(),
                    blockEntity.getBlockPos().getX() + 0.5,
                    blockEntity.getBlockPos().getY() + 0.5,
                    blockEntity.getBlockPos().getZ() + 0.5,
                    stack);
        } else {
            player.drop(stack, false);
        }
    }

    private void refreshOutput() {
        ItemStack output = ItemStack.EMPTY;
        ForgeAction action = currentAction();
        if (action != null) {
            GearForgeLogic.ForgeOutput result = GearForgeLogic.produce(
                    level, action, workpiece(), blueprints(), requirementStacks(), player);
            if (result != null && !result.isEmpty()) {
                output = result.result();
            }
        }
        Slot slot = this.getSlot(GearForgeLogic.OUTPUT_MENU_SLOT);
        if (!ItemStack.matches(slot.getItem(), output)) {
            slot.set(output);
        }
        outputValid = output.isEmpty() ? 0 : 1;
    }

    private void refreshHint() {
        if (actionCount == 0) {
            if (mode() == GearForgeLogic.Mode.CONVERT) {
                hint = HINT_UNUSABLE;
            } else if (mode() == GearForgeLogic.Mode.MODIFY) {
                hint = HINT_NO_ACTION;
            } else if (GearForgeLogic.collectBlueprints(blueprints()).isEmpty()) {
                hint = HINT_NO_BLUEPRINT;
            } else {

                hint = HINT_NO_ACTION;
            }
            return;
        }
        ForgeAction action = currentAction();
        if (action == null) {
            hint = HINT_NO_ACTION;
            return;
        }
        if (action.blueprintMissing) {
            hint = HINT_MISSING_BLUEPRINT;
            return;
        }
        List<ForgeRequirement> requirements = action.requirements;
        List<ItemStack> stacks = requirementStacks();
        for (int i = 0; i < requirements.size() && i < GearForgeLogic.REQUIREMENT_COUNT; i++) {
            if (!requirements.get(i).test(stacks.get(i))) {
                hint = HINT_NEED_MATERIALS;
                return;
            }
        }
        hint = outputValid != 0 ? HINT_NONE : HINT_MIXED_MATERIAL;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (level.isClientSide) {
            return false;
        }
        if (id >= BUTTON_ACTION_BASE && id < BUTTON_ACTION_BASE + CAND_ROWS) {
            int index = actionIndexAtRow(id - BUTTON_ACTION_BASE);
            if (index < 0 || index >= actionCount) {
                return false;
            }
            selectedIndex = index;
            scrollSelectionIntoView();
        } else if (id == BUTTON_ACTION_SCROLL_UP || id == BUTTON_ACTION_SCROLL_DOWN) {
            int delta = id == BUTTON_ACTION_SCROLL_DOWN ? 1 : -1;
            int next = clamp(actionScroll + delta, 0, maxActionScroll());
            if (next == actionScroll) {
                return false;
            }
            actionScroll = next;
        } else if (id == BUTTON_SCROLL_UP || id == BUTTON_SCROLL_DOWN
                || id == BUTTON_SCROLL_PAGE_UP || id == BUTTON_SCROLL_PAGE_DOWN) {
            int delta = switch (id) {
                case BUTTON_SCROLL_UP -> -1;
                case BUTTON_SCROLL_DOWN -> 1;
                case BUTTON_SCROLL_PAGE_UP -> -GearForgeLogic.BLUEPRINT_PAGE_ROWS;
                default -> GearForgeLogic.BLUEPRINT_PAGE_ROWS;
            };
            if (!scrollTo(scroll + delta)) {
                return false;
            }
        } else if (id >= BUTTON_SCROLL_TO_BASE
                && id <= BUTTON_SCROLL_TO_BASE + GearForgeLogic.BLUEPRINT_MAX_SCROLL) {
            if (!scrollTo(id - BUTTON_SCROLL_TO_BASE)) {
                return false;
            }
        } else if (id == BUTTON_FORGE) {
            if (!forge()) {
                return false;
            }
        } else if (id >= BUTTON_BLUEPRINT_BASE && id < BUTTON_BLUEPRINT_BASE + GearForgeLogic.BLUEPRINT_VISIBLE) {
            int storage = scroll * GearForgeLogic.BLUEPRINT_COLS + (id - BUTTON_BLUEPRINT_BASE);
            if (storage < 0 || storage >= GearForgeLogic.BLUEPRINT_COUNT) {
                return false;
            }
            activeBlueprint = storage;
            selectedIndex = 0;
            scrollSelectionIntoView();
        } else {
            return false;
        }
        dirty = true;
        serverTick();
        broadcastChanges();
        return true;
    }

    private boolean forge() {
        ForgeAction action = currentAction();
        if (action == null || outputValid == 0) {
            return false;
        }
        GearForgeLogic.ForgeOutput produced = GearForgeLogic.produce(
                level, action, workpiece(), blueprints(), requirementStacks(), player);
        if (produced == null || produced.isEmpty()) {
            return false;
        }
        GearForgeLogic.consume(action, container);
        ItemStack result = produced.result();
        if (result.getItem() instanceof GearItem && container.getItem(GearForgeLogic.WORKPIECE_SLOT).isEmpty()) {
            container.setItem(GearForgeLogic.WORKPIECE_SLOT, result);
        } else if (!player.getInventory().add(result)) {
            player.drop(result, false);
        }
        for (ItemStack returned : produced.returned()) {
            giveBack(returned);
        }
        dirty = true;
        serverTick();
        return true;
    }

    @Override
    public int getCount() {
        return DATA_COUNT;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case DATA_MODE -> mode;
            case DATA_SELECTED -> selectedIndex;
            case DATA_ACTION_COUNT -> actionCount;
            case DATA_REQ_COUNT -> requirementCount;
            case DATA_SCROLL -> scroll;
            case DATA_OUTPUT_VALID -> outputValid;
            case DATA_HINT -> hint;
            case DATA_ACTIVE_BLUEPRINT -> activeBlueprint;
            case DATA_ACTION_SCROLL -> actionScroll;
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {

        if (!level.isClientSide) {
            return;
        }
        switch (index) {
            case DATA_MODE -> mode = value;
            case DATA_SELECTED -> selectedIndex = value;
            case DATA_ACTION_COUNT -> actionCount = value;
            case DATA_REQ_COUNT -> requirementCount = value;
            case DATA_SCROLL -> scroll = value;
            case DATA_OUTPUT_VALID -> outputValid = value;
            case DATA_HINT -> hint = value;
            case DATA_ACTIVE_BLUEPRINT -> activeBlueprint = value;
            case DATA_ACTION_SCROLL -> actionScroll = value;
            default -> {
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < GearForgeLogic.FORGE_MENU_SLOTS) {
            if (!this.moveItemStackTo(stack, GearForgeLogic.FORGE_MENU_SLOTS, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, copy);
        } else {
            if (stack.getItem() instanceof IBlueprint) {
                boolean moved = this.moveItemStackTo(stack, GearForgeLogic.BLUEPRINT_WINDOW_MENU_START,
                        GearForgeLogic.BLUEPRINT_WINDOW_MENU_START + GearForgeLogic.BLUEPRINT_VISIBLE, false);
                if (!stack.isEmpty()) {
                    moved |= storeBlueprintInLibrary(stack);
                }
                if (!moved) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.getItem() instanceof GearItem) {
                if (!this.moveItemStackTo(stack, GearForgeLogic.WORKPIECE_MENU_SLOT,
                        GearForgeLogic.WORKPIECE_MENU_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, GearForgeLogic.REQUIREMENT_MENU_START,
                    GearForgeLogic.REQUIREMENT_MENU_START + GearForgeLogic.REQUIREMENT_COUNT, false)) {
                if (!this.moveItemStackTo(stack, GearForgeLogic.WORKPIECE_MENU_SLOT,
                        GearForgeLogic.WORKPIECE_MENU_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return copy;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static List<ItemStack> copy(List<ItemStack> list) {
        List<ItemStack> out = new ArrayList<>(list.size());
        for (ItemStack stack : list) {
            out.add(stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        return out;
    }

    private static boolean sameStacks(List<ItemStack> a, List<ItemStack> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!ItemStack.matches(a.get(i), b.get(i))) {
                return false;
            }
        }
        return true;
    }

    private class BlueprintSlot extends Slot {

        private final int windowPos;

        private final int tailPos;

        BlueprintSlot(Container container, int windowPos, int tailPos, int x, int y) {
            super(container, GearForgeLogic.BLUEPRINT_WINDOW_MENU_START
                    + (windowPos >= 0 ? windowPos : GearForgeLogic.BLUEPRINT_VISIBLE + tailPos), x, y);
            this.windowPos = windowPos;
            this.tailPos = tailPos;
        }

        private boolean isWindow() {
            return windowPos >= 0;
        }

        private int backingIndex() {
            if (level.isClientSide) {
                return windowPos >= 0
                        ? GearForgeLogic.BLUEPRINT_WINDOW_MENU_START + windowPos
                        : GearForgeLogic.BLUEPRINT_TAIL_MENU_START + tailPos;
            }
            if (windowPos >= 0) {
                return GearForgeLogic.BLUEPRINT_START + scroll * GearForgeLogic.BLUEPRINT_COLS + windowPos;
            }
            int base = scroll * GearForgeLogic.BLUEPRINT_COLS;
            if (tailPos < base) {
                return GearForgeLogic.BLUEPRINT_START + tailPos;
            }
            return GearForgeLogic.BLUEPRINT_START + base + GearForgeLogic.BLUEPRINT_VISIBLE + (tailPos - base);
        }

        @Override
        public ItemStack getItem() {
            return container.getItem(backingIndex());
        }

        @Override
        public void set(ItemStack stack) {
            container.setItem(backingIndex(), stack);
        }

        @Override
        public void setChanged() {
            container.setChanged();
        }

        @Override
        public ItemStack remove(int amount) {
            return container.removeItem(backingIndex(), amount);
        }

        @Override
        public boolean isActive() {
            return isWindow();
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isWindow() && stack.getItem() instanceof IBlueprint;
        }

        @Override
        public boolean mayPickup(Player player) {
            return isWindow();
        }

        @Override
        public boolean isSameInventory(Slot other) {
            return other instanceof BlueprintSlot;
        }
    }

    private class WorkpieceSlot extends Slot {
        WorkpieceSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return GearForgeLogic.SINGLE;
        }
    }

    private class RequirementSlot extends Slot {
        private final int index;

        RequirementSlot(Container container, int slot, int x, int y, int index) {
            super(container, slot, x, y);
            this.index = index;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            List<ForgeRequirement> requirements = requirements();
            return index < requirementCount && index < requirements.size() && requirements.get(index).test(stack);
        }

        @Override
        public boolean isActive() {
            return index < requirementCount;
        }
    }

    private class OutputSlot extends Slot {
        OutputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            if (!level.isClientSide) {
                ForgeAction action = currentAction();
                if (action != null) {
                    GearForgeLogic.ForgeOutput result = GearForgeLogic.produce(
                            level, action, workpiece(), blueprints(), requirementStacks(), player);
                    GearForgeLogic.consume(action, container);
                    if (result != null) {
                        for (ItemStack returned : result.returned()) {
                            giveBack(returned);
                        }
                    }
                }
                dirty = true;
                serverTick();
            }
            super.onTake(player, stack);
        }
    }
}
