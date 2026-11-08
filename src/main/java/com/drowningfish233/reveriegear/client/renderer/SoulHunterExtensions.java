package com.drowningfish233.reveriegear.client.renderer;

import com.drowningfish233.reveriegear.registry.RFItems;
import com.sammy.malum.client.model.SoulHunterArmorModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.silentchaos512.gear.client.util.GearColorUtils;
import net.silentchaos512.gear.setup.gear.PartTypes;
import org.jetbrains.annotations.NotNull;

public class SoulHunterExtensions implements IClientItemExtensions {

    @Override
    public @NotNull HumanoidModel<?> getGenericArmorModel(@NotNull LivingEntity entity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot armorSlot, @NotNull HumanoidModel<?> original) {
        SoulHunterArmorModel armorModel = new SoulHunterArmorModel(
                Minecraft.getInstance().getEntityModels().bakeLayer(SoulHunterArmorModel.LAYER)
        );

        armorModel.slot = armorSlot;
        armorModel.copyFromDefault(original);

        armorModel.body.xRot = original.body.xRot;
        armorModel.body.yRot = original.body.yRot;
        armorModel.body.zRot = original.body.zRot;

        armorModel.leftArm.xRot = original.leftArm.xRot;
        armorModel.leftArm.yRot = original.leftArm.yRot;
        armorModel.leftArm.zRot = original.leftArm.zRot;

        armorModel.rightArm.xRot = original.rightArm.xRot;
        armorModel.rightArm.yRot = original.rightArm.yRot;
        armorModel.rightArm.zRot = original.rightArm.zRot;

        armorModel.leftLeg.xRot = original.leftLeg.xRot;
        armorModel.leftLeg.yRot = original.leftLeg.yRot;
        armorModel.leftLeg.zRot = original.leftLeg.zRot;
        armorModel.rightLeg.xRot = original.rightLeg.xRot;
        armorModel.rightLeg.yRot = original.rightLeg.yRot;
        armorModel.rightLeg.zRot = original.rightLeg.zRot;

        armorModel.leftLegging.xRot = armorModel.leftLeg.xRot;
        armorModel.leftLegging.yRot = armorModel.leftLeg.yRot;
        armorModel.leftLegging.zRot = armorModel.leftLeg.zRot;
        armorModel.rightLegging.xRot = armorModel.rightLeg.xRot;
        armorModel.rightLegging.yRot = armorModel.rightLeg.yRot;
        armorModel.rightLegging.zRot = armorModel.rightLeg.zRot;

        armorModel.leftFoot.xRot = armorModel.leftLeg.xRot;
        armorModel.leftFoot.yRot = armorModel.leftLeg.yRot;
        armorModel.leftFoot.zRot = armorModel.leftLeg.zRot;
        armorModel.rightFoot.xRot = armorModel.rightLeg.xRot;
        armorModel.rightFoot.yRot = armorModel.rightLeg.yRot;
        armorModel.rightFoot.zRot = armorModel.rightLeg.zRot;

        armorModel.setupAnim(entity, 0, 0, entity.tickCount, entity.getYRot(), entity.getXRot());

        ClientHooks.copyModelProperties(original, armorModel);

        return armorModel;
    }

    @Override
    public int getArmorLayerTintColor(ItemStack stack, LivingEntity entity, ArmorMaterial.Layer layer, int layerIdx, int fallbackColor) {
        if (layerIdx == 0) {
            return GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.MAIN.get());
        }
        return -1;
    }

    public static void register(RegisterClientExtensionsEvent event) {
        IClientItemExtensions extensions = new SoulHunterExtensions();
        event.registerItem(extensions,
                RFItems.SOUL_HUNTER_HELMET.gearItem(),
                RFItems.SOUL_HUNTER_CHESTPLATE.gearItem(),
                RFItems.SOUL_HUNTER_LEGGINGS.gearItem(),
                RFItems.SOUL_HUNTER_BOOTS.gearItem()
        );
    }
}
