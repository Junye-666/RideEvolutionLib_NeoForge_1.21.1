package com.jpigeon.rideevolutionlib.compat.geckoLib.armor;

import com.jpigeon.rideevolutionlib.compat.util.GeoAnimatableSupport;
import com.jpigeon.rideevolutionlib.compat.util.GeoRenderRegistryUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
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
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public abstract class BaseRiderArmorItem extends ArmorItem implements GeoItem {
    protected final String modId, riderName, formName;
    protected final boolean animated;
    protected final GeoAnimatableSupport<BaseRiderArmorItem> support;

    @OnlyIn(Dist.CLIENT)
    private GeoArmorRenderer<?> cachedRenderer;

    public BaseRiderArmorItem(String modId, String riderName, String formName, Holder<ArmorMaterial> material, Type type, Properties properties, boolean animated) {
        super(material, type, properties.stacksTo(1));
        this.modId = modId;
        this.riderName = riderName;
        this.formName = formName;
        this.animated = animated;
        this.support = new GeoAnimatableSupport<>(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar r) {
        registerAnimationControllers(r);
    }

    protected abstract void registerAnimationControllers(AnimatableManager.ControllerRegistrar r);

    protected void addController(AnimatableManager.ControllerRegistrar r, String name,
                                 AnimationController<BaseRiderArmorItem> controller) {
        support.register(name, controller);
        r.add(controller);
    }

    public void setAnimState(String state) {
        support.setState(state);
    }

    public String getCurrentAnimState() {
        return support.getState();
    }

    protected AnimationController<BaseRiderArmorItem> createLoopController(String a) {
        return support.loop(a);
    }

    protected AnimationController<BaseRiderArmorItem> createOnceController(String a) {
        return support.once(a);
    }

    protected AnimationController<BaseRiderArmorItem> createHoldController(String a) {
        return support.hold(a);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return support.cache();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public GeoRenderProvider getRenderProvider() {
        return new GeoRenderProvider() {
            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
                    @Nullable T entity, ItemStack stack,
                    @Nullable EquipmentSlot slot, @Nullable HumanoidModel<T> original) {
                if (cachedRenderer == null) {
                    cachedRenderer = GeoRenderRegistryUtil.createArmorRenderer(modId, riderName, formName, animated);
                }
                return (HumanoidModel<?>) cachedRenderer;
            }
        };
    }
}