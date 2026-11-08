package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.Utils.GearCombatUtils;
import com.github.L_Ender.cataclysm.entity.projectile.Brontes_Entity;
import com.github.L_Ender.cataclysm.init.ModDataComponents;
import com.github.L_Ender.cataclysm.items.Brontes;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearBrontesItem extends Brontes implements GearWeapon {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;

    public RFGearBrontesItem(Supplier<GearType> gearType, Properties properties) {
        super(GearHelper.DEFAULT_DUMMY_TIER,
                GearHelper.getBaseItemProperties()
                .fireResistant()
        );
        this.gearType = gearType;
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
    public float getRepairModifier(ItemStack stack) {
        return getGearType().armorDurabilityMultiplier();
    }

    @Override
    public ItemColor getItemColors() {
        return ColorHandlers::getShieldColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        GearClientHelper.addInformation(stack, context, tooltip, flag);
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (GearHelper.isBroken(stack)) {
            return 0.25F;
        }
        return GearHelper.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (GearHelper.isBroken(stack)) {
            return false;
        }

        GearPropertiesData properties = GearData.getProperties(stack);
        TagKey<Block> toolBlockSet = getToolBlockSet(properties);
        return state.is(Objects.requireNonNullElse(toolBlockSet, BlockTags.MINEABLE_WITH_PICKAXE));

    }

    public TagKey<Block> getToolBlockSet(GearPropertiesData properties) {
        return BlockTags.MINEABLE_WITH_PICKAXE;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        if (GearHelper.isBroken(stack)) {
            return false;
        }
        return getGearType().canPerformAction(ability);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        return GearHelper.onBlockDestroyed(stack, level, state, pos, entity);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder, true);
        return builder.build();
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return GearHelper.getEnchantmentValue(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(getDurabilityStat());
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
        GearHelper.attemptDamage(stack, 1, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        GearHelper.inventoryTick(stack, level, entity, slot, selected);
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int i = this.getUseDuration(stack, entity) - timeLeft;
            float f = getPowerForTime(i);
            if (!((double) f < 0.5D)) {
                if (!level.isClientSide) {
                    if (stack.get(ModDataComponents.THROWN_HAMMER) == null && level.getWorldBorder().isWithinBounds(player.blockPosition())) {
                        float actualDamage = GearCombatUtils.getActualWeaponDamage(entity, stack);

                        Brontes_Entity brontes = new Brontes_Entity(level, player);
                        brontes.setBaseDamage(actualDamage);
                        brontes.setAreaDamage(actualDamage * 1.5F);
                        brontes.setStormDamage(actualDamage * 2.0F);
                        brontes.setOwner(player);
                        brontes.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);

                        if (level.addFreshEntity(brontes)) {
                            stack.set(ModDataComponents.THROWN_HAMMER, brontes.getUUID());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        GearHelper.postHurtEnemy(stack, target, attacker);
    }

}
