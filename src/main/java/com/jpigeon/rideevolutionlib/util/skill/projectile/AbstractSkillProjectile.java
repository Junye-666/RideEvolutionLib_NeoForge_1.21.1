package com.jpigeon.rideevolutionlib.util.skill.projectile;

import com.jpigeon.rideevolutionlib.util.skill.SkillUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 抽象弹射物基类，提供可配置的弹射物行为
 */
public abstract class AbstractSkillProjectile extends ThrowableItemProjectile {
    protected float baseDamage = 5.0f;
    protected float explosionPower = 0.0f;
    protected boolean causesFire = false;
    protected boolean destroyBlocks = false;
    protected Consumer<AbstractSkillProjectile> onSpawn = null;
    protected Consumer<AbstractSkillProjectile> onTick = null;
    protected BiConsumer<AbstractSkillProjectile, LivingEntity> onHitEntity = null;
    protected Consumer<AbstractSkillProjectile> onHitBlock = null;
    protected Consumer<AbstractSkillProjectile> onHitGround = null;
    protected int lifeTime = 100; // 最大存活时间（ticks）
    protected int tickCount = 0;
    protected float gravity = 0.03f;

    public AbstractSkillProjectile(EntityType<? extends AbstractSkillProjectile> type, Level level) {
        super(type, level);
    }

    public AbstractSkillProjectile(EntityType<? extends AbstractSkillProjectile> type, double x, double y, double z, Level level) {
        super(type, x, y, z, level);
    }

    public AbstractSkillProjectile(EntityType<? extends AbstractSkillProjectile> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    /**
     * 触发生成回调
     */
    public void triggerOnSpawn() {
        if (this.onSpawn != null) {
            this.onSpawn.accept(this);
        }
    }

    /**
     * 触发tick回调
     */
    protected void triggerOnTick() {
        if (this.onTick != null) {
            this.onTick.accept(this);
        }
    }

    /**
     * 触发实体命中回调
     */
    protected void triggerOnHitEntity(LivingEntity target) {
        if (this.onHitEntity != null) {
            this.onHitEntity.accept(this, target);
        }
    }

    /**
     * 触发方块命中回调
     */
    protected void triggerOnHitBlock() {
        if (this.onHitBlock != null) {
            this.onHitBlock.accept(this);
        }
    }

    /**
     * 触发地面命中回调
     */
    protected void triggerOnHitGround() {
        if (this.onHitGround != null) {
            this.onHitGround.accept(this);
        }
    }

    @Override
    public void tick() {
        super.tick();
        tickCount++;

        // 自定义tick逻辑
        triggerOnTick();

        // 生命周期检查
        if (tickCount > lifeTime && !this.level().isClientSide) {
            this.onExpire();
            this.discard();
        }

        // 重力效果
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -gravity, 0));
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            switch (result.getType()) {
                case ENTITY:
                    EntityHitResult entityResult = (EntityHitResult) result;
                    if (entityResult.getEntity() instanceof LivingEntity living) {
                        this.onEntityHit(living);
                    }
                    break;
                case BLOCK:
                    triggerOnHitBlock();
                    this.onBlockHit();
                    break;
                case MISS:
                    break;
            }

            // 爆炸效果
            if (explosionPower > 0) {
                this.createExplosion();
            }

            // 地面命中
            if (result.getType() == HitResult.Type.BLOCK) {
                triggerOnHitGround();
            }
        }
    }

    protected void onEntityHit(LivingEntity target) {
        // 默认伤害逻辑
        if (this.getOwner() != null) {
            DamageSource damageSource = this.damageSources().thrown(this, this.getOwner());
            target.hurt(damageSource, baseDamage);
        }

        // 自定义命中逻辑
        triggerOnHitEntity(target);
    }

    protected void onBlockHit() {
        // 方块命中默认逻辑
        if (explosionPower > 0 && destroyBlocks) {
            this.createExplosion();
        }
        this.discard();
    }

    protected void onExpire() {
        // 消失时的效果
        if (explosionPower > 0) {
            this.createExplosion();
        }
    }

    protected void createExplosion() {
        Player ownerPlayer = (this.getOwner() instanceof Player p) ? p : null;
        SkillUtils.explode(ownerPlayer, this.level(),
                this.getX(), this.getY(), this.getZ(),
                explosionPower, explosionPower * 3.0f);
    }

    // ===== 配置方法 =====

    public AbstractSkillProjectile setBaseDamage(float damage) {
        this.baseDamage = damage;
        return this;
    }

    public AbstractSkillProjectile setExplosionPower(float power) {
        this.explosionPower = power;
        return this;
    }

    public AbstractSkillProjectile setDestroyBlocks(boolean destroy) {
        this.destroyBlocks = destroy;
        return this;
    }

    public AbstractSkillProjectile setCausesFire(boolean causesFire) {
        this.causesFire = causesFire;
        return this;
    }

    public AbstractSkillProjectile setLifeTime(int ticks) {
        this.lifeTime = ticks;
        return this;
    }

    public AbstractSkillProjectile setGravity(float gravity) {
        this.gravity = gravity;
        this.setNoGravity(gravity <= 0);
        return this;
    }

    public AbstractSkillProjectile onSpawn(Consumer<AbstractSkillProjectile> callback) {
        this.onSpawn = callback;
        return this;
    }

    public AbstractSkillProjectile onTick(Consumer<AbstractSkillProjectile> callback) {
        this.onTick = callback;
        return this;
    }

    public AbstractSkillProjectile onHitEntity(BiConsumer<AbstractSkillProjectile, LivingEntity> callback) {
        this.onHitEntity = callback;
        return this;
    }

    public AbstractSkillProjectile onHitBlock(Consumer<AbstractSkillProjectile> callback) {
        this.onHitBlock = callback;
        return this;
    }

    public AbstractSkillProjectile onHitGround(Consumer<AbstractSkillProjectile> callback) {
        this.onHitGround = callback;
        return this;
    }

    @Override
    protected abstract @NotNull Item getDefaultItem();
}