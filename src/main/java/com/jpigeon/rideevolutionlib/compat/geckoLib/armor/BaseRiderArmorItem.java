package com.jpigeon.rideevolutionlib.compat.geckoLib.armor;

import com.jpigeon.rideevolutionlib.compat.geckoLib.AnimationManager;
import com.jpigeon.rideevolutionlib.compat.util.GeoRenderRegistryUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;

import static software.bernie.geckolib.animation.Animation.LoopType.*;

public abstract class BaseRiderArmorItem extends ArmorItem implements GeoItem {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final String modId;
    protected final String riderName;
    protected final String formName;
    protected final boolean animated;
    protected final AnimationManager<BaseRiderArmorItem> animationManager;
    protected final Map<String, AnimationController<BaseRiderArmorItem>> controllers = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    private GenericArmorRenderer cachedRenderer;

    public BaseRiderArmorItem(String modId, String riderName, String formName, Holder<ArmorMaterial> material, Type type, Properties properties, boolean animated) {
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoRenderProvider getRenderProvider() {
        return new GeoRenderProvider() {
            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
                    @Nullable T livingEntity, ItemStack itemStack,
                    @Nullable EquipmentSlot slot, @Nullable HumanoidModel<T> original) {
                if (cachedRenderer == null) {
                    cachedRenderer = GeoRenderRegistryUtil.createArmorRenderer(modId, riderName, formName, animated);
                }
                return cachedRenderer;
            }
        };
    }

    /**
     * 子类必须实现的动画控制器注册方法
     */
    protected abstract void registerAnimationControllers(AnimatableManager.ControllerRegistrar registrar);

    /**
     * 添加动画控制器并存储引用
     */
    protected void addController(AnimatableManager.ControllerRegistrar registrar, String name,
                                 AnimationController<BaseRiderArmorItem> controller) {
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

    protected AnimationController<BaseRiderArmorItem> createLoopController(String anim) {
        return animationManager.loop(anim);
    }

    protected AnimationController<BaseRiderArmorItem> createOnceController(String anim) {
        return animationManager.once(anim);
    }

    protected AnimationController<BaseRiderArmorItem> createHoldController(String anim) {
        return animationManager.hold(anim);
    }

    protected AnimationController<BaseRiderArmorItem> createStateController(
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
