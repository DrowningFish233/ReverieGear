package com.drowningfish233.reveriegear.block.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.PartInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public final class ForgeAction {

    public enum Kind {

        CRAFT,

        CONVERT,

        REPLACE,

        INSTALL,

        REMOVE
    }

    public static final Comparator<ForgeAction> ORDER = Comparator
            .comparingInt(ForgeAction::groupRank)
            .thenComparingInt((ForgeAction a) -> a.producesGear ? 1 : 0)
            .thenComparingInt(a -> a.requirements.size())
            .thenComparing(a -> a.sortKey);

    private int groupRank() {
        return switch (kind) {
            case CRAFT -> 0;
            case REMOVE -> 1;
            case INSTALL -> 2;
            case REPLACE -> 3;
            case CONVERT -> 4;
        };
    }

    public final Kind kind;
    public final ItemStack icon;
    @Nullable
    public final RecipeHolder<CraftingRecipe> recipe;
    @Nullable
    public final PartType partType;
    @Nullable
    public final PartInstance installedPart;
    public final List<ForgeRequirement> requirements;

    public final int[] requirementIngredientIndex;

    public final int blueprintIngredientIndex;

    public final int blueprintSlotIndex;
    public final boolean blueprintMissing;

    public final boolean producesGear;
    final String sortKey;

    private ForgeAction(Kind kind, ItemStack icon, @Nullable RecipeHolder<CraftingRecipe> recipe,
                        @Nullable PartType partType, @Nullable PartInstance installedPart,
                        List<ForgeRequirement> requirements, int[] requirementIngredientIndex,
                        int blueprintIngredientIndex, int blueprintSlotIndex, boolean blueprintMissing,
                        boolean producesGear, String sortKey) {
        this.kind = kind;
        this.icon = icon;
        this.recipe = recipe;
        this.partType = partType;
        this.installedPart = installedPart;
        this.requirements = List.copyOf(requirements);
        this.requirementIngredientIndex = requirementIngredientIndex;
        this.blueprintIngredientIndex = blueprintIngredientIndex;
        this.blueprintSlotIndex = blueprintSlotIndex;
        this.blueprintMissing = blueprintMissing;
        this.producesGear = producesGear;
        this.sortKey = sortKey;
    }

    static ForgeAction craft(RecipeHolder<CraftingRecipe> recipe, ItemStack icon,
                             List<ForgeRequirement> requirements, int[] requirementIngredientIndex,
                             int blueprintIngredientIndex, int blueprintSlotIndex,
                             boolean producesGear, String sortKey) {
        return new ForgeAction(Kind.CRAFT, icon, recipe, null, null, requirements,
                requirementIngredientIndex, blueprintIngredientIndex, blueprintSlotIndex, false,
                producesGear, sortKey);
    }

    static ForgeAction convert(RecipeHolder<CraftingRecipe> recipe, ItemStack icon) {
        return new ForgeAction(Kind.CONVERT, icon, recipe, null, null, List.of(),
                new int[0], -1, -1, false, true, "convert");
    }

    static ForgeAction part(Kind kind, RecipeHolder<CraftingRecipe> recipe, ItemStack icon, PartType partType,
                           @Nullable PartInstance installedPart, List<ForgeRequirement> requirements,
                           int[] requirementIngredientIndex, int blueprintIngredientIndex,
                           int blueprintSlotIndex, boolean blueprintMissing, String sortKey) {
        return new ForgeAction(kind, icon, recipe, partType, installedPart, requirements,
                requirementIngredientIndex, blueprintIngredientIndex, blueprintSlotIndex, blueprintMissing,
                false, sortKey);
    }

    static ForgeAction remove(PartType partType, PartInstance installedPart, ItemStack icon, String sortKey) {
        return new ForgeAction(Kind.REMOVE, icon, null, partType, installedPart, List.of(),
                new int[0], -1, -1, false, false, sortKey);
    }

    public boolean needsMaterials() {
        return kind != Kind.REMOVE;
    }

    public boolean assembly() {
        return kind == Kind.CRAFT && blueprintIngredientIndex < 0;
    }

    public boolean isModify() {
        return kind == Kind.REPLACE || kind == Kind.INSTALL || kind == Kind.REMOVE;
    }
}
