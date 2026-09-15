package com.jpigeon.rideevolutionlib.util.entity;

import com.jpigeon.rideevolutionlib.compat.geckoLib.entity.BaseRiderEffectEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractOwnerFollowEffect extends BaseRiderEffectEntity {
    private final EntityDataAccessor<Optional<UUID>> ownerUuid;
    private final int maxLifetime;
    private int lifetime = 0;

    protected AbstractOwnerFollowEffect(EntityType<?> type, Level level,
                                        String modId, String riderName, String entityName, int maxLifetime) {
        super(type, level, modId, riderName, entityName);
        this.ownerUuid = SynchedEntityData.defineId(getClass(), EntityDataSerializers.OPTIONAL_UUID);
        this.maxLifetime = maxLifetime;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder b) {
        super.defineSynchedData(b);
        b.define(ownerUuid, Optional.empty());
    }

    public @Nullable Player getOwner() {
        return entityData.get(ownerUuid).map(u -> level().getPlayerByUUID(u)).orElse(null);
    }

    public void setOwner(@Nullable Player p) {
        entityData.set(ownerUuid, p != null ? Optional.of(p.getUUID()) : Optional.empty());
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
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        entityData.get(ownerUuid).ifPresent(u -> tag.putUUID("OwnerUUID", u));
    }

    @Override public boolean isCustomNameVisible() { return false; }
}
