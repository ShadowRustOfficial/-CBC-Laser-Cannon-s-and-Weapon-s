package com.example.colorcannons.breech;

import com.example.colorcannons.config.ColorCannonsConfig;
import com.example.colorcannons.mount.FeFixedCannonMountBlockEntity;
import com.example.colorcannons.registry.ModColorModes;
import com.example.colorcannons.registry.ModBlockEntities;
import com.example.colorcannons.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import rbasamoyai.createbigcannons.cannon_control.ControlPitchContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AutocannonBreechBlockEntity;

/** FE-powered laser breech using CBC's normal projectile pipeline. */
@EventBusSubscriber(modid = com.example.colorcannons.ColorCannonsMod.MOD_ID)
public class LaserAutocannonBreechBlockEntity extends AutocannonBreechBlockEntity {
    private static final ResourceLocation CBC_AUTOCANNON_FIRE =
            ResourceLocation.fromNamespaceAndPath("createbigcannons", "fire_autocannon");
    private static final java.util.Map<Level, java.util.Map<Vec3, Long>> LASER_SOUND_MARKERS =
            new java.util.WeakHashMap<>();

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
        // Preserve CBC's cooldown and animation lifecycle. Only the shared
        // fire_autocannon sound is suppressed, and only at this laser's muzzle.
        super.handleFiring();
        if (cachedLevel == null || cachedLevel.isClientSide || cachedMount == null || cachedGlobalPos == null)
            return;
        lastLaserShotTick = cachedLevel.getGameTime();
        markLaserFiringSound(cachedLevel, cachedGlobalPos);
        cachedLevel.playSound(null, cachedGlobalPos.x, cachedGlobalPos.y, cachedGlobalPos.z,
                ModSounds.forMode(cachedMount.getColorMode()), SoundSource.BLOCKS, 8.0f, 1.0f);
    }

    private static synchronized void markLaserFiringSound(Level level, Vec3 position) {
        LASER_SOUND_MARKERS.computeIfAbsent(level, ignored -> new java.util.HashMap<>())
                .put(position, level.getGameTime() + 2);
    }

    @SubscribeEvent
    public static void onLevelSound(PlayLevelSoundEvent.AtPosition event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide())
            return;
        if (event.getSound() == null || !event.getSound().value().getLocation().equals(CBC_AUTOCANNON_FIRE))
            return;

        java.util.Map<Vec3, Long> markers = LASER_SOUND_MARKERS.get(level);
        if (markers == null || markers.isEmpty())
            return;

        long now = level.getGameTime();
        java.util.Iterator<java.util.Map.Entry<Vec3, Long>> iterator = markers.entrySet().iterator();
        while (iterator.hasNext()) {
            java.util.Map.Entry<Vec3, Long> entry = iterator.next();
            if (entry.getValue() < now) {
                iterator.remove();
                continue;
            }
            if (entry.getKey().distanceToSqr(event.getPosition()) <= 1.0) {
                event.setCanceled(true);
                iterator.remove();
                return;
            }
        }
    }
}
