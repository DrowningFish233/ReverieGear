package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.ReverieGear;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.confluence.terraentity.init.TEAttributes;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Supplier;

public class RFGearSummonerArmor extends RFGearSetArmorItem {

    private static final RawAnimation WINGS_IDLE = RawAnimation.begin().then("idle", LoopType.LOOP);

    public RFGearSummonerArmor(Supplier<GearType> gearType, Type armorType) {
        super(gearType, armorType);
    }

    @Override
    public Object getRenderProvider() {
        return geoRenderProvider(
                ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/arbitrium_robes_armor.geo.json"),
                ReverieGear.getId("textures/models/armor/summoner_geo.png"),
                ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/arbitrium_robes_armor.animation.json"));
    }

    @Override
    protected void registerGeoControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "rf_summoner_wings", 10,
                state -> state.setAndContinue(WINGS_IDLE)));
    }

    @Override
    protected float getArmorMultiplier() {
        return 0.70f;
    }

    @Override
    protected float getToughnessMultiplier() {
        return 0.85f;
    }

    @Override
    public float getArmorProtection(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        if (this.getType().getSlot() == EquipmentSlot.HEAD) return 1.0f;
        return super.getArmorProtection(stack);
    }

    @Override
    public float getArmorToughness(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        if (this.getType().getSlot() == EquipmentSlot.HEAD) return 1.0f;
        return super.getArmorToughness(stack);
    }

    public float getMagicArmor(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.MAGIC_ARMOR);
    }

    public float getMagicDamage(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.MAGIC_DAMAGE);
    }

    public float getSummonCapacityBonus(ItemStack stack) {
        float capacity = Math.max(0.0f, getMagicArmor(stack)) * 0.08f;
        if (this.getType().getSlot() == EquipmentSlot.HEAD) {
            capacity += Math.max(0.0f, getBaseArmorProtection(stack) - 1.0f) / 10.0f;
        }
        if (capacity <= 0.0f) return 0.0f;
        float original = Math.max(1.0f, Math.round(capacity));
        return Math.max(1.0f, original * 0.5f);
    }


    public float getSummonDamageBonus(ItemStack stack) {
        return net.minecraft.util.Mth.clamp(Math.max(0.0f, getMagicDamage(stack)) * 0.05f, 0.0f, 0.25f);
    }

    @Override
    protected void addSetAttributeModifiers(ItemStack stack, ItemAttributeModifiers.Builder builder,
                                            EquipmentSlotGroup equipmentSlotGroup) {
        float capacity = getSummonCapacityBonus(stack);
        if (capacity > 0) {
            builder.add(TEAttributes.MINION_CAPACITY,
                    new AttributeModifier(setModifierId("summoner_capacity"),
                            capacity, AttributeModifier.Operation.ADD_VALUE),
                    equipmentSlotGroup);
        }

        float summonDamage = getSummonDamageBonus(stack);
        if (summonDamage > 0) {
            builder.add(TEAttributes.SUMMON_DAMAGE,
                    new AttributeModifier(setModifierId("summoner_damage"),
                            summonDamage, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                    equipmentSlotGroup);
        }
    }

}
