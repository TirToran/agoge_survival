package net.torantyr.groundwork_sling.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.torantyr.groundwork_sling.entity.ModEntities;

public class SlingProjectile extends ThrowableItemProjectile {
    private float baseDamage = 1.0F; // set by the ammo
    private boolean crit = false;

    // Used by the game when it loads or spawns the entity
    public SlingProjectile(EntityType<? extends SlingProjectile> type, Level level) {
        super(type, level);
    }

    // Used by the sling when it fires one
    public SlingProjectile(Level level, LivingEntity shooter) {
        super(ModEntities.SLING_PROJECTILE.get(), shooter, level);
    }

    public void setBaseDamage(float baseDamage) {
        this.baseDamage = baseDamage;
    }

    public void setCrit(boolean crit) {
        this.crit = crit;
    }

    private int knockback = 0;
    public void setKnockback(int knockback) {
        this.knockback = knockback;
    }
    private boolean flame = false;
    public void setFlame(boolean flame) {
        this.flame = flame;
        if (flame) {
            this.setSecondsOnFire(100); // the projectile itself burns in flight
        }
    }

    // What the projectile looks like in flight when no item has been set
    @Override
    protected Item getDefaultItem() {
        return Items.FLINT;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        float speed = (float) this.getDeltaMovement().length();
        int finalDamage = Mth.ceil(speed * baseDamage);   // speed times base, rounded up, like an arrow
        if (crit) {
            finalDamage += this.random.nextInt(finalDamage / 2 + 2); // the bow's crit bonus
        }
        if (flame) {
            result.getEntity().setSecondsOnFire(5);
        }
        if (knockback > 0 && result.getEntity() instanceof LivingEntity target) {
            double resist = Math.max(0.0D, 1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            Vec3 push = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(knockback * 0.6D * resist);
            if (push.lengthSqr() > 0.0D) {
                target.push(push.x, 0.1D, push.z);
            }
        }
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), finalDamage);
    }

    @Override
    public void tick() {
        super.tick();
        if (crit && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CRIT, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard(); // remove the projectile after it hits anything
        }
    }
}