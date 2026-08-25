package com.example.colorcannons.registry;

import com.example.colorcannons.ColorCannonsMod;
import com.example.colorcannons.breech.LaserAutocannonBreechBlockEntity;
import com.example.colorcannons.mount.FeFixedCannonMountBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ColorCannonsMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LaserAutocannonBreechBlockEntity>> LASER_BREECH =
            BLOCK_ENTITIES.register("laser_autocannon_breech", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new LaserAutocannonBreechBlockEntity(pos, state),
                    ModBlocks.LASER_AUTOCANNON_BREECH.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FeFixedCannonMountBlockEntity>> FE_FIXED_CANNON_MOUNT =
            BLOCK_ENTITIES.register("fe_fixed_cannon_mount", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FeFixedCannonMountBlockEntity(pos, state),
                    ModBlocks.FE_FIXED_CANNON_MOUNT.get()).build(null));
}
