package com.example.colorcannons.client;

import com.example.colorcannons.ColorCannonsMod;
import com.example.colorcannons.registry.ModColorModes;
import com.example.colorcannons.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.SubscribeEvent;

@EventBusSubscriber(modid = ColorCannonsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ColorCannonsClient {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BLUE_TRACER.get(), ctx -> new ColorTracerRenderer<>(ctx, ModColorModes.BLUE));
        event.registerEntityRenderer(ModEntities.RED_TRACER.get(), ctx -> new ColorTracerRenderer<>(ctx, ModColorModes.RED));
    }
}
