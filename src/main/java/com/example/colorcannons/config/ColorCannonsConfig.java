package com.example.colorcannons.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Central configuration for the FE laser cannon. */
public class ColorCannonsConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue FE_CAPACITY;
    public static final ModConfigSpec.IntValue FE_PER_SHOT;
    public static final ModConfigSpec.IntValue FE_MAX_TRANSFER;
    public static final ModConfigSpec.DoubleValue TRACER_SIZE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue TRACER_LENGTH_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue REQUIRE_FE_TO_FIRE;
    public static final ModConfigSpec.DoubleValue IMPACT_EXPLOSION_POWER;
    public static final ModConfigSpec.IntValue FIRE_INTERVAL_TICKS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("FE Fixed Cannon Mount settings").push("fe_mount");
        FE_CAPACITY = builder.comment("Maximum FE stored by the mount.")
                .defineInRange("feCapacity", 5_000_000, 1000, Integer.MAX_VALUE);
        FE_PER_SHOT = builder.comment("FE consumed by each laser shot.")
                .defineInRange("fePerShot", 2000, 0, Integer.MAX_VALUE);
        FE_MAX_TRANSFER = builder.comment("Maximum FE/tick accepted by the mount.")
                .defineInRange("feMaxTransfer", 10_000, 1, Integer.MAX_VALUE);
        REQUIRE_FE_TO_FIRE = builder.comment("Refuse to fire when insufficient FE is stored.")
                .define("requireFeToFire", true);
        builder.pop();
        builder.comment("Laser firing").push("firing");
        FIRE_INTERVAL_TICKS = builder.comment("Minimum ticks between laser shots. 20 ticks = 1 second.")
                .defineInRange("fireIntervalTicks", 20, 1, 200);
        builder.pop();
        builder.comment("Laser impact combat").push("impact");
        IMPACT_EXPLOSION_POWER = builder.comment("CBC impact explosion power.")
                .defineInRange("explosionPower", 3.0, 0.1, 8.0);
        builder.pop();
        builder.comment("Tracer projectile visuals").push("tracer");
        TRACER_SIZE_MULTIPLIER = builder.comment("Tracer thickness multiplier.")
                .defineInRange("sizeMultiplier", 1.0, 0.1, 10.0);
        TRACER_LENGTH_MULTIPLIER = builder.comment("Length of the moving Star-Wars-style projectile tracer.")
                .defineInRange("lengthMultiplier", 1.75, 0.1, 20.0);
        builder.pop();
        SPEC = builder.build();
    }
}
