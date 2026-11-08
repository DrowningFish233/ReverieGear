package com.drowningfish233.reveriegear.block.forge;

import com.drowningfish233.reveriegear.Utils.GearDataRepair;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.crafting.ingredient.BlueprintIngredient;
import net.silentchaos512.gear.crafting.recipe.ConversionRecipe;
import net.silentchaos512.gear.crafting.recipe.ShapelessCompoundPartRecipe;
import net.silentchaos512.gear.crafting.recipe.ShapelessGearRecipe;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public final class GearForgeLogic {

    public static final int BLUEPRINT_START = 0;
    public static final int BLUEPRINT_COLS = 3;


    public static final int BLUEPRINT_VISIBLE_ROWS = 5;


    public static final int BLUEPRINT_ROWS = 18;
    public static final int BLUEPRINT_COUNT = BLUEPRINT_COLS * BLUEPRINT_ROWS;
    public static final int BLUEPRINT_VISIBLE = BLUEPRINT_COLS * BLUEPRINT_VISIBLE_ROWS;
    public static final int BLUEPRINT_MAX_SCROLL = BLUEPRINT_ROWS - BLUEPRINT_VISIBLE_ROWS;
    public static final int BLUEPRINT_PAGE_ROWS = BLUEPRINT_VISIBLE_ROWS;

    public static final int WORKPIECE_SLOT = BLUEPRINT_START + BLUEPRINT_COUNT;

    public static final int REQUIREMENT_START = WORKPIECE_SLOT + 1;
    public static final int REQUIREMENT_COUNT = 9;

    public static final int OUTPUT_SLOT = REQUIREMENT_START + REQUIREMENT_COUNT;

    public static final int CONTAINER_SIZE = OUTPUT_SLOT + 1;


    public static final int LEGACY_BLUEPRINT_COUNT = 27;
    public static final int LEGACY_WORKPIECE_SLOT = LEGACY_BLUEPRINT_COUNT;
    public static final int LEGACY_REQUIREMENT_START = LEGACY_WORKPIECE_SLOT + 1;
    public static final int LEGACY_CONTAINER_SIZE = LEGACY_REQUIREMENT_START + REQUIREMENT_COUNT + 1;

    public static final int BLUEPRINT_TAIL_COUNT = BLUEPRINT_COUNT - BLUEPRINT_VISIBLE;
    public static final int BLUEPRINT_WINDOW_MENU_START = 0;
    public static final int BLUEPRINT_TAIL_MENU_START = BLUEPRINT_VISIBLE;
    public static final int BLUEPRINT_MENU_COUNT = BLUEPRINT_COUNT;
    public static final int WORKPIECE_MENU_SLOT = BLUEPRINT_MENU_COUNT;
    public static final int REQUIREMENT_MENU_START = WORKPIECE_MENU_SLOT + 1;
    public static final int OUTPUT_MENU_SLOT = REQUIREMENT_MENU_START + REQUIREMENT_COUNT;
    public static final int FORGE_MENU_SLOTS = OUTPUT_MENU_SLOT + 1;

    public static final int SINGLE = 1;


    private static final ReentrantLock SILENT_GEAR_LOCK = new ReentrantLock();

    public static void lockSilentGear() {
        SILENT_GEAR_LOCK.lock();
    }

    public static void unlockSilentGear() {
        SILENT_GEAR_LOCK.unlock();
    }

    private GearForgeLogic() {
    }

    public enum Mode {

        CRAFT,

        MODIFY,

        CONVERT
    }

    public static Mode modeOf(ItemStack workpiece) {
        if (workpiece.isEmpty()) {
            return Mode.CRAFT;
        }
        return workpiece.getItem() instanceof GearItem ? Mode.MODIFY : Mode.CONVERT;
    }

    public static boolean isGearRecipe(CraftingRecipe recipe) {
        return recipe instanceof ShapelessGearRecipe
                || recipe instanceof ShapelessCompoundPartRecipe
                || recipe instanceof ConversionRecipe;
    }

    public static List<ForgeAction> actions(Level level, List<ItemStack> blueprints, ItemStack workpiece,
                                            int activeBlueprint) {
        if (level == null) {
            return List.of();
        }
        lockSilentGear();
        try {
            return switch (modeOf(workpiece)) {
                case CRAFT -> craftActions(level, blueprints, activeBlueprint);
                case MODIFY -> modifyActions(level, blueprints, workpiece);
                case CONVERT -> convertActions(level, workpiece);
            };
        } finally {
            unlockSilentGear();
        }
    }

    public static List<ItemStack> collectBlueprints(List<ItemStack> library) {
        List<ItemStack> out = new ArrayList<>();
        for (int i = 0; i < Math.min(BLUEPRINT_COUNT, library.size()); i++) {
            ItemStack stack = library.get(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IBlueprint) {
                out.add(stack);
            }
        }
        return out;
    }

    private static List<ForgeAction> craftActions(Level level, List<ItemStack> blueprints, int activeBlueprint) {
        if (activeBlueprint < 0 || activeBlueprint >= blueprints.size()) {
            return List.of();
        }
        ItemStack blueprint = blueprints.get(activeBlueprint);
        if (blueprint.isEmpty() || !(blueprint.getItem() instanceof IBlueprint blueprintItem)) {
            return List.of();
        }
        GearType blueprintGearType = blueprintItem.getGearType(blueprint);

        List<ForgeAction> out = new ArrayList<>();
        Map<String, ForgeAction> best = new HashMap<>();
        for (RecipeHolder<CraftingRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            CraftingRecipe recipe = holder.value();
            boolean producesGear = recipe instanceof ShapelessGearRecipe;
            boolean producesPart = recipe instanceof ShapelessCompoundPartRecipe;
            if (!producesGear && !producesPart) {
                continue;
            }

            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            int blueprintIndex = findBlueprintIngredient(ingredients);
            if (blueprintIndex >= 0) {
                if (!ingredients.get(blueprintIndex).test(blueprint)) {
                    continue;
                }
            } else {
                if (!(recipe instanceof ShapelessGearRecipe gearRecipe)) {
                    continue;
                }
                if (!gearRecipe.getOutputItem().getGearType().equals(blueprintGearType)) {
                    continue;
                }
            }

            ItemStack icon = recipeResult(level, recipe);
            if (icon.isEmpty()) {
                continue;
            }

            List<ForgeRequirement> requirements = new ArrayList<>();
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < ingredients.size(); i++) {
                if (i == blueprintIndex) {
                    continue;
                }
                requirements.add(ForgeRequirement.of(ingredients.get(i)));
                indices.add(i);
            }
            if (requirements.size() > REQUIREMENT_COUNT) {
                continue;
            }

            String key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(icon.getItem())
                    + "|" + requirements.size() + "|" + describe(requirements);
            ForgeAction action = ForgeAction.craft(holder, icon, requirements, toIntArray(indices),
                    blueprintIndex, activeBlueprint, producesGear, holder.id().toString());
            ForgeAction existing = best.get(key);
            if (existing == null || action.sortKey.compareTo(existing.sortKey) < 0) {
                best.put(key, action);
            }
        }

        out.addAll(best.values());
        out.sort(ForgeAction.ORDER);
        return out;
    }

    private static String describe(List<ForgeRequirement> requirements) {
        StringBuilder sb = new StringBuilder();
        for (ForgeRequirement requirement : requirements) {
            PartType type = requirement.partType();
            sb.append(type == null ? "any" : typeName(type)).append(',');
        }
        return sb.toString();
    }

    private static List<ForgeAction> modifyActions(Level level, List<ItemStack> blueprints, ItemStack gear) {
        if (!(gear.getItem() instanceof GearItem gearItem)) {
            return List.of();
        }
        GearType gearType = gearItem.getGearType();
        Map<PartType, RecipeHolder<CraftingRecipe>> recipes = compoundPartRecipes(level, gearType);
        List<ForgeAction> out = new ArrayList<>();

        for (PartType type : SgRegistries.PART_TYPE) {
            if (type.is(PartTypes.NONE) || type.isUpgrade()) {
                continue;
            }

            PartInstance current = GearData.getPartOfType(gear, type);
            boolean required = gearItem.requiresPartOfType(type);

            if (current == null && !required && !type.isRemovable()) {
                continue;
            }

            Optional<? extends PartInstance> example = type.makeCompoundPart(gearType, Const.Materials.EXAMPLE);
            if (example.isEmpty() || !gearItem.supportsPart(gear, example.get())) {
                continue;
            }

            RecipeHolder<CraftingRecipe> recipe = recipes.get(type);
            if (recipe == null) {
                continue;
            }

            NonNullList<Ingredient> ingredients = recipe.value().getIngredients();
            int blueprintIndex = findBlueprintIngredient(ingredients);
            int blueprintSlot = blueprintIndex >= 0 ? findBlueprintSlot(ingredients.get(blueprintIndex), blueprints) : -1;
            boolean blueprintMissing = blueprintIndex >= 0 && blueprintSlot < 0;

            List<ForgeRequirement> requirements = new ArrayList<>();
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < ingredients.size(); i++) {
                if (i == blueprintIndex) {
                    continue;
                }
                requirements.add(ForgeRequirement.of(ingredients.get(i)));
                indices.add(i);
            }
            if (requirements.size() > REQUIREMENT_COUNT) {
                continue;
            }

            ItemStack icon = current != null ? current.getItem()
                    : example.map(PartInstance::getItem).orElse(ItemStack.EMPTY);
            ItemStack examplePart = example.map(PartInstance::getItem).orElse(ItemStack.EMPTY);
            String key = "part:" + typeName(type) + ":" + (current == null ? "install" : "replace");
            ForgeAction.Kind kind = current == null ? ForgeAction.Kind.INSTALL : ForgeAction.Kind.REPLACE;

            out.add(ForgeAction.part(kind,
                    recipe, icon, type, current, requirements, toIntArray(indices), blueprintIndex,
                    blueprintSlot, blueprintMissing, key));

            if (!examplePart.isEmpty()) {
                out.add(ForgeAction.part(kind, null, examplePart, type, current,
                        List.of(ForgeRequirement.finishedPart(type, List.of(examplePart))),
                        new int[]{0}, -1, -1, false, key + ":ready"));
            }

            if (current != null && type.isRemovable()) {
                out.add(ForgeAction.remove(type, current, current.getItem(),
                        "part:" + typeName(type) + ":remove"));
            }
        }

        return out;
    }

    private static Map<PartType, RecipeHolder<CraftingRecipe>> compoundPartRecipes(Level level, GearType gearType) {
        Map<PartType, RecipeHolder<CraftingRecipe>> exact = new HashMap<>();
        Map<PartType, RecipeHolder<CraftingRecipe>> generic = new HashMap<>();
        Map<PartType, RecipeHolder<CraftingRecipe>> loose = new HashMap<>();
        for (RecipeHolder<CraftingRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            if (!(holder.value() instanceof ShapelessCompoundPartRecipe recipe)) {
                continue;
            }
            ItemStack result = recipeResult(level, recipe);
            if (result.isEmpty() || !(result.getItem() instanceof CompoundPartItem partItem)) {
                continue;
            }
            GearType recipeType = partItem.getGearType();
            if (recipeType == gearType) {
                exact.putIfAbsent(partItem.getPartType(), holder);
            } else if (recipeType == GearTypes.NONE.get()) {
                generic.putIfAbsent(partItem.getPartType(), holder);
            } else if (recipeType.matches(gearType)) {
                loose.putIfAbsent(partItem.getPartType(), holder);
            }
        }
        Map<PartType, RecipeHolder<CraftingRecipe>> merged = new HashMap<>(generic);
        merged.putAll(loose);
        merged.putAll(exact);
        return merged;
    }

    private static List<ForgeAction> convertActions(Level level, ItemStack workpiece) {
        if (workpiece.isEmpty() || workpiece.getItem() instanceof GearItem) {
            return List.of();
        }
        if (!Config.Common.isLoaded() || !Config.Common.allowConversionRecipes.getAsBoolean()) {
            return List.of();
        }

        for (RecipeHolder<CraftingRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            if (!(holder.value() instanceof ConversionRecipe recipe)) {
                continue;
            }
            boolean ingredientMatches = false;
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.test(workpiece)) {
                    ingredientMatches = true;
                    break;
                }
            }
            if (!ingredientMatches) {
                continue;
            }

            CraftingInput input = CraftingInput.of(1, 1, List.of(workpiece.copyWithCount(1)));
            ItemStack preview = assemble(level, recipe, input);
            if (preview.isEmpty()) {
                continue;
            }
            return List.of(ForgeAction.convert(holder, preview));
        }
        return List.of();
    }

    private static final Map<GearType, List<PartType>> SUPPORTED_PART_TYPES = new HashMap<>();

    public static List<PartType> supportedPartTypes(GearType gearType) {
        lockSilentGear();
        try {
            return SUPPORTED_PART_TYPES.computeIfAbsent(gearType, GearForgeLogic::computeSupportedPartTypes);
        } finally {
            unlockSilentGear();
        }
    }

    private static List<PartType> computeSupportedPartTypes(GearType gearType) {
        GearItem item = GearType.getItem(gearType);
        if (item == null) {
            return List.of();
        }
        ItemStack gear = new ItemStack(item);
        List<PartType> out = new ArrayList<>();
        for (PartType type : SgRegistries.PART_TYPE) {
            if (type.is(PartTypes.NONE) || type.is(PartTypes.MAIN)) {
                continue;
            }
            List<GearPart> parts = SgRegistries.PART.getPartsOfType(type);
            if (parts.isEmpty()) {
                continue;
            }
            if (item.supportsPart(gear, PartInstance.of(parts.getFirst()))) {
                out.add(type);
            }
        }
        return List.copyOf(out);
    }

    public record ForgeOutput(ItemStack result, List<ItemStack> returned) {
        public static final ForgeOutput EMPTY = new ForgeOutput(ItemStack.EMPTY, List.of());

        public boolean isEmpty() {
            return result.isEmpty();
        }
    }

    @Nullable
    public static ForgeOutput produce(Level level, ForgeAction action, ItemStack workpiece,
                                      List<ItemStack> blueprints, List<ItemStack> requirementStacks,
                                      @Nullable Player player) {
        if (level == null || action == null) {
            return null;
        }
        lockSilentGear();
        try {
            return switch (action.kind) {
                case CRAFT -> {
                    ItemStack result = craftResult(level, action, blueprints, requirementStacks);
                    yield result.isEmpty() ? null : new ForgeOutput(result, List.of());
                }
                case CONVERT -> {
                    if (workpiece.isEmpty() || action.recipe == null
                            || !(action.recipe.value() instanceof ConversionRecipe recipe)) {
                        yield null;
                    }
                    CraftingInput input = CraftingInput.of(1, 1, List.of(workpiece.copyWithCount(1)));
                    ItemStack result = assemble(level, recipe, input);
                    yield result.isEmpty() ? null : new ForgeOutput(result, List.of());
                }
                case REPLACE, INSTALL -> action.recipe == null
                        ? installReadyPart(action, workpiece, requirementStacks, player)
                        : swapResult(level, action, workpiece, blueprints, requirementStacks, player);
                case REMOVE -> removeResult(workpiece, action, player);
            };
        } finally {
            unlockSilentGear();
        }
    }

    private static ItemStack craftResult(Level level, ForgeAction action, List<ItemStack> blueprints,
                                         List<ItemStack> requirementStacks) {
        if (action.recipe == null) {
            return ItemStack.EMPTY;
        }
        CraftingInput input = buildInput(action, blueprints, requirementStacks);
        if (input == null) {
            return ItemStack.EMPTY;
        }
        CraftingRecipe recipe = action.recipe.value();
        if (!recipe.matches(input, level)) {
            return ItemStack.EMPTY;
        }
        return assemble(level, recipe, input);
    }

    @Nullable
    private static ForgeOutput swapResult(Level level, ForgeAction action, ItemStack gear,
                                          List<ItemStack> blueprints, List<ItemStack> requirementStacks,
                                          @Nullable Player player) {
        if (gear.isEmpty() || action.partType == null) {
            return null;
        }
        ItemStack partStack = craftResult(level, action, blueprints, requirementStacks);
        if (partStack.isEmpty()) {
            return null;
        }
        PartInstance newPart = PartInstance.from(partStack);
        if (newPart == null || !newPart.getType().equals(action.partType)) {
            return null;
        }
        return applyPart(gear, action, newPart, player);
    }

    @Nullable
    private static ForgeOutput installReadyPart(ForgeAction action, ItemStack gear,
                                                List<ItemStack> requirementStacks,
                                                @Nullable Player player) {
        if (gear.isEmpty() || action.partType == null || requirementStacks.isEmpty()) {
            return null;
        }
        ItemStack stack = requirementStacks.getFirst();
        if (stack.isEmpty()) {
            return null;
        }
        PartInstance newPart = PartInstance.from(stack);
        if (newPart == null || !newPart.getType().equals(action.partType)) {
            return null;
        }
        if (gear.getItem() instanceof GearItem gearItem && !gearItem.supportsPart(gear, newPart)) {
            return null;
        }
        return applyPart(gear, action, newPart, player);
    }

    private static ForgeOutput applyPart(ItemStack gear, ForgeAction action, PartInstance newPart,
                                         @Nullable Player player) {
        ItemStack result = gear.copy();
        List<PartInstance> parts = new ArrayList<>(GearData.getConstruction(gear).parts());
        List<PartInstance> existing = new ArrayList<>(GearData.getConstruction(gear).parts().getPartsOfType(action.partType));
        List<ItemStack> returned = new ArrayList<>();
        if (!existing.isEmpty() && existing.size() >= action.partType.maxPerItem()) {
            PartInstance old = existing.getFirst();
            parts.remove(old);
            old.onRemoveFromGear(result);
            if (!Config.Common.destroySwappedParts.get()) {
                returned.add(old.getItem());
            }
        }
        parts.add(newPart);
        GearData.writeConstructionParts(result, parts);
        GearData.recalculateGearData(result, player);
        newPart.onAddToGear(result);
        return new ForgeOutput(result, returned);
    }

    @Nullable
    private static ForgeOutput removeResult(ItemStack gear, ForgeAction action, @Nullable Player player) {
        if (gear.isEmpty() || action.partType == null) {
            return null;
        }
        PartInstance old = GearData.getPartOfType(gear, action.partType);
        ItemStack result = gear.copy();
        if (!GearData.removeFirstPartOfType(result, action.partType)) {
            return null;
        }
        GearData.recalculateGearData(result, player);
        List<ItemStack> returned = old == null ? List.of() : List.of(old.getItem());
        return new ForgeOutput(result, returned);
    }

    @Nullable
    private static CraftingInput buildInput(ForgeAction action, List<ItemStack> blueprints,
                                            List<ItemStack> requirementStacks) {
        CraftingRecipe recipe = action.recipe.value();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        List<ItemStack> grid = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));

        if (action.blueprintIngredientIndex >= 0) {
            if (action.blueprintSlotIndex < 0 || action.blueprintSlotIndex >= blueprints.size()) {
                return null;
            }
            ItemStack blueprint = blueprints.get(action.blueprintSlotIndex);
            if (blueprint.isEmpty()) {
                return null;
            }
            grid.set(action.blueprintIngredientIndex, blueprint.copyWithCount(1));
        }

        for (int i = 0; i < action.requirementIngredientIndex.length; i++) {
            if (i >= requirementStacks.size()) {
                return null;
            }
            ItemStack stack = requirementStacks.get(i);
            if (stack.isEmpty()) {
                return null;
            }
            int slot = action.requirementIngredientIndex[i];
            if (slot < 0 || slot > 8) {
                return null;
            }
            grid.set(slot, stack.copyWithCount(1));
        }

        return CraftingInput.of(3, 3, grid);
    }

    public static void consume(ForgeAction action, Container container) {
        if (action == null) {
            return;
        }
        if (action.blueprintIngredientIndex >= 0 && action.blueprintSlotIndex >= 0) {
            ItemStack blueprint = container.getItem(BLUEPRINT_START + action.blueprintSlotIndex);
            if (!blueprint.isEmpty() && !blueprint.hasCraftingRemainingItem()) {
                blueprint.shrink(1);
            }
        }
        if (action.needsMaterials()) {
            for (int i = 0; i < Math.min(action.requirements.size(), REQUIREMENT_COUNT); i++) {
                ItemStack stack = container.getItem(REQUIREMENT_START + i);
                if (!stack.isEmpty()) {
                    stack.shrink(1);
                }
            }
        }
        if (action.kind != ForgeAction.Kind.CRAFT) {
            ItemStack workpiece = container.getItem(WORKPIECE_SLOT);
            if (!workpiece.isEmpty()) {
                workpiece.shrink(1);
            }
        }
        container.setChanged();
    }

    private static int findBlueprintIngredient(NonNullList<Ingredient> ingredients) {
        for (int i = 0; i < ingredients.size(); i++) {
            if (ingredients.get(i).getCustomIngredient() instanceof BlueprintIngredient) {
                return i;
            }
        }
        return -1;
    }

    private static int findBlueprintSlot(Ingredient ingredient, List<ItemStack> library) {
        for (int i = 0; i < Math.min(BLUEPRINT_COUNT, library.size()); i++) {
            ItemStack stack = library.get(i);
            if (!stack.isEmpty() && ingredient.test(stack)) {
                return i;
            }
        }
        return -1;
    }

    private static ItemStack recipeResult(Level level, CraftingRecipe recipe) {
        try {
            ItemStack result = recipe.getResultItem(level.registryAccess());
            if (result != null && !result.isEmpty()) {
                return result;
            }
        } catch (RuntimeException ignored) {

        }
        if (recipe instanceof ShapelessGearRecipe gearRecipe) {
            return new ItemStack(gearRecipe.getOutputItem().asItem());
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack assemble(Level level, CraftingRecipe recipe, CraftingInput input) {
        try {
            ItemStack result = recipe.assemble(input, level.registryAccess());
            if (result == null) {
                return ItemStack.EMPTY;
            }
            if (!result.isEmpty() && GearDataRepair.needsRepair(result)) {
                ItemStack retry = recipe.assemble(input, level.registryAccess());
                if (retry != null && !retry.isEmpty() && !GearDataRepair.needsRepair(retry)) {
                    return retry;
                }
            }
            return result;
        } catch (RuntimeException e) {
            return ItemStack.EMPTY;
        }
    }

    private static String typeName(PartType type) {
        var key = SgRegistries.PART_TYPE.getKey(type);
        return key == null ? "unknown" : key.toString();
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] out = new int[list.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = list.get(i);
        }
        return out;
    }
}
