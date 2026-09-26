package com.jpigeon.rideevolutionlib.compat.geckoLib.entity;

import com.jpigeon.rideevolutionlib.compat.util.GeoResourcePaths;
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

/**
 * 通用骑士 Geo 效果实体基类。
 * <p>
 * 不使用动画状态机：注册的 controller 一律"无条件播放"。
 * 特效实体通常只播一个动画，播完由 {@code tick()} 里的生命周期逻辑 discard。
 * <p>
 * 如需在外部动态切换某个 controller 的动画，可通过 {@link #getController(String)}
 * 拿到 controller 引用后自行操作。
 */
public abstract class BaseRiderEffectEntity extends Entity implements GeoEntity {
    protected final String modId;
    protected final String riderName;
    protected final String entityName;

    protected final GeoResourcePaths paths;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 名称 → 控制器。供 {@link #getController(String)} 使用。
     */
    protected final Map<String, AnimationController<BaseRiderEffectEntity>> controllers = new HashMap<>();

    public BaseRiderEffectEntity(EntityType<?> entityType, Level level,
                                 String modId, String riderName, String entityName) {
        super(entityType, level);
        this.modId = modId;
        this.riderName = riderName;
        this.entityName = entityName;
        this.paths = GeoResourcePaths.entity(modId, riderName, entityName);

        this.noPhysics = true;
        this.setNoGravity(true);
    }

    // ==================== 动画 ====================

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registerAnimationControllers(registrar);
    }

    protected abstract void registerAnimationControllers(AnimatableManager.ControllerRegistrar registrar);

    protected void addController(AnimatableManager.ControllerRegistrar registrar,
                                 String name,
                                 AnimationController<BaseRiderEffectEntity> controller) {
        controllers.put(name, controller);
        registrar.add(controller);
    }

    @Nullable
    public AnimationController<BaseRiderEffectEntity> getController(String name) {
        return controllers.get(name);
    }

    /**
     * 循环播放。
     */
    protected AnimationController<BaseRiderEffectEntity> createLoopController(String anim) {
        return new AnimationController<>(this, anim + "_controller", 0, state -> {
            state.getController().setAnimation(RawAnimation.begin().thenLoop(anim));
            return PlayState.CONTINUE;
        });
    }

    /**
     * 播放一次即停（{@code PLAY_ONCE}）。
     */
    protected AnimationController<BaseRiderEffectEntity> createOnceController(String anim) {
        return new AnimationController<>(this, anim + "_controller", 0, state -> {
            state.getController().setAnimation(
                    RawAnimation.begin().then(anim, Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        });
    }

    /**
     * 播放一次并停在最后一帧（{@code HOLD_ON_LAST_FRAME}）。
     */
    protected AnimationController<BaseRiderEffectEntity> createHoldController(String anim) {
        return new AnimationController<>(this, anim + "_controller", 0, state -> {
            state.getController().setAnimation(
                    RawAnimation.begin().then(anim, Animation.LoopType.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        });
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

    // ==================== GeoEntity ====================

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object entity) {
        return tickCount;
    }

    // ==================== Entity 基础 ====================

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

    // ==================== 透明度支持 ====================

    public float getCurrentAlpha() {
        return 1.0f;
    }

    public boolean shouldApplyTransparency() {
        return false;
    }
}