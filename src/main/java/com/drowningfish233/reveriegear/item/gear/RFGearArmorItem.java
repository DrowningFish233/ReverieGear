package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.api.RFTextureType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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

public class RFGearArmorItem extends ArmorItem implements GearArmor {

    private static final Cache<String, Integer> ARMOR_COLORS = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final Supplier<GearType> gearType;

    public RFGearArmorItem(Supplier<GearType> gearType, ArmorItem.Type armorType) {
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

    private static float getGenericArmorProtection(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof RFGearArmorItem) {
            return ((RFGearArmorItem) item).getArmorProtection(stack);
        } else if (item instanceof ArmorItem) {
            return ((ArmorItem) item).getDefense();
        }
        return 0;
    }

    private static int getPlayerTotalArmorValue(LivingEntity player) {
        float total = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            total += getGenericArmorProtection(armor);
        }
        return Math.round(total);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        EquipmentSlot slot = this.getEquipmentSlot();
        if (slot == this.getType().getSlot()) {
            var equipmentSlotGroup = EquipmentSlotGroup.bySlot(slot);
            var resourcelocation = ResourceLocation.withDefaultNamespace("armor." + this.getType().getName());

            float armor = getArmorProtection(stack);
            float toughness = getArmorToughness(stack);

            builder.add(Attributes.ARMOR, new AttributeModifier(resourcelocation, armor, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourcelocation, toughness, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);

            float knockbackResistance = GearData.getProperties(stack).getNumber(GearProperties.KNOCKBACK_RESISTANCE) / 10f;
            if (knockbackResistance > 0) {
                builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(resourcelocation, knockbackResistance, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            }

            float healthBonus = armor / 2f;
            if (healthBonus > 0) {
                builder.add(Attributes.MAX_HEALTH,
                        new AttributeModifier(ReverieGear.getId("armor_health_bonus_" + this.getType().getName()),
                                healthBonus,
                                AttributeModifier.Operation.ADD_VALUE),
                        equipmentSlotGroup);
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
        return RFTextureType.DEFAULT.getArmorTexture(innerModel);
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
