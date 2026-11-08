package com.drowningfish233.reveriegear.item.gear;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.sammy.malum.common.entity.bolt.HexBoltEntity;
import com.sammy.malum.common.item.curiosities.weapons.staff.HexStaffItem;
import com.sammy.malum.common.item.spirit.ISpiritAffiliatedItem;
import com.sammy.malum.core.systems.spirit.type.SpiritLike;
import com.sammy.malum.registry.common.MalumAttributes;
import com.sammy.malum.registry.common.MalumParticleEffectTypes;
import com.sammy.malum.registry.common.MalumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.client.util.GearColorUtils;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.helpers.RandomHelper;
import team.lodestar.lodestone.helpers.SoundHelper;
import team.lodestar.lodestone.registry.common.LodestoneAttributes;
import team.lodestar.lodestone.registry.common.tag.LodestoneDamageTypeTags;
import team.lodestar.lodestone.systems.item.LodestoneItemProperties;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearHexStaffItem extends HexStaffItem implements GearWeapon, ISpiritAffiliatedItem {

    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;
    private final SpiritLike definingSpirit;

    public RFGearHexStaffItem(Supplier<GearType> gearType, SpiritLike definingSpirit) {
        super(GearHelper.DEFAULT_DUMMY_TIER, 1, 1, 1,
                new LodestoneItemProperties()
                        .component(SgDataComponents.GEAR_CONSTRUCTION.get(),
                                new GearConstructionData(PartList.empty(), true, 0, 0))
                        .component(SgDataComponents.GEAR_PROPERTIES.get(),
                                new GearPropertiesData(Map.of()))
                        .stacksTo(1)
                        .durability(400)
                        .setNoRepair());
        this.gearType = gearType;
        this.definingSpirit = definingSpirit;
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
        return "mainhand".equalsIgnoreCase(slot) || "offhand".equalsIgnoreCase(slot);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, context, tooltip, flagIn);
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder);

        float magicDamage = getMagicDamageFromGear(stack) * 1.2f;
        builder.add(LodestoneAttributes.MAGIC_DAMAGE,
                new AttributeModifier(LodestoneAttributes.BASE_MAGIC_DAMAGE, magicDamage, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        float attackSpeed = getAttackSpeedFromGear(stack);
        builder.add(MalumAttributes.CHARGE_DURATION,
                new AttributeModifier(MalumAttributes.BASE_CHARGE_DURATION, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        int chargeCapacity = getChargeCapacityFromGear(stack);
        builder.add(MalumAttributes.CHARGE_CAPACITY,
                new AttributeModifier(MalumAttributes.BASE_CHARGE_CAPACITY, chargeCapacity, AttributeModifier.Operation.ADD_VALUE),
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
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return GearHelper.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        GearHelper.postHurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        return GearHelper.onBlockDestroyed(stack, level, state, pos, entityLiving);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        GearHelper.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return GearHelper.onItemUse(context);
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
    public SpiritLike getDefiningSpiritType() {
        return definingSpirit;
    }

    @Override
    public void modifyAttributeTooltipEvent(AddAttributeTooltipsEvent event) {
        super.modifyAttributeTooltipEvent(event);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return itemAbility.equals(ItemAbilities.SWORD_DIG);
    }

    private float getMagicDamageFromGear(ItemStack stack) {
        if (GearHelper.isGear(stack)) {
            return GearData.getProperties(stack).getNumber(GearProperties.MAGIC_DAMAGE, 1.0f);
        }
        return 5.0f;
    }

    private float getAttackSpeedFromGear(ItemStack stack) {
        if (GearHelper.isGear(stack)) {
            return GearData.getProperties(stack).getNumber(GearProperties.ATTACK_SPEED, 1.0f);
        }
        return 1.0f;
    }

    private int getChargeCapacityFromGear(ItemStack stack) {
        float magicDamage = getMagicDamageFromGear(stack);
        return Math.max(1, (int) Math.ceil(magicDamage / 4.0f));
    }

    @Override
    public void outgoingDamageEvent(LivingDamageEvent.Pre event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        if (attacker.level() instanceof ServerLevel level) {
            if (event.getSource().is(LodestoneDamageTypeTags.CAN_TRIGGER_MAGIC)) {
                int mainPartColor = getMainPartColor(stack);

                SoundHelper.playSound(attacker, MalumSoundEvents.STAFF_STRIKES.get(), 1.0F, RandomHelper.randomBetween(attacker.getRandom(), 0.85f, 1.25f));
                MalumParticleEffectTypes.STAFF_SLAM.createEffect()
                        .originatesFrom(attacker)
                        .targets(target)
                        .color(new Color(mainPartColor))
                        .forwardOffset(1.4f)
                        .upwardOffset(0.3f)
                        .spawn(level);
            }
        }
        super.outgoingDamageEvent(event, attacker, target, stack);
    }

    @Override
    public void fireProjectile(LivingEntity player, ItemStack stack, Level level, InteractionHand hand, int count) {
        float pitchOffset = 3.0F + (float) count;
        int spawnDelay = count * 3;
        float velocity = 3.0F + 0.5F * (float) count;

        float magicDamage = (float) player.getAttributeValue(LodestoneAttributes.MAGIC_DAMAGE);

        Vec3 pos = getProjectileSpawnPos(player, hand, 0.5F, 0.5F);

        HexBoltEntity entity = new HexBoltEntity(level, pos.x, pos.y, pos.z);
        entity.setData(player, magicDamage, spawnDelay);
        entity.setItem(stack);
        entity.shootFromRotation(player, player.getXRot(), player.getYRot(), -pitchOffset, velocity, 0.0F);
        level.addFreshEntity(entity);
    }

    private int getMainPartColor(ItemStack stack) {
        PartType mainType = PartTypes.MAIN.get();
        return GearColorUtils.getBlendedColorForPartInGear(stack, mainType);
    }

}
