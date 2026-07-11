package com.jpigeon.rideevolutionlib.compat.curios;

import com.jpigeon.ridebattlelib.common.config.RiderConfig;
import com.jpigeon.ridebattlelib.common.event.FindRiderConfigEvent;
import com.jpigeon.ridebattlelib.common.registry.RiderRegistry;
import com.jpigeon.rideevolutionlib.RideEvolutionLib;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.CuriosApi;

@EventBusSubscriber(modid = RideEvolutionLib.MODID)
public class CuriosIntegration {

    @SubscribeEvent
    public static void onFindRiderConfig(FindRiderConfigEvent event) {
        // 如果已经有外部强制指定，则跳过
        if (event.getConfig() != null) return;

        Player player = event.getPlayer();

        // 遍历所有已注册的骑士，检查是否装备在 Curios 槽中
        for (RiderConfig config : RiderRegistry.getRegisteredRiders()) {
            if (isCuriosDriverEquipped(player, config)) {
                event.setConfig(config);
                return;
            }
        }
    }

    private static boolean isCuriosDriverEquipped(Player player, RiderConfig config) {
        Item driverItem = config.getDriverItem();
        if (driverItem == Items.AIR) return false;

        // 1. 先保留原版检查（避免覆盖原版逻辑）
        if (player.getItemBySlot(config.getDriverSlot()).is(driverItem)) {
            return true;
        }

        // 2. 检查 Curios（默认检查 belt 槽，你也可以根据 config 的 id 定制槽位）
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.findFirstCurio(driverItem).isPresent())
                .orElse(false);
    }
}