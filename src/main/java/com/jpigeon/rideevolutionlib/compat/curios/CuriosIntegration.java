package com.jpigeon.rideevolutionlib.compat.curios;

import com.jpigeon.ridebattlelib.common.config.RiderConfig;
import com.jpigeon.ridebattlelib.common.event.FindRiderConfigEvent;
import com.jpigeon.ridebattlelib.common.registry.RiderRegistry;
import com.jpigeon.rideevolutionlib.RideEvolutionLib;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = RideEvolutionLib.MODID)
public class CuriosIntegration {

    @SubscribeEvent
    public static void onFindRiderConfig(FindRiderConfigEvent event) {
        // 如果已经有外部强制指定，则跳过
        if (event.getConfig() != null) return;

        Player player = event.getPlayer();

        // 遍历所有已注册的骑士，检查是否装备在 Curios 槽中
        for (RiderConfig config : RiderRegistry.getRegisteredRiders()) {
            if (CuriosLoader.isDriverEquipped(player, config.getDriverItem(), config.getDriverSlot())) {
                event.setConfig(config);
                return;
            }
        }
    }
}