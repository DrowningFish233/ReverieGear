package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.Utils.gear.RFGearCombatStats;
import com.github.L_Ender.cataclysm.entity.projectile.Cursed_Sandstorm_Entity;
import com.github.L_Ender.cataclysm.items.Wrath_of_the_desert;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.silentchaos512.gear.api.item.GearRangedWeapon;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearSeekingBowItem extends Wrath_of_the_desert implements GearRangedWeapon {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get(),
            PartTypes.CORD.get()
    ));

    private final Supplier<GearType> gearType;

    public RFGearSeekingBowItem(Supplier<GearType> gearType, Properties properties) {
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
        tooltip.add(Component.translatable("item.reveriegear.seeking_bow.desc").withStyle(ChatFormatting.DARK_GREEN));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder, false);
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
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeleft) {
        if (living instanceof Player player) {
            Entity pointedEntity = getPlayerLookTarget(level, living);
            int i = this.getUseDuration(stack, living) - timeleft;
            float f = Wrath_of_the_desert.getPowerForTime(RFGearCombatStats.scaleDrawTicks(stack, i));
            if (!((double) f < 0.1D)) {
                if (!level.isClientSide) {
                    float gearDamage = RFGearCombatStats.getRangedDamage(stack, 7.5F);
                    float finalDamage = Math.max(0.1F, gearDamage * f);

                    float baseYaw = player.getYRot();
                    float pitch = player.getXRot();

                    float rightX = Mth.cos(baseYaw * ((float) Math.PI / 180F));
                    float rightZ = Mth.sin(baseYaw * ((float) Math.PI / 180F));
                    double rightOffset = -1;

                    for (int j = -1; j <= 1; j++) {
                        float yaw = baseYaw + (j * 15);

                        float directionX = -Mth.sin(yaw * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));
                        float directionY = -Mth.sin(pitch * ((float) Math.PI / 180F));
                        float directionZ = Mth.cos(yaw * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));

                        double theta = yaw * (Math.PI / 180);
                        double vecX = Math.cos(theta);
                        double vecZ = Math.sin(theta);
                        double x = player.getX() + vecX + (rightX * rightOffset);
                        double Z = player.getZ() + vecZ + (rightZ * rightOffset);

                        if (pointedEntity instanceof LivingEntity target && !target.isAlliedTo(living)) {
                            Cursed_Sandstorm_Entity largefireball = new Cursed_Sandstorm_Entity(player, directionX, directionY, directionZ, player.level(), finalDamage, target);
                            largefireball.setOwner(player);
                            largefireball.setPos(x, player.getEyeY() - 0.5D, Z);
                            largefireball.setUp(15);
                            level.addFreshEntity(largefireball);
                        } else {
                            Cursed_Sandstorm_Entity largefireball = new Cursed_Sandstorm_Entity(player, directionX, directionY, directionZ, player.level(), finalDamage, null);
                            largefireball.setOwner(player);
                            largefireball.setPos(x, player.getEyeY() - 0.5D, Z);
                            largefireball.setUp(15);
                            level.addFreshEntity(largefireball);
                        }
                    }

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                            1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    player.awardStat(Stats.ITEM_USED.get(this));
                    player.getCooldowns().addCooldown(this, getCooldown(stack));
                }
            }
        }
    }

    private Entity getPlayerLookTarget(Level level, LivingEntity living) {
        Entity pointedEntity = null;
        double range = 40.0D;
        Vec3 srcVec = living.getEyePosition();
        Vec3 lookVec = living.getViewVector(1.0F);
        Vec3 destVec = srcVec.add(lookVec.x() * range, lookVec.y() * range, lookVec.z() * range);
        float var9 = 2.0F;
        List<Entity> possibleList = level.getEntities(living, living.getBoundingBox()
                .expandTowards(lookVec.x() * range, lookVec.y() * range, lookVec.z() * range)
                .inflate(var9, var9, var9));
        double hitDist = 0.0D;

        for (Entity possibleEntity : possibleList) {
            AABB collisionBB = possibleEntity.getBoundingBox().inflate(1.0D, 1.0D, 1.0D);
            Optional<Vec3> interceptPos = collisionBB.clip(srcVec, destVec);
            if (collisionBB.contains(srcVec)) {
                if (0.0D < hitDist || hitDist == 0.0D) {
                    pointedEntity = possibleEntity;
                    hitDist = 0.0D;
                }
            } else if (interceptPos.isPresent()) {
                double possibleDist = srcVec.distanceTo(interceptPos.get());
                if (possibleDist < hitDist || hitDist == 0.0D) {
                    pointedEntity = possibleEntity;
                    hitDist = possibleDist;
                }
            }
        }

        return pointedEntity;
    }

    private int getCooldown(ItemStack stack) {
        return RFGearCombatStats.scaleRangedCooldown(stack, 40);
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
