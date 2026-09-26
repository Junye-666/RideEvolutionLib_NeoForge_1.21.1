package com.jpigeon.rideevolutionlib.compat.geckoLib.item;

import com.jpigeon.rideevolutionlib.compat.util.GeoAnimatableSupport;
import com.jpigeon.rideevolutionlib.compat.util.GeoRenderRegistryUtil;
import com.jpigeon.rideevolutionlib.compat.util.GeoResourcePaths;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 通用骑士 Geo 物品基类。
 * <p>对外 API 与旧版一致，内部改为组合 {@link GeoAnimatableSupport} + {@link GeoResourcePaths}。
 */
public abstract class BaseRiderGeoItem extends Item implements GeoItem {
    protected final String modId;
    protected final String riderName;
    protected final String itemName;
    protected final boolean animated;

    protected final GeoAnimatableSupport<BaseRiderGeoItem> support;
    protected final GeoResourcePaths paths;

    @OnlyIn(Dist.CLIENT)
    private GeoItemRenderer<?> cachedRenderer;

    public BaseRiderGeoItem(String modId, String riderName, String itemName,
                            Properties properties, boolean animated) {
        super(properties);
        this.modId = modId;
        this.riderName = riderName;
        this.itemName = itemName;
        this.animated = animated;
        this.paths = GeoResourcePaths.item(modId, riderName, itemName, animated);
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
                                 AnimationController<BaseRiderGeoItem> controller) {
        support.register(name, controller);
        registrar.add(controller);
    }

    public void setAnimState(String state) {
        support.setState(state);
    }

    public String getCurrentAnimState() {
        return support.getState();
    }

    protected AnimationController<BaseRiderGeoItem> createLoopController(String anim) {
        return support.loop(anim);
    }

    protected AnimationController<BaseRiderGeoItem> createOnceController(String anim) {
        return support.once(anim);
    }

    protected AnimationController<BaseRiderGeoItem> createHoldController(String anim) {
        return support.hold(anim);
    }

    // ==================== 渲染 ====================

    @OnlyIn(Dist.CLIENT)
    @Override
    public GeoRenderProvider getRenderProvider() {
        return new GeoRenderProvider() {
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (cachedRenderer == null) {
                    cachedRenderer = GeoRenderRegistryUtil.createItemRenderer(
                            modId, riderName, itemName, animated);
                }
                return cachedRenderer;
            }
        };
    }

    // ==================== GeoAnimatable ====================

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return support.cache();
    }

    // ==================== 资源路径（保留访问器，向后兼容） ====================

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