package com.drowningfish233.reveriegear.client.renderer;

import com.drowningfish233.reveriegear.ReverieGear;
import com.drowningfish233.reveriegear.registry.RFItems;
import com.drowningfish233.reveriegear.item.gear.RFGearCursedBowItem;
import com.github.L_Ender.cataclysm.client.model.item.*;
import com.github.L_Ender.cataclysm.client.render.CMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.silentchaos512.gear.client.util.GearColorUtils;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;

@OnlyIn(Dist.CLIENT)
public class CataclysmGearRenderer extends BlockEntityWithoutLevelRenderer {

    private static final Infernal_Forge_Model INFERNAL_FORGE_MODEL;
    private static final Meat_Shredder_Model MEAT_SHREDDER_MODEL;
    private static final Cursed_Bow_Model CURSED_BOW_MODEL;
    private static final Wrath_of_Desert_Model SEEKING_BOW_MODEL;
    private static final Wither_Assault_SHoulder_Weapon_Model VOID_ASSAULT_SHOULDER_WEAPON_MODEL;
    private static final Ancient_Spear_Model ANCIENT_SPEAR_MODEL;
    private static final Incinerator_Model THE_INCINERATOR_MODEL;
    private static final Bulwark_of_the_flame_Model FLAME_BULWARK_MODEL;
    private static final The_Annihilator_Model THE_ANNIHILATOR_MODEL;
    private static final Astrape_Model ASTRAPE_MODEL;
    private static final Ceraunus_Item_Model CERAUNUS_MODEL;
    private static final Tidal_Claws_Model TIDAL_CLAWS_MODEL;
    private static final Brontes_Model BRONTES_MODEL;

    public CataclysmGearRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        LocalPlayer player = Minecraft.getInstance().player;
        float ageInTicks = player == null ? 0.0F : (float)player.tickCount + partialTick;

        int mainColor = GearColorUtils.getBlendedColorForPartInGear(stack, GearData.getCoatingOrMainPart(stack) != null ? GearData.getCoatingOrMainPart(stack).get().getType() : PartTypes.MAIN.get());
        int rodColor = GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.ROD.get());
        int tipColor = GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.TIP.get());
        int cordColor = GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.CORD.get());
        int gripColor = GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.GRIP.get());

        if (stack.is(RFItems.INFERNAL_FORGE.gearItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/infernal_forge/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/infernal_forge/rod.png");
            ResourceLocation textureTip = ReverieGear.getId("textures/item/infernal_forge/tip.png");
            ResourceLocation textureLayer = ReverieGear.getId("textures/item/infernal_forge/infernal_forge_layer.png");
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureMain), stack.hasFoil());
            INFERNAL_FORGE_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureRod), stack.hasFoil());
            INFERNAL_FORGE_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            VertexConsumer tipBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureTip), stack.hasFoil());
            if (GearData.hasPartOfType(stack, PartTypes.TIP.get())) {
                INFERNAL_FORGE_MODEL.renderToBuffer(poseStack, tipBuffer, packedLight, packedOverlay, tipColor);
            }
            VertexConsumer layerBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureLayer), stack.hasFoil());
            INFERNAL_FORGE_MODEL.renderToBuffer(poseStack, layerBuffer, packedLight, packedOverlay);
            VertexConsumer layerBufferGlow = ItemRenderer.getArmorFoilBuffer(buffer, CMRenderTypes.eyes(textureLayer), stack.hasFoil());
            INFERNAL_FORGE_MODEL.renderToBuffer(poseStack, layerBufferGlow, packedLight, packedOverlay);
            poseStack.popPose();
        }

        if (stack.is(RFItems.MEAT_SHREDDER.gearItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);

            ResourceLocation textureMain = ReverieGear.getId("textures/item/meat_shredder/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/meat_shredder/rod.png");
            ResourceLocation textureLayer = ReverieGear.getId("textures/item/meat_shredder/meat_shredder_layer.png");
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureMain), stack.hasFoil());
            MEAT_SHREDDER_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureRod), stack.hasFoil());
            MEAT_SHREDDER_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            VertexConsumer layerBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureLayer), stack.hasFoil());
            MEAT_SHREDDER_MODEL.renderToBuffer(poseStack, layerBuffer, packedLight, packedOverlay);
            VertexConsumer layerBufferGlow = ItemRenderer.getArmorFoilBuffer(buffer, CMRenderTypes.eyes(textureLayer), stack.hasFoil());
            MEAT_SHREDDER_MODEL.renderToBuffer(poseStack, layerBufferGlow, packedLight, packedOverlay);
            MEAT_SHREDDER_MODEL.animateStack(stack);
            poseStack.popPose();
        }

        if (stack.is(RFItems.CURSED_BOW.gearItem())) {
            float pullAmount = RFGearCursedBowItem.getPullingAmount(stack, partialTick);
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/cursed_bow/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/cursed_bow/rod.png");
            ResourceLocation textureLayer = ReverieGear.getId("textures/item/cursed_bow/cursed_bow_ghost.png");
            CURSED_BOW_MODEL.setupAnim(null, pullAmount, ageInTicks, 0.0F, 0.0F, 0.0F);
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureMain), stack.hasFoil());
            CURSED_BOW_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureRod), stack.hasFoil());
            CURSED_BOW_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            VertexConsumer layerBuffer = ItemRenderer.getArmorFoilBuffer(buffer, CMRenderTypes.getGhost(textureLayer), stack.hasFoil());
            CURSED_BOW_MODEL.renderToBuffer(poseStack, layerBuffer, packedLight, packedOverlay);
            poseStack.popPose();
        }

        if (stack.is(RFItems.SEEKING_BOW.gearItem())) {
            float pullAmount = RFGearCursedBowItem.getPullingAmount(stack, partialTick);
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/seeking_bow/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/seeking_bow/rod.png");
            ResourceLocation textureString = ReverieGear.getId("textures/item/seeking_bow/string.png");
            ResourceLocation textureLayer = ReverieGear.getId("textures/item/seeking_bow/seeking_bow_ghost.png");
            SEEKING_BOW_MODEL.setupAnim(null, pullAmount, ageInTicks, 0.0F, 0.0F, 0.0F);
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureMain), stack.hasFoil());
            SEEKING_BOW_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureRod), stack.hasFoil());
            SEEKING_BOW_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            VertexConsumer stringBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureString), stack.hasFoil());
            SEEKING_BOW_MODEL.renderToBuffer(poseStack, stringBuffer, packedLight, packedOverlay, GearData.hasPartOfType(stack, PartTypes.GRIP.get()) ? gripColor : cordColor);
            VertexConsumer layerBuffer = ItemRenderer.getArmorFoilBuffer(buffer, CMRenderTypes.getGhost(textureLayer), stack.hasFoil());
            SEEKING_BOW_MODEL.renderToBuffer(poseStack, layerBuffer, packedLight, packedOverlay);
            poseStack.popPose();
        }

        if (stack.is(RFItems.VOID_ASSAULT_SHOULDER_WEAPON.gearItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/void_assault_shoulder_weapon/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/void_assault_shoulder_weapon/rod.png");
            ResourceLocation textureTip = ReverieGear.getId("textures/item/void_assault_shoulder_weapon/tip.png");
            ResourceLocation textureGrip = ReverieGear.getId("textures/item/void_assault_shoulder_weapon/grip.png");
            ResourceLocation textureLayer = ReverieGear.getId("textures/item/void_assault_shoulder_weapon/void_assault_shoulder_weapon_layer.png");
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureMain), stack.hasFoil());
            VOID_ASSAULT_SHOULDER_WEAPON_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureRod), stack.hasFoil());
            VOID_ASSAULT_SHOULDER_WEAPON_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            VertexConsumer tipBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureTip), stack.hasFoil());
            if (GearData.hasPartOfType(stack, PartTypes.TIP.get())) {
                VOID_ASSAULT_SHOULDER_WEAPON_MODEL.renderToBuffer(poseStack, tipBuffer, packedLight, packedOverlay, tipColor);
            }
            VertexConsumer gripBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureGrip), stack.hasFoil());
            if (GearData.hasPartOfType(stack, PartTypes.GRIP.get())) {
                VOID_ASSAULT_SHOULDER_WEAPON_MODEL.renderToBuffer(poseStack, gripBuffer, packedLight, packedOverlay, gripColor);
            }
            VertexConsumer layerBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureLayer), stack.hasFoil());
            VOID_ASSAULT_SHOULDER_WEAPON_MODEL.renderToBuffer(poseStack, layerBuffer, packedLight, packedOverlay);
            VertexConsumer layerBufferGlow = ItemRenderer.getArmorFoilBuffer(buffer, CMRenderTypes.eyes(textureLayer), stack.hasFoil());
            VOID_ASSAULT_SHOULDER_WEAPON_MODEL.renderToBuffer(poseStack, layerBufferGlow, packedLight, packedOverlay);
            poseStack.popPose();
        }

        if (stack.is(RFItems.ANCIENT_SPEAR.gearItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/ancient_spear/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/ancient_spear/rod.png");
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureMain), stack.hasFoil());
            ANCIENT_SPEAR_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureRod), stack.hasFoil());
            ANCIENT_SPEAR_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            poseStack.popPose();
        }

        if (stack.is(RFItems.INCINERATOR.gearItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/incinerator/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/incinerator/rod.png");
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureMain), stack.hasFoil());
            THE_INCINERATOR_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureRod), stack.hasFoil());
            THE_INCINERATOR_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            poseStack.popPose();
        }

        if (stack.is(RFItems.FLAME_BULWARK.gearItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/flame_bulwark/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/flame_bulwark/rod.png");
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureMain), stack.hasFoil());
            FLAME_BULWARK_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureRod), stack.hasFoil());
            FLAME_BULWARK_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            poseStack.popPose();
        }

        if (stack.is(RFItems.ANNIHILATOR.gearItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/annihilator/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/annihilator/rod.png");
            ResourceLocation textureLayer = ReverieGear.getId("textures/item/annihilator/annihilator_ghost.png");
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureMain), stack.hasFoil());
            THE_ANNIHILATOR_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureRod), stack.hasFoil());
            THE_ANNIHILATOR_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            VertexConsumer layerBuffer = ItemRenderer.getArmorFoilBuffer(buffer, CMRenderTypes.getGhost(textureLayer), stack.hasFoil());
            THE_ANNIHILATOR_MODEL.renderToBuffer(poseStack, layerBuffer, packedLight, packedOverlay);
            poseStack.popPose();
        }

        if (stack.is(RFItems.ASTRAPE.gearItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/astrape/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/astrape/rod.png");
            ResourceLocation textureTip = ReverieGear.getId("textures/item/astrape/tip.png");
            ResourceLocation textureGrip = ReverieGear.getId("textures/item/astrape/grip.png");
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureMain), stack.hasFoil());
            ASTRAPE_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureRod), stack.hasFoil());
            ASTRAPE_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            VertexConsumer tipBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureTip), stack.hasFoil());
            if (GearData.hasPartOfType(stack, PartTypes.TIP.get())) {
                ASTRAPE_MODEL.renderToBuffer(poseStack, tipBuffer, packedLight, packedOverlay, tipColor);
            }
            VertexConsumer gripBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureGrip), stack.hasFoil());
            if (GearData.hasPartOfType(stack, PartTypes.GRIP.get())) {
                ASTRAPE_MODEL.renderToBuffer(poseStack, gripBuffer, packedLight, packedOverlay, gripColor);
            }
            poseStack.popPose();
        }

        if (stack.is(RFItems.CERAUNUS.gearItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/ceraunus/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/ceraunus/rod.png");
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureMain), stack.hasFoil());
            CERAUNUS_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureRod), stack.hasFoil());
            CERAUNUS_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            poseStack.popPose();
        }

        if (stack.is(RFItems.TIDAL_CLAWS.gearItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/tidal_claws/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/tidal_claws/rod.png");
            ResourceLocation textureTip = ReverieGear.getId("textures/item/tidal_claws/tip.png");
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureMain), stack.hasFoil());
            TIDAL_CLAWS_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, packedOverlay, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureRod), stack.hasFoil());
            TIDAL_CLAWS_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, packedOverlay, rodColor);
            VertexConsumer tipBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(textureTip), stack.hasFoil());
            if (GearData.hasPartOfType(stack, PartTypes.TIP.get())) {
                TIDAL_CLAWS_MODEL.renderToBuffer(poseStack, tipBuffer, packedLight, packedOverlay, tipColor);
            }
            poseStack.popPose();
        }

        if (stack.is(RFItems.BRONTES.gearItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            ResourceLocation textureMain = ReverieGear.getId("textures/item/brontes/main.png");
            ResourceLocation textureRod = ReverieGear.getId("textures/item/brontes/rod.png");
            ResourceLocation textureTip = ReverieGear.getId("textures/item/brontes/tip.png");
            ResourceLocation textureGrip = ReverieGear.getId("textures/item/brontes/grip.png");
            ResourceLocation textureLayer = ReverieGear.getId("textures/item/brontes/brontes_layer.png");
            VertexConsumer mainBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureMain), stack.hasFoil());
            BRONTES_MODEL.renderToBuffer(poseStack, mainBuffer, packedLight, OverlayTexture.NO_OVERLAY, mainColor);
            VertexConsumer rodBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureRod), stack.hasFoil());
            BRONTES_MODEL.renderToBuffer(poseStack, rodBuffer, packedLight, OverlayTexture.NO_OVERLAY, rodColor);
            VertexConsumer tipBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureTip), stack.hasFoil());
            if (GearData.hasPartOfType(stack, PartTypes.TIP.get())) {
                BRONTES_MODEL.renderToBuffer(poseStack, tipBuffer, packedLight, OverlayTexture.NO_OVERLAY, tipColor);
            }
            VertexConsumer gripBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureGrip), stack.hasFoil());
            if (GearData.hasPartOfType(stack, PartTypes.GRIP.get())) {
                BRONTES_MODEL.renderToBuffer(poseStack, gripBuffer, packedLight, OverlayTexture.NO_OVERLAY, gripColor);
            }
            VertexConsumer layerBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(textureLayer), stack.hasFoil());
            BRONTES_MODEL.renderToBuffer(poseStack, layerBuffer, packedLight, OverlayTexture.NO_OVERLAY);
            VertexConsumer layerBufferGlow = ItemRenderer.getArmorFoilBuffer(buffer, CMRenderTypes.eyes(textureLayer), stack.hasFoil());
            BRONTES_MODEL.renderToBuffer(poseStack, layerBufferGlow, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

    }

    static {
        INFERNAL_FORGE_MODEL = new Infernal_Forge_Model();
        MEAT_SHREDDER_MODEL = new Meat_Shredder_Model();
        CURSED_BOW_MODEL = new Cursed_Bow_Model();
        SEEKING_BOW_MODEL = new Wrath_of_Desert_Model();
        VOID_ASSAULT_SHOULDER_WEAPON_MODEL = new Wither_Assault_SHoulder_Weapon_Model();
        ANCIENT_SPEAR_MODEL = new Ancient_Spear_Model();
        THE_INCINERATOR_MODEL = new Incinerator_Model();
        FLAME_BULWARK_MODEL = new Bulwark_of_the_flame_Model();
        THE_ANNIHILATOR_MODEL = new The_Annihilator_Model();
        ASTRAPE_MODEL = new Astrape_Model();
        CERAUNUS_MODEL = new Ceraunus_Item_Model();
        TIDAL_CLAWS_MODEL = new Tidal_Claws_Model();
        BRONTES_MODEL = new Brontes_Model();
    }
}

