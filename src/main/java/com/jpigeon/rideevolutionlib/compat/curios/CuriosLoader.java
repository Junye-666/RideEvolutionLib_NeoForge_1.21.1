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

    static {
        if (CURIOS_LOADED) {
            try {
                Class<?> curiosApiClass = Class.forName("top.theillusivec4.curios.api.CuriosApi");
                getCuriosInventoryMethod = curiosApiClass.getMethod("getCuriosInventory", LivingEntity.class);

                Class<?> handlerClass = Class.forName("top.theillusivec4.curios.api.type.capability.ICuriosItemHandler");
                findFirstCurioMethod = handlerClass.getMethod("findFirstCurio", Item.class);

            } catch (Exception e) {
                LOGGER.error("Failed to initialize Curios reflection", e);
                // 标记为不可用，后续调用会直接返回 false
            }
        }
    }

    /**
     * 检查玩家是否在 Curios 槽位中装备了指定物品（仅当 Curios 加载时）
     */
    public static boolean isItemInCurios(Player player, Item item) {
        if (!CURIOS_LOADED || player == null || item == null) {
            return false;
        }
        if (getCuriosInventoryMethod == null || findFirstCurioMethod == null) {
            return false;
        }
        try {
            // 调用 CuriosApi.getCuriosInventory(player)
            Optional<?> handlerOpt = (Optional<?>) getCuriosInventoryMethod.invoke(null, player);
            if (handlerOpt.isEmpty()) {
                return false;
            }
            Object handler = handlerOpt.get();
            Optional<?> resultOpt = (Optional<?>) findFirstCurioMethod.invoke(handler, item);
            return resultOpt != null && resultOpt.isPresent();
        } catch (Exception e) {
            LOGGER.warn("Failed to check Curios item: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 综合检查：原版槽位 + Curios 槽位
     */
    public static boolean isDriverEquipped(Player player, Item driverItem, EquipmentSlot fallbackSlot) {
        // 先检查原版槽位
        if (player.getItemBySlot(fallbackSlot).is(driverItem)) {
            return true;
        }
        // 再检查 Curios
        return isItemInCurios(player, driverItem);
    }
}
