package com.drowningfish233.reveriegear.client.renderer;

import com.drowningfish233.reveriegear.registry.RFItems;
import com.drowningfish233.reveriegear.item.gear.RFGearMalignantStrongholdArmor;
import com.sammy.malum.client.model.MalignantStrongholdArmorModel;
import com.sammy.malum.client.scarf.ScarfRenderHandler;
import com.sammy.malum.common.data.component.ItemSkinComponent;
import com.sammy.malum.config.ClientConfig;
import com.sammy.malum.registry.client.MalumRenderTypeTokens;
import com.sammy.malum.registry.common.item.MalumDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
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
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;

import java.awt.*;

public class MalignantStrongholdExtensions implements IClientItemExtensions {

    @Override
    public @NotNull HumanoidModel<?> getGenericArmorModel(@NotNull LivingEntity entity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot armorSlot, @NotNull HumanoidModel<?> original) {
        MalignantStrongholdArmorModel armorModel = new MalignantStrongholdArmorModel(
                Minecraft.getInstance().getEntityModels().bakeLayer(MalignantStrongholdArmorModel.LAYER)
        );

        armorModel.slot = armorSlot;
        armorModel.copyFromDefault(original);

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

        if (armorSlot == EquipmentSlot.CHEST) {
            ItemSkinComponent skin = itemStack.get(MalumDataComponents.APPLIED_ITEM_SKIN);
            var scarfToken = skin != null
                    ? RenderTypeToken.createToken(ResourceLocation.fromNamespaceAndPath(skin.name().getNamespace(), "textures/vfx/scarf/" + skin.name().getPath() + ".png"))
                    : MalumRenderTypeTokens.SCARF;

            ScarfRenderHandler.addScarfRenderer(entity, (consumer) -> {
                boolean noSkin = skin == null;
                int scarfCount = noSkin ? 2 : 1;
                float scale = noSkin ? 0.35F : 0.4F;
                float endingScale = noSkin ? 0.45F : 1.0F;
                float offset = noSkin ? 0.25F : 0.2F;

                for (int i = 0; i < scarfCount; ++i) {
                    ScarfRenderHandler.ScarfRenderData data = new ScarfRenderHandler.ScarfRenderData(
                            scarfToken,
                            ClientConfig.SCARF_LENGTH.getConfigValue()
                    ).setPredicate(() -> {
                        ItemStack stack = entity.getItemBySlot(armorSlot);
                        return !stack.isEmpty() && stack.getItem() instanceof RFGearMalignantStrongholdArmor;
                    }).setScale(scale).setEndingScale(endingScale).setHorizontalOffset(i == 0 ? -offset : offset);

                    if (noSkin) {
                        data.setSecondaryColor(new Color(150, 150, 150));
                    }

                    consumer.accept(data);
                }
            });
        }

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
        IClientItemExtensions extensions = new MalignantStrongholdExtensions();
        event.registerItem(extensions,
                RFItems.MALIGNANT_STRONGHOLD_HELMET.gearItem(),
                RFItems.MALIGNANT_STRONGHOLD_CHESTPLATE.gearItem(),
                RFItems.MALIGNANT_STRONGHOLD_LEGGINGS.gearItem(),
                RFItems.MALIGNANT_STRONGHOLD_BOOTS.gearItem()
        );
    }
}
