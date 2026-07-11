package com.jpigeon.rideevolutionlib.compat.geckoLib.block;

import com.jpigeon.rideevolutionlib.compat.geckoLib.AnimationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;

import static software.bernie.geckolib.animation.Animation.LoopType.*;

public abstract class BaseRiderGeoBlockEntity extends BlockEntity implements GeoBlockEntity {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final String modId;
    protected final String riderName;
    protected final String blockName;
    protected final boolean animated;
    protected final AnimationManager<BaseRiderGeoBlockEntity> animationManager;
    protected final Map<String, AnimationController<BaseRiderGeoBlockEntity>> controllers = new HashMap<>();

    public BaseRiderGeoBlockEntity(String modId, String riderName, String blockName, BlockEntityType<?> type, BlockPos pos, BlockState state, boolean animated) {
        super(type, pos, state);
        this.modId = modId;
        this.riderName = riderName;
        this.blockName = blockName;
        this.animated = animated;
        this.animationManager = new AnimationManager<>(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registerAnimationControllers(registrar);
    }

    protected abstract void registerAnimationControllers(AnimatableManager.ControllerRegistrar registrar);

    protected void addController(AnimatableManager.ControllerRegistrar registrar, String name,
                                 AnimationController<BaseRiderGeoBlockEntity> controller) {
        controllers.put(name, controller);
        animationManager.registerController(name, controller);
        registrar.add(controller);
    }

    public void setAnimState(String state) {
        animationManager.setState(state);
    }

    public String getCurrentAnimState() {
        return animationManager.getCurrentState();
    }

    protected AnimationController<BaseRiderGeoBlockEntity> createLoopController(
            String animationName) {
        return createStateController(animationName, LOOP);
    }

    protected AnimationController<BaseRiderGeoBlockEntity> createOnceController(
            String animationName) {
        return createStateController(animationName, PLAY_ONCE);
    }

    protected AnimationController<BaseRiderGeoBlockEntity> createHoldController(
            String animationName) {
        return createStateController(animationName, HOLD_ON_LAST_FRAME);
    }

    protected AnimationController<BaseRiderGeoBlockEntity> createStateController(
            String animationName, Animation.LoopType loopType) {
        return new AnimationController<>(this, animationName + "_controller", 0, state -> {
            // 只有当管理器当前状态匹配时才播放动画
            if (animationManager.getCurrentState().equals(animationName)) {
                if (loopType.equals(LOOP)) {
                    state.getController().setAnimation(RawAnimation.begin().thenLoop(animationName));
                } else if (loopType.equals(PLAY_ONCE)) {
                    state.getController().setAnimation(RawAnimation.begin().then(animationName, PLAY_ONCE));
                } else if (loopType.equals(HOLD_ON_LAST_FRAME)) {
                    state.getController().setAnimation(RawAnimation.begin().then(animationName, HOLD_ON_LAST_FRAME));
                }
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // 资源路径生成工具方法
    protected ResourceLocation getModelPath() {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "geo/" + riderName.toLowerCase() + "/block/" + blockName.toLowerCase() + ".geo.json");
    }

    protected ResourceLocation getTexturePath() {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "textures/block/" + riderName.toLowerCase() + "/" + blockName.toLowerCase() + ".png");
    }

    protected ResourceLocation getAnimationPath() {
        if (animated) {
            return ResourceLocation.fromNamespaceAndPath(modId,
                    "animations/" + riderName.toLowerCase() + "/block/" + blockName.toLowerCase() + ".animation.json");
        }
        return ResourceLocation.fromNamespaceAndPath(modId,
                "animations/no_block.animation.json");
    }
}