package com.example.colorcannons.mount;

import com.example.colorcannons.registry.ModColorModes;
import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
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

/** Create value control for the mount's colour selector. */
public class ColorModeValueBehaviour extends com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour {
    public static final BehaviourType<ColorModeValueBehaviour> TYPE = new BehaviourType<>();
    private final ValueBoxTransform slotPositioning;

    public ColorModeValueBehaviour(SmartBlockEntity be) {
        super(be);
        setLabel(Component.translatable("colorcannons.fe_fixed_cannon_mount.color_mode"));
        slotPositioning = new ColorModeValueBox();
        between(0, 1);
        withFormatter(v -> ModColorModes.byOrdinalSafe(v).name());
    }

    public ModColorModes getColorMode() { return ModColorModes.byOrdinalSafe(getValue()); }

    @Override
    public boolean testHit(Vec3 hit) {
        LevelAccessor level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = blockEntity.getBlockState();
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(pos));
        return slotPositioning.testHit(level, pos, state, localHit);
    }

    @Override
    public MutableComponent formatValue(ValueSettings settings) {
        ModColorModes mode = ModColorModes.byOrdinalSafe(settings.value());
        return Component.literal(mode.name()).withStyle(mode == ModColorModes.RED ? ChatFormatting.RED : ChatFormatting.BLUE);
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        if (!valueSetting.equals(getValueSettings())) playFeedbackSound(this);
        setValue(valueSetting.value() == 1 ? 1 : 0);
        blockEntity.setChanged();
        blockEntity.sendData();
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(
                Component.literal("BLUE").withStyle(ChatFormatting.BLUE),
                Component.literal("RED").withStyle(ChatFormatting.RED));
        return new ValueSettingsBoard(label, 1, 1, rows, new ValueSettingsFormatter(this::formatValue));
    }

    @Override
    public ValueBoxTransform getSlotPositioning() { return slotPositioning; }
    @Override
    public BehaviourType<?> getType() { return TYPE; }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putInt("ColorMode", ModColorModes.byOrdinalSafe(getValue()).ordinal());
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        setValue(nbt.contains("ColorMode") ? nbt.getInt("ColorMode") : ModColorModes.BLUE.ordinal());
    }

    private static class ColorModeValueBox extends CenteredSideValueBoxTransform {
        ColorModeValueBox() {
            super((state, dir) -> state.getValue(BlockStateProperties.FACING) != dir);
        }
        @Override
        protected Vec3 getSouthLocation() {
            return new Vec3(8.0 / 16.0, 8.0 / 16.0, 14.0 / 16.0);
        }
    }
}
