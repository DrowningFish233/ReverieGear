package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.Utils.GearCombatUtils;
import com.drowningfish233.reveriegear.attachment.RFAttachments;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.client.util.GearColorUtils;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearGaleSpinner extends Item implements GearWeapon {

    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    public static final int MIN_WHIRL_HIT_INTERVAL = 4;
    public static final float ATTACK_SPEED_BASE = 3.0F;
    public static final int WHIRL_DURABILITY_PER_HIT = 1;

    private final Supplier<GearType> gearType;

    public RFGearGaleSpinner(Supplier<GearType> gearType) {
        super(GearHelper.getBaseItemProperties()
                .stacksTo(1)
                .durability(500)
                .fireResistant());
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
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
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColors() {
        return RFGearGaleSpinner::getColor;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        int color;
        switch (tintIndex) {
            case 0 -> color = GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.ROD.get());
            case 1 -> color = GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.MAIN.get());
            default -> color = -1;
        }
        return color;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder, true);
        return builder.build();
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
    public UseAnim getUseAnimation(ItemStack stack) {
        if (GearHelper.isBroken(stack)) {
            return UseAnim.NONE;
        }
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return Integer.MAX_VALUE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (GearHelper.isBroken(stack)) {
            return InteractionResultHolder.pass(stack);
        }

        player.startUsingItem(usedHand);

        if (!level.isClientSide) {
            player.setData(RFAttachments.SPINNING.get(), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.8F);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseDuration) {
        if (!(living instanceof Player player)) return;
        if (GearHelper.isBroken(stack)) {
            if (!level.isClientSide) player.stopUsingItem();
            return;
        }

        if (level.isClientSide) return;

        int hitInterval = getWhirlHitInterval(player);
        if (remainingUseDuration % hitInterval != 0) {
            return;
        }

        GearHelper.attemptDamage(stack, WHIRL_DURABILITY_PER_HIT, player,
                player.getUsedItemHand());

        float actualDamage = GearCombatUtils.getActualWeaponDamage(player, stack);
        float damage = actualDamage * 0.25F;

        double radius = getWhirlRadius(player);

        AABB area = player.getBoundingBox().inflate(radius, 1.0D, radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive() && !e.isAlliedTo(player));

        for (LivingEntity target : targets) {
            target.hurt(player.damageSources().playerAttack(player), damage);
            double dx = target.getX() - player.getX();
            double dz = target.getZ() - player.getZ();
            double dist = Math.max(0.01D, Math.sqrt(dx * dx + dz * dz));
            target.knockback(0.4F, -dx / dist, -dz / dist);
        }

        if (!targets.isEmpty()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.8F, 1.2F);
        }
    }

    private static int getWhirlHitInterval(Player player) {
        double attackSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED.getDelegate());
        if (attackSpeed <= 0) {
            return 20;
        }
        int interval = (int) Math.round(ATTACK_SPEED_BASE / attackSpeed * MIN_WHIRL_HIT_INTERVAL);
        return Math.max(MIN_WHIRL_HIT_INTERVAL, interval);
    }

    private static double getWhirlRadius(Player player) {
        double reach = player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE.getDelegate());
        return Math.max(1.0D, reach / 2.0D);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        if (living instanceof Player player) {
            if (!level.isClientSide) {
                player.setData(RFAttachments.SPINNING.get(), false);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.PLAYERS, 0.6F, 1.0F);
            }
        }
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        if (entity instanceof Player player) {
            if (!player.level().isClientSide) {
                player.setData(RFAttachments.SPINNING.get(), false);
            }
        }
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
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount,
                                                   @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity,
                              int slot, boolean selected) {
        GearHelper.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack,
                                               boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        GearClientHelper.addInformation(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.reveriegear.gale_spinner.desc")
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.reveriegear.gale_spinner.desc2")
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float gearSpeed = GearHelper.getDestroySpeed(stack, state);
        return state.is(BlockTags.SWORD_EFFICIENT) ? Math.max(15.0F, gearSpeed) : gearSpeed;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        if (GearHelper.isBroken(stack)) {
            return false;
        }
        return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(ability);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }
}
