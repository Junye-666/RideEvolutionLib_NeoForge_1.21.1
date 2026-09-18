package com.jpigeon.rideevolutionlib.util.entity;

import com.jpigeon.rideevolutionlib.compat.geckoLib.entity.BaseRiderEffectEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractOwnerFollowEffect extends BaseRiderEffectEntity {

    private final int maxLifetime;
    private int lifetime = 0;

    protected AbstractOwnerFollowEffect(EntityType<?> type, Level level,
                                        String modId, String riderName,
                                        String entityName, int maxLifetime) {
        super(type, level, modId, riderName, entityName);
        this.maxLifetime = maxLifetime;
    }

    /**
     * 子类必须返回一个 {@code static final} 的 accessor。
     * <p>
     * 必须 static：{@link #defineSynchedData} 在 {@code Entity} 构造链中
     * 被调用，早于本类实例字段初始化，实例字段此时全是 null。
     */
    protected abstract EntityDataAccessor<Optional<UUID>> ownerAccessor();

    /**
     * 辅助 {@code ownerAccessor} 创建 EntityDataAccessor
     * @param entityClass 实体Class本身
     */
    protected static EntityDataAccessor<Optional<UUID>> createOwnerAccessor(
            Class<? extends Entity> entityClass) {
        return SynchedEntityData.defineId(entityClass, EntityDataSerializers.OPTIONAL_UUID);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ownerAccessor(), Optional.empty());
    }

    public @Nullable Player getOwner() {
        return entityData.get(ownerAccessor())
                .map(uuid -> level().getPlayerByUUID(uuid))
                .orElse(null);
    }

    public void setOwner(@Nullable Player player) {
        entityData.set(ownerAccessor(),
                player != null ? Optional.of(player.getUUID()) : Optional.empty());
    }

    @Override
    public void tick() {
        super.tick();
        lifetime++;
        Player owner = getOwner();
        if (owner == null || lifetime >= maxLifetime || owner.isRemoved()) {
            onEffectEnd();
            discard();
            return;
        }
        setPos(owner.getX(), owner.getY(), owner.getZ());
        setYRot(owner.getYRot());
        onFollowTick(owner);
    }

    protected void onFollowTick(Player owner) {}
    protected void onEffectEnd() {}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        entityData.get(ownerAccessor()).ifPresent(u -> tag.putUUID("OwnerUUID", u));
    }

    @Override
    public boolean isCustomNameVisible() { return false; }
}