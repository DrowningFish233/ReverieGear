package com.drowningfish233.reveriegear.block.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.crafting.ingredient.IGearIngredient;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ForgeRequirement {

    private static final int MAX_EXAMPLES = 48;

    private final Predicate<ItemStack> predicate;
    private final Supplier<List<ItemStack>> exampleSource;
    @Nullable
    private final PartType partType;
    @Nullable
    private final PartMaterialIngredient materialIngredient;

    private List<ItemStack> examples;
    @Nullable
    private Boolean silentGearMaterial;

    private ForgeRequirement(Predicate<ItemStack> predicate, Supplier<List<ItemStack>> exampleSource,
                             @Nullable PartType partType, @Nullable PartMaterialIngredient materialIngredient) {
        this.predicate = predicate;
        this.exampleSource = exampleSource;
        this.partType = partType;
        this.materialIngredient = materialIngredient;
    }

    public static ForgeRequirement of(Ingredient ingredient) {
        var custom = ingredient.getCustomIngredient();
        PartType type = custom instanceof IGearIngredient gearIngredient ? gearIngredient.getPartType() : null;
        PartMaterialIngredient material = custom instanceof PartMaterialIngredient mat ? mat : null;
        return new ForgeRequirement(ingredient::test, () -> cap(ingredient.getItems()), type, material);
    }

    public static ForgeRequirement finishedPart(PartType type, List<ItemStack> examples) {
        return new ForgeRequirement(
                stack -> {
                    PartInstance part = PartInstance.from(stack);
                    return part != null && part.getType().equals(type);
                },
                () -> examples,
                type,
                null);
    }

    private static List<ItemStack> cap(ItemStack[] items) {
        List<ItemStack> out = new ArrayList<>(Math.min(items.length, MAX_EXAMPLES));
        for (ItemStack stack : items) {
            if (stack.isEmpty()) {
                continue;
            }
            out.add(stack);
            if (out.size() >= MAX_EXAMPLES) {
                break;
            }
        }
        return out;
    }

    public boolean test(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        GearForgeLogic.lockSilentGear();
        try {
            return predicate.test(stack);
        } finally {
            GearForgeLogic.unlockSilentGear();
        }
    }

    public List<ItemStack> examples() {
        GearForgeLogic.lockSilentGear();
        try {
            if (examples == null) {
                examples = exampleSource.get();
            }
            return examples;
        } finally {
            GearForgeLogic.unlockSilentGear();
        }
    }

    public boolean silentGearMaterial() {
        GearForgeLogic.lockSilentGear();
        try {
            if (silentGearMaterial == null) {
                if (materialIngredient != null) {
                    silentGearMaterial = Boolean.TRUE;
                } else {
                    silentGearMaterial = examples().stream().anyMatch(s -> MaterialInstance.from(s) != null);
                }
            }
            return silentGearMaterial;
        } finally {
            GearForgeLogic.unlockSilentGear();
        }
    }

    @Nullable
    public PartType partType() {
        return partType;
    }

    @Nullable
    public PartMaterialIngredient materialIngredient() {
        return materialIngredient;
    }
}
