package com.drowningfish233.reveriegear.client.renderer;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.item.gear.RFGearPumpChargeShotgunItem;
import com.drowningfish233.reveriegear.registry.RFItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.jadenxgamer.elysium_api.api.util.ClientItemData;
import net.jadenxgamer.netherexp.client.rendering.JNERenderType;
import net.jadenxgamer.netherexp.client.rendering.item.PumpChargeShotgunModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.silentchaos512.gear.client.util.GearColorUtils;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;

@OnlyIn(Dist.CLIENT)
public class NetherexpGearRenderer extends BlockEntityWithoutLevelRenderer {

    private static final PumpChargeShotgunModel PUMP_CHARGE_SHOTGUN_MODEL =
            new PumpChargeShotgunModel(Minecraft.getInstance().getEntityModels().bakeLayer(PumpChargeShotgunModel.LAYER));

    public NetherexpGearRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);

        if (!stack.is(RFItems.PUMP_CHARGE_SHOTGUN.gearItem())) {
            return;
        }

        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        LocalPlayer player = Minecraft.getInstance().player;
        float ageInTicks = player == null ? 0.0F : (float) player.tickCount + partialTick;

        int mainColor = GearColorUtils.getBlendedColorForPartInGear(stack,
                GearData.getCoatingOrMainPart(stack) != null
                        ? GearData.getCoatingOrMainPart(stack).get().getType()
                        : PartTypes.MAIN.get());
        int rodColor = GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.ROD.get());

        int pumps = RFGearPumpChargeShotgunItem.getPumps(stack);
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(1.0F, 1.0F, 1.0F);

        ResourceLocation textureMain = ReverieGear.getId("textures/item/pump_charge_shotgun/main_" + pumps + ".png");
        ResourceLocation textureRod = ReverieGear.getId("textures/item/pump_charge_shotgun/rod_" + pumps + ".png");
        ResourceLocation textureGlow = ReverieGear.getId("textures/item/pump_charge_shotgun/glow_" + pumps + ".png");

        PUMP_CHARGE_SHOTGUN_MODEL.setupAnim(player, (RFGearPumpChargeShotgunItem) stack.getItem(), stack, displayContext, ageInTicks);
        PUMP_CHARGE_SHOTGUN_MODEL.root().render(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(textureMain)), packedLight, packedOverlay, mainColor);
        PUMP_CHARGE_SHOTGUN_MODEL.root().render(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(textureRod)), packedLight, packedOverlay, rodColor);
        PUMP_CHARGE_SHOTGUN_MODEL.renderToBuffer(poseStack, buffer.getBuffer(JNERenderType.noShadeEntityCutoutNoCull(textureGlow)), 15728880, packedOverlay, 16777215);

        if (stack.hasFoil()) {
            PUMP_CHARGE_SHOTGUN_MODEL.root().render(poseStack, ItemRenderer.getFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureMain), false, true), packedLight, packedOverlay, mainColor);
            PUMP_CHARGE_SHOTGUN_MODEL.root().render(poseStack, ItemRenderer.getFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureRod), false, true), packedLight, packedOverlay, rodColor);
        }

        if (player != null) {
            if (ClientItemData.has(stack, "isSmoking")) {
                PUMP_CHARGE_SHOTGUN_MODEL.renderSmokeParticles(player, poseStack, displayContext);
            }
            if (ClientItemData.has(stack, "shootFlash")) {
                PUMP_CHARGE_SHOTGUN_MODEL.renderFlashParticles(stack, player, poseStack, displayContext);
            }
        }

        poseStack.popPose();
    }
}
