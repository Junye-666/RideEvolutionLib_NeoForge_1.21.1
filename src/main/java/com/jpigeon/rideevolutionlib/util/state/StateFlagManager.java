package com.jpigeon.rideevolutionlib.util.state;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 {@link ResourceLocation} 的短时状态标志管理器。
 * <p>
 * 用于替代散落的 {@code player.addTag/removeTag} + {@code ScheduleUtils} 组合。
 * <p>
 * 线程安全：所有公开方法可在主线程调用；内部使用 {@link ConcurrentHashMap}。
 */
public final class StateFlagManager {
    private StateFlagManager() {
    }

    /**
     * playerId → (flagId → 过期 tick)
     */
    private static final Map<UUID, Map<ResourceLocation, Long>> ACTIVE = new ConcurrentHashMap<>();

    // ==================== 写操作 ====================

    /**
     * 应用一个带 TTL 的标志。若已存在，取更晚的过期时间（不会缩短）。
     *
     * @param ttlTicks 存活 tick 数；{@code <= 0} 表示永久
     */
    public static void apply(Player player, ResourceLocation flag, int ttlTicks) {
        if (player == null || flag == null) return;

        long now = player.level().getGameTime();
        long expiry = ttlTicks <= 0 ? Long.MAX_VALUE : now + ttlTicks;

        ACTIVE.computeIfAbsent(player.getUUID(), k -> new ConcurrentHashMap<>())
                .merge(flag, expiry, Math::max);
    }

    /**
     * 应用一个永久标志（直到显式 remove 或玩家登出）。
     */
    public static void applyPermanent(Player player, ResourceLocation flag) {
        apply(player, flag, 0);
    }

    /**
     * 刷新标志的过期时间（语义同 {@link #apply}，命名更直观）。
     */
    public static void refresh(Player player, ResourceLocation flag, int ttlTicks) {
        apply(player, flag, ttlTicks);
    }

    /**
     * 移除指定标志。
     */
    public static void remove(Player player, ResourceLocation flag) {
        if (player == null || flag == null) return;
        Map<ResourceLocation, Long> flags = ACTIVE.get(player.getUUID());
        if (flags != null) {
            flags.remove(flag);
            if (flags.isEmpty()) ACTIVE.remove(player.getUUID());
        }
    }

    /**
     * 清空该玩家的所有标志（登出、死亡重生时调用）。
     */
    public static void clearAll(Player player) {
        if (player != null) ACTIVE.remove(player.getUUID());
    }

    // ==================== 读操作 ====================

    /**
     * 检查标志是否活跃。过期项会被惰性移除。
     */
    public static boolean isActive(Player player, ResourceLocation flag) {
        if (player == null || flag == null) return false;

        Map<ResourceLocation, Long> flags = ACTIVE.get(player.getUUID());
        if (flags == null) return false;

        Long expiry = flags.get(flag);
        if (expiry == null) return false;

        if (player.level().getGameTime() >= expiry) {
            flags.remove(flag);
            if (flags.isEmpty()) ACTIVE.remove(player.getUUID());
            return false;
        }
        return true;
    }

    /**
     * 返回该玩家当前所有活跃标志（已惰性清理过期项）。
     * <p>
     * 返回的是快照副本，可安全遍历。
     */
    public static Set<ResourceLocation> activeFlags(Player player) {
        if (player == null) return Collections.emptySet();

        Map<ResourceLocation, Long> flags = ACTIVE.get(player.getUUID());
        if (flags == null) return Collections.emptySet();

        long now = player.level().getGameTime();
        Set<ResourceLocation> result = new HashSet<>();

        flags.entrySet().removeIf(e -> {
            if (now >= e.getValue()) return true;   // 过期 → 移除
            result.add(e.getKey());                 // 活跃 → 收集
            return false;
        });

        if (flags.isEmpty()) ACTIVE.remove(player.getUUID());
        return Collections.unmodifiableSet(result);
    }
}