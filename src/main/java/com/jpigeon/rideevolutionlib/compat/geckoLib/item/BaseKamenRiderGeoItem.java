package com.jpigeon.rideevolutionlib.compat.geckoLib.item;

import com.jpigeon.rideevolutionlib.compat.geckoLib.AnimationManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static software.bernie.geckolib.animation.Animation.LoopType.*;

public abstract class BaseKamenRiderGeoItem extends Item implements GeoItem {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final String modId;
    protected final String riderName;
    protected final String itemName;
    protected final boolean animated;
    protected final AnimationManager<BaseKamenRiderGeoItem> animationManager;
    protected final Map<String, AnimationController<BaseKamenRiderGeoItem>> controllers = new HashMap<>();

    public BaseKamenRiderGeoItem(String modId, String riderName, String itemName,  Properties properties, boolean animated) {
        super(properties);
        this.modId = modId;
        this.riderName = riderName;
        this.itemName = itemName;
        this.animated = animated;
        this.animationManager = new AnimationManager<>(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registerAnimationControllers(registrar);
    }

    protected abstract void registerAnimationControllers(AnimatableManager.ControllerRegistrar registrar);

    protected void addController(AnimatableManager.ControllerRegistrar registrar, String name,
                                 AnimationController<BaseKamenRiderGeoItem> controller) {
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

    protected AnimationController<BaseKamenRiderGeoItem> createLoopController(
            String animationName) {
        return createStateController(animationName, LOOP);
    }

    protected AnimationController<BaseKamenRiderGeoItem> createOnceController(
            String animationName) {
        return createStateController(animationName, PLAY_ONCE);
    }

    protected AnimationController<BaseKamenRiderGeoItem> createHoldController(
            String animationName) {
        return createStateController(animationName, HOLD_ON_LAST_FRAME);
    }

    protected AnimationController<BaseKamenRiderGeoItem> createStateController(
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

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<BaseKamenRiderGeoItem> renderer;

            @Override
            public GeoItemRenderer<BaseKamenRiderGeoItem> getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = createRenderer();
                }
                return this.renderer;
            }
        });
    }

    protected abstract GeoItemRenderer<BaseKamenRiderGeoItem> createRenderer();

    // 资源路径生成工具方法
    protected ResourceLocation getModelPath() {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "geo/" + riderName.toLowerCase() + "/item/" + riderName.toLowerCase() + "_" + itemName.toLowerCase() + ".geo.json");
    }

    protected ResourceLocation getTexturePath() {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "textures/item/" + riderName.toLowerCase() + "/geo_item/" + riderName.toLowerCase() + "_" + itemName.toLowerCase() + ".png");
    }

    protected ResourceLocation getAnimationPath() {
        if (animated) {
            return ResourceLocation.fromNamespaceAndPath(modId,
                    "animations/" + riderName.toLowerCase() + "/item/" + riderName.toLowerCase() + "_" + itemName.toLowerCase() + ".animation.json");
        }
        return ResourceLocation.fromNamespaceAndPath(modId,
                "animations/no_item.animation.json");
    }
}
