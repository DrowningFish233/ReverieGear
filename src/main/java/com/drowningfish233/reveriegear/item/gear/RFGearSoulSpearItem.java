package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.Utils.GearCombatUtils;
import com.drowningfish233.reveriegear.Utils.gear.RFGearCombatStats;
import com.github.L_Ender.cataclysm.Attachment.RenderRushAttachment;
import com.github.L_Ender.cataclysm.entity.projectile.Phantom_Halberd_Entity;
import com.github.L_Ender.cataclysm.init.ModDataAttachments;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.items.Soul_Render;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
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

public class RFGearSoulSpearItem extends Soul_Render implements GearWeapon {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;

    public RFGearSoulSpearItem(Supplier<GearType> gearType, Properties properties) {
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
        return EquipmentSlot.MAINHAND.getName().equalsIgnoreCase(slot)
                || EquipmentSlot.OFFHAND.getName().equalsIgnoreCase(slot);
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
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        if (living instanceof Player player) {
            int i = this.getUseDuration(stack, living) - timeLeft;

            float actualDamage = GearCombatUtils.getActualWeaponDamage(living, stack);

            if (living.isShiftKeyDown()) {
                int cooldown = getCooldown(player, stack);
                strikeWindmillHalberd(level, player, 7, 5, 1.0, 1.0, 0.2, 1, actualDamage);
                if (!level.isClientSide) {
                    player.getCooldowns().addCooldown(this, cooldown);
                }
            } else {
                int t = Mth.clamp(i, 0, 60);
                if (t > 0) {
                    float f = 0.1F * t;
                    Vec3 vec3 = player.getDeltaMovement().add(
                            player.getViewVector(1.0F).normalize().multiply(f, f * 0.15F, f)
                    );
                    living.setDeltaMovement(vec3.add(0, living.onGround() ? 0.2F : 0, 0));

                    RenderRushAttachment charge = living.getData(ModDataAttachments.RENDER_RUSH_ATTACHMENT);
                    charge.setRush(true);
                    charge.setTimer(t / 2);
                    charge.setdamage(actualDamage);

                    if (!level.isClientSide) {
                        int cooldown = getCooldown(player, stack);
                        player.getCooldowns().addCooldown(this, cooldown);
                    }
                }
            }
        }
    }

    private void strikeWindmillHalberd(Level level, LivingEntity player, int numberOfBranches, int particlesPerBranch, double initialRadius, double radiusIncrement, double curveFactor, int delay, float gearDamage) {
        float finalDamage = gearDamage * 2.0F;

        float angleIncrement = (float) (2 * Math.PI / numberOfBranches);
        for (int branch = 0; branch < numberOfBranches; ++branch) {
            float baseAngle = angleIncrement * branch;
            for (int i = 0; i < particlesPerBranch; ++i) {
                double currentRadius = initialRadius + i * radiusIncrement;
                float currentAngle = (float) (baseAngle + i * angleIncrement / initialRadius + (i * curveFactor));
                double xOffset = currentRadius * Math.cos(currentAngle);
                double zOffset = currentRadius * Math.sin(currentAngle);
                double spawnX = player.getX() + xOffset;
                double spawnY = player.getY() + 0.3D;
                double spawnZ = player.getZ() + zOffset;
                int d3 = delay * (i + 1);

                if (level.isClientSide) {
                    level.addParticle(ModParticle.PHANTOM_WING_FLAME.get(), spawnX, spawnY, spawnZ, 0, 0, 0);
                } else {
                    spawnHalberd(level, player, spawnX, spawnZ, player.getY() - 5, player.getY() + 3, currentAngle, d3, finalDamage);
                }
            }
        }
    }

    private void spawnHalberd(Level level, LivingEntity player, double x, double z, double minY, double maxY,
                              float rotation, int delay, float damage) {
        BlockPos blockpos = BlockPos.containing(x, maxY, z);
        boolean flag = false;
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = level.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(level, blockpos1, Direction.UP)) {
                if (!level.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = level.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(level, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (flag) {
            Phantom_Halberd_Entity halberd = new Phantom_Halberd_Entity(
                    level, x, (double) blockpos.getY() + d0, z, rotation, delay, player, damage
            );
            level.addFreshEntity(halberd);
        }
    }

    private int getCooldown(LivingEntity entity, ItemStack stack) {
        return RFGearCombatStats.scaleMeleeCooldown(entity, stack, 100, 1.05F);
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
