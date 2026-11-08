package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.entity.RFSummonSwordEntity;
import com.drowningfish233.reveriegear.registry.RFEntityRegistry;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.item.SummonItem;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearSummonSword extends SummonItem<RFSummonSwordEntity> implements GearWeapon {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;

    public RFGearSummonSword(Supplier<GearType> gearType) {
        super(new Item.Properties().stacksTo(1).fireResistant(),
                RFEntityRegistry.RF_SUMMON_SWORD,
                1,
                6.0f
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
        float gearDamage = GearData.getProperties(stack).getNumber(GearProperties.ATTACK_DAMAGE, 5.0F);
        float summonDamage = gearDamage * 0.5F;
        tooltip.add(Component.translatable("item.reveriegear.summon_sword.desc")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.reveriegear.summon_sword.damage",
                DECIMAL_FORMAT.format(summonDamage)).withStyle(ChatFormatting.GREEN));
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

    @Override
    public void summon(Player player, ItemStack stack) {
        Level level = player.level();
        if (level.isClientSide) return;

        float gearDamage = GearData.getProperties(stack).getNumber(GearProperties.ATTACK_DAMAGE, 2.0F);
        float summonDamage = gearDamage * 0.5F;

        RFSummonSwordEntity entity = RFEntityRegistry.RF_SUMMON_SWORD.get().create(level);
        if (entity != null) {
            BlockPos pos = player.blockPosition().above();
            entity.setPos(pos.getX(), pos.getY(), pos.getZ());
            entity.summon(player, stack);
            entity.setCost(consume);
            entity.setCustomDamage(summonDamage);
            level.addFreshEntity(entity);
            entity.playSound(TESounds.ROUTINE_SUMMON.get(), 1.0F, 1.0F);

            var data = player.getData(TEAttachments.SUMMONER_STORAGE.get());
            data.summon(consume, entity.getId());
            if (player instanceof ServerPlayer serverPlayer)
                data.sync(serverPlayer);
        }
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
