package com.example.colorcannons.breech;

import com.example.colorcannons.mount.FeFixedCannonMountBlockEntity;
import com.example.colorcannons.registry.ModColorModes;
import com.example.colorcannons.registry.ModBlockEntities;
import com.example.colorcannons.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.cannon_control.ControlPitchContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AutocannonBreechBlockEntity;

/**
 * The one class that actually implements "no ammo from magazines — just FE
 * from the mount". Everything about recoil, animation, fire-rate scroll
 * value, and the barrel-travel/speed-buildup logic in
 * {@code MountedAutocannonContraption.fireShot} is 100% inherited from
 * {@link AutocannonBreechBlockEntity} untouched. Only three hooks are
 * overridden:
 *
 *  - {@link #canFire()}: stock fire-rate/cooldown gate, AND the connected
 *    FE mount must have >= fePerShot stored.
 *  - {@link #extractNextInput()}: instead of pulling a real ItemStack out
 *    of the input buffer or magazine (both of which this breech never
 *    reads), it debits FE from the mount and hands back a freshly
 *    manufactured {@link com.example.colorcannons.munitions.LaserRoundItem}
 *    stack — so from {@code fireShot}'s point of view a "round" was always
 *    there, it just never physically existed anywhere. WHICH color round
 *    gets manufactured is read live off the mount's Color Mode slider
 *    ({@link FeFixedCannonMountBlockEntity#getColorMode()}) at the moment
 *    of firing — there's only one breech block/item; the mount's slider is
 *    what decides blue vs red each shot, per spec.
 *  - {@link #handleFiring()}: plays the currently-selected color's laser
 *    sound (your AT-AT_Fire_Single.ogg). CBC's own base mechanical "clank"
 *    sound (the single, shared "createbigcannons:fire_autocannon" event
 *    every non-machine-gun autocannon in the game plays — hardcoded inside
 *    the private internals of {@code MountedAutocannonContraption.fireShot},
 *    so it can't be swapped per-breech in code) is silenced at the asset
 *    level instead: see assets/createbigcannons/sounds.json in this addon,
 *    which ships a {@code "replace": true} override pointing that shared
 *    event at a near-silent file. Net effect: only your AT-AT sound plays
 *    when this breech fires. NOTE this override is global — every
 *    autocannon in the world (including stock CBC ones, if you have any)
 *    goes quiet on that base clank too, since it's one shared event; there
 *    is no way to scope the mute to just this addon's breech without
 *    duplicating CBC's private firing method.
 *
 * The FE mount reference is captured every tick via
 * {@link #tickFromContraption}, which is called once per game tick for
 * every autocannon-family block entity in an assembled contraption — see
 * {@code MountedAutocannonContraption.tick()}. There's a one-tick lag
 * immediately after assembly before the first fire is possible; harmless
 * for a block that's expected to sit mounted and firing continuously.
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
        super.handleFiring();
        if (cachedLevel != null && cachedGlobalPos != null && !cachedLevel.isClientSide && cachedMount != null) {
            cachedLevel.playSound(null, cachedGlobalPos.x, cachedGlobalPos.y, cachedGlobalPos.z,
                    ModSounds.forMode(cachedMount.getColorMode()), SoundSource.BLOCKS, 8.0f, 1.0f);
        }
    }
}
