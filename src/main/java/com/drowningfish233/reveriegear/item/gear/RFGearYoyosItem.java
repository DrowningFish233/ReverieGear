package com.drowningfish233.reveriegear.item.gear;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import org.confluence.terraentity.api.event.SummonEvent;
import org.confluence.terraentity.api.item.ILeftClickStateItem;
import org.confluence.terraentity.api.item.IProjectileModifier;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.confluence.terraentity.entity.proj.BaseProj;
import org.confluence.terraentity.entity.proj.YoyosEntity;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.init.entity.TESummonEntities;
import org.confluence.terraentity.item.YoyosItem;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearYoyosItem extends YoyosItem<BaseProj<?>> implements GearWeapon, ILeftClickStateItem, IProjectileModifier<BaseProj<?>> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.CORD.get()
    ));

    private final Supplier<GearType> gearType;
    private ItemStack gearStack = ItemStack.EMPTY;

    public RFGearYoyosItem(Supplier<GearType> gearType) {
        super(new Item.Properties().stacksTo(1).fireResistant().durability(500),
                5.0f,
                10,
                0xFFFFFF,
                3.0f,
                "rally"
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
                case 1 -> GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.CORD.get());
                default -> 0xFFFFFFFF;
            };
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        GearClientHelper.addInformation(stack, context, tooltip, flag);
        float gearDamage = getGearAttackDamage(stack);
        float actualDamage = gearDamage * 0.5F;
        float maxRange = 8.0F + gearDamage * 0.5F;
        float existTimeDisplay = 2.0F + gearDamage * 0.1F + 1.0F;
        tooltip.add(Component.translatable("tooltip.reveriegear.yoyos.damage",
                        DECIMAL_FORMAT.format(actualDamage))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.reveriegear.yoyos.desc")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.reveriegear.yoyos.range",
                DECIMAL_FORMAT.format(maxRange)).withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.reveriegear.yoyos.time",
                DECIMAL_FORMAT.format(existTimeDisplay)).withStyle(ChatFormatting.GREEN));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
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
            return 1.5F;
        }
        if (GearHelper.isBroken(stack)) {
            return 0F;
        }
        float baseDamage = GearData.getProperties(stack).getNumber(GearProperties.ATTACK_DAMAGE, 1.5F);
        return baseDamage * 0.8F;
    }

    @Override
    public float getAttackDamage() {
        if (!gearStack.isEmpty()) {
            return getGearAttackDamage(gearStack) * 0.5F;
        }
        return 1.5f;
    }

    @Override
    public float getMaxRange() {
        if (!gearStack.isEmpty()) {
            float damage = getGearAttackDamage(gearStack);
            return 8.0F + damage * 0.5F;
        }
        return 10.0f;
    }

    @Override
    public float getExistTime() {
        if (!gearStack.isEmpty()) {
            float damage = getGearAttackDamage(gearStack);
            return 2.0F + damage * 0.1F;
        }
        return 3.0f;
    }

    @Override
    public void onLeftClick(Player player, ItemStack itemStack) {
        if (player.level().isClientSide) return;
        WeaponStorage weaponStorage = player.getData(TEAttachments.WEAPON_STORAGE);
        if (weaponStorage.yoyosEntity != null && weaponStorage.yoyosEntity.isAlive()) {
            weaponStorage.yoyosEntity.onReceiveLeftClick(player, itemStack);
            return;
        }

        Level level = player.level();
        YoyosEntity<?> proj = TESummonEntities.YOYOS_ENTITY.get().create(level);
        if (proj != null) {
            this.gearStack = itemStack.copy();
            float existTime = 2.0F + getGearAttackDamage(itemStack) * 0.1F;

            player.getCooldowns().addCooldown(itemStack.getItem(), (int) (existTime * 20));
            weaponStorage.yoyosEntity = proj;
            proj.setPos(player.getX(), player.getY(0.5f), player.getZ());
            proj.setOwnerUUID(player.getUUID());
            proj.setWeaponItem(itemStack);
            level.addFreshEntity(proj);

            AdapterUtils.postGameEvent(new SummonEvent.Pre<>(player, itemStack, TESummonEntities.YOYOS_ENTITY.get()));
        }
    }

    @Override
    public void onLeftRelease(Player player, ItemStack itemStack) {
        if (player.level().isClientSide) return;
        WeaponStorage weaponStorage = player.getData(TEAttachments.WEAPON_STORAGE);
        if (weaponStorage.yoyosEntity != null && weaponStorage.yoyosEntity.isAlive()) {
            weaponStorage.yoyosEntity.onReceiveLeftRelease(player, itemStack);
        }
    }

    @Override
    public boolean canSwitchWithoutRelease(Player player, ItemStack itemStack) {
        return false;
    }

    @Override
    public void onWhellScroll(Player player, ItemStack itemStack, int scrollAmount) {
        if (player.level().isClientSide) return;
        WeaponStorage weaponStorage = player.getData(TEAttachments.WEAPON_STORAGE);
        if (weaponStorage.yoyosEntity != null && weaponStorage.yoyosEntity.isAlive()) {
            weaponStorage.yoyosEntity.onReceiveWhellScroll(player, itemStack, scrollAmount);
        }
    }

    @Override
    public void modifyProjectile(Level level, LivingEntity shooter, BaseProj<?> projectile) {
        float gearDamage = getGearAttackDamage(shooter.getMainHandItem());
        projectile.setDamage(gearDamage * 0.5F);
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
