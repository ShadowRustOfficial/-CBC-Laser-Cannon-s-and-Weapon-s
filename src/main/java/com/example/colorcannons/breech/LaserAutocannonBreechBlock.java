package com.example.colorcannons.breech;

import com.example.colorcannons.registry.ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AbstractAutocannonBreechBlockEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AutocannonBreechBlock;
import rbasamoyai.createbigcannons.index.CBCAutocannonMaterials;

public class LaserAutocannonBreechBlock extends AutocannonBreechBlock {
    public LaserAutocannonBreechBlock(BlockBehaviour.Properties properties) { super(properties, CBCAutocannonMaterials.CAST_IRON); }
    @Override public BlockEntityType<? extends AbstractAutocannonBreechBlockEntity> getBlockEntityType() { return ModBlockEntities.LASER_BREECH.get(); }
}
