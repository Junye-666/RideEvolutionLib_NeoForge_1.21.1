package com.jpigeon.rideevolutionlib.compat.geckoLib.item;

import com.jpigeon.rideevolutionlib.compat.geckoLib.AnimationManager;
import com.jpigeon.rideevolutionlib.compat.util.GeoRenderRegistryUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;

import static software.bernie.geckolib.animation.Animation.LoopType.*;

public abstract class BaseRiderGeoItem extends Item implements GeoItem {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final String modId;
    protected final String riderName;
    protected final String itemName;
    protected final boolean animated;
    protected final AnimationManager<BaseRiderGeoItem> animationManager;
    protected final Map<String, AnimationController<BaseRiderGeoItem>> controllers = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    private GenericItemRenderer cachedRenderer;

    public BaseRiderGeoItem(String modId, String riderName, String itemName, Properties properties, boolean animated) {
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoRenderProvider getRenderProvider() {
        return new GeoRenderProvider() {
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (cachedRenderer == null) {
                    cachedRenderer = GeoRenderRegistryUtil.createItemRenderer(modId, riderName, itemName, animated);
                }
                return cachedRenderer;
            }
        };
    }

    protected abstract void registerAnimationControllers(AnimatableManager.ControllerRegistrar registrar);

    protected void addController(AnimatableManager.ControllerRegistrar registrar, String name,
                                 AnimationController<BaseRiderGeoItem> controller) {
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

    protected AnimationController<BaseRiderGeoItem> createLoopController(String anim) {
        return animationManager.loop(anim);
    }

    protected AnimationController<BaseRiderGeoItem> createOnceController(String anim) {
        return animationManager.once(anim);
    }

    protected AnimationController<BaseRiderGeoItem> createHoldController(String anim) {
        return animationManager.hold(anim);
    }

    protected AnimationController<BaseRiderGeoItem> createStateController(
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
