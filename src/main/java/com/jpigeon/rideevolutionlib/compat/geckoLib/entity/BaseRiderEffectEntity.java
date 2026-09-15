package com.jpigeon.rideevolutionlib.compat.geckoLib.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseRiderEffectEntity extends Entity implements GeoEntity {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final String modId;
    protected final String riderName;
    protected final String entityName;

    protected final Map<String, AnimationController<BaseRiderEffectEntity>> controllers = new HashMap<>();

    public BaseRiderEffectEntity(EntityType<?> entityType, Level level,
                                 String modId, String riderName, String entityName) {
        super(entityType, level);
        this.modId = modId;
        this.riderName = riderName;
        this.entityName = entityName;

        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registerAnimationControllers(registrar);
    }

    /**
     * 子类必须实现的动画控制器注册方法
     */
    protected abstract void registerAnimationControllers(AnimatableManager.ControllerRegistrar registrar);

    /**
     * 添加动画控制器并存储引用
     */
    protected void addController(AnimatableManager.ControllerRegistrar registrar, String name,
                                 AnimationController<BaseRiderEffectEntity> controller) {
        controllers.put(name, controller);
        registrar.add(controller);
    }

    /**
     * 获取指定的动画控制器
     */
    @Nullable
    public AnimationController<BaseRiderEffectEntity> getController(String name) {
        return controllers.get(name);
    }

    /**
     * 创建简单的循环动画控制器
     */
    protected AnimationController<BaseRiderEffectEntity> createLoopController(String animationName) {
        return new AnimationController<>(this, animationName + "_controller", 0, state -> {
            state.getController().setAnimation(RawAnimation.begin().thenLoop(animationName));
            return PlayState.CONTINUE;
        });
    }

    /**
     * 创建单次播放动画控制器
     */
    protected AnimationController<BaseRiderEffectEntity> createOnceController(String animationName) {
        return new AnimationController<>(this, animationName + "_controller", 0, state -> {
            state.getController().setAnimation(
                    RawAnimation.begin().then(animationName, Animation.LoopType.HOLD_ON_LAST_FRAME)
            );
            return PlayState.CONTINUE;
        });
    }

    //========== 资源路径生成工具方法 ==========

    /**
     * 获取模型资源路径
     */
    protected ResourceLocation getModelPath() {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "geo/" + riderName.toLowerCase() + "/entity/" +
                        riderName.toLowerCase() + "_" + entityName.toLowerCase() + ".geo.json");
    }

    /**
     * 获取纹理资源路径
     */
    protected ResourceLocation getTexturePath() {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "textures/entity/" + riderName.toLowerCase() + "/" +
                        riderName.toLowerCase() + "_" + entityName.toLowerCase() + ".png");
    }

    /**
     * 获取动画资源路径
     */
    protected ResourceLocation getAnimationPath() {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "animations/" + riderName.toLowerCase() + "/entity/" +
                        riderName.toLowerCase() + "_" + entityName.toLowerCase() + ".animation.json");
    }

    //========== GeckoLib接口实现 ==========

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object entity) {
        return tickCount;
    }

    //========== 实体基础方法 ==========


    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    //========== 可选的透明度支持 ==========

    /**
     * 获取当前透明度（0.0-1.0），子类可重写
     */
    public float getCurrentAlpha() {
        return 1.0f;
    }

    /**
     * 是否应用透明度效果
     */
    public boolean shouldApplyTransparency() {
        return false;
    }
}

