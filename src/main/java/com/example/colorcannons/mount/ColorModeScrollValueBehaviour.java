package com.example.colorcannons.mount;

import com.example.colorcannons.registry.ModColorModes;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Create ValueSettings behaviour for selecting the laser colour. */
public class ColorModeScrollValueBehaviour extends ValveHandleBlockEntity.ValveHandleScrollValueBehaviour {
    public static final BehaviourType<ColorModeScrollValueBehaviour> TYPE = new BehaviourType<>();
    private final ValueBoxTransform slotPositioning;

    public ColorModeScrollValueBehaviour(SmartBlockEntity be) {
        super(be);
        this.setLabel(Component.translatable("colorcannons.fe_fixed_cannon_mount.color_mode"));
        this.slotPositioning = new ColorModeValueBox();
        this.between(0, 1);
        this.withFormatter(v -> ModColorModes.byOrdinalSafe(v).name());
    }

    public ModColorModes getColorMode() { return ModColorModes.byOrdinalSafe(this.getValue()); }

    @Override
    public boolean testHit(Vec3 hit) {
        Level level = this.blockEntity.getLevel();
        BlockPos pos = this.blockEntity.getBlockPos();
        BlockState state = this.blockEntity.getBlockState();
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(pos));
        return this.slotPositioning.testHit((LevelAccessor) level, pos, state, localHit);
    }

    @Override
    public MutableComponent formatValue(ValueSettings settings) {
        return Component.literal(ModColorModes.byOrdinalSafe(settings.value()).name());
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        if (!valueSetting.equals(this.getValueSettings())) this.playFeedbackSound(this);
        this.setValue(valueSetting.value());
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(Component.literal("Blue"), Component.literal("Red"));
        return new ValueSettingsBoard(this.label, 1, 1, rows, new ValueSettingsFormatter(this::formatValue));
    }

    @Override public ValueBoxTransform getSlotPositioning() { return this.slotPositioning; }
    @Override public BehaviourType<?> getType() { return TYPE; }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putInt("ColorMode", this.value);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        this.value = nbt.getInt("ColorMode");
    }

    private static class ColorModeValueBox extends CenteredSideValueBoxTransform {
        ColorModeValueBox() { super((state, dir) -> state.getValue(BlockStateProperties.FACING) != dir); }
        @Override protected Vec3 getSouthLocation() { return new Vec3(8.0 / 16.0, 8.0 / 16.0, 15.5 / 16.0); }
    }
}
