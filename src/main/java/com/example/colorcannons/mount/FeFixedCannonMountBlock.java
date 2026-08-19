package com.example.colorcannons.mount;

import com.example.colorcannons.registry.ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlock;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity;

public class FeFixedCannonMountBlock extends FixedCannonMountBlock {
    public FeFixedCannonMountBlock(BlockBehaviour.Properties properties) { super(properties); }
    @Override public BlockEntityType<? extends FixedCannonMountBlockEntity> getBlockEntityType() { return ModBlockEntities.FE_FIXED_CANNON_MOUNT.get(); }
}
