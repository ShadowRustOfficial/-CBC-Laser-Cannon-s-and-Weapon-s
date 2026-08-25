package com.example.colorcannons.munitions;

import com.example.colorcannons.config.ColorCannonsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.autocannon.ap_round.APAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;

/**
 * The actual projectile used by the laser rounds. CBC still owns the complete
 * projectile tick/penetration pipeline, but the laser has explicit properties
 * so its damage cannot silently fall back to zero if a custom entity-property
 * JSON is not discovered by the handler.
 */
public class LaserAutocannonProjectile extends APAutocannonProjectile {

    private boolean impactExplosionQueued;
    /** World-space point where CBC spawned the projectile (the cannon muzzle). */
    private Vec3 muzzleOrigin;

    private static final EntityDamagePropertiesComponent DAMAGE =
            new EntityDamagePropertiesComponent(
                    24.0f,
                    false,
                    true,
                    true,
                    0.6f);

    private static final BallisticPropertiesComponent BALLISTICS =
            new BallisticPropertiesComponent(
                    0.0,
                    0.0,
                    false,
                    2.0f,
                    24.0f,
                    24.0f,
                    0.0f);

    public LaserAutocannonProjectile(EntityType<? extends LaserAutocannonProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        Vec3 origin = muzzleOrigin != null ? muzzleOrigin : position();
        buffer.writeDouble(origin.x);
        buffer.writeDouble(origin.y);
        buffer.writeDouble(origin.z);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        super.readSpawnData(buffer);
        muzzleOrigin = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public void tick() {
        if (muzzleOrigin == null) {
            muzzleOrigin = position();
        }
        super.tick();
    }

    public Vec3 getMuzzleOrigin() {
        return muzzleOrigin != null ? muzzleOrigin : position();
    }

    @Override
    public EntityDamagePropertiesComponent getDamageProperties() {
        return DAMAGE;
    }

    @Override
    protected BallisticPropertiesComponent getBallisticProperties() {
        return BALLISTICS;
    }

    @Override
    protected boolean onImpact(HitResult hitResult, AbstractCannonProjectile.ImpactResult result,
            ProjectileContext context) {
        if (!level().isClientSide && !impactExplosionQueued && hitResult instanceof BlockHitResult blockHit) {
            impactExplosionQueued = true;
            BlockPos pos = blockHit.getBlockPos();
            // Queue the explosion through CBC's ProjectileContext so CBC
            // executes it using its own ImpactExplosion/terrain-damage path.
            context.queueExplosion(pos, (float) ColorCannonsConfig.IMPACT_EXPLOSION_POWER.get().floatValue());
        }
        return super.onImpact(hitResult, result, context);
    }
}
