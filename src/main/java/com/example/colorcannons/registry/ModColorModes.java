package com.example.colorcannons.registry;

import net.minecraft.resources.ResourceLocation;
import com.example.colorcannons.ColorCannonsMod;

/**
 * Single source of truth for "which color modes exist". Adding a new color
 * mode (per the "Additional color modes" requirement) means adding one enum
 * constant here plus its sound event + tracer color/texture — nothing else
 * in the mount, slider or item code needs to change since they all switch
 * on this enum via {@link #next()} and the getters below.
 */
public enum ModColorModes {

    BLUE(0x2E6BFF, "blue_autocannon_fire"),
    RED(0xFF2E2E, "red_autocannon_fire");

    private final int tracerColor;
    private final String soundEventName;

    ModColorModes(int tracerColor, String soundEventName) {
        this.tracerColor = tracerColor;
        this.soundEventName = soundEventName;
    }

    public int getTracerColor() {
        return tracerColor;
    }

    public ResourceLocation getSoundEventId() {
        return ResourceLocation.fromNamespaceAndPath(ColorCannonsMod.MOD_ID, soundEventName);
    }

    public ModColorModes next() {
        ModColorModes[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public static ModColorModes byOrdinalSafe(int ordinal) {
        ModColorModes[] values = values();
        if (ordinal < 0 || ordinal >= values.length) return BLUE;
        return values[ordinal];
    }
}
