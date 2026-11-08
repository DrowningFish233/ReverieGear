package com.drowningfish233.reveriegear.item.gear;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.client.util.GearColorUtils;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.confluence.terraentity.entity.proj.WhipEntity;
import org.confluence.terraentity.init.TEAttributes;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.item.BaseWhipItem;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearWhipItem extends BaseWhipItem implements GearWeapon {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;

    private static final double BASE_COOLDOWN = 3.5;
    private static final double MIN_COOLDOWN = 0.5;
    private static final double MAX_COOLDOWN = 5.0;

    public RFGearWhipItem(Supplier<GearType> gearType) {
        super(new Item.Properties()
                        .stacksTo(1)
                        .fireResistant()
                        .durability(500),
                4.0f,
                1.0f,
                1.6f,
                5,
                0.5f
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
    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColors() {
        return (stack, tintIndex) -> {
            return switch (tintIndex) {
                case 0 -> GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.MAIN.get());
                case 1 -> GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.ROD.get());
                default -> 0xFFFFFFFF;
            };
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        GearClientHelper.addInformation(stack, context, tooltip, flag);
        float gearDamage = getGearAttackDamage(stack);
        float gearSpeed = getGearAttackSpeed(stack);
        float gearRange = getGearRange(stack);

        double baseAttackSpeed = 4.0 * (1 + gearSpeed);
        double cooldown = calculateCooldown(baseAttackSpeed);

        tooltip.add(Component.translatable("item.reveriegear.whip.desc")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.reveriegear.whip.damage",
                DECIMAL_FORMAT.format(gearDamage)).withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.reveriegear.whip.speed",
                DECIMAL_FORMAT.format(gearSpeed)).withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.reveriegear.whip.cooldown",
                DECIMAL_FORMAT.format(cooldown)).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.reveriegear.whip.range",
                DECIMAL_FORMAT.format(gearRange)).withStyle(ChatFormatting.GREEN));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        float gearDamage = getGearAttackDamage(stack);
        float gearSpeed = getGearAttackSpeed(stack);
        float gearRange = getGearRange(stack);
        float gearMarkDamage = getGearMarkDamage(stack);

        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder, false);

        builder.add(TEAttributes.SUMMON_DAMAGE,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("reveriegear", "whip_damage"),
                        gearDamage,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND);

        builder.add(Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("reveriegear", "whip_speed"),
                        gearSpeed,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ),
                EquipmentSlotGroup.MAINHAND);

        builder.add(TEAttributes.WHIP_RANGE,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("reveriegear", "whip_range"),
                        gearRange,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ),
                EquipmentSlotGroup.MAINHAND);

        builder.add(TEAttributes.MARK_DAMAGE,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("reveriegear", "whip_mark_damage"),
                        gearMarkDamage,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND);

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

    private float getGearAttackDamage(ItemStack stack) {
        if (stack.isEmpty() || !GearHelper.isGear(stack)) {
            return 4.0F;
        }
        return GearData.getProperties(stack).getNumber(GearProperties.ATTACK_DAMAGE, 4.0F);
    }

    private float getGearAttackSpeed(ItemStack stack) {
        if (stack.isEmpty() || !GearHelper.isGear(stack)) {
            return 0.2F;
        }
        return GearData.getProperties(stack).getNumber(GearProperties.ATTACK_SPEED, 0.2F);
    }

    private float getGearRange(ItemStack stack) {
        if (stack.isEmpty() || !GearHelper.isGear(stack)) {
            return 5.0F;
        }
        float reach = GearData.getProperties(stack).getNumber(GearProperties.ATTACK_REACH, 5.0F);
        return Math.max(3.0F, reach);
    }

    private float getGearMarkDamage(ItemStack stack) {
        if (stack.isEmpty() || !GearHelper.isGear(stack)) {
            return 0.0F;
        }
        return GearData.getProperties(stack).getNumber(GearProperties.CHARGING_VALUE, 0.5F);
    }

    private double calculateCooldown(double attackSpeed) {
        double reduction = 1.0 / (attackSpeed * 0.3 + 0.7);
        return Math.min(MAX_COOLDOWN, Math.max(MIN_COOLDOWN, BASE_COOLDOWN * reduction));
    }

    private int getWhipCooldownTicks(Player player) {
        double playerAttackSpeed = player.getAttribute(Attributes.ATTACK_SPEED).getValue();

        double cooldownSeconds = calculateCooldown(playerAttackSpeed);
        return (int) Math.round(cooldownSeconds * 20.0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (usedHand == InteractionHand.OFF_HAND) {
            return InteractionResultHolder.success(stack);
        }
        if (!level.isClientSide) {

            int cooldownTicks = getWhipCooldownTicks(player);
            player.getCooldowns().addCooldown(this, cooldownTicks);

            WhipEntity whipEntity = TEProjectileEntities.WHIP_PROJECTILE.get().create(level);
            if (whipEntity != null) {
                whipEntity.setWeapon(stack);
                whipEntity.setExistTick(cooldownTicks);
                whipEntity.setOwner(player);
                whipEntity.setPos(player.position().add(0, player.getBbHeight() * 0.5F, 0)
                        .add(TEUtils.getPlayerHandPos(player)));
                whipEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.05F, 0.0F);
                whipEntity.hitCooldown = this.hitCooldown;
                level.addFreshEntity(whipEntity);
            }
        } else {
            clickTime = player.tickCount;
            cooldownTime = getWhipCooldownTicks(player);
        }
        player.swing(usedHand);
        return InteractionResultHolder.success(stack);
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
