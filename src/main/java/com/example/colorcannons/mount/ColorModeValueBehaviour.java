package com.example.colorcannons.mount;

import com.example.colorcannons.registry.ModColorModes;
import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Dedicated Create scroll control for the laser colour.
 *
 * Values are deliberately limited to two discrete states:
 * 0 = BLUE, 1 = RED.
 * The transform has its own hitbox so it cannot overlap CBC's pitch control.
 */
public class ColorModeValueBehaviour extends ScrollValueBehaviour {
    public static final BehaviourType<ColorModeValueBehaviour> TYPE = new BehaviourType<>();
    private final ValueBoxTransform slotPositioning;

    public ColorModeValueBehaviour(SmartBlockEntity be) {
        super(Component.translatable("colorcannons.fe_fixed_cannon_mount.color_mode"), be, new ColorModeValueBox());
        this.slotPositioning = getSlotPositioning();
        between(0, 1);
        withFormatter(v -> ModColorModes.byOrdinalSafe(v).name());
    }

    public ModColorModes getColorMode() {
        return ModColorModes.byOrdinalSafe(getValue());
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlDown) {
        int value = valueSetting.value() <= 0 ? ModColorModes.BLUE.ordinal() : ModColorModes.RED.ordinal();
        if (value == getValue())
            return;
        setValue(value);
        playFeedbackSound(this);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(
                Component.literal("BLUE").withStyle(ChatFormatting.BLUE),
                Component.literal("RED").withStyle(ChatFormatting.RED));
        return new ValueSettingsBoard(label, 1, 1, rows,
                new ValueSettingsFormatter(settings -> formatValue(settings)));
    }

    private MutableComponent formatValue(ValueSettings settings) {
        ModColorModes mode = ModColorModes.byOrdinalSafe(settings.value());
        return Component.literal(mode.name())
                .withStyle(mode == ModColorModes.RED ? ChatFormatting.RED : ChatFormatting.BLUE);
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
        super.write(nbt, registries, clientPacket);
        nbt.putInt("ColorMode", ModColorModes.byOrdinalSafe(getValue()).ordinal());
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        if (nbt.contains("ColorMode"))
            setValue(nbt.getInt("ColorMode"));
        else
            setValue(ModColorModes.BLUE.ordinal());
    }

    private static class ColorModeValueBox extends CenteredSideValueBoxTransform {
        ColorModeValueBox() {
            super((state, direction) -> state.getValue(BlockStateProperties.FACING) != direction);
        }

        @Override
        protected Vec3 getSouthLocation() {
            // Keep this control on the dedicated colour face and away from
            // CBC's pitch/yaw bearing control hitbox.
            return new Vec3(8.0 / 16.0, 8.0 / 16.0, 15.5 / 16.0);
        }
    }
}
