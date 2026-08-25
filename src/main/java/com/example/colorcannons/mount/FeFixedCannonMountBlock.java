package com.example.colorcannons.mount;

import com.example.colorcannons.registry.ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlock;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity;

/**
 * A real, direct copy of CBC's FixedCannonMountBlock — same shape,
 * rotation/placement math (getAssemblyFace/getFiringFace), wrenching, and
 * redstone tick handling, all inherited untouched. Renders with CBC's own
 * fixed-cannon-mount model/texture (see blockstates/fe_fixed_cannon_mount.json,
 * which is a straight copy of CBC's own blockstate pointing at CBC's
 * models) — no new textures supplied, per your request. The only override
 * is which BlockEntity type it instantiates.
 */
public class FeFixedCannonMountBlock extends FixedCannonMountBlock {

    public FeFixedCannonMountBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends FixedCannonMountBlockEntity> getBlockEntityType() {
        return ModBlockEntities.FE_FIXED_CANNON_MOUNT.get();
    }
}
