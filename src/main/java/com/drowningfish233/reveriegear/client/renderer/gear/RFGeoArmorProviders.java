package com.drowningfish233.reveriegear.client.renderer.gear;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;
import software.bernie.geckolib.animatable.client.DeferredGeoRenderProvider;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;


public final class RFGeoArmorProviders {

    private RFGeoArmorProviders() {
    }


    public static Object armor(ResourceLocation model, ResourceLocation texture, ResourceLocation animation) {
        return new ArmorProvider(model, texture, animation);
    }

    private static final class ArmorProvider implements DeferredGeoRenderProvider {

        private final MutableObject<GeoRenderProvider> provider;

        private ArmorProvider(ResourceLocation model, ResourceLocation texture, ResourceLocation animation) {
            this.provider = new MutableObject<GeoRenderProvider>(new ArmorRenderProvider(model, texture, animation));
        }

        @Override
        public MutableObject<GeoRenderProvider> getRenderProvider() {
            return this.provider;
        }
    }

    private static final class ArmorRenderProvider implements GeoRenderProvider {

        private final RFGeoArmorRenderer renderer;

        private ArmorRenderProvider(ResourceLocation model, ResourceLocation texture, ResourceLocation animation) {
            this.renderer = new RFGeoArmorRenderer(new RFGeoArmorModel(model, texture, animation));
        }

        @Override
        public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<T> original) {
            return this.renderer;
        }
    }
}
