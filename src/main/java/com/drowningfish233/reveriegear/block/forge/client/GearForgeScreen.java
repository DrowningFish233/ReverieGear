package com.drowningfish233.reveriegear.block.forge.client;

import com.drowningfish233.reveriegear.block.forge.ForgeAction;
import com.drowningfish233.reveriegear.block.forge.ForgeRequirement;
import com.drowningfish233.reveriegear.block.forge.GearForgeLogic;
import com.drowningfish233.reveriegear.block.forge.GearForgeMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GearForgeScreen extends AbstractContainerScreen<GearForgeMenu> {

    private static final int PANEL = 0xFFC6C6C6;
    private static final int EDGE_LIGHT = 0xFFFFFFFF;
    private static final int EDGE_DARK = 0xFF555555;
    private static final int SLOT_BORDER = 0xFF373737;
    private static final int SLOT_FILL = 0xFF8B8B8B;
    private static final int SLOT_FILL_DIM = 0xFF9E9E9E;
    private static final int ACCENT_OK = 0xFF3FBF5F;
    private static final int ACCENT_MATERIAL = 0xFFE0A020;

    private static final int ACCENT_HIGHLIGHT = 0xFFFFC83C;
    private static final int ACCENT_BAD = 0xFFB03030;
    private static final int ACCENT_SELECT = 0xFF2F7FD0;
    private static final int LABEL_INK = 0x404040;
    private static final int DIM_INK = 0x808080;
    private static final int HINT_INK = 0x606060;
    private static final int ROW_HOVER = 0xFFDCDCDC;
    private static final int ROW_LOCKED = 0xFFB8B8B8;

    private static final int PART_NAME_WIDTH = GearForgeMenu.PART_LIST_W;

    private static final int GHOST_CYCLE_TICKS = 15;

    private static final int SCROLL_THUMB_MIN_PX = 14;

    private static final float MATERIAL_LUMINANCE_LIMIT = 145f;

    private final int[] ghostIndex = new int[GearForgeLogic.REQUIREMENT_COUNT];
    private int ghostCycle;

    private boolean draggingScrollBar;
    private double scrollGrabOffset;
    private int scrollDragRow = -1;

    private int hoveredPartRow = -1;
    @Nullable
    private PartType hoveredPartType;

    private final List<Slot> highlightedSlots = new ArrayList<>();

    private int previewSlot = -1;
    private ItemStack previewInput = ItemStack.EMPTY;
    private List<ItemStack> previewSlots = List.of();
    private ItemStack previewStack = ItemStack.EMPTY;

    public GearForgeScreen(GearForgeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = GearForgeMenu.IMAGE_WIDTH;
        this.imageHeight = GearForgeMenu.IMAGE_HEIGHT;
        this.inventoryLabelX = GearForgeMenu.INV_X;
        this.inventoryLabelY = GearForgeMenu.INV_LABEL_Y;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {


        GearForgeLogic.lockSilentGear();
        try {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            if (!renderCustomTooltip(guiGraphics, mouseX, mouseY)) {
                this.renderTooltip(guiGraphics, mouseX, mouseY);
            }
        } finally {
            GearForgeLogic.unlockSilentGear();
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        guiGraphics.fill(x - 1, y - 1, x + imageWidth + 1, y + imageHeight + 1, 0xFF000000);
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, PANEL);
        guiGraphics.fill(x, y, x + imageWidth - 1, y + 1, EDGE_LIGHT);
        guiGraphics.fill(x, y, x + 1, y + imageHeight - 1, EDGE_LIGHT);
        guiGraphics.fill(x + 1, y + imageHeight - 1, x + imageWidth, y + imageHeight, EDGE_DARK);
        guiGraphics.fill(x + imageWidth - 1, y + 1, x + imageWidth, y + imageHeight, EDGE_DARK);

        drawTitle(guiGraphics);

        drawWorkpieceFrame(guiGraphics);
        drawSlotBackgrounds(guiGraphics);
        drawInventoryHighlights(guiGraphics);
        drawPartList(guiGraphics, mouseX, mouseY);
        drawGhosts(guiGraphics, mouseX, mouseY);
        drawCandidates(guiGraphics, mouseX, mouseY);
        drawBlueprintDecorations(guiGraphics, mouseX, mouseY);
        drawScrollBar(guiGraphics, mouseX, mouseY);
        drawActionScrollBar(guiGraphics);
        drawForgeButton(guiGraphics, mouseX, mouseY);
        drawHint(guiGraphics);
    }

    private void drawTitle(GuiGraphics guiGraphics) {
        guiGraphics.drawString(this.font, this.title, this.leftPos + 8, this.topPos + 6, LABEL_INK, false);
        label(guiGraphics, "gui.reveriegear.gear_forge.label.blueprints",
                GearForgeMenu.LIB_X, GearForgeMenu.LABEL_Y);
        label(guiGraphics, "gui.reveriegear.gear_forge.label.workpiece",
                GearForgeMenu.FRAME_X, GearForgeMenu.LABEL_Y);
        label(guiGraphics, "gui.reveriegear.gear_forge.label.requirements",
                GearForgeMenu.MAT_X, GearForgeMenu.LABEL_Y);
        label(guiGraphics, "gui.reveriegear.gear_forge.label.actions",
                GearForgeMenu.CAND_X, GearForgeMenu.LABEL_Y);
        label(guiGraphics, "gui.reveriegear.gear_forge.label.player_inventory",
                GearForgeMenu.INV_X, GearForgeMenu.INV_LABEL_Y);
    }

    private void label(GuiGraphics guiGraphics, String key, int x, int y) {
        guiGraphics.drawString(this.font, Component.translatable(key),
                this.leftPos + x, this.topPos + y, LABEL_INK, false);
    }

    private void drawSlotBackgrounds(GuiGraphics guiGraphics) {
        for (Slot slot : this.menu.slots) {
            if (!slot.isActive() || slot.x < 0 || slot.y < 0) {
                continue;
            }
            boolean dim = slot.index == GearForgeLogic.OUTPUT_MENU_SLOT && !this.menu.outputValid();
            drawSlot(guiGraphics, this.leftPos + slot.x, this.topPos + slot.y, borderColor(slot), dim);
        }
    }

    private int borderColor(Slot slot) {
        int index = slot.index;
        if (index == GearForgeLogic.WORKPIECE_MENU_SLOT) {
            return slot.hasItem() ? ACCENT_MATERIAL : SLOT_BORDER;
        }
        if (index == GearForgeLogic.OUTPUT_MENU_SLOT) {
            return this.menu.outputValid() ? ACCENT_OK : SLOT_BORDER;
        }
        if (index >= GearForgeLogic.REQUIREMENT_MENU_START
                && index < GearForgeLogic.REQUIREMENT_MENU_START + GearForgeLogic.REQUIREMENT_COUNT) {
            int k = index - GearForgeLogic.REQUIREMENT_MENU_START;
            List<ForgeRequirement> requirements = this.menu.requirements();
            if (k >= requirements.size()) {
                return SLOT_BORDER;
            }
            ItemStack current = slot.getItem();
            if (!current.isEmpty()) {
                return requirements.get(k).test(current) ? ACCENT_OK : ACCENT_BAD;
            }
            return requirements.get(k).silentGearMaterial() ? ACCENT_MATERIAL : SLOT_BORDER;
        }
        return SLOT_BORDER;
    }

    private void drawSlot(GuiGraphics guiGraphics, int x, int y, int border, boolean dim) {
        guiGraphics.fill(x - 1, y - 1, x + 17, y + 17, border);
        guiGraphics.fill(x, y, x + 16, y + 16, dim ? SLOT_FILL_DIM : SLOT_FILL);
        guiGraphics.fill(x, y, x + 16, y + 1, 0xFF6E6E6E);
        guiGraphics.fill(x, y, x + 1, y + 16, 0xFF6E6E6E);
    }

    private void drawInventoryHighlights(GuiGraphics guiGraphics) {
        highlightedSlots.clear();
        ForgeAction action = this.menu.currentAction();

        if (action == null || action.blueprintMissing) {
            return;
        }
        List<ForgeRequirement> unmet = unmetRequirements();
        if (unmet.isEmpty()) {
            return;
        }
        for (int i = GearForgeLogic.FORGE_MENU_SLOTS; i < this.menu.slots.size(); i++) {
            Slot slot = this.menu.getSlot(i);
            if (!slot.hasItem() || !acceptsAny(unmet, slot.getItem())) {
                continue;
            }
            highlightedSlots.add(slot);
            drawHighlight(guiGraphics, this.leftPos + slot.x, this.topPos + slot.y);
        }
    }

    private List<ForgeRequirement> unmetRequirements() {
        List<ForgeRequirement> requirements = this.menu.requirements();
        List<ForgeRequirement> out = new ArrayList<>();
        for (int k = 0; k < this.menu.requirementCount() && k < requirements.size(); k++) {
            ForgeRequirement requirement = requirements.get(k);
            Slot slot = this.menu.getSlot(GearForgeLogic.REQUIREMENT_MENU_START + k);
            if (!accepts(requirement, slot.getItem())) {
                out.add(requirement);
            }
        }
        return out;
    }

    private static boolean acceptsAny(List<ForgeRequirement> requirements, ItemStack stack) {
        for (ForgeRequirement requirement : requirements) {
            if (accepts(requirement, stack)) {
                return true;
            }
        }
        return false;
    }

    private static boolean accepts(ForgeRequirement requirement, ItemStack stack) {
        try {
            return requirement.test(stack);
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static void drawHighlight(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x - 2, y - 2, x + 18, y - 1, ACCENT_HIGHLIGHT);
        guiGraphics.fill(x - 2, y + 17, x + 18, y + 18, ACCENT_HIGHLIGHT);
        guiGraphics.fill(x - 2, y - 1, x - 1, y + 17, ACCENT_HIGHLIGHT);
        guiGraphics.fill(x + 17, y - 1, x + 18, y + 17, ACCENT_HIGHLIGHT);
    }

    private void drawWorkpieceFrame(GuiGraphics guiGraphics) {
        int x = this.leftPos + GearForgeMenu.FRAME_X;
        int y = this.topPos + GearForgeMenu.FRAME_Y;
        int size = GearForgeMenu.FRAME_SIZE;
        guiGraphics.fill(x, y, x + size, y + size, SLOT_FILL);
        guiGraphics.fill(x, y, x + size - 1, y + 1, 0xFF6E6E6E);
        guiGraphics.fill(x, y, x + 1, y + size - 1, 0xFF6E6E6E);
        guiGraphics.fill(x + 1, y + size - 1, x + size, y + size, EDGE_LIGHT);
        guiGraphics.fill(x + size - 1, y + 1, x + size, y + size, EDGE_LIGHT);

        ItemStack workpiece = this.menu.workpiece();
        String name = workpiece.isEmpty()
                ? Component.translatable("gui.reveriegear.gear_forge.part.empty").getString()
                : workpiece.getHoverName().getString();
        int color = workpiece.isEmpty() ? DIM_INK : LABEL_INK;
        String text = ellipsize(name, size - 4);
        guiGraphics.drawString(this.font, text,
                x + (size - this.font.width(text)) / 2,
                this.topPos + GearForgeMenu.WORKPIECE_NAME_Y, color, false);
    }

    private void drawPartList(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.hoveredPartRow = -1;
        this.hoveredPartType = null;
        ItemStack workpiece = this.menu.workpiece();
        if (!(workpiece.getItem() instanceof GearItem gearItem)) {
            return;
        }

        List<Row> rows = partRows(workpiece, gearItem);
        for (int i = 0; i < rows.size() && i < GearForgeMenu.PART_ROWS; i++) {
            Row row = rows.get(i);
            int x = this.leftPos + GearForgeMenu.PART_LIST_X;
            int y = this.topPos + GearForgeMenu.PART_LIST_Y + i * GearForgeMenu.PART_ROW_H;
            guiGraphics.fill(x, y + 1, x + 5, y + 6, row.installed() ? row.ink() : DIM_INK);
            guiGraphics.drawString(this.font, ellipsize(row.text(), PART_NAME_WIDTH), x + 8, y,
                    row.installed() ? row.ink() : DIM_INK, false);
            if (isInside(mouseX, mouseY, x - 1, y - 1, GearForgeMenu.PART_LIST_W + 4, GearForgeMenu.PART_ROW_H)) {
                this.hoveredPartRow = i;
                this.hoveredPartType = row.type();
            }
        }
    }

    private record Row(PartType type, boolean installed, String text, int ink) {
    }

    private List<Row> partRows(ItemStack workpiece, GearItem gearItem) {
        List<Row> installed = new ArrayList<>();
        List<Row> required = new ArrayList<>();
        List<Row> optional = new ArrayList<>();

        for (PartType type : SgRegistries.PART_TYPE) {
            if (type.is(PartTypes.NONE) || type.isUpgrade()) {
                continue;
            }
            PartInstance current = GearData.getPartOfType(workpiece, type);
            boolean isRequired = gearItem.requiresPartOfType(type);
            if (current == null && !isRequired && !type.isRemovable()) {
                continue;
            }

            Row row;
            if (current == null) {
                row = new Row(type, false, type.getDisplayName().getString(), DIM_INK);
            } else {
                String materials = materialNames(current);
                row = new Row(type, true,
                        materials.isEmpty() ? current.getDisplayName().getString() : materials,
                        readableOnLight(materialColor(current, type, gearItem)));
            }
            if (current != null) {
                installed.add(row);
            } else if (isRequired) {
                required.add(row);
            } else {
                optional.add(row);
            }
        }

        List<Row> out = new ArrayList<>(installed);
        out.addAll(required);
        out.addAll(optional);
        return out;
    }

    private static String materialNames(PartInstance part) {
        List<MaterialInstance> materials = part.getMaterials();
        if (materials.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String first = null;
        for (MaterialInstance material : materials) {
            String name = material.getSimpleName().getString();
            if (name.equals(first) || sb.indexOf(name) >= 0) {
                continue;
            }
            if (first == null) {
                first = name;
                sb.append(name);
            } else if (sb.indexOf("+") < 0) {
                sb.append('+').append(name);
            } else {
                return sb + "+…";
            }
        }
        return sb.toString();
    }

    private static int materialColor(PartInstance part, PartType type, GearItem gearItem) {
        List<MaterialInstance> materials = part.getMaterials();
        if (materials.isEmpty()) {
            return 0xFF207020;
        }
        return readableOnLight(materials.getFirst().getNameColor(type, gearItem.getGearType()));
    }

    private static int readableOnLight(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        float luminance = 0.299f * r + 0.587f * g + 0.114f * b;
        if (luminance > MATERIAL_LUMINANCE_LIMIT) {
            float k = MATERIAL_LUMINANCE_LIMIT / luminance;
            r = (int) (r * k);
            g = (int) (g * k);
            b = (int) (b * k);
        }
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private void drawGhosts(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.minecraft == null || this.minecraft.level == null) {
            return;
        }
        ghostCycle = (int) ((this.minecraft.level.getGameTime() / GHOST_CYCLE_TICKS) & 0x7FFFFFFF);
        List<ForgeRequirement> requirements = this.menu.requirements();
        for (int k = 0; k < requirements.size() && k < GearForgeLogic.REQUIREMENT_COUNT; k++) {
            Slot slot = this.menu.getSlot(GearForgeLogic.REQUIREMENT_MENU_START + k);
            if (!slot.isActive() || slot.hasItem()) {
                ghostIndex[k] = -1;
                continue;
            }
            List<ItemStack> options = requirements.get(k).examples();
            if (options.isEmpty()) {
                ghostIndex[k] = -1;
                continue;
            }
            boolean hovering = isInside(mouseX, mouseY, this.leftPos + slot.x, this.topPos + slot.y, 16, 16);
            int pick = hovering && ghostIndex[k] >= 0 && ghostIndex[k] < options.size()
                    ? ghostIndex[k]
                    : ghostCycle % options.size();
            ghostIndex[k] = pick;
            guiGraphics.renderItem(options.get(pick), this.leftPos + slot.x, this.topPos + slot.y);
        }
    }

    private void drawBlueprintDecorations(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int activePos = this.menu.activeBlueprintWindowPos();
        for (int i = 0; i < GearForgeLogic.BLUEPRINT_VISIBLE; i++) {
            Slot slot = this.menu.getSlot(GearForgeLogic.BLUEPRINT_WINDOW_MENU_START + i);
            if (!slot.isActive()) {
                continue;
            }
            int x = this.leftPos + slot.x;
            int y = this.topPos + slot.y;
            if (i == activePos) {
                guiGraphics.fill(x - 2, y - 2, x + 18, y - 1, ACCENT_SELECT);
                guiGraphics.fill(x - 2, y + 17, x + 18, y + 18, ACCENT_SELECT);
                guiGraphics.fill(x - 2, y - 2, x - 1, y + 18, ACCENT_SELECT);
                guiGraphics.fill(x + 17, y - 2, x + 18, y + 18, ACCENT_SELECT);
            }
            if (isInside(mouseX, mouseY, x - 1, y - 1, 18, 18)) {
                guiGraphics.fill(x - 1, y - 1, x + 17, y + 17, 0x40FFFFFF);
            }
        }
    }

    private void drawCandidates(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<ForgeAction> actions = this.menu.actions();
        for (int i = 0; i < GearForgeMenu.CAND_ROWS; i++) {
            int index = this.menu.actionScroll() + i;
            int x = this.leftPos + GearForgeMenu.CAND_X;
            int y = this.topPos + candidateY(i);
            boolean inRange = index < actions.size();
            boolean selected = inRange && index == this.menu.selectedIndex();
            boolean hovered = inRange && isInside(mouseX, mouseY, x, y, GearForgeMenu.CAND_W, GearForgeMenu.CAND_ROW_H);

            guiGraphics.fill(x, y, x + GearForgeMenu.CAND_W, y + GearForgeMenu.CAND_ROW_H,
                    !inRange ? ROW_LOCKED : (selected ? 0xFFBFD8F0 : (hovered ? ROW_HOVER : SLOT_FILL)));
            guiGraphics.fill(x, y, x + GearForgeMenu.CAND_W, y + 1, selected ? ACCENT_SELECT : 0xFF6E6E6E);
            guiGraphics.fill(x, y + GearForgeMenu.CAND_ROW_H - 1, x + GearForgeMenu.CAND_W, y + GearForgeMenu.CAND_ROW_H,
                    selected ? ACCENT_SELECT : 0xFF555555);
            if (!inRange) {
                continue;
            }

            ForgeAction action = actions.get(index);
            if (!action.icon.isEmpty()) {
                guiGraphics.renderItem(action.icon, x + 1, y);
            }

            guiGraphics.drawString(this.font, ellipsize(actionName(action).getString(), GearForgeMenu.CAND_W - 21),
                    x + 19, y + 4, selected ? 0xFF103050 : LABEL_INK, false);
        }
    }

    private Component actionName(ForgeAction action) {
        return switch (action.kind) {
            case CRAFT -> {
                Component name = action.icon.isEmpty() ? Component.literal("?") : action.icon.getHoverName();
                yield action.assembly()
                        ? name.copy().append(Component.translatable("gui.reveriegear.gear_forge.action.assembly"))
                        : name;
            }
            case CONVERT -> Component.translatable("gui.reveriegear.gear_forge.action.convert");
            case REPLACE -> Component.translatable(action.recipe == null
                    ? "gui.reveriegear.gear_forge.action.replace_ready"
                    : "gui.reveriegear.gear_forge.action.replace", action.partType.getDisplayName());
            case INSTALL -> Component.translatable(action.recipe == null
                    ? "gui.reveriegear.gear_forge.action.install_ready"
                    : "gui.reveriegear.gear_forge.action.install", action.partType.getDisplayName());
            case REMOVE -> Component.translatable("gui.reveriegear.gear_forge.action.remove",
                    action.partType.getDisplayName());
        };
    }

    private static int candidateY(int i) {
        return GearForgeMenu.CAND_Y + i * GearForgeMenu.CAND_ROW_H;
    }

    private void drawScrollBar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ItemStack active = this.menu.activeBlueprintStack();

        String name = active.isEmpty()
                ? Component.translatable("gui.reveriegear.gear_forge.label.no_blueprint").getString()
                : "▸ " + active.getHoverName().getString();
        guiGraphics.drawString(this.font, ellipsize(name, GearForgeMenu.LIB_W),
                this.leftPos + GearForgeMenu.LIB_X, this.topPos + GearForgeMenu.CAPTION_Y,
                active.isEmpty() ? DIM_INK : LABEL_INK, false);

        int x = this.leftPos + GearForgeMenu.SCROLLBAR_X;
        int y = this.topPos + GearForgeMenu.LIB_Y;
        int w = GearForgeMenu.SCROLLBAR_W;
        int h = GearForgeMenu.LIB_H;

        guiGraphics.fill(x, y, x + w, y + h, 0xFF8B8B8B);
        guiGraphics.fill(x, y, x + 1, y + h, 0xFF5F5F5F);
        guiGraphics.fill(x + w - 1, y, x + w, y + h, 0xFFB8B8B8);

        int max = this.menu.maxScroll();
        if (max <= 0) {
            return;
        }

        int thumbH = scrollThumbHeight(h);
        int travel = h - thumbH;
        int thumbY = y + travel * this.menu.firstVisibleRow() / max;
        boolean hovered = isOverScrollBar(mouseX, mouseY);

        int body = this.draggingScrollBar ? 0xFF9CC6EE : (hovered ? 0xFFD7E7F7 : 0xFFD8D8D8);
        guiGraphics.fill(x, thumbY, x + w, thumbY + thumbH, body);
        guiGraphics.fill(x, thumbY, x + w, thumbY + 1, 0xFFFFFFFF);
        guiGraphics.fill(x, thumbY + thumbH - 1, x + w, thumbY + thumbH, 0xFF4A4A4A);
    }

    private static int scrollThumbHeight(int trackHeight) {
        return Math.max(SCROLL_THUMB_MIN_PX,
                trackHeight * GearForgeLogic.BLUEPRINT_VISIBLE_ROWS / GearForgeLogic.BLUEPRINT_ROWS);
    }

    private boolean isOverScrollBar(double mouseX, double mouseY) {
        return isInside(mouseX, mouseY,
                this.leftPos + GearForgeMenu.SCROLLBAR_X - 1,
                this.topPos + GearForgeMenu.LIB_Y,
                GearForgeMenu.SCROLLBAR_W + 2,
                GearForgeMenu.LIB_H);
    }

    private int scrollThumbTop() {
        int y = this.topPos + GearForgeMenu.LIB_Y;
        int h = GearForgeMenu.LIB_H;
        int max = this.menu.maxScroll();
        if (max <= 0) {
            return y;
        }
        int travel = h - scrollThumbHeight(h);
        return y + travel * this.menu.firstVisibleRow() / max;
    }

    private void drawActionScrollBar(GuiGraphics guiGraphics) {
        int max = this.menu.maxActionScroll();
        if (max <= 0) {
            return;
        }
        int x = this.leftPos + GearForgeMenu.ACTION_SCROLLBAR_X;
        int y = this.topPos + GearForgeMenu.CAND_Y;
        int h = GearForgeMenu.CAND_ROWS * GearForgeMenu.CAND_ROW_H;
        guiGraphics.fill(x, y, x + GearForgeMenu.ACTION_SCROLLBAR_W, y + h, 0xFF9E9E9E);
        int total = max + GearForgeMenu.CAND_ROWS;
        int thumbH = Math.max(12, h * GearForgeMenu.CAND_ROWS / total);
        int travel = h - thumbH;
        int thumbY = y + travel * this.menu.actionScroll() / max;
        guiGraphics.fill(x, thumbY, x + GearForgeMenu.ACTION_SCROLLBAR_W, thumbY + thumbH, 0xFF404040);
    }

    private void drawForgeButton(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = this.leftPos + GearForgeMenu.FORGE_X;
        int y = this.topPos + GearForgeMenu.FORGE_Y;
        int w = GearForgeMenu.FORGE_W;
        int h = GearForgeMenu.FORGE_H;
        boolean enabled = this.menu.outputValid();
        boolean hovered = isInside(mouseX, mouseY, x, y, w, h);

        guiGraphics.fill(x, y, x + w, y + h, enabled && hovered ? 0xFF9ED49E : (enabled ? 0xFF7FBF7F : 0xFFA0A0A0));
        guiGraphics.fill(x, y, x + w - 1, y + 1, EDGE_LIGHT);
        guiGraphics.fill(x, y, x + 1, y + h - 1, EDGE_LIGHT);
        guiGraphics.fill(x + 1, y + h - 1, x + w, y + h, EDGE_DARK);
        guiGraphics.fill(x + w - 1, y + 1, x + w, y + h, EDGE_DARK);

        Component text = Component.translatable("gui.reveriegear.gear_forge.button.forge");
        guiGraphics.drawString(this.font, text,
                x + (w - this.font.width(text)) / 2,
                y + (h - 8) / 2 + 1, enabled ? 0xFF103010 : 0xFF606060, false);
    }

    private void drawHint(GuiGraphics guiGraphics) {
        Component hint = hintComponent();
        if (hint == null) {
            return;
        }
        List<String> lines = wrap(hint.getString(), GearForgeMenu.HINT_WIDTH, GearForgeMenu.HINT_LINES);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(this.font, lines.get(i),
                    this.leftPos + GearForgeMenu.HINT_X,
                    this.topPos + GearForgeMenu.HINT_Y + i * GearForgeMenu.HINT_LINE_H, HINT_INK, false);
        }
    }

    @Nullable
    private Component hintComponent() {
        return switch (this.menu.hint()) {
            case GearForgeMenu.HINT_NO_BLUEPRINT ->
                    Component.translatable("gui.reveriegear.gear_forge.hint.no_blueprint");
            case GearForgeMenu.HINT_NO_ACTION ->
                    Component.translatable("gui.reveriegear.gear_forge.hint.no_action");
            case GearForgeMenu.HINT_MISSING_BLUEPRINT ->
                    Component.translatable("gui.reveriegear.gear_forge.hint.missing_blueprint");
            case GearForgeMenu.HINT_NEED_MATERIALS ->
                    Component.translatable("gui.reveriegear.gear_forge.hint.need_materials");
            case GearForgeMenu.HINT_MIXED_MATERIAL ->
                    Component.translatable("gui.reveriegear.gear_forge.hint.mixed_material");
            case GearForgeMenu.HINT_UNUSABLE ->
                    Component.translatable("gui.reveriegear.gear_forge.hint.unusable");
            default -> null;
        };
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
        List<Component> lines = super.getTooltipFromContainerItem(stack);
        if (this.hoveredSlot == null || this.hoveredSlot.index < GearForgeLogic.FORGE_MENU_SLOTS) {
            return lines;
        }
        if (!highlightedSlots.contains(this.hoveredSlot)) {
            return lines;
        }
        List<Component> out = new ArrayList<>(lines);
        out.add(Component.translatable("gui.reveriegear.gear_forge.slot.highlight")
                .withStyle(ChatFormatting.GOLD));
        return out;
    }

    private boolean renderCustomTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!this.menu.getCarried().isEmpty()) {
            return false;
        }

        List<Component> preview = materialPreviewTooltip(mouseX, mouseY);
        if (preview != null) {
            guiGraphics.renderComponentTooltip(this.font, preview, mouseX, mouseY);
            return true;
        }

        int blueprintPos = blueprintWindowPosAt(mouseX, mouseY);
        if (blueprintPos >= 0) {
            guiGraphics.renderComponentTooltip(this.font, blueprintTooltip(blueprintPos), mouseX, mouseY);
            return true;
        }

        List<Component> scroll = scrollBarTooltip(mouseX, mouseY);
        if (scroll != null) {
            guiGraphics.renderComponentTooltip(this.font, scroll, mouseX, mouseY);
            return true;
        }

        List<Component> action = actionTooltipAt(mouseX, mouseY);
        if (action != null) {
            guiGraphics.renderComponentTooltip(this.font, action, mouseX, mouseY);
            return true;
        }

        List<Component> part = partTooltip();
        if (part != null) {
            guiGraphics.renderComponentTooltip(this.font, part, mouseX, mouseY);
            return true;
        }

        List<Component> forge = forgeButtonTooltip(mouseX, mouseY);
        if (forge != null) {
            guiGraphics.renderComponentTooltip(this.font, forge, mouseX, mouseY);
            return true;
        }

        Slot hovered = this.hoveredSlot;
        if (hovered != null && isRequirementSlot(hovered.index) && !hovered.hasItem()) {
            guiGraphics.renderComponentTooltip(this.font,
                    requirementTooltip(hovered.index - GearForgeLogic.REQUIREMENT_MENU_START), mouseX, mouseY);
            return true;
        }
        return false;
    }

    private static boolean isRequirementSlot(int index) {
        return index >= GearForgeLogic.REQUIREMENT_MENU_START
                && index < GearForgeLogic.REQUIREMENT_MENU_START + GearForgeLogic.REQUIREMENT_COUNT;
    }

    @Nullable
    private List<Component> scrollBarTooltip(double mouseX, double mouseY) {
        if (!isOverScrollBar(mouseX, mouseY)) {
            return null;
        }
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("gui.reveriegear.gear_forge.scroll.stored",
                        this.menu.storedBlueprintCount(), GearForgeLogic.BLUEPRINT_COUNT)
                .withStyle(ChatFormatting.WHITE));
        if (this.menu.maxScroll() > 0) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.scroll.rows",
                            this.menu.firstVisibleRow() + 1, this.menu.lastVisibleRow() + 1,
                            GearForgeLogic.BLUEPRINT_ROWS)
                    .withStyle(ChatFormatting.GRAY));
            lines.add(Component.translatable("gui.reveriegear.gear_forge.scroll.hint")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        return lines;
    }

    private int blueprintWindowPosAt(double mouseX, double mouseY) {
        for (int i = 0; i < GearForgeLogic.BLUEPRINT_VISIBLE; i++) {
            Slot slot = this.menu.getSlot(GearForgeLogic.BLUEPRINT_WINDOW_MENU_START + i);
            if (!slot.isActive()) {
                continue;
            }
            if (isInside(mouseX, mouseY, this.leftPos + slot.x - 1, this.topPos + slot.y - 1, 18, 18)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isActiveBlueprintWindow(int windowPos) {
        return this.menu.activeBlueprintWindowPos() == windowPos;
    }

    private List<Component> blueprintTooltip(int windowPos) {
        List<Component> lines = new ArrayList<>();
        Slot slot = this.menu.getSlot(GearForgeLogic.BLUEPRINT_WINDOW_MENU_START + windowPos);
        if (!slot.hasItem()) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.blueprint.empty")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return lines;
        }
        ItemStack stack = slot.getItem();
        lines.add(stack.getHoverName().copy().withStyle(ChatFormatting.WHITE));
        lines.add(Component.translatable("gui.reveriegear.gear_forge.blueprint.hint_take")
                .withStyle(ChatFormatting.DARK_GRAY));

        if (!(stack.getItem() instanceof IBlueprint blueprint)) {
            return lines;
        }

        PartType partType = blueprint.getPartType(stack);
        if (partType == null || !partType.is(PartTypes.MAIN)) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.blueprint.base")
                    .withStyle(ChatFormatting.GOLD));
            return lines;
        }
        List<PartType> supported = GearForgeLogic.supportedPartTypes(blueprint.getGearType(stack));
        if (supported.isEmpty()) {
            return lines;
        }
        lines.add(Component.translatable("gui.reveriegear.gear_forge.blueprint.supported")
                .withStyle(ChatFormatting.GOLD));
        for (PartType type : supported) {
            lines.add(type.getDisplayName().copy().withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    @Nullable
    private List<Component> actionTooltipAt(double mouseX, double mouseY) {
        int index = candidateIndexAt(mouseX, mouseY);
        List<ForgeAction> actions = this.menu.actions();
        if (index < 0 || index >= actions.size()) {
            return null;
        }
        ForgeAction action = actions.get(index);
        List<Component> lines = new ArrayList<>();

        lines.add(actionName(action).copy().withStyle(ChatFormatting.GOLD));

        if (action.installedPart != null) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.part.installed",
                            action.installedPart.getDisplayName().copy().withStyle(ChatFormatting.YELLOW))
                    .withStyle(ChatFormatting.GRAY));
        }
        if (!action.icon.isEmpty()) {
            lines.add(action.icon.getHoverName().copy().withStyle(ChatFormatting.WHITE));
        }
        if (action.blueprintMissing) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.require.blueprint_missing")
                    .withStyle(ChatFormatting.RED));
        } else if (action.blueprintIngredientIndex >= 0) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.require.blueprint")
                    .withStyle(ChatFormatting.AQUA));
        } else if (action.assembly()) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.require.assembly")
                    .withStyle(ChatFormatting.AQUA));
        }
        if (action.recipe == null && action.isModify()) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.require.ready_part")
                    .withStyle(ChatFormatting.AQUA));
        }
        if (action.kind == ForgeAction.Kind.CRAFT && !action.assembly()) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.action.craft")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (!action.requirements.isEmpty()) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.require.count",
                            action.requirements.size())
                    .withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    private List<Component> requirementTooltip(int k) {
        List<ForgeRequirement> requirements = this.menu.requirements();
        if (k < 0 || k >= requirements.size()) {
            return List.of(Component.translatable("gui.reveriegear.gear_forge.slot.unused")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        ForgeRequirement requirement = requirements.get(k);
        List<Component> lines = new ArrayList<>();
        if (requirement.partType() != null) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.require.part",
                            requirement.partType().getDisplayName().copy().withStyle(ChatFormatting.YELLOW))
                    .withStyle(ChatFormatting.GOLD));
        } else {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.require.any")
                    .withStyle(ChatFormatting.WHITE));
        }
        if (requirement.silentGearMaterial()) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.require.material")
                    .withStyle(ChatFormatting.GOLD));
        }
        lines.add(Component.translatable("gui.reveriegear.gear_forge.slot.strict")
                .withStyle(ChatFormatting.DARK_GRAY));
        return lines;
    }

    @Nullable
    private List<Component> materialPreviewTooltip(double mouseX, double mouseY) {
        if (this.minecraft == null || this.minecraft.player == null) {
            return null;
        }
        Slot hovered = this.hoveredSlot;
        if (hovered == null || !isRequirementSlot(hovered.index) || hovered.hasItem()) {
            return null;
        }
        int k = hovered.index - GearForgeLogic.REQUIREMENT_MENU_START;
        ItemStack candidate = ghostAt(k);
        if (candidate.isEmpty()) {
            return null;
        }

        ItemStack preview = previewWith(k, candidate);
        if (preview.isEmpty()) {
            return null;
        }
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("gui.reveriegear.gear_forge.preview.title")
                .withStyle(ChatFormatting.GOLD));
        lines.add(preview.getHoverName().copy().withStyle(ChatFormatting.WHITE));
        lines.add(Component.translatable("gui.reveriegear.gear_forge.preview.with",
                        candidate.getHoverName().copy().withStyle(ChatFormatting.YELLOW))
                .withStyle(ChatFormatting.GRAY));
        return lines;
    }

    private ItemStack ghostAt(int k) {
        List<ForgeRequirement> requirements = this.menu.requirements();
        if (k < 0 || k >= requirements.size() || k >= ghostIndex.length || ghostIndex[k] < 0) {
            return ItemStack.EMPTY;
        }
        List<ItemStack> options = requirements.get(k).examples();
        int pick = ghostIndex[k];
        return pick < options.size() ? options.get(pick) : ItemStack.EMPTY;
    }

    private ItemStack previewWith(int k, ItemStack candidate) {
        ForgeAction action = this.menu.currentAction();
        if (action == null || this.minecraft == null || this.minecraft.level == null) {
            return ItemStack.EMPTY;
        }
        List<ItemStack> current = this.menu.requirementStacks();
        if (k == previewSlot
                && ItemStack.matches(previewInput, candidate)
                && sameStacks(previewSlots, current)) {
            return previewStack;
        }
        previewSlot = k;
        previewInput = candidate.copyWithCount(1);
        previewSlots = copyOf(current);
        previewStack = ItemStack.EMPTY;

        List<ItemStack> stacks = new ArrayList<>(current);
        if (k < stacks.size()) {
            stacks.set(k, candidate.copyWithCount(1));
        }
        try {

            GearForgeLogic.ForgeOutput out = GearForgeLogic.produce(this.minecraft.level, action,
                    this.menu.workpiece(), this.menu.blueprints(), stacks, null);
            previewStack = out == null ? ItemStack.EMPTY : out.result();
        } catch (RuntimeException ignored) {
            previewStack = ItemStack.EMPTY;
        }
        return previewStack;
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

    private static List<ItemStack> copyOf(List<ItemStack> list) {
        List<ItemStack> out = new ArrayList<>(list.size());
        for (ItemStack stack : list) {
            out.add(stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        return out;
    }

    @Nullable
    private List<Component> partTooltip() {
        if (this.hoveredPartType == null) {
            return null;
        }
        ItemStack workpiece = this.menu.workpiece();
        PartInstance part = this.hoveredPartRow >= 0 ? GearData.getPartOfType(workpiece, this.hoveredPartType) : null;
        List<Component> lines = new ArrayList<>();
        if (part == null) {
            lines.add(this.hoveredPartType.getDisplayName().copy().withStyle(ChatFormatting.WHITE));
            lines.add(Component.translatable("gui.reveriegear.gear_forge.part.tooltip.empty")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return lines;
        }
        lines.add(part.getDisplayName().copy().withStyle(ChatFormatting.WHITE));
        lines.add(Component.translatable("gui.reveriegear.gear_forge.part.tooltip.type",
                        this.hoveredPartType.getDisplayName())
                .withStyle(ChatFormatting.GRAY));
        String materials = materialNames(part);
        if (!materials.isEmpty()) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.part.tooltip.materials", materials)
                    .withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    @Nullable
    private List<Component> forgeButtonTooltip(double mouseX, double mouseY) {
        if (!isInside(mouseX, mouseY, this.leftPos + GearForgeMenu.FORGE_X, this.topPos + GearForgeMenu.FORGE_Y,
                GearForgeMenu.FORGE_W, GearForgeMenu.FORGE_H)) {
            return null;
        }
        List<Component> lines = new ArrayList<>();
        Slot output = this.menu.getSlot(GearForgeLogic.OUTPUT_MENU_SLOT);
        if (this.menu.outputValid() && output.hasItem()) {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.button.forge.tooltip",
                            output.getItem().getHoverName())
                    .withStyle(ChatFormatting.GOLD));
        } else {
            lines.add(Component.translatable("gui.reveriegear.gear_forge.button.forge.tooltip.idle")
                    .withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.minecraft != null && this.minecraft.gameMode != null) {
            if (isOverScrollBar(mouseX, mouseY)) {
                int thumbTop = scrollThumbTop();
                int thumbH = scrollThumbHeight(GearForgeMenu.LIB_H);
                if (mouseY >= thumbTop && mouseY < thumbTop + thumbH) {
                    this.draggingScrollBar = true;
                    this.scrollGrabOffset = mouseY - thumbTop;
                    this.scrollDragRow = this.menu.firstVisibleRow();
                } else {
                    click(mouseY < thumbTop
                            ? GearForgeMenu.BUTTON_SCROLL_PAGE_UP
                            : GearForgeMenu.BUTTON_SCROLL_PAGE_DOWN);
                }
                return true;
            }

            if (this.menu.getCarried().isEmpty() && !hasShiftDown()) {
                int blueprintPos = blueprintWindowPosAt(mouseX, mouseY);
                Slot slot = blueprintPos >= 0
                        ? this.menu.getSlot(GearForgeLogic.BLUEPRINT_WINDOW_MENU_START + blueprintPos) : null;
                if (slot != null && slot.hasItem() && !isActiveBlueprintWindow(blueprintPos)) {
                    click(GearForgeMenu.BUTTON_BLUEPRINT_BASE + blueprintPos);
                    return true;
                }
            }

            int row = candidateRowAt(mouseX, mouseY);
            if (row >= 0 && this.menu.actionIndexAtRow(row) >= 0) {
                click(GearForgeMenu.BUTTON_ACTION_BASE + row);
                return true;
            }
            if (isInside(mouseX, mouseY, this.leftPos + GearForgeMenu.FORGE_X, this.topPos + GearForgeMenu.FORGE_Y,
                    GearForgeMenu.FORGE_W, GearForgeMenu.FORGE_H)) {
                click(GearForgeMenu.BUTTON_FORGE);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingScrollBar && button == 0 && this.minecraft != null && this.minecraft.gameMode != null) {
            int y = this.topPos + GearForgeMenu.LIB_Y;
            int h = GearForgeMenu.LIB_H;
            int max = this.menu.maxScroll();
            int travel = h - scrollThumbHeight(h);
            if (max > 0 && travel > 0) {
                double thumbTop = mouseY - this.scrollGrabOffset - y;
                int row = (int) Math.round(thumbTop * max / (double) travel);
                row = Math.max(0, Math.min(max, row));
                if (row != this.scrollDragRow) {
                    this.scrollDragRow = row;
                    click(GearForgeMenu.BUTTON_SCROLL_TO_BASE + row);
                }
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.draggingScrollBar && button == 0) {
            this.draggingScrollBar = false;
            this.scrollDragRow = -1;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.minecraft != null && this.minecraft.gameMode != null && scrollY != 0) {
            if (isInside(mouseX, mouseY,
                    this.leftPos + GearForgeMenu.LIB_X - 1, this.topPos + GearForgeMenu.LIB_Y - 1,
                    GearForgeMenu.LIB_W + GearForgeMenu.SCROLLBAR_W + 3, GearForgeMenu.LIB_H + 2)) {
                boolean page = hasShiftDown();
                click(scrollY > 0
                        ? (page ? GearForgeMenu.BUTTON_SCROLL_PAGE_UP : GearForgeMenu.BUTTON_SCROLL_UP)
                        : (page ? GearForgeMenu.BUTTON_SCROLL_PAGE_DOWN : GearForgeMenu.BUTTON_SCROLL_DOWN));
                return true;
            }
            if (this.menu.maxActionScroll() > 0 && isInside(mouseX, mouseY,
                    this.leftPos + GearForgeMenu.CAND_X, this.topPos + GearForgeMenu.CAND_Y,
                    GearForgeMenu.CAND_W, GearForgeMenu.CAND_ROWS * GearForgeMenu.CAND_ROW_H)) {
                click(scrollY > 0 ? GearForgeMenu.BUTTON_ACTION_SCROLL_UP
                        : GearForgeMenu.BUTTON_ACTION_SCROLL_DOWN);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void click(int buttonId) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    private int candidateRowAt(double mouseX, double mouseY) {
        for (int i = 0; i < GearForgeMenu.CAND_ROWS; i++) {
            int x = this.leftPos + GearForgeMenu.CAND_X;
            int y = this.topPos + candidateY(i);
            if (isInside(mouseX, mouseY, x, y, GearForgeMenu.CAND_W, GearForgeMenu.CAND_ROW_H)) {
                return i;
            }
        }
        return -1;
    }

    private int candidateIndexAt(double mouseX, double mouseY) {
        int row = candidateRowAt(mouseX, mouseY);
        return row < 0 ? -1 : this.menu.actionIndexAtRow(row);
    }

    private String ellipsize(String text, int maxWidth) {
        if (text == null || text.isEmpty() || this.font.width(text) <= maxWidth) {
            return text == null ? "" : text;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (this.font.width(sb.toString() + text.charAt(i)) > maxWidth - 5) {
                break;
            }
            sb.append(text.charAt(i));
        }
        return sb + "…";
    }

    private List<String> wrap(String text, int maxWidth, int maxLines) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }
        StringBuilder line = new StringBuilder();
        boolean truncated = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (line.length() > 0 && this.font.width(line.toString() + c) > maxWidth) {
                if (lines.size() == maxLines - 1) {
                    truncated = true;
                    break;
                }
                lines.add(line.toString());
                line.setLength(0);
            }
            line.append(c);
        }
        if (line.length() > 0) {
            lines.add((truncated ? line + "…" : line.toString()));
        }
        return lines;
    }

    private static boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
