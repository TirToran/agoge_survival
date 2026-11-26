package net.torantyr.groundwork_sling.items.custom;

import net.minecraft.resources.ResourceLocation;
import net.torantyr.groundwork_sling.groundwork_sling;
import software.bernie.geckolib.model.GeoModel;

public class SlingModel extends GeoModel<SlingItem> {

    @Override
    public ResourceLocation getModelResource(SlingItem animatable) {
        return new ResourceLocation(groundwork_sling.MODID, "geo/item/sling.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SlingItem animatable) {
        return new ResourceLocation(groundwork_sling.MODID, "textures/item/sling_spinning.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SlingItem animatable) {
        return new ResourceLocation(groundwork_sling.MODID, "animations/item/sling.animation.json");
    }
}