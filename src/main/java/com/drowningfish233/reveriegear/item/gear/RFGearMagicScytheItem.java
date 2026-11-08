package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.registry.RFAttributesRegistry;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.sammy.malum.common.item.curiosities.weapons.scythe.MalumScytheItem;
import com.sammy.malum.common.item.spirit.ISpiritAffiliatedItem;
import com.sammy.malum.core.systems.spirit.type.SpiritLike;
import com.sammy.malum.registry.common.MalumDamageTypes;
import com.sammy.malum.registry.common.MalumParticleEffectTypes;
import com.sammy.malum.registry.common.enchantment.EnchantmentKeys;
import com.sammy.malum.registry.common.item.MalumItems;
import com.sammy.malum.visual_effects.networked.MalumNetworkedWeaponParticleEffectType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
import team.lodestar.lodestone.helpers.CurioHelper;
import team.lodestar.lodestone.helpers.SoundHelper;
import team.lodestar.lodestone.registry.common.LodestoneAttributes;
import team.lodestar.lodestone.systems.item.LodestoneItemProperties;
import team.lodestar.lodestone.systems.network.WeaponParticleEffectType;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearMagicScytheItem extends MalumScytheItem implements GearWeapon, ISpiritAffiliatedItem {

    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    private final Supplier<GearType> gearType;
    private final SpiritLike definingSpirit;

    public RFGearMagicScytheItem(Supplier<GearType> gearType, SpiritLike definingSpirit) {
        super(GearHelper.DEFAULT_DUMMY_TIER, 1, 1,
                new LodestoneItemProperties()
                        .component(SgDataComponents.GEAR_CONSTRUCTION.get(),
                                new GearConstructionData(PartList.empty(), true, 0, 0))
                        .component(SgDataComponents.GEAR_PROPERTIES.get(),
                                new GearPropertiesData(Map.of()))
                        .stacksTo(1)
                        .durability(100)
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
        float magicDamage = GearData.getProperties(stack).getNumber(GearProperties.MAGIC_DAMAGE) * 1.5f;
        if (magicDamage > 0) {
            builder.add(LodestoneAttributes.MAGIC_DAMAGE,
                    new AttributeModifier(LodestoneAttributes.BASE_MAGIC_DAMAGE, magicDamage, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND);
        }
        builder.add(
                BuiltInRegistries.ATTRIBUTE.wrapAsHolder(RFAttributesRegistry.ARCANE_SCYTHE_CONVERSION.get()),
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("reveriegear", "arcane_scythe_conversion"),
                        1.0,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
        );
        GearHelper.addAttributeModifiers(stack, builder, true);

        builder.add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("reveriegear", "damage_reduction"),
                        -0.75,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
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
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(GearProperties.DURABILITY);
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (EnchantmentKeys.getEnchantmentLevel(level, EnchantmentKeys.REBOUND, stack) > 0) {
            return super.use(level, player, hand);
        }

        EnchantmentKeys.getEnchantmentLevel(level, EnchantmentKeys.ASCENSION, stack);

        return super.use(level, player, hand);
    }

    public static boolean canSweep(LivingEntity attacker) {
        return !isEnhanced(attacker) && !CurioHelper.hasCurioEquipped(attacker, MalumItems.NECKLACE_OF_THE_HIDDEN_BLADE.get());
    }

    public static boolean isEnhanced(LivingEntity attacker) {
        return CurioHelper.hasCurioEquipped(attacker, MalumItems.NECKLACE_OF_THE_NARROW_EDGE.get());
    }

    @Override
    public void outgoingDamageEvent(LivingDamageEvent.Pre event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        Level level = attacker.level();
        if (level instanceof ServerLevel serverLevel) {
            if (event.getSource().is(MalumDamageTypes.SCYTHE_MELEE)) {
                int mainPartColor = getMainPartColor(stack);

                MalumNetworkedWeaponParticleEffectType.MalumWeaponParticleEffectBuilder<WeaponParticleEffectType.WeaponParticleEffectData> particle =
                        MalumParticleEffectTypes.SCYTHE_SLASH.createEffect()
                                .originatesFrom(attacker)
                                .targets(target)
                                .color(new Color(mainPartColor))
                                .upwardOffset(-0.4F)
                                .forwardOffset(0.8F);

                if (!canSweep(attacker)) {
                    SoundHelper.playSound(attacker, this.getScytheSound(false).value(), 1.0F, 0.75F);
                    particle.verticalSlashRotation().horizontalOffset(0.6F).spawn(serverLevel);
                } else {
                    SoundHelper.playSound(attacker, this.getScytheSound(true).value(), 1.0F, 1.0F);
                    particle.mirroredRandomly(attacker.getRandom()).spawn(serverLevel);
                    trySweep(attacker, target, event.getNewDamage());
                }
            }
        }
    }

    private int getMainPartColor(ItemStack stack) {
        PartType mainType = PartTypes.MAIN.get();
        return GearColorUtils.getBlendedColorForPartInGear(stack, mainType);
    }
}
