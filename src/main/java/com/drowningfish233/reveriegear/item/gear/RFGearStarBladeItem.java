package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.handler.TianshaMaxHealthHandler;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearStarBladeItem extends Item implements GearWeapon {

    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private static final double CLEAVE_RANGE = 4.0D;

    private final Supplier<GearType> gearType;

    public RFGearStarBladeItem(Supplier<GearType> gearType) {
        super(GearHelper.getBaseItemProperties()
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
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        GearClientHelper.addInformation(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.reveriegear.tiansha_star_blade.desc")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.reveriegear.tiansha_star_blade.desc2")
                .withStyle(ChatFormatting.DARK_PURPLE));
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
    public UseAnim getUseAnimation(ItemStack stack) {
        if (GearHelper.isBroken(stack)) {
            return UseAnim.NONE;
        }
        return UseAnim.SPYGLASS;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (GearHelper.isBroken(stack)) {
            return InteractionResultHolder.pass(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(stack);
        }

        performDetonate(level, player, stack);

        int cooldown = getDetonateCooldown(stack);
        player.getCooldowns().addCooldown(this, cooldown);

        return InteractionResultHolder.consume(stack);
    }

    private void performDetonate(Level level, Player player, ItemStack stack) {
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(CLEAVE_RANGE, CLEAVE_RANGE / 2, CLEAVE_RANGE),
                entity -> entity != player
                        && entity.isAlive()
                        && entity.position().distanceTo(player.position()) <= CLEAVE_RANGE
                        && isInFrontOf(player, entity)
        );

        for (LivingEntity target : targets) {
            float reduction = TianshaMaxHealthHandler.getCurrentReduction(target);
            if (reduction > 0) {
                TianshaMaxHealthHandler.detonateReduction(target, player);
                spawnDetonateEffect(target, reduction);

                GearHelper.attemptDamage(stack, 1, player, InteractionHand.MAIN_HAND);
            }
        }
    }

    private void spawnDetonateEffect(LivingEntity target, float amount) {
        if (!(target.level() instanceof ServerLevel level)) return;

        int count = Math.min(20, Math.max(5, (int) (amount / 2)));

        for (int i = 0; i < count; i++) {
            double angle = (i / (double) count) * 2 * Math.PI;
            double radius = 1.5;

            double x = target.getX() + Math.cos(angle) * radius;
            double z = target.getZ() + Math.sin(angle) * radius;
            double y = target.getY() + 0.5;

            level.sendParticles(ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get(),
                    x, y, z, 1, 0, 0.1, 0, 0);
        }

        for (int i = 0; i < 10; i++) {
            double x = target.getX() + (target.getRandom().nextDouble() - 0.5) * 2.0;
            double y = target.getY() + target.getRandom().nextDouble() * 2.0 + 0.5;
            double z = target.getZ() + (target.getRandom().nextDouble() - 0.5) * 2.0;

            level.sendParticles(ParticleRegistry.CLEANSE_PARTICLE.get(),
                    x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private boolean isInFrontOf(Player player, LivingEntity target) {
        Vec3 toTarget = target.position().subtract(player.position()).normalize();
        Vec3 lookVec = player.getLookAngle();
        return toTarget.dot(lookVec) > 0.3;
    }
    private int getDetonateCooldown(ItemStack stack) {
        float attackSpeed = GearData.getProperties(stack)
                .getNumber(GearProperties.ATTACK_SPEED, 1.7F);
        int cooldown = Math.round(120 * (1.7F / Math.max(0.1F, attackSpeed)));
        return Math.min(Math.max(cooldown, 40), 600);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
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

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        GearHelper.postHurtEnemy(stack, target, attacker);
    }

}
