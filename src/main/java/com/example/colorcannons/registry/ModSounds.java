package com.example.colorcannons.registry;

import com.example.colorcannons.ColorCannonsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers the two firing sound events referenced by the spec:
 * "blue_autocannon_fire" and "red_autocannon_fire". The actual audio is
 * wired up in sounds.json, which points at
 * assets/colorcannons/sounds/blue_autocannon_fire.ogg and
 * .../red_autocannon_fire.ogg — both currently the supplied
 * AT-AT_Fire_Single.ogg. Swap those two .ogg files for distinct blue/red
 * takes later without touching any code.
 */
public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, ColorCannonsMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BLUE_AUTOCANNON_FIRE =
            SOUND_EVENTS.register("blue_autocannon_fire", () -> SoundEvent.createVariableRangeEvent(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(ColorCannonsMod.MOD_ID, "blue_autocannon_fire")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RED_AUTOCANNON_FIRE =
            SOUND_EVENTS.register("red_autocannon_fire", () -> SoundEvent.createVariableRangeEvent(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(ColorCannonsMod.MOD_ID, "red_autocannon_fire")));

    /** Convenience lookup used by the FE mount and the autocannon block entities. */
    public static SoundEvent forMode(ModColorModes mode) {
        return switch (mode) {
            case BLUE -> BLUE_AUTOCANNON_FIRE.get();
            case RED -> RED_AUTOCANNON_FIRE.get();
        };
    }
}
