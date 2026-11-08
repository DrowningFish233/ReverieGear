package com.drowningfish233.reveriegear.Utils;

import com.drowningfish233.reveriegear.core.GearPartSyncGuard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;

import java.util.Collection;
import java.util.Optional;

public final class GearDataRepair {

    private GearDataRepair() {
    }

    public static boolean needsRepair(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof GearItem)) {
            return false;
        }
        GearConstructionData data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
        return data == null || data.isExample() || data.parts().isEmpty() || hasExampleMaterial(data)
                || isExampleMarked(stack);
    }

    public static void repairWrittenParts(ItemStack gear, Collection<PartInstance> parts) {
        if (gear == null || gear.isEmpty() || parts == null || parts.isEmpty() || !(gear.getItem() instanceof GearItem)) {
            return;
        }
        GearConstructionData data = gear.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data != null && !data.parts().isEmpty()) {
            return;
        }
        PartList preserved = PartList.of(parts);
        if (preserved.isEmpty()) {
            return;
        }
        GearConstructionData repaired;
        GearPartSyncGuard.beginForce();
        try {
            repaired = new GearConstructionData(preserved, false,
                    data != null ? data.brokenCount() : 0, data != null ? data.repairedCount() : 0);
        } finally {
            GearPartSyncGuard.endForce();
        }
        if (repaired.parts().isEmpty()) {
            return;
        }
        gear.set(SgDataComponents.GEAR_CONSTRUCTION, repaired);
        try {
            GearData.recalculateGearData(gear, null);
        } catch (RuntimeException e) {
            if (data != null) {
                gear.set(SgDataComponents.GEAR_CONSTRUCTION, data);
            } else {
                gear.remove(SgDataComponents.GEAR_CONSTRUCTION);
            }
        }
    }

    public static ItemStack repairFromGrid(ItemStack result, Level level, CraftingInput input) {
        if (result == null || result.isEmpty() || level == null || input == null || input.isEmpty()) {
            return result;
        }
        if (!needsRepair(result)) {
            return result;
        }
        try {
            Optional<RecipeHolder<CraftingRecipe>> found = level.getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, input, level);
            if (found.isPresent()) {
                ItemStack candidate = found.get().value().assemble(input, level.registryAccess());
                if (candidate != null && !candidate.isEmpty() && !needsRepair(candidate)) {
                    return candidate;
                }
            }
        } catch (RuntimeException ignored) {
        }
        return result;
    }

    public static boolean hasExampleMaterial(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof GearItem)) {
            return false;
        }
        return hasExampleMaterial(stack.get(SgDataComponents.GEAR_CONSTRUCTION));
    }

    private static boolean hasExampleMaterial(GearConstructionData data) {
        if (data == null) {
            return false;
        }
        ResourceLocation example = Const.Materials.EXAMPLE.getId();
        for (PartInstance part : data.parts()) {
            if (part == null || !part.isValid()) {
                continue;
            }
            for (MaterialInstance material : part.getMaterials()) {
                if (material != null && example.equals(material.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isExampleMarked(ItemStack stack) {
        GearConstructionData data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data != null && data.isExample()) {
            return true;
        }
        return Boolean.TRUE.equals(stack.get(SgDataComponents.GEAR_IS_EXAMPLE.get()));
    }
}
