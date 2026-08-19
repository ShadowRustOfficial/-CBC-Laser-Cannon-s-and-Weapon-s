package com.example.colorcannons.registry;

import com.example.colorcannons.ColorCannonsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.createbigcannons.munitions.autocannon.ap_round.APAutocannonProjectile;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ColorCannonsMod.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<APAutocannonProjectile>> BLUE_TRACER = ENTITY_TYPES.register("blue_tracer", () -> EntityType.Builder.<APAutocannonProjectile>of(APAutocannonProjectile::new, MobCategory.MISC).sized(0.2f, 0.2f).clientTrackingRange(10).updateInterval(1).build("blue_tracer"));
    public static final DeferredHolder<EntityType<?>, EntityType<APAutocannonProjectile>> RED_TRACER = ENTITY_TYPES.register("red_tracer", () -> EntityType.Builder.<APAutocannonProjectile>of(APAutocannonProjectile::new, MobCategory.MISC).sized(0.2f, 0.2f).clientTrackingRange(10).updateInterval(1).build("red_tracer"));
    public static EntityType<APAutocannonProjectile> tracerFor(ModColorModes mode) { return switch (mode) { case BLUE -> BLUE_TRACER.get(); case RED -> RED_TRACER.get(); }; }
}
