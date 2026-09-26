package com.jpigeon.rideevolutionlib.compat.util;

import com.jpigeon.rideevolutionlib.compat.geckoLib.AnimationManager;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 一个 GeoAnimatable 的通用"支撑骨架"：缓存 + 动画状态机。
 * <p>用组合替代继承：每个 Base 类持有一个 {@code GeoAnimatableSupport<Self>}，
 * 通过它暴露 {@code loop/once/hold/setState}。
 */
public final class GeoAnimatableSupport<T extends GeoAnimatable> {
    private final AnimatableInstanceCache cache;
    private final AnimationManager<T> manager;

    public GeoAnimatableSupport(T owner) {
        this.cache = GeckoLibUtil.createInstanceCache(owner);
        this.manager = new AnimationManager<>(owner);
    }

    public AnimatableInstanceCache cache() {
        return cache;
    }

    public AnimationManager<T> manager() {
        return manager;
    }

    public void setState(String s) {
        manager.setState(s);
    }

    public String getState() {
        return manager.getCurrentState();
    }

    public AnimationController<T> loop(String name) {
        return manager.loop(name);
    }

    public AnimationController<T> once(String name) {
        return manager.once(name);
    }

    public AnimationController<T> hold(String name) {
        return manager.hold(name);
    }

    public void register(String name, AnimationController<T> controller) {
        manager.registerController(name, controller);
    }

    public AnimationController<T> get(String name) {
        return manager.getController(name);
    }
}