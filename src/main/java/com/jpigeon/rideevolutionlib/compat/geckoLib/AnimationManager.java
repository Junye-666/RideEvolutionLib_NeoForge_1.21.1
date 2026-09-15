package com.jpigeon.rideevolutionlib.compat.geckoLib;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.HashMap;
import java.util.Map;

import static software.bernie.geckolib.animation.Animation.LoopType.*;

/**
 * 简化版动画状态管理器
 * 只做一件事：管理动画状态切换
 */
public class AnimationManager<T extends GeoAnimatable> {
    private final T animatable;
    private final Map<String, AnimationController<T>> controllers = new HashMap<>();
    private String currentState = "idle";

    public AnimationManager(T animatable) {
        this.animatable = animatable;
    }

    /**
     * 注册控制器
     */
    public void registerController(String name, AnimationController<T> controller) {
        controllers.put(name, controller);
    }

    /**
     * 切换状态
     */
    public void setState(String stateName) {
        if (!currentState.equals(stateName)) {
            currentState = stateName;
            // 重置所有控制器
            controllers.values().forEach(AnimationController::forceAnimationReset);
        }
    }

    /**
     * 获取当前状态
     */
    public String getCurrentState() {
        return currentState;
    }

    /**
     * 获取指定控制器
     */
    public AnimationController<T> getController(String name) {
        return controllers.get(name);
    }

    public T getAnimatable() {
        return animatable;
    }

    /**
     * 创建一个状态驱动的控制器：仅当 {@link #getCurrentState()} 与动画名一致时播放。
     * <p>
     * 注意：本方法 <b>不</b> 自动调用 {@link #registerController}。
     * 控制器通过 {@code addController} 注册，与现有约定一致。
     */
    public AnimationController<T> create(String animName, Animation.LoopType loopType) {
        return new AnimationController<>(animatable, animName + "_controller", 0, state -> {
            if (!currentState.equals(animName)) return PlayState.STOP;

            RawAnimation raw;
            if (loopType == Animation.LoopType.LOOP) {
                raw = RawAnimation.begin().thenLoop(animName);
            } else if (loopType == Animation.LoopType.PLAY_ONCE) {
                raw = RawAnimation.begin().then(animName, Animation.LoopType.PLAY_ONCE);
            } else if (loopType == Animation.LoopType.HOLD_ON_LAST_FRAME) {
                raw = RawAnimation.begin().then(animName, Animation.LoopType.HOLD_ON_LAST_FRAME);
            } else {
                // 未知 LoopType 兜底：当作 loop 处理，避免 NPE / 静默停止
                raw = RawAnimation.begin().thenLoop(animName);
            }

            state.getController().setAnimation(raw);
            return PlayState.CONTINUE;
        });
    }

    public AnimationController<T> loop(String animName) {
        return create(animName, LOOP);
    }

    public AnimationController<T> once(String animName) {
        return create(animName, PLAY_ONCE);
    }

    public AnimationController<T> hold(String animName) {
        return create(animName, HOLD_ON_LAST_FRAME);
    }
}
