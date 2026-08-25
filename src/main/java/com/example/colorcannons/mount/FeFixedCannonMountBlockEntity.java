package com.example.colorcannons.mount;

import com.example.colorcannons.config.ColorCannonsConfig;
import com.example.colorcannons.registry.ModBlockEntities;
import com.example.colorcannons.registry.ModColorModes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity;
import com.simibubi.create.content.contraptions.AssemblyException;

/**
 * FE-powered fixed cannon mount for the Color Autocannons laser cannon.
 *
 * This subclasses CBC's mount so pitch/yaw control, redstone assembly,
 * contraption attachment, disassembly, and cannon-control integration remain
 * the stock CBC implementation. The assembly method is overridden because
 * CBC's implementation hard-codes its own Fixed Cannon Mount block entry;
 * that check rejects this addon's subclass block before it ever reaches
 * AbstractMountedCannonContraption.assemble().
 */
public class FeFixedCannonMountBlockEntity extends FixedCannonMountBlockEntity {

    private final EnergyStorage energyStorage = new EnergyStorage(
            ColorCannonsConfig.FE_CAPACITY.get(),
            ColorCannonsConfig.FE_MAX_TRANSFER.get(),
            0
    );

    private ColorModeValueBehaviour colorModeSlot;

    public FeFixedCannonMountBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FE_FIXED_CANNON_MOUNT.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.colorModeSlot = new ColorModeValueBehaviour(this);
        behaviours.add(this.colorModeSlot);
    }

    public ModColorModes getColorMode() {
        return colorModeSlot != null ? colorModeSlot.getColorMode() : ModColorModes.BLUE;
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public boolean hasEnoughFeForShot() {
        if (!ColorCannonsConfig.REQUIRE_FE_TO_FIRE.get())
            return true;
        return energyStorage.getEnergyStored() >= ColorCannonsConfig.FE_PER_SHOT.get();
    }

    public void consumeFeForShot() {
        energyStorage.extractEnergy(ColorCannonsConfig.FE_PER_SHOT.get(), false);
        setChanged();
    }

    /**
     * CBC's stock implementation begins with CBCBlocks.FIXED_CANNON_MOUNT.has(state).
     * That is intentionally false for our custom mount block, so the inherited
     * implementation silently returns before assembling. Reproduce the stock
     * assembly flow without that identity check and use attach(), which updates
     * CBC's protected mountedContraption/running state correctly.
     */
    @Override
    protected void assemble() throws AssemblyException {
        BlockPos controllerPos = getBlockPos();
        net.minecraft.core.Direction facing = getBlockState()
                .getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING);
        BlockPos cannonPos = controllerPos.relative(facing);

        Level level = getLevel();
        if (level == null || level.isOutsideBuildHeight(cannonPos))
            throw rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity
                    .cannonBlockOutsideOfWorld(cannonPos);

        AbstractMountedCannonContraption contraption = getLaserContraption(cannonPos);
        if (contraption == null)
            return;

        if (!contraption.assemble(level, cannonPos))
            return;

        net.minecraft.core.Direction initialOrientation = contraption.initialOrientation();

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);

        PitchOrientedContraptionEntity entity = PitchOrientedContraptionEntity.create(
                level, contraption, initialOrientation, this);

        attach(entity);
        resetContraptionToOffset();
        level.addFreshEntity(entity);
        sendData();
        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, controllerPos);
    }

    private AbstractMountedCannonContraption getLaserContraption(BlockPos pos) {
        if (getLevel() == null)
            return null;
        net.minecraft.world.level.block.Block block = getLevel().getBlockState(pos).getBlock();
        if (block instanceof rbasamoyai.createbigcannons.cannons.CannonContraptionProviderBlock provider)
            return provider.getCannonContraption();
        return null;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("StoredFE", energyStorage.getEnergyStored());
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.contains("StoredFE"))
            energyStorage.receiveEnergy(tag.getInt("StoredFE"), false);
    }
}
