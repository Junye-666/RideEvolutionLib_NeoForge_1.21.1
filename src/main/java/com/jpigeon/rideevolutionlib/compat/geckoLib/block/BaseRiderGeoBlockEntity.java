package com.jpigeon.rideevolutionlib.compat.geckoLib.block;

import com.jpigeon.rideevolutionlib.compat.util.GeoAnimatableSupport;
import com.jpigeon.rideevolutionlib.compat.util.GeoResourcePaths;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;

/**
 * 通用骑士 Geo 方块实体基类。
 */
public abstract class BaseRiderGeoBlockEntity extends BlockEntity implements GeoBlockEntity {
    protected final String modId;
    protected final String riderName;
    protected final String blockName;
    protected final boolean animated;

    protected final GeoAnimatableSupport<BaseRiderGeoBlockEntity> support;
    protected final GeoResourcePaths paths;

    public BaseRiderGeoBlockEntity(String modId, String riderName, String blockName,
                                   BlockEntityType<?> type, BlockPos pos, BlockState state,
                                   boolean animated) {
        super(type, pos, state);
        this.modId = modId;
        this.riderName = riderName;
        this.blockName = blockName;
        this.animated = animated;
        this.paths = GeoResourcePaths.block(modId, riderName, blockName, animated);
        this.support = new GeoAnimatableSupport<>(this);
    }

    // ==================== 动画 ====================

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registerAnimationControllers(registrar);
    }

    protected abstract void registerAnimationControllers(AnimatableManager.ControllerRegistrar registrar);

    protected void addController(AnimatableManager.ControllerRegistrar registrar,
                                 String name,
                                 AnimationController<BaseRiderGeoBlockEntity> controller) {
        support.register(name, controller);
        registrar.add(controller);
    }

    public void setAnimState(String state) {
        support.setState(state);
    }

    public String getCurrentAnimState() {
        return support.getState();
    }

    protected AnimationController<BaseRiderGeoBlockEntity> createLoopController(String anim) {
        return support.loop(anim);
    }

    protected AnimationController<BaseRiderGeoBlockEntity> createOnceController(String anim) {
        return support.once(anim);
    }

    protected AnimationController<BaseRiderGeoBlockEntity> createHoldController(String anim) {
        return support.hold(anim);
    }

    // ==================== GeoAnimatable ====================

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return support.cache();
    }

    // ==================== 资源路径 ====================

    protected ResourceLocation getModelPath() {
        return paths.model();
    }

    protected ResourceLocation getTexturePath() {
        return paths.texture();
    }

    protected ResourceLocation getAnimationPath() {
        return paths.animation();
    }
}