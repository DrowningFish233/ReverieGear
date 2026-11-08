package com.drowningfish233.reveriegear.client.renderer.gear;

import com.drowningfish233.reveriegear.item.gear.RFGearSetArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RFGeoArmorModel extends GeoModel<RFGearSetArmorItem> {

    private final ResourceLocation modelLocation;
    private final ResourceLocation textureLocation;
    private final ResourceLocation animationLocation;

    public RFGeoArmorModel(ResourceLocation modelLocation, ResourceLocation textureLocation, ResourceLocation animationLocation) {
        this.modelLocation = modelLocation;
        this.textureLocation = textureLocation;
        this.animationLocation = animationLocation;
    }

    @Override
    public ResourceLocation getModelResource(RFGearSetArmorItem animatable) {
        return this.modelLocation;
    }

    @Override
    public ResourceLocation getTextureResource(RFGearSetArmorItem animatable) {
        return this.textureLocation;
    }

    @Override
    public ResourceLocation getAnimationResource(RFGearSetArmorItem animatable) {
        return this.animationLocation;
    }
}
