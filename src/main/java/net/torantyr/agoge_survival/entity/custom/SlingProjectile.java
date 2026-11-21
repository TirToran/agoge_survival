package net.torantyr.agoge_survival.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.torantyr.agoge_survival.entity.ModEntities;

public class SlingProjectile extends ThrowableItemProjectile {
    private float damage = 3.0F;

    //Used by the game when it loads or spawns the entity
    public SlingProjectile(EntityType<? extends SlingProjectile> type, Level level) {
        super(type, level);
    }

    //User by sling when it fires one
    public SlingProjectile(Level level, LivingEntity shooter) {
        super(ModEntities.SLING_PROJECTILE.get(), shooter, level);
    }

    //What the projectile looks like in flight
    @Override
    protected Item getDefaultItem() {
        return Items.FLINT;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), damage);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard(); // remove the projectile after it hits anything
        }
    }
}
