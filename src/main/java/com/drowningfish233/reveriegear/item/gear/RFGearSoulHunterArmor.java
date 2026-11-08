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

public class RFGearSoulHunterArmor extends ArmorItem implements GearArmor {

    private static final Cache<String, Integer> ARMOR_COLORS = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final Supplier<GearType> gearType;

    public RFGearSoulHunterArmor(Supplier<GearType> gearType, ArmorItem.Type armorType) {
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

    public float getSoulWardCapacity(ItemStack stack) {
        float magicArmor = getMagicArmor(stack);
        return magicArmor / 5.0f;
    }

    public float getSoulWardRecoveryRate(ItemStack stack) {
        float charging = getChargingValue(stack);
        return charging * 0.05f * 2.0f;
    }

    public float getSoulWardIntegrity(ItemStack stack) {
        float magicArmor = getMagicArmor(stack);
        float charging = getChargingValue(stack);
        return (magicArmor * 0.01125f + charging * 0.005f) * 1.25f;
    }

    public float getGeasLimitBonus(ItemStack stack) {
        return GearData.getProperties(stack).getNumber(RFGearProperties.GEAS_LIMIT);
    }

    public float getKnockbackResistance(ItemStack stack) {
        return GearData.getProperties(stack).getNumber(GearProperties.KNOCKBACK_RESISTANCE) / 10f;
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

            float soulWardCapacity = getSoulWardCapacity(stack) * 0.9f;
            if (soulWardCapacity > 0) {
                builder.add(MalumAttributes.SOUL_WARD_CAPACITY,
                        new AttributeModifier(resourcelocation, soulWardCapacity,
                                AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            }

            float soulWardRecoveryRate = getSoulWardRecoveryRate(stack)* 0.9f;
            if (soulWardRecoveryRate > 0) {
                builder.add(MalumAttributes.SOUL_WARD_RECOVERY_RATE,
                        new AttributeModifier(resourcelocation, soulWardRecoveryRate,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE), equipmentSlotGroup);
            }

            float soulWardRecoveryGain = getSoulWardRecoveryRate(stack) * 1.25f;
            if (soulWardRecoveryGain > 0) {
                builder.add(MalumAttributes.SOUL_WARD_RECOVERY_GAIN,
                        new AttributeModifier(resourcelocation, soulWardRecoveryGain,
                                AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            }

            float soulWardIntegrity = getSoulWardIntegrity(stack) * 0.25f;
            if (soulWardIntegrity > 0) {
                builder.add(MalumAttributes.SOUL_WARD_INTEGRITY,
                        new AttributeModifier(resourcelocation, soulWardIntegrity,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE), equipmentSlotGroup);
            }

            float geasLimitBonus = getGeasLimitBonus(stack);
            if (geasLimitBonus > 0) {
                builder.add(MalumAttributes.GEAS_LIMIT,
                        new AttributeModifier(resourcelocation, geasLimitBonus,
                                AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            }

            float knockbackResistance = getKnockbackResistance(stack);
            if (knockbackResistance > 0) {
                builder.add(Attributes.KNOCKBACK_RESISTANCE,
                        new AttributeModifier(resourcelocation, knockbackResistance,
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
        return ResourceLocation.fromNamespaceAndPath("malum", "textures/armor/soul_hunter.png");
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
