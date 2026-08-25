package com.example.colorcannons.registry;

import com.example.colorcannons.ColorCannonsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.example.colorcannons.munitions.LaserAutocannonProjectile;

/**
 * Both tracer colors reuse CBC's own APAutocannonProjectile class
 * unchanged -- its damage/ballistics are already fully data-driven via
 * CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE, keyed off
 * whatever EntityType the instance belongs to. So no new projectile Java
 * class is needed at all: we just register it under two different ids
 * ("blue_tracer" / "red_tracer"), ship a matching properties JSON for each
 * under data/colorcannons/munition_properties/projectiles/, and bind a
 * differently-colored ColorTracerRenderer to each id client-side (see
 * client.ColorCannonsClient). The EntityType id is the only thing that
 * carries "which color" information for the projectile.
 */
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ColorCannonsMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<LaserAutocannonProjectile>> BLUE_TRACER =
            ENTITY_TYPES.register("blue_tracer", () -> EntityType.Builder
                    .<LaserAutocannonProjectile>of(LaserAutocannonProjectile::new, MobCategory.MISC)
                    .sized(0.2f, 0.2f)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("blue_tracer"));

    public static final DeferredHolder<EntityType<?>, EntityType<LaserAutocannonProjectile>> RED_TRACER =
            ENTITY_TYPES.register("red_tracer", () -> EntityType.Builder
                    .<LaserAutocannonProjectile>of(LaserAutocannonProjectile::new, MobCategory.MISC)
                    .sized(0.2f, 0.2f)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("red_tracer"));

    public static EntityType<LaserAutocannonProjectile> tracerFor(ModColorModes mode) {
        return switch (mode) {
            case BLUE -> BLUE_TRACER.get();
            case RED -> RED_TRACER.get();
        };
    }
}
