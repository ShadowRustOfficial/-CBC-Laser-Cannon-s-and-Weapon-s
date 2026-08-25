package com.example.colorcannons.registry;

import com.example.colorcannons.ColorCannonsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registered laser firing and impact sounds. */
public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, ColorCannonsMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BLUE_AUTOCANNON_FIRE =
            SOUND_EVENTS.register("blue_autocannon_fire", () -> SoundEvent.createVariableRangeEvent(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(ColorCannonsMod.MOD_ID, "blue_autocannon_fire")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RED_AUTOCANNON_FIRE =
            SOUND_EVENTS.register("red_autocannon_fire", () -> SoundEvent.createVariableRangeEvent(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(ColorCannonsMod.MOD_ID, "red_autocannon_fire")));

    public static final DeferredHolder<SoundEvent, SoundEvent> LASER_IMPACT =
            SOUND_EVENTS.register("laser_impact", () -> SoundEvent.createVariableRangeEvent(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(ColorCannonsMod.MOD_ID, "laser_impact")));

    public static SoundEvent forMode(ModColorModes mode) {
        return switch (mode) {
            case BLUE -> BLUE_AUTOCANNON_FIRE.get();
            case RED -> RED_AUTOCANNON_FIRE.get();
        };
    }
}
