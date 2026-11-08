package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.api.RFTextureType;
import com.drowningfish233.reveriegear.client.renderer.gear.RFGeoArmorProviders;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.api.item.GearArmor;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.setup.SgArmorMaterials;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RFGearSetArmorItem extends ArmorItem implements GearArmor, GeoItem {

    private final Supplier<GearType> gearType;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private Object geoRenderProvider;

    protected RFGearSetArmorItem(Supplier<GearType> gearType, Type armorType) {
        super(SgArmorMaterials.DUMMY, armorType, GearHelper.getBaseItemProperties());
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        registerGeoControllers(controllers);
    }

    protected void registerGeoControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    protected Object geoRenderProvider(ResourceLocation model, ResourceLocation texture, ResourceLocation animation) {
        if (this.geoRenderProvider == null) {
            this.geoRenderProvider = RFGeoArmorProviders.armor(model, texture, animation);
        }
        return this.geoRenderProvider;
    }

    @Override
    public boolean isValidSlot(String slot) {
        return this.getType().getSlot().getName().equalsIgnoreCase(slot);
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        return getGearType().armorDurabilityMultiplier();
    }


    @Override
    public Collection<PartType> getRequiredParts() {
        return List.of(PartTypes.MAIN.get());
    }


    protected abstract float getArmorMultiplier();


    protected abstract float getToughnessMultiplier();


    protected abstract void addSetAttributeModifiers(ItemStack stack,
                                                     ItemAttributeModifiers.Builder builder,
                                                     EquipmentSlotGroup equipmentSlotGroup);


    public float getBaseArmorProtection(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.ARMOR);
    }


    public float getBaseArmorToughness(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.ARMOR_TOUGHNESS) / 4f;
    }

    public float getArmorProtection(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return this.getBaseArmorProtection(stack) * this.getArmorMultiplier();
    }

    public float getArmorToughness(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return this.getBaseArmorToughness(stack) * this.getToughnessMultiplier();
    }

    public float getKnockbackResistance(ItemStack stack) {
        return GearData.getProperties(stack).getNumber(GearProperties.KNOCKBACK_RESISTANCE) / 10f;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        EquipmentSlot slot = this.getEquipmentSlot();
        if (slot != this.getType().getSlot()) {
            return builder.build();
        }

        var equipmentSlotGroup = EquipmentSlotGroup.bySlot(slot);
        var id = ResourceLocation.withDefaultNamespace("armor." + this.getType().getName());

        builder.add(Attributes.ARMOR,
                new AttributeModifier(id, getArmorProtection(stack), AttributeModifier.Operation.ADD_VALUE),
                equipmentSlotGroup);
        builder.add(Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(id, getArmorToughness(stack), AttributeModifier.Operation.ADD_VALUE),
                equipmentSlotGroup);

        float knockbackResistance = getKnockbackResistance(stack);
        if (knockbackResistance > 0) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(id, knockbackResistance, AttributeModifier.Operation.ADD_VALUE),
                    equipmentSlotGroup);
        }

        if (!GearHelper.isBroken(stack)) {
            addSetAttributeModifiers(stack, builder, equipmentSlotGroup);
        }

        GearHelper.addAttributeModifiers(stack, builder, false);
        return builder.build();
    }

    protected ResourceLocation setModifierId(String name) {
        return ReverieGear.getId("set_" + name + "_" + this.getType().getName());
    }

    protected static Holder<Attribute> attribute(Supplier<Attribute> supplier) {
        return BuiltInRegistries.ATTRIBUTE.wrapAsHolder(supplier.get());
    }

    @Nullable
    protected static Holder<Attribute> combatRollAttribute(String name) {
        return BuiltInRegistries.ATTRIBUTE
                .getHolder(ResourceLocation.fromNamespaceAndPath("combat_roll", name))
                .orElse(null);
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
