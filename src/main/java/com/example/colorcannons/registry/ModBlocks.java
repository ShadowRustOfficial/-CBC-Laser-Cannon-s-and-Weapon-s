package com.example.colorcannons.registry;

import com.example.colorcannons.ColorCannonsMod;
import com.example.colorcannons.breech.LaserAutocannonBreechBlock;
import com.example.colorcannons.mount.FeFixedCannonMountBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.createbigcannons.index.CBCBlocks;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ColorCannonsMod.MOD_ID);
    public static final DeferredBlock<LaserAutocannonBreechBlock> LASER_AUTOCANNON_BREECH = BLOCKS.register("laser_autocannon_breech", () -> new LaserAutocannonBreechBlock(BlockBehaviour.Properties.ofFullCopy(CBCBlocks.CAST_IRON_AUTOCANNON_BREECH.get())));
    public static final DeferredBlock<FeFixedCannonMountBlock> FE_FIXED_CANNON_MOUNT = BLOCKS.register("fe_fixed_cannon_mount", () -> new FeFixedCannonMountBlock(BlockBehaviour.Properties.ofFullCopy(CBCBlocks.FIXED_CANNON_MOUNT.get())));
}
