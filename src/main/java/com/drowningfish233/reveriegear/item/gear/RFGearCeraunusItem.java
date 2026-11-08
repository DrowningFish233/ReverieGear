package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.Utils.GearCombatUtils;
import com.drowningfish233.reveriegear.Utils.gear.RFGearCombatStats;
import com.github.L_Ender.cataclysm.entity.effect.Wave_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Player_Ceraunus_Entity;
import com.github.L_Ender.cataclysm.init.ModDataComponents;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.github.L_Ender.cataclysm.items.Ceraunus;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearCeraunusItem extends Ceraunus implements GearWeapon {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;

    public RFGearCeraunusItem(Supplier<GearType> gearType, Properties properties) {
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        GearClientHelper.addInformation(stack, context, tooltip, flag);
        super.appendHoverText(stack, context, tooltip, flag);
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
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
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
                    float actualDamage = GearCombatUtils.getActualWeaponDamage(entity, stack);
                    int cooldown = getCooldown(entity, stack);

                    float yawRadians = (float) (Math.toRadians(90 + player.getYRot()));
                    double vecX = Math.cos(yawRadians);
                    double vecZ = Math.sin(yawRadians);
                    double vec = 2.0D;
                    double spawnX = entity.getX() + vecX * vec;
                    double spawnY = entity.getY();
                    double spawnZ = entity.getZ() + vecZ * vec;
                    int numberOfWaves = 4;
                    float angleStep = 25.0f;
                    double firstAngleOffset = (numberOfWaves - 1) / 2.0 * angleStep;

                    if (entity.isShiftKeyDown()) {
                        player.getCooldowns().addCooldown(this, cooldown);
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                ModSounds.HEAVY_SMASH.get(), SoundSource.PLAYERS, 0.6F, 1.0F);

                        float waveDamage = actualDamage * 0.8F;

                        for (int k = 0; k < numberOfWaves; k++) {
                            double angle = player.getYRot() - firstAngleOffset + (k * angleStep);
                            double rad = Math.toRadians(angle);
                            double dx = -Math.sin(rad);
                            double dz = Math.cos(rad);

                            Wave_Entity waveEntity = new Wave_Entity(level, entity, 60, waveDamage);
                            waveEntity.setOwner(player);
                            waveEntity.setPos(spawnX, spawnY, spawnZ);
                            waveEntity.setState(1);
                            waveEntity.setYRot(-(float) (Mth.atan2(dx, dz) * (180F / Math.PI)));
                            entity.level().addFreshEntity(waveEntity);
                        }
                    } else {
                        if (stack.get(ModDataComponents.THROWN_ANCHOR) == null && level.getWorldBorder().isWithinBounds(player.blockPosition())) {
                            Player_Ceraunus_Entity launchedBlock = new Player_Ceraunus_Entity(level, player);
                            launchedBlock.setOwner(player);
                            launchedBlock.setBaseDamage(actualDamage);
                            launchedBlock.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
                            if (level.addFreshEntity(launchedBlock)) {
                                stack.set(ModDataComponents.THROWN_ANCHOR, launchedBlock.getUUID());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (GearHelper.isBroken(stack)) {
            return InteractionResultHolder.pass(stack);
        }

        if (stack.get(ModDataComponents.THROWN_ANCHOR) == null && level.getWorldBorder().isWithinBounds(player.blockPosition())) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        } else {
            return InteractionResultHolder.fail(stack);
        }
    }

    private int getCooldown(LivingEntity entity, ItemStack stack) {
        return RFGearCombatStats.scaleMeleeCooldown(entity, stack, 150, 0.7F);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
        GearHelper.attemptDamage(stack, 1, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        GearHelper.postHurtEnemy(stack, target, attacker);
    }

}
