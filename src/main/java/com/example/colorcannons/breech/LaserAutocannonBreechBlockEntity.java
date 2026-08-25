package com.example.colorcannons.breech;

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

/**
 * FE-powered laser breech using CBC's normal autocannon firing pipeline.
 *
 * The laser deliberately does NOT call super.handleFiring(): CBC's base hook
 * emits its shared autocannon firing sound. Omitting that hook here scopes the
 * suppression to this breech only; no global createbigcannons sound override
 * is installed, so ordinary CBC autocannons retain their normal sound.
 */
public class LaserAutocannonBreechBlockEntity extends AutocannonBreechBlockEntity {

    private FeFixedCannonMountBlockEntity cachedMount;
    private Level cachedLevel;
    private Vec3 cachedGlobalPos;

    public LaserAutocannonBreechBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_BREECH.get(), pos, state);
    }

    @Override
    public void tickFromContraption(Level level, PitchOrientedContraptionEntity poce, BlockPos localPos) {
        super.tickFromContraption(level, poce, localPos);
        this.cachedLevel = level;
        this.cachedGlobalPos = poce.toGlobalVector(Vec3.atCenterOf(localPos), 0.0f);
        ControlPitchContraption controller = poce.getController();
        this.cachedMount = controller instanceof FeFixedCannonMountBlockEntity mount ? mount : null;
    }

    private boolean hasChargedMount() {
        return cachedMount != null && cachedMount.hasEnoughFeForShot();
    }

    @Override
    public boolean canFire() {
        return super.canFire() && hasChargedMount();
    }

    @Override
    public ItemStack extractNextInput() {
        if (!hasChargedMount()) {
            return ItemStack.EMPTY;
        }
        ModColorModes mode = cachedMount.getColorMode();
        cachedMount.consumeFeForShot();
        return new ItemStack(com.example.colorcannons.registry.ModItems.laserRoundFor(mode));
    }

    @Override
    public void handleFiring() {
        // Intentionally do not call super.handleFiring(). The parent hook is
        // the CBC shared autocannon sound path. Suppression here is scoped to
        // this laser breech and leaves every stock CBC autocannon untouched.
        if (cachedLevel != null && cachedGlobalPos != null && !cachedLevel.isClientSide && cachedMount != null) {
            cachedLevel.playSound(null, cachedGlobalPos.x, cachedGlobalPos.y, cachedGlobalPos.z,
                    ModSounds.forMode(cachedMount.getColorMode()), SoundSource.BLOCKS, 8.0f, 1.0f);
        }
    }
}
