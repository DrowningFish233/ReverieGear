package com.drowningfish233.reveriegear.item.gear;

import com.github.L_Ender.cataclysm.Attachment.ChargeAttachment;
import com.github.L_Ender.cataclysm.init.ModDataAttachments;
import com.github.L_Ender.cataclysm.items.Bulwark_of_the_flame;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
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

public class RFGearShieldItem extends Bulwark_of_the_flame implements GearItem {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;

    public RFGearShieldItem(Supplier<GearType> gearType) {
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
        tooltip.add(Component.translatable("item.reveriegear.flame_bulwark.desc").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(Component.translatable("item.reveriegear.flame_bulwark.desc2").withStyle(ChatFormatting.DARK_GREEN));
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
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            if (entityLiving.isShiftKeyDown() && !entityLiving.isFallFlying()) {
                int i = this.getUseDuration(stack, entityLiving) - timeLeft;
                int t = Mth.clamp(i, 1, 4);

                float chargeSpeed = getChargeSpeedMultiplier(stack);
                float chargeDamage = getChargeDamageMultiplier(stack);

                float f7 = entityLiving.getYRot();
                float f = entityLiving.getXRot();

                float f1 = -Mth.sin(f7 * ((float) Math.PI / 180F)) * Mth.cos(f * ((float) Math.PI / 180F));
                float f2 = -Mth.sin(f * ((float) Math.PI / 180F));
                float f3 = Mth.cos(f7 * ((float) Math.PI / 180F)) * Mth.cos(f * ((float) Math.PI / 180F));
                float f4 = Mth.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                float f5 = chargeSpeed * (t / 6.0F);
                f1 *= f5 / f4;
                f3 *= f5 / f4;

                entityLiving.push(f1, 0, f3);

                if (entityLiving.onGround()) {
                    float jumpHeight = getChargeJumpHeight(stack);
                    entityLiving.move(MoverType.SELF, new Vec3(0.0D, jumpHeight, 0.0D));
                }

                ChargeAttachment charge = entityLiving.getData(ModDataAttachments.CHARGE_ATTACHMENT);
                charge.setCharge(true);
                charge.setTimer(t * 2);
                charge.seteffectiveChargeTime(t * 2);
                charge.setknockbackSpeedIndex(t * 2);
                charge.setdamagePerEffectiveCharge(chargeDamage);
                charge.setdx(f1 * 0.1F);
                charge.setdZ(f3 * 0.1F);

                if (!level.isClientSide) {
                    int cooldown = getCooldown(stack);
                    player.getCooldowns().addCooldown(this, cooldown);
                }
            }
        }
    }

    private float getChargeSpeedMultiplier(ItemStack stack) {
        float attackSpeed = GearData.getProperties(stack)
                .getNumber(GearProperties.ATTACK_SPEED, 4.0F);
        return Math.max(0.5F, 1.5F + attackSpeed * 0.3F);
    }

    private float getChargeJumpHeight(ItemStack stack) {
        float armorToughness = GearData.getProperties(stack)
                .getNumber(GearProperties.ARMOR_TOUGHNESS, 0.0F);
        return 0.6F + armorToughness * 0.05F;
    }

    private float getChargeDamageMultiplier(ItemStack stack) {
        float attackDamage = GearData.getProperties(stack)
                .getNumber(GearProperties.ATTACK_DAMAGE, 5.0F);
        return Math.max(0.5F, attackDamage * 0.15F);
    }

    private int getCooldown(ItemStack stack) {
        float attackSpeed = GearData.getProperties(stack)
                .getNumber(GearProperties.ATTACK_SPEED, 4.0F);
        int cooldown = Math.round(40 * (4.0F / Math.max(0.1F, attackSpeed)));
        return Math.min(cooldown, 100);
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
