package com.jpigeon.rideevolutionlib.compat.geckoLib.block;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericBlockEntityModel extends GeoModel<BaseRiderGeoBlockEntity> {
    private final ResourceLocation modelPath;
    private final ResourceLocation texturePath;
    private final ResourceLocation animationPath;

    public GenericBlockEntityModel(
            ResourceLocation modelPath,
            ResourceLocation texturePath,
            ResourceLocation animationPath
    ) {
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.animationPath = animationPath;
    }

    @Override
    public ResourceLocation getModelResource(BaseRiderGeoBlockEntity animatable) {
        return modelPath;
    }

    @Override
    public ResourceLocation getTextureResource(BaseRiderGeoBlockEntity animatable) {
        return texturePath;
    }

    @Override
    public ResourceLocation getAnimationResource(BaseRiderGeoBlockEntity animatable) {
        return animationPath;
    }
}

