package com.drowningfish233.reveriegear.item.gear;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.sammy.malum.common.entity.SunderingAnchorProjectileEntity;
import com.sammy.malum.common.item.curiosities.TemporarilyDisabledItem;
import com.sammy.malum.common.item.curiosities.weapons.SunderingAnchorItem;
import com.sammy.malum.common.item.spirit.ISpiritAffiliatedItem;
import com.sammy.malum.common.worldevent.DelayedDamageWorldEvent;
import com.sammy.malum.core.systems.spirit.type.SpiritLike;
import com.sammy.malum.registry.common.MalumDamageTypes;
import com.sammy.malum.registry.common.MalumParticleEffectTypes;
import com.sammy.malum.registry.common.MalumSoundEvents;
import com.sammy.malum.registry.common.item.MalumItems;
import com.sammy.malum.visual_effects.networked.attack.SunderingAnchorSlashParticleEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
import team.lodestar.lodestone.handlers.WorldEventHandler;
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

public class RFGearSunderingAnchorItem extends SunderingAnchorItem implements GearWeapon, ISpiritAffiliatedItem {

    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;

    public RFGearSunderingAnchorItem(Supplier<GearType> gearType) {
        super(GearHelper.DEFAULT_DUMMY_TIER, 0,
                new LodestoneItemProperties()
                        .component(SgDataComponents.GEAR_CONSTRUCTION.get(),
                                new GearConstructionData(PartList.empty(), true, 0, 0))
                        .component(SgDataComponents.GEAR_PROPERTIES.get(),
                                new GearPropertiesData(Map.of()))
                        .stacksTo(1)
                        .durability(400)
                        .setNoRepair());
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
        return "mainhand".equalsIgnoreCase(slot) || "offhand".equalsIgnoreCase(slot);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, context, tooltip, flagIn);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder);

        float magicDamage = getMagicDamage(stack);
        builder.add(LodestoneAttributes.MAGIC_DAMAGE,
                new AttributeModifier(LodestoneAttributes.BASE_MAGIC_DAMAGE, magicDamage, AttributeModifier.Operation.ADD_VALUE),
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
        return SunderingAnchorItem.getSunderingAnchorSpirit();
    }

    private float getPhysicalDamage(LivingEntity attacker) {
        return (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    private float getMagicDamage(LivingEntity attacker) {
        return (float) attacker.getAttributeValue(LodestoneAttributes.MAGIC_DAMAGE);
    }

    private float getMagicDamage(ItemStack stack) {
        float magicDamage = GearData.getProperties(stack).getNumber(GearProperties.MAGIC_DAMAGE, 4.0f);
        return (float) Math.floor(magicDamage * 0.8f);
    }

    private float getAttackSpeedModifier(ItemStack stack) {
        float attackSpeed = GearData.getProperties(stack).getNumber(GearProperties.ATTACK_SPEED, 4.0f);
        float cooldownMultiplier = 2.0f / Math.max(1.0f, attackSpeed);
        return Math.min(2.0f, Math.max(0.5f, cooldownMultiplier));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(stack);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            int slot = usedHand == InteractionHand.OFF_HAND ? player.getInventory().getContainerSize() - 1 : player.getInventory().selected;

            float physicalDamage = getPhysicalDamage(player);
            float magicDamage = getMagicDamage(player);

            Vec3 pos = getProjectileSpawnPos(player, usedHand, 0.5f, 0.5f);
            SunderingAnchorProjectileEntity entity = new SunderingAnchorProjectileEntity(level, pos.x, pos.y, pos.z);
            entity.setData(player, physicalDamage, magicDamage, slot);
            entity.setItem(stack);
            entity.setOwner(player);
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 0.0F);

            level.addFreshEntity(entity);
            SoundHelper.playSound(player, MalumSoundEvents.SUNDERING_ANCHOR_THROW.get(), 0.5f,
                    RandomHelper.randomBetween(level.getRandom(), 1.5f, 2.0f));

            TemporarilyDisabledItem.disable(serverPlayer, slot, MalumItems.SOUL_OF_THE_ANCHOR);

            float cooldownScalar = getAttackSpeedModifier(stack);
            SunderingAnchorItem.applyCooldown(stack, player, cooldownScalar);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void outgoingDamageEvent(LivingDamageEvent.Pre event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        if (attacker.level() instanceof ServerLevel level) {
            DamageSource source = event.getSource();
            RandomSource random = level.random;

            if (source.is(LodestoneDamageTypeTags.IS_MAGIC)) {
                applyHatred(target);
            }

            if (source.is(LodestoneDamageTypeTags.CAN_TRIGGER_MAGIC)) {
                int slashCount = 3 + Mth.floor(random.nextFloat() * 3.0F);
                float splitDamage = event.getNewDamage() / (float)slashCount;

                if (target.isAlive()) {
                    for (int i = 0; i < slashCount; ++i) {
                        WorldEventHandler.addWorldEvent(level,
                                (new DelayedDamageWorldEvent(target))
                                        .setAttacker(attacker)
                                        .setDamageData(0.0F, splitDamage, i * 2)
                                        .setPhysicalDamageType(MalumDamageTypes.SUNDERING_ANCHOR_PHYSICAL_COMBO)
                                        .setMagicDamageType(MalumDamageTypes.SUNDERING_ANCHOR_MAGIC_COMBO)
                                        .setSound(MalumSoundEvents.SUNDERING_ANCHOR_EXTRA_SWING, 1.2F, 1.4F, 0.7F));
                    }
                }

                event.setNewDamage(splitDamage);
                float pitch = RandomHelper.randomBetween(level.getRandom(), 1.0F, 1.2F);
                SoundHelper.playSound(attacker, MalumSoundEvents.SUNDERING_ANCHOR_SWING.get(), 2.0F, pitch);

                int mainPartColor = getMainPartColor(stack);

                MalumParticleEffectTypes.SUNDERING_ANCHOR_SLASH.createEffect()
                        .originatesFrom(attacker)
                        .forwardOffset(1.5F)
                        .horizontalOffset(0.5F)
                        .upwardOffset(0.2F)
                        .color(new Color(mainPartColor))
                        .customData(new SunderingAnchorSlashParticleEffect.SunderingAnchorSlashEffectData(slashCount))
                        .randomSlashRotation(random)
                        .spawn(level);
            }
        }
    }

    private int getMainPartColor(ItemStack stack) {
        PartType mainType = PartTypes.MAIN.get();
        return GearColorUtils.getBlendedColorForPartInGear(stack, mainType);
    }

}
