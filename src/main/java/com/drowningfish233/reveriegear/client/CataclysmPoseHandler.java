package com.drowningfish233.reveriegear.client;

import com.drowningfish233.reveriegear.registry.RFItems;
import com.github.L_Ender.cataclysm.client.event.EventPosePlayerHand;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

final class CataclysmPoseHandler {
    private CataclysmPoseHandler() {
    }

    static void onPoseHand(EventPosePlayerHand event) {
        LivingEntity player = (LivingEntity) event.getEntityIn();
        if (player.getItemInHand(InteractionHand.OFF_HAND).is(RFItems.ANNIHILATOR.gearItem())
                && player.getItemInHand(InteractionHand.MAIN_HAND).is(RFItems.ANNIHILATOR.gearItem())
                && player.isUsingItem()) {
            if (player.getMainArm() == HumanoidArm.LEFT) {
                event.getModel().rightArm.xRot = event.getModel().rightArm.xRot * 0.5F - (float) Math.PI;
                event.getModel().rightArm.yRot = 0.0F;
            } else {
                event.getModel().leftArm.xRot = event.getModel().leftArm.xRot * 0.5F - (float) Math.PI;
                event.getModel().leftArm.yRot = 0.0F;
            }
        }
    }
}
