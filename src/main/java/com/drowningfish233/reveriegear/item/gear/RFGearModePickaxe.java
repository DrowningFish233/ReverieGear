package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.core.data.MiningModeData;
import com.drowningfish233.reveriegear.registry.RFDataComponents;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
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

public class RFGearModePickaxe extends GearPickaxeItem implements GearWeapon, IAoeTool {

    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;
    private static final int MAX_BLOCKS = 64;

    public enum MiningMode {
        SMALL_TUNNEL("small_tunnel"),
        SMALL_SQUARE("small_square"),
        LARGE_TUNNEL("large_tunnel"),
        MINING_TUNNEL("mining_tunnel"),
        ESCAPE_TUNNEL("escape_tunnel");

        private final String name;

        MiningMode(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public RFGearModePickaxe(Supplier<GearType> gearType) {
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                MiningMode currentMode = getMode(stack);
                MiningMode nextMode = getNextMode(currentMode);
                setMode(stack, nextMode);

                player.sendSystemMessage(Component.translatable("message.reveriegear.mode_switch",
                        Component.translatable("mode.reveriegear." + nextMode.getName()).withStyle(ChatFormatting.GOLD)));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        return super.use(level, player, hand);
    }

    public MiningMode getMode(ItemStack stack) {
        MiningModeData data = stack.get(RFDataComponents.MINING_MODE.get());
        if (data == null) {
            return MiningMode.SMALL_TUNNEL;
        }
        String modeName = data.mode();
        for (MiningMode mode : MiningMode.values()) {
            if (mode.getName().equals(modeName)) {
                return mode;
            }
        }
        return MiningMode.SMALL_TUNNEL;
    }

    private void setMode(ItemStack stack, MiningMode mode) {
        stack.set(RFDataComponents.MINING_MODE.get(), new MiningModeData(mode.getName()));
    }

    private MiningMode getNextMode(MiningMode current) {
        MiningMode[] modes = MiningMode.values();
        int nextIndex = (current.ordinal() + 1) % modes.length;
        return modes[nextIndex];
    }

    @Override
    public List<BlockPos> getExtraBlocks(Level world, @Nullable BlockHitResult rt, Player player, ItemStack stack) {
        if (player.isShiftKeyDown() || rt == null) {
            return new ArrayList<>();
        }

        List<BlockPos> positions = getSelectedBlocks(world, rt.getBlockPos(), rt.getDirection(),
                getMode(stack), stack, player);
        positions.removeIf(pos -> pos.equals(rt.getBlockPos()));
        return positions;
    }

    public List<BlockPos> getSelectedBlocks(Level world, BlockPos centerPos, Direction face,
                                            MiningMode mode, ItemStack stack, Player player) {
        List<BlockPos> positions = new ArrayList<>();
        BlockState centerState = world.getBlockState(centerPos);

        if (!isEffectiveOnBlock(stack, centerState, player)) {
            return positions;
        }
        if (centerState.getBlock() instanceof EntityBlock) {
            return positions;
        }

        switch (mode) {
            case SMALL_TUNNEL ->
                    getSmallTunnelBlocks(world, centerPos, face, stack, player, positions);
            case SMALL_SQUARE ->
                    getLargeTunnelBlocks(world, centerPos, face, stack, player, 1, positions);
            case LARGE_TUNNEL ->
                    getLargeTunnelBlocks(world, centerPos, face, stack, player, Integer.MAX_VALUE, positions);
            case MINING_TUNNEL ->
                    getDiagonalTunnelBlocks(world, centerPos, face, stack, player, -1, positions);
            case ESCAPE_TUNNEL ->
                    getDiagonalTunnelBlocks(world, centerPos, face, stack, player, 1, positions);
        }

        return positions;
    }

    private boolean isValidTarget(Level world, BlockPos pos, ItemStack stack, Player player) {
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) return false;
        if (state.getBlock() instanceof LiquidBlock) return false;
        if (state.getDestroySpeed(world, pos) < 0) return false;
        return isEffectiveOnBlock(stack, state, player);
    }

    private void getSmallTunnelBlocks(Level world, BlockPos centerPos, Direction face,
                                      ItemStack stack, Player player, List<BlockPos> positions) {
        for (int i = 0; i < MAX_BLOCKS; i++) {
            BlockPos pos = centerPos.relative(face, -i);
            if (!isValidTarget(world, pos, stack, player)) break;
            positions.add(pos);
        }
    }

    private void getLargeTunnelBlocks(Level world, BlockPos centerPos, Direction face,
                                      ItemStack stack, Player player, int maxDepth,
                                      List<BlockPos> positions) {
        BlockPos basePos = centerPos;
        positions.add(basePos);

        for (int depth = 0; depth < maxDepth && positions.size() < MAX_BLOCKS; depth++) {
            int size = positions.size();

            outer:
            for (int a = -1; a <= 1; a++) {
                for (int b = -1; b <= 1; b++) {
                    if (depth > 0 || a != 0 || b != 0) {
                        BlockPos pos = switch (face.getAxis()) {
                            case X -> basePos.offset(0, a, b);
                            case Y -> basePos.offset(a, 0, b);
                            case Z -> basePos.offset(a, b, 0);
                        };

                        if (isValidTarget(world, pos, stack, player)) {
                            positions.add(pos);
                            if (positions.size() >= MAX_BLOCKS) {
                                break outer;
                            }
                        }
                    }
                }
            }

            if (positions.size() == size) break;

            basePos = basePos.relative(face.getOpposite());
        }
    }

    private void getDiagonalTunnelBlocks(Level world, BlockPos centerPos, Direction face,
                                         ItemStack stack, Player player, int yDirection,
                                         List<BlockPos> positions) {
        Direction direction = face.getAxis().isVertical()
                ? player.getDirection().getOpposite()
                : face;

        for (int i = 0; i < MAX_BLOCKS; i++) {
            BlockPos pos = centerPos.offset(-direction.getStepX() * i, yDirection * i, -direction.getStepZ() * i);
            if (!isValidTarget(world, pos, stack, player)) break;
            positions.add(pos);
        }
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

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, tooltipContext, tooltip, flagIn);

        MiningMode mode = getMode(stack);
        tooltip.add(Component.translatable("tooltip.reveriegear.current_mode")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.translatable("mode.reveriegear." + mode.getName()).withStyle(ChatFormatting.AQUA)));
        tooltip.add(Component.translatable("tooltip.reveriegear.mode." + mode.getName() + ".desc")
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.reveriegear.switch_mode")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.keybind("key.sneak").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" + ").withStyle(ChatFormatting.GRAY))
                .append(Component.keybind("key.use").withStyle(ChatFormatting.YELLOW)));

        tooltip.add(Component.translatable("tooltip.reveriegear.mode_preview")
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.reveriegear.range_info", MAX_BLOCKS)
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
