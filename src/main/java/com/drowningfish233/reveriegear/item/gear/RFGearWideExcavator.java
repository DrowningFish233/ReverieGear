package com.drowningfish233.reveriegear.item.gear;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbility;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.item.gear.GearPickaxeItem;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.IAoeTool;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class RFGearWideExcavator extends GearPickaxeItem implements GearWeapon, IAoeTool {

    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;
    private static final int RADIUS = 2;
    private static final int RANGE = 5;

    public RFGearWideExcavator(Supplier<GearType> gearType) {
        super(gearType);
        this.gearType = gearType;
    }

    @Override
    public TagKey<Block> getToolBlockSet(GearPropertiesData properties) {
        return SgTags.Blocks.MINEABLE_WITH_PAXEL;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    @Override
    public boolean isValidSlot(String slot) {
        return EquipmentSlot.MAINHAND.getName().equalsIgnoreCase(slot);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        if (GearHelper.isBroken(stack)) {
            return false;
        }
        return getGearType().canPerformAction(ability);
    }

    @Nullable
    @Override
    public HitResult rayTraceBlocks(Level world, Player player) {
        return getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
    }

    @Override
    public List<BlockPos> getExtraBlocks(Level world, @Nullable BlockHitResult rt, Player player, ItemStack stack) {
        List<BlockPos> positions = new ArrayList<>();

        if (player.isCrouching() || rt == null || rt.getBlockPos() == null || rt.getDirection() == null) {
            return positions;
        }

        BlockPos centerPos = rt.getBlockPos();
        BlockState centerState = world.getBlockState(centerPos);

        if (!isEffectiveOnBlock(stack, centerState, player)) {
            return positions;
        }

        Direction face = player.getDirection();

        for (int i = 0; i < RANGE; i++) {
            for (int j = -RADIUS; j <= RADIUS; j++) {
                for (int k = -RADIUS; k <= RADIUS; k++) {
                    BlockPos targetPos = centerPos.relative(face, i)
                            .relative(face.getClockWise(), j)
                            .relative(Direction.UP, k);

                    if (targetPos.equals(centerPos)) continue;

                    attemptAddExtraBlock(world, centerState, targetPos, stack, player, positions);
                }
            }
        }

        return positions;
    }

    @Override
    public boolean isEffectiveOnBlock(ItemStack stack, BlockState state, Player player) {
        boolean isCorrectTool = stack.getItem().isCorrectToolForDrops(stack, state);
        return isCorrectTool && stack.getDestroySpeed(state) > 1f;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return GearHelper.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        GearHelper.postHurtEnemy(stack, target, attacker);
    }

}
