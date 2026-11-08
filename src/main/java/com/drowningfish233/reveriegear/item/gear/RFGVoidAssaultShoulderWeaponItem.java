package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.api.ICustomDamageEntity;
import com.drowningfish233.reveriegear.Utils.gear.RFGearCombatStats;
import com.github.L_Ender.cataclysm.entity.projectile.Void_Howitzer_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.github.L_Ender.cataclysm.items.Void_Assault_SHoulder_Weapon;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGVoidAssaultShoulderWeaponItem extends Void_Assault_SHoulder_Weapon implements GearRangedWeapon {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get(),
            PartTypes.CORD.get()
    ));

    private final Supplier<GearType> gearType;

    public RFGVoidAssaultShoulderWeaponItem(Supplier<GearType> gearType) {
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
        tooltip.add(Component.translatable("item.reveriegear.void_assault_shoulder_weapon.desc").withStyle(ChatFormatting.DARK_GREEN));
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
            int i = this.getUseDuration(stack, living) - timeleft;
            float f = getPowerForTime(RFGearCombatStats.scaleDrawTicks(stack, i));
            if (!((double) f < 0.5D)) {
                float rangedDamage = RFGearCombatStats.getRangedDamage(stack, 8.0F) * 1.5F;
                if (rangedDamage <= 0) {
                    return;
                }

                GearHelper.attemptDamage(stack, 10, player, EquipmentSlot.MAINHAND);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.ROCKET_LAUNCH.get(), SoundSource.PLAYERS, 1.0F, 0.7F);

                int cooldown = getCooldown(stack);
                player.getCooldowns().addCooldown(this, cooldown);

                if (!level.isClientSide) {
                    Void_Howitzer_Entity rocket = new Void_Howitzer_Entity(ModEntities.VOID_HOWITZER.get(), level, player);
                    rocket.setOwner(player);
                    rocket.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 1.0F, 1.0F);
                    ((ICustomDamageEntity) rocket).setCustomDamage(rangedDamage);
                    level.addFreshEntity(rocket);
                }
            }
        }
    }

    private int getCooldown(ItemStack stack) {
        return RFGearCombatStats.scaleRangedCooldown(stack, 100);
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
