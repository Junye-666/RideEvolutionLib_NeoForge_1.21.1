package com.jpigeon.rideevolutionlib.util.state;

import com.jpigeon.rideevolutionlib.RideEvolutionLib;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * 每秒驱动一次惰性清理；玩家登出时清空。
 * <p>
 * 由 REL 主类手动注册到 {@code NeoForge.EVENT_BUS}。
 */
@EventBusSubscriber(modid = RideEvolutionLib.MODID, value = Dist.DEDICATED_SERVER)
public final class StateFlagTicker {
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        // 每秒清理一次，遍历所有玩家
        if (event.getServer().getTickCount() % 20 != 0) return;

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            StateFlagManager.activeFlags(player);   // 内部会惰性清理过期项
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        StateFlagManager.clearAll(event.getEntity());
    }
}