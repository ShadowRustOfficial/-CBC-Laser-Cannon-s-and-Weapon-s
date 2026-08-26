package com.example.colorcannons.breech;

import com.example.colorcannons.config.ColorCannonsConfig;
import com.example.colorcannons.mount.FeFixedCannonMountBlockEntity;
import com.example.colorcannons.registry.ModColorModes;
import com.example.colorcannons.registry.ModBlockEntities;
import com.example.colorcannons.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.cannon_control.ControlPitchContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AutocannonBreechBlockEntity;

/** FE-powered laser breech using CBC's normal projectile pipeline. */
public class LaserAutocannonBreechBlockEntity extends AutocannonBreechBlockEntity {
    private FeFixedCannonMountBlockEntity cachedMount;
    private Level cachedLevel;
    private Vec3 cachedGlobalPos;
    private long lastLaserShotTick = Long.MIN_VALUE;

    public LaserAutocannonBreechBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_BREECH.get(), pos, state);
    }

    @Override
    public void tickFromContraption(Level level, PitchOrientedContraptionEntity poce, BlockPos localPos) {
        super.tickFromContraption(level, poce, localPos);
        cachedLevel = level;
        cachedGlobalPos = poce.toGlobalVector(Vec3.atCenterOf(localPos), 0.0f);
        ControlPitchContraption controller = poce.getController();
        cachedMount = controller instanceof FeFixedCannonMountBlockEntity mount ? mount : null;
    }

    private boolean hasChargedMount() {
        return cachedMount != null && cachedMount.hasEnoughFeForShot();
    }

    private boolean intervalReady() {
        if (cachedLevel == null || cachedLevel.isClientSide)
            return true;
        long now = cachedLevel.getGameTime();
        return lastLaserShotTick == Long.MIN_VALUE
                || now - lastLaserShotTick >= ColorCannonsConfig.FIRE_INTERVAL_TICKS.get();
    }

    @Override
    public boolean canFire() {
        return super.canFire() && hasChargedMount() && intervalReady();
    }

    @Override
    public ItemStack extractNextInput() {
        if (!hasChargedMount() || !intervalReady())
            return ItemStack.EMPTY;
        ModColorModes mode = cachedMount.getColorMode();
        cachedMount.consumeFeForShot();
        return new ItemStack(com.example.colorcannons.registry.ModItems.laserRoundFor(mode));
    }

    @Override
    public void handleFiring() {
        // Never call super.handleFiring(): that is the CBC shared autocannon
        // sound hook. Keeping this override local leaves ordinary CBC cannons
        // completely untouched.
        if (cachedLevel == null || cachedLevel.isClientSide || cachedMount == null)
            return;
        lastLaserShotTick = cachedLevel.getGameTime();
        cachedLevel.playSound(null, cachedGlobalPos.x, cachedGlobalPos.y, cachedGlobalPos.z,
                ModSounds.forMode(cachedMount.getColorMode()), SoundSource.BLOCKS, 8.0f, 1.0f);
    }
}
