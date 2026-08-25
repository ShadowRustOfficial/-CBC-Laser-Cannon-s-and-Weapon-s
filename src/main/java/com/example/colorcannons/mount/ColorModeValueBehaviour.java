package com.example.colorcannons.mount;

import com.example.colorcannons.registry.ModColorModes;
import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Discrete two-state colour selector for the FE fixed mount.
 *
 * This deliberately implements Create's ValueSettingsBehaviour directly
 * instead of inheriting ValveHandleScrollValueBehaviour. The latter is a
 * scroll-value control intended for continuously adjustable values such as
 * CBC pitch/yaw and can compete for scroll input with the neighbouring mount
 * controls. Here the colour is a state, not a slider: row 0 = BLUE and row 1
 * = RED, with the numeric value always zero.
 */
public class ColorModeValueBehaviour extends BlockEntityBehaviour implements ValueSettingsBehaviour {

    public static final BehaviourType<ColorModeValueBehaviour> TYPE = new BehaviourType<>();

    private final ValueBoxTransform slotPositioning;
    private int row;

    public ColorModeValueBehaviour(SmartBlockEntity be) {
        super(be);
        this.row = 0;
        this.slotPositioning = new ColorModeValueBox();
    }

    public ModColorModes getColorMode() {
        return row == 1 ? ModColorModes.RED : ModColorModes.BLUE;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean testHit(Vec3 hit) {
        LevelAccessor level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = blockEntity.getBlockState();
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(pos));
        return slotPositioning.testHit(level, pos, state, localHit);
    }

    public MutableComponent formatValue(ValueSettingsBehaviour.ValueSettings settings) {
        return settings.row() == 1
                ? Component.literal("RED").withStyle(ChatFormatting.RED)
                : Component.literal("BLUE").withStyle(ChatFormatting.BLUE);
    }

    @Override
    public ValueSettingsBehaviour.ValueSettings getValueSettings() {
        return new ValueSettingsBehaviour.ValueSettings(row, 0);
    }

    @Override
    public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings settings, boolean ctrlDown) {
        int newRow = settings.row() == 1 ? 1 : 0;
        if (newRow == row)
            return;
        row = newRow;
        blockEntity.setChanged();
        blockEntity.sendData();
        playFeedbackSound(this);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(
                Component.literal("BLUE").withStyle(ChatFormatting.BLUE),
                Component.literal("RED").withStyle(ChatFormatting.RED));
        return new ValueSettingsBoard(
                Component.literal("Color Mode"),
                1,
                1,
                rows,
                new ValueSettingsFormatter(this::formatValue));
    }

    @Override
    public ValueBoxTransform getSlotPositioning() {
        return slotPositioning;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putInt("ColorMode", row);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        row = nbt.getInt("ColorMode") == 1 ? 1 : 0;
    }

    private static class ColorModeValueBox extends CenteredSideValueBoxTransform {
        ColorModeValueBox() {
            super((state, dir) -> state.getValue(BlockStateProperties.FACING) != dir);
        }

        @Override
        protected Vec3 getSouthLocation() {
            // Exactly centred on the mount face, between CBC's pitch (x=4)
            // and yaw (x=12) controls. The dedicated behaviour above means
            // it no longer participates in their scroll-value handling.
            return new Vec3(0.5, 0.5, 15.5 / 16.0);
        }
    }
}
