package com.example.colorcannons.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Central, data-driven config for everything the spec calls "modifiable":
 * FE capacity, FE cost per shot, tracer size, and (as a forward hook)
 * per-color-mode overrides so a third/fourth color mode can be added later
 * without touching code — only this config + {@link com.example.colorcannons.registry.ModColorModes}.
 */
public class ColorCannonsConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue FE_CAPACITY;
    public static final ModConfigSpec.IntValue FE_PER_SHOT;
    public static final ModConfigSpec.IntValue FE_MAX_TRANSFER;

    public static final ModConfigSpec.DoubleValue TRACER_SIZE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue TRACER_LENGTH_MULTIPLIER;

    public static final ModConfigSpec.BooleanValue REQUIRE_FE_TO_FIRE;

    public static final ModConfigSpec.DoubleValue IMPACT_EXPLOSION_POWER;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("FE Fixed Cannon Mount settings").push("fe_mount");

        FE_CAPACITY = builder
                .comment("Maximum FE the FE Fixed Cannon Mount can store.")
                .defineInRange("feCapacity", 5_000_000, 1000, Integer.MAX_VALUE);

        FE_PER_SHOT = builder
                .comment("FE consumed for every shot fired from the FE Fixed Cannon Mount.")
                .defineInRange("fePerShot", 2000, 0, Integer.MAX_VALUE);

        FE_MAX_TRANSFER = builder
                .comment("Max FE/tick the mount will accept from cables/generators.")
                .defineInRange("feMaxTransfer", 10_000, 1, Integer.MAX_VALUE);

        REQUIRE_FE_TO_FIRE = builder
                .comment("If true, the mount refuses to fire when stored FE < fePerShot.")
                .define("requireFeToFire", true);

        builder.pop();

        builder.comment("Laser impact combat").push("impact");

        IMPACT_EXPLOSION_POWER = builder
                .comment("CBC impact explosion power. Kept small for a focused laser strike while still damaging armor blocks.")
                .defineInRange("explosionPower", 1.5, 0.1, 4.0);

        builder.pop();

        builder.comment("Tracer projectile visuals").push("tracer");

        TRACER_SIZE_MULTIPLIER = builder
                .comment("Scale applied to the default Autocannon shell size for the tracer bolt.")
                .defineInRange("sizeMultiplier", 1.0, 0.1, 10.0);

        TRACER_LENGTH_MULTIPLIER = builder
                .comment("Scale applied to the tracer's visual trail length (Star-Wars blaster bolt look).")
                .defineInRange("lengthMultiplier", 1.0, 0.1, 20.0);

        builder.pop();

        SPEC = builder.build();
    }
}
