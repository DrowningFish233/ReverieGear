package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.registry.gear.RFGearProperties;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sammy.malum.registry.common.MalumAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.api.item.GearArmor;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.setup.SgArmorMaterials;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RFGearMalignantStrongholdArmor extends ArmorItem implements GearArmor {

    private static final Cache<String, Integer> ARMOR_COLORS = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final Supplier<GearType> gearType;

    public RFGearMalignantStrongholdArmor(Supplier<GearType> gearType, ArmorItem.Type armorType) {
        super(SgArmorMaterials.DUMMY, armorType, GearHelper.getBaseItemProperties());
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public boolean isValidSlot(String slot) {
        return this.getType().getSlot().getName().equalsIgnoreCase(slot);
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        return getGearType().armorDurabilityMultiplier();
    }

    public float getArmorProtection(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.ARMOR);
    }

    public float getArmorToughness(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.ARMOR_TOUGHNESS) / 4f;
    }

    public float getMagicArmor(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.MAGIC_ARMOR);
    }

    public float getChargingValue(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.CHARGING_VALUE);
    }

    public float getMalignantConversion(ItemStack stack) {
        return 0.25f;
    }

    public float getMalignantAegisCapacity(ItemStack stack) {
        float magicArmor = getMagicArmor(stack);
        return (magicArmor / 4.0f) * 0.75f;
    }

    public float getMalignantAegisRecoveryRate(ItemStack stack) {
        float charging = getChargingValue(stack);
        return 0.03f + charging * 0.015f;
    }

    public float getMalignantAegisRecoveryGain(ItemStack stack) {
        float magicArmor = getMagicArmor(stack);
        return 0.02f + magicArmor * 0.005f;
    }

    public float getGeasLimitBonus(ItemStack stack) {
        return GearData.getProperties(stack).getNumber(RFGearProperties.GEAS_LIMIT);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        EquipmentSlot slot = this.getEquipmentSlot();
        if (slot == this.getType().getSlot()) {
            var equipmentSlotGroup = EquipmentSlotGroup.bySlot(slot);
            var resourcelocation = ResourceLocation.withDefaultNamespace("armor." + this.getType().getName());

            builder.add(Attributes.ARMOR, new AttributeModifier(resourcelocation, getArmorProtection(stack), AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourcelocation, getArmorToughness(stack), AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);

            float malignantConversion = getMalignantConversion(stack);
            if (malignantConversion > 0) {
                builder.add(MalumAttributes.MALIGNANT_CONVERSION,
                        new AttributeModifier(resourcelocation, malignantConversion,
                                AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            }

            float malignantAegisCapacity = getMalignantAegisCapacity(stack) *0.9f;
            if (malignantAegisCapacity > 0) {
                builder.add(MalumAttributes.MALIGNANT_AEGIS_CAPACITY,
                        new AttributeModifier(resourcelocation, malignantAegisCapacity,
                                AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            }

            float malignantAegisRecoveryRate = getMalignantAegisRecoveryRate(stack);
            if (malignantAegisRecoveryRate > 0) {
                builder.add(MalumAttributes.MALIGNANT_AEGIS_RECOVERY_RATE,
                        new AttributeModifier(resourcelocation, malignantAegisRecoveryRate,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE), equipmentSlotGroup);
            }

            float malignantAegisRecoveryGain = getMalignantAegisRecoveryGain(stack);
            if (malignantAegisRecoveryGain > 0) {
                builder.add(MalumAttributes.MALIGNANT_AEGIS_RECOVERY_GAIN,
                        new AttributeModifier(resourcelocation, malignantAegisRecoveryGain,
                                AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            }

            float geasLimitBonus = getGeasLimitBonus(stack);
            if (geasLimitBonus > 0) {
                builder.add(MalumAttributes.GEAS_LIMIT,
                        new AttributeModifier(resourcelocation, geasLimitBonus,
                                AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            }

            GearHelper.addAttributeModifiers(stack, builder, false);
        }
        return builder.build();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(getDurabilityStat());
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (GearHelper.isUnbreakable(stack))
            return;
        if (!(Config.Common.isLoaded() && Config.Common.gearBreaksPermanently.get()))
            damage = Mth.clamp(damage, 0, getMaxDamage(stack));
        super.setDamage(stack, damage);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, item -> {
            GearHelper.onBroken(stack, entity instanceof Player ? (Player) entity : null, this.getType().getSlot());
            onBroken.accept(item);
        });
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
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        var equipped = itemSlot >= 36 && itemSlot <= 39;
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, equipped);
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return TraitHelper.hasTrait(stack, Const.Traits.BRILLIANT);
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        if (GearHelper.isBroken(stack)) {
            return ReverieGear.getId("textures/models/armor/empty.png");
        }
        return ResourceLocation.fromNamespaceAndPath("malum", "textures/armor/malignant_stronghold.png");
    }

    public static int getArmorColor(ItemStack stack) {
        var data = GearData.getConstruction(stack);
        var part = data.getCoatingOrMainPart();
        if (part != null) {
            return part.getColor(stack);
        }
        return Color.VALUE_WHITE;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, tooltipContext, tooltip, flagIn);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }
}
