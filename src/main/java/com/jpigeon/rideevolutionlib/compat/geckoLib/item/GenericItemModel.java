package com.jpigeon.rideevolutionlib.compat.geckoLib.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericItemModel extends GeoModel<BaseRiderGeoItem> {
    private final ResourceLocation modelPath;
    private final ResourceLocation texturePath;
    private final ResourceLocation animationPath;

    public GenericItemModel(ResourceLocation modelPath, ResourceLocation texturePath,
                            ResourceLocation animationPath) {
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.animationPath = animationPath;
    }

    @Override
    public ResourceLocation getModelResource(BaseRiderGeoItem item) {
        return modelPath;
    }

    @Override
    public ResourceLocation getTextureResource(BaseRiderGeoItem item) {
        return texturePath;
    }

    @Override
    public ResourceLocation getAnimationResource(BaseRiderGeoItem item) {
        return animationPath;
    }
}
