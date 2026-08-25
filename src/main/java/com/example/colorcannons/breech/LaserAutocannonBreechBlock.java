package com.example.colorcannons.breech;

import com.example.colorcannons.registry.ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AbstractAutocannonBreechBlockEntity;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AutocannonBreechBlock;
import rbasamoyai.createbigcannons.index.CBCAutocannonMaterials;

/**
 * A real, direct copy of CBC's AutocannonBreechBlock -- same shape,
 * placement, wrenching, and seat interaction, all inherited untouched.
 * Built on the existing "cast_iron" AutocannonMaterial so it connects to
 * normal cast-iron barrel/recoil-spring blocks in survival exactly like
 * the stock breech does (no new material/blockstate family needed just to
 * swap the breech). The only override is which BlockEntity type it
 * instantiates, so the block itself still renders with CBC's own
 * cast-iron breech model/texture (see blockstates/*.json) -- this addon
 * supplies no new textures for it, per your request to keep the stock
 * look. There is one physical breech (renamed "Laser Autocannon Breech" --
 * see lang file -- so it's unmistakable in your inventory next to the
 * stock AP/machine-gun/flak breeches); which color it fires is chosen live
 * from the connected FE Fixed Cannon Mount's slider.
 */
public class LaserAutocannonBreechBlock extends AutocannonBreechBlock {

    public LaserAutocannonBreechBlock(BlockBehaviour.Properties properties) {
        super(properties, CBCAutocannonMaterials.CAST_IRON);
    }

    @Override
    public BlockEntityType<? extends AbstractAutocannonBreechBlockEntity> getBlockEntityType() {
        return ModBlockEntities.LASER_BREECH.get();
    }
}
