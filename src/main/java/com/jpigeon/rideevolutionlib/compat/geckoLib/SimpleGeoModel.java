package com.jpigeon.rideevolutionlib.compat.geckoLib;

import com.jpigeon.rideevolutionlib.compat.util.GeoResourcePaths;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

/**
 * 通用 GeoModel：从 {@link GeoResourcePaths} 读三个路径。
 * <p>可选注入一个 {@code setCustomAnimations} 钩子（例如骑士盔甲的 driver 骨骼约束）。
 */
public class SimpleGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
    private final GeoResourcePaths paths;
    private final @Nullable BiConsumer<SimpleGeoModel<T>, AnimationState<T>> customAnimHook;

    public SimpleGeoModel(GeoResourcePaths paths) {
        this(paths, null);
    }

    public SimpleGeoModel(GeoResourcePaths paths,
                          @Nullable BiConsumer<SimpleGeoModel<T>, AnimationState<T>> customAnimHook) {
        this.paths = paths;
        this.customAnimHook = customAnimHook;
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> state) {
        super.setCustomAnimations(animatable, instanceId, state);
        if (customAnimHook != null) customAnimHook.accept(this, state);
    }

    @Override public ResourceLocation getModelResource(T a)     { return paths.model(); }
    @Override public ResourceLocation getTextureResource(T a)   { return paths.texture(); }
    @Override public ResourceLocation getAnimationResource(T a) { return paths.animation(); }
}
