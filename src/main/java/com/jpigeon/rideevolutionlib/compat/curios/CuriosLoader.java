package com.jpigeon.rideevolutionlib.compat.curios;

import com.jpigeon.rideevolutionlib.RideEvolutionLib;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.Optional;

public class CuriosLoader {
    private static final Logger LOGGER = RideEvolutionLib.LOGGER;
    private static final boolean CURIOS_LOADED = ModList.get().isLoaded("curios");

    private static Method getCuriosInventoryMethod;
    private static Method findFirstCurioMethod;
    private static boolean available = false;

    static {
        if (CURIOS_LOADED) {
            try {
                Class<?> curiosApiClass = Class.forName("top.theillusivec4.curios.api.CuriosApi");
                getCuriosInventoryMethod = curiosApiClass.getMethod("getCuriosInventory", LivingEntity.class);

                Class<?> handlerClass = Class.forName("top.theillusivec4.curios.api.type.capability.ICuriosItemHandler");
                findFirstCurioMethod = handlerClass.getMethod("findFirstCurio", Item.class);

                available = true;
            } catch (Exception e) {
                LOGGER.error("Failed to initialize Curios reflection — Curios 集成将不可用", e);
                available = false;
            }
        }
    }

    /** 供外部判断 Curios 集成是否可用 */
    public static boolean isAvailable() {
        return available;
    }

    public static boolean isItemInCurios(Player player, Item item) {
        if (!available || player == null || item == null) return false;
        try {
            Optional<?> handlerOpt = (Optional<?>) getCuriosInventoryMethod.invoke(null, player);
            if (handlerOpt.isEmpty()) return false;
            Object handler = handlerOpt.get();
            Optional<?> resultOpt = (Optional<?>) findFirstCurioMethod.invoke(handler, item);
            return resultOpt != null && resultOpt.isPresent();
        } catch (Exception e) {
            LOGGER.warn("Failed to check Curios item: {}", e.getMessage());
            return false;
        }
    }

    public static boolean isDriverEquipped(Player player, Item driverItem, EquipmentSlot fallbackSlot) {
        if (player.getItemBySlot(fallbackSlot).is(driverItem)) return true;
        return isItemInCurios(player, driverItem);
    }
}