package com.example.colorcannons.registry;

import com.example.colorcannons.ColorCannonsMod;
import com.example.colorcannons.mount.FeFixedCannonMountBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = ColorCannonsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.FE_FIXED_CANNON_MOUNT.get(), (blockEntity, side) -> ((FeFixedCannonMountBlockEntity) blockEntity).getEnergyStorage());
    }
}
