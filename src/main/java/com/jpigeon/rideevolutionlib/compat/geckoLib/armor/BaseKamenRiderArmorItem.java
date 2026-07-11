package com.jpigeon.rideevolutionlib.compat.geckoLib.armor;

import com.jpigeon.rideevolutionlib.compat.geckoLib.AnimationManager;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static software.bernie.geckolib.animation.Animation.LoopType.*;

public abstract class BaseKamenRiderArmorItem extends ArmorItem implements GeoItem {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final String modId;
    protected final String riderName;
    protected final String formName;
    protected final boolean animated;
    protected final AnimationManager<BaseKamenRiderArmorItem> animationManager;
    protected final Map<String, AnimationController<BaseKamenRiderArmorItem>> controllers = new HashMap<>();

    public BaseKamenRiderArmorItem(String modId, String riderName, String formName, Holder<ArmorMaterial> material, Type type, Properties properties, boolean animated) {
        super(material, type, properties.stacksTo(1));
        this.modId = modId;
        this.riderName = riderName;
        this.formName = formName;
        this.animated = animated;
        this.animationManager = new AnimationManager<>(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registerAnimationControllers(registrar);
    }

    protected abstract void registerAnimationControllers(AnimatableManager.ControllerRegistrar registrar);

    protected void addController(AnimatableManager.ControllerRegistrar registrar, String name,
                                 AnimationController<BaseKamenRiderArmorItem> controller) {
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

    protected AnimationController<BaseKamenRiderArmorItem> createLoopController(
            String animationName) {
        return createStateController(animationName, LOOP);
    }

    protected AnimationController<BaseKamenRiderArmorItem> createOnceController(
            String animationName) {
        return createStateController(animationName, PLAY_ONCE);
    }

    protected AnimationController<BaseKamenRiderArmorItem> createHoldController(
            String animationName) {
        return createStateController(animationName, HOLD_ON_LAST_FRAME);
    }

    protected AnimationController<BaseKamenRiderArmorItem> createStateController(
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
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
                    @Nullable T livingEntity, ItemStack itemStack,
                    @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if (this.renderer == null) {
                    this.renderer = createRenderer();
                }
                return this.renderer;
            }
        });
    }

    protected abstract GeoArmorRenderer<?> createRenderer();

    // 资源路径生成工具方法
    protected ResourceLocation getModelPath() {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "geo/" + riderName.toLowerCase() + "/armor/" + riderName.toLowerCase() + "_" + formName.toLowerCase() + ".geo.json");

    }

    protected ResourceLocation getTexturePath() {
        return ResourceLocation.fromNamespaceAndPath(modId,
                "textures/armor/" + riderName.toLowerCase() + "/" + riderName.toLowerCase() + "_" + formName.toLowerCase() + ".png");
    }

    protected ResourceLocation getAnimationPath() {
        if (animated) {
            return ResourceLocation.fromNamespaceAndPath(modId,
                    "animations/" + riderName.toLowerCase() + "/" + riderName.toLowerCase() + "_" + formName.toLowerCase() + ".animation.json");
        }
        return ResourceLocation.fromNamespaceAndPath(modId,
                "animations/no_armor.animation.json");
    }
}