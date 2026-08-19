package com.example.colorcannons.mount;

import com.example.colorcannons.config.ColorCannonsConfig;
import com.example.colorcannons.registry.ModColorModes;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity;

public class FeFixedCannonMountBlockEntity extends FixedCannonMountBlockEntity {
    private final EnergyStorage energyStorage = new EnergyStorage(ColorCannonsConfig.FE_CAPACITY.get(), ColorCannonsConfig.FE_MAX_TRANSFER.get(), 0);
    private ColorModeScrollValueBehaviour colorModeSlot;

    public FeFixedCannonMountBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        colorModeSlot = new ColorModeScrollValueBehaviour(this);
        behaviours.add(colorModeSlot);
    }

    public ModColorModes getColorMode() { return colorModeSlot != null ? colorModeSlot.getColorMode() : ModColorModes.BLUE; }
    public EnergyStorage getEnergyStorage() { return energyStorage; }
    public boolean hasEnoughFeForShot() { return !ColorCannonsConfig.REQUIRE_FE_TO_FIRE.get() || energyStorage.getEnergyStored() >= ColorCannonsConfig.FE_PER_SHOT.get(); }
    public void consumeFeForShot() { energyStorage.extractEnergy(ColorCannonsConfig.FE_PER_SHOT.get(), false); setChanged(); }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("StoredFE", energyStorage.getEnergyStored());
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.contains("StoredFE")) energyStorage.receiveEnergy(tag.getInt("StoredFE"), false);
    }
}
