package com.drowningfish233.reveriegear.item.gear;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import org.confluence.terraentity.item.Boomerang;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearBoomerangItem extends Boomerang implements GearWeapon {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;

    public RFGearBoomerangItem(Supplier<GearType> gearType) {
        super(5.0f,
                new BoomerangModifier(),
                new Item.Properties()
                        .stacksTo(1)
                        .durability(500)
                .fireResistant()
        );
        this.gearType = gearType;

        this.boomerangModifier.damage = 5.0f;
        this.boomerangModifier.flySpeed = 1.52f;
        this.boomerangModifier.backSpeed = 1.52f;
        this.boomerangModifier.maxPenetration = 1;

        this.boomerangModifier.setDurability(200);
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

        float gearSpeed = getGearFlySpeed(stack);
        float attackDamage = getGearAttackDamage(stack);

        tooltip.add(Component.translatable("item.reveriegear.boomerang.desc")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.reveriegear.boomerang.speed",
                DECIMAL_FORMAT.format(gearSpeed)).withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.reveriegear.boomerang.damage",
                DECIMAL_FORMAT.format(attackDamage)).withStyle(ChatFormatting.RED));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return ItemAttributeModifiers.EMPTY;
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
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(getDurabilityStat());
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
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

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (usedHand == InteractionHand.OFF_HAND) {
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            GearHelper.attemptDamage(stack, 1, player, EquipmentSlot.MAINHAND);
        }
        this.boomerangModifier.damage = getGearAttackDamage(stack);

        float gearSpeed = getGearFlySpeed(stack);
        this.boomerangModifier.flySpeed = gearSpeed;
        this.boomerangModifier.backSpeed = gearSpeed;

        return super.use(level, player, usedHand);
    }

    private float getGearAttackDamage(ItemStack stack) {
        if (stack.isEmpty() || !GearHelper.isGear(stack)) {
            return 5.0F;
        }
        return GearData.getProperties(stack).getNumber(GearProperties.ATTACK_DAMAGE, 5.0F);
    }

    private float getGearFlySpeed(ItemStack stack) {
        if (stack.isEmpty() || !GearHelper.isGear(stack)) {
            return 1.52F;
        }
        float attackSpeed = GearData.getProperties(stack).getNumber(GearProperties.ATTACK_SPEED, 1.6F);
        return 0.5F + attackSpeed * 0.6F;
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
