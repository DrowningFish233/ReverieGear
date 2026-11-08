package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.ReverieGear;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.silentchaos512.gear.api.item.GearType;

import java.util.function.Supplier;

public class RFGearTravelerArmor extends RFGearSetArmorItem {

    public RFGearTravelerArmor(Supplier<GearType> gearType, Type armorType) {
        super(gearType, armorType);
    }

    @Override
    public Object getRenderProvider() {
        return geoRenderProvider(
                ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/spectral_spelunker_armor.geo.json"),
                ReverieGear.getId("textures/models/armor/traveler_geo.png"),
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json"));
    }

    @Override
    protected float getArmorMultiplier() {
        return 0.85f;
    }

    @Override
    protected float getToughnessMultiplier() {
        return 0.85f;
    }

    @Override
    protected void addSetAttributeModifiers(ItemStack stack, ItemAttributeModifiers.Builder builder,
                                            EquipmentSlotGroup equipmentSlotGroup) {
        builder.add(Attributes.MOVEMENT_SPEED,
                new AttributeModifier(setModifierId("traveler_movement_speed"),
                        0.03f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                equipmentSlotGroup);

        Holder<Attribute> distance = combatRollAttribute("distance");
        if (distance != null) {
            builder.add(distance,
                    new AttributeModifier(setModifierId("traveler_roll_distance"),
                            0.25f, AttributeModifier.Operation.ADD_VALUE),
                    equipmentSlotGroup);
        }

    }

}
