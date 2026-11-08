package com.drowningfish233.reveriegear.client.renderer.gear;

import com.drowningfish233.reveriegear.item.gear.RFGearArmorItem;
import com.drowningfish233.reveriegear.item.gear.RFGearSetArmorItem;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.util.GearHelper;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.Color;

public class RFGeoArmorRenderer extends GeoArmorRenderer<RFGearSetArmorItem> {

    public RFGeoArmorRenderer(GeoModel<RFGearSetArmorItem> model) {
        super(model);
    }

    @Override
    public Color getRenderColor(RFGearSetArmorItem animatable, float partialTick, int packedLight) {
        ItemStack stack = this.getCurrentStack();
        if (stack == null || stack.isEmpty()) {
            return Color.WHITE;
        }
        if (GearHelper.isBroken(stack)) {
            return Color.ofARGB(0, 0, 0, 0);
        }
        return Color.ofOpaque(RFGearArmorItem.getArmorColor(stack));
    }
}
