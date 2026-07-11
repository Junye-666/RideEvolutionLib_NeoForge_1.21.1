package com.jpigeon.rideevolutionlib.compat.geckoLib.block;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GenericBlockEntityRenderer extends GeoBlockRenderer<BaseRiderGeoBlockEntity> {
    public GenericBlockEntityRenderer(BlockEntityRendererProvider.Context context,
                                      GeoModel<BaseRiderGeoBlockEntity> model) {
        super(model);
    }
}

