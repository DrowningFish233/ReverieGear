package com.drowningfish233.reveriegear.item.gear;

import com.drowningfish233.reveriegear.ReverieGear;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.function.Supplier;

public class RFGearRangerArmor extends RFGearSetArmorItem {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private static final float PRE_DEAD_KING_PROJECTILE_CAP = 0.10f;
    private static final float PROJECTILE_CAP = 0.20f;

    public RFGearRangerArmor(Supplier<GearType> gearType, Type armorType) {
        super(gearType, armorType);
    }

    @Override
    public Object getRenderProvider() {
        return geoRenderProvider(
                ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/bounty_hunter_armor.geo.json"),
                ReverieGear.getId("textures/models/armor/ranger_geo.png"),
                ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json"));
    }

    @Override
    protected float getArmorMultiplier() {
        return 0.85f;
    }

    @Override
    protected float getToughnessMultiplier() {
        return 1.0f;
    }

    public float getRangedDamage(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.RANGED_DAMAGE);
    }

    public static int wornPieceCount(Player player) {
        int count = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof RFGearRangerArmor && !GearHelper.isBroken(stack)) {
                count++;
            }
        }
        return count;
    }

    public static boolean isWearingFullSet(Player player) {
        return wornPieceCount(player) == ARMOR_SLOTS.length;
    }

    public float getProjectileDamageBonus(ItemStack stack) {
        return Mth.clamp(Math.max(0.0f, getRangedDamage(stack)) * 0.04f, 0.0f, PROJECTILE_CAP);
    }

    @Override
    protected void addSetAttributeModifiers(ItemStack stack, ItemAttributeModifiers.Builder builder,
                                            EquipmentSlotGroup equipmentSlotGroup) {
        float projectileDamage = getProjectileDamageBonus(stack);
        if (projectileDamage > 0) {
            builder.add(ALObjects.Attributes.PROJECTILE_DAMAGE,
                    new AttributeModifier(setModifierId("ranger_projectile_damage"),
                            projectileDamage, AttributeModifier.Operation.ADD_VALUE),
                    equipmentSlotGroup);
        }
    }

}
