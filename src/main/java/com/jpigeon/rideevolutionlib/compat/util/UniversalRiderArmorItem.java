package com.jpigeon.rideevolutionlib.compat.util;

import com.jpigeon.rideevolutionlib.compat.geckoLib.armor.BaseRiderArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * 通用骑士形态盔甲：只带 idle 循环动画，无需每形态写一个子类。
 */
public class UniversalRiderArmorItem extends BaseRiderArmorItem {
    public UniversalRiderArmorItem(String modId, String riderName, String formName,
                                   Holder<ArmorMaterial> material, Type type,
                                   Properties properties, boolean animated) {
        super(modId, riderName, formName, material, type, properties, animated);
    }

    @Override
    protected void registerAnimationControllers(AnimatableManager.ControllerRegistrar registrar) {
        addController(registrar, "idle", createLoopController("idle"));
    }
}
