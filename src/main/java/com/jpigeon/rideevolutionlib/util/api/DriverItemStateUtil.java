package com.jpigeon.rideevolutionlib.util.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class DriverItemStateUtil {
    private DriverItemStateUtil() {}

    public static void setDriverAnim(ItemStack driver, ResourceLocation formId) {
        if (driver == null || driver.isEmpty() || formId == null) return;
        if (driver.getItem() instanceof FormIdAnimatable animatable) {
            animatable.setStateByFormId(formId);
        }
    }
}
