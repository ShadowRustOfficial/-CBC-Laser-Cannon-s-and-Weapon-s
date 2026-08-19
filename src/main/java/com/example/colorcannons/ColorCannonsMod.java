package com.example.colorcannons;

import com.example.colorcannons.config.ColorCannonsConfig;
import com.example.colorcannons.registry.ModBlockEntities;
import com.example.colorcannons.registry.ModBlocks;
import com.example.colorcannons.registry.ModEntities;
import com.example.colorcannons.registry.ModItems;
import com.example.colorcannons.registry.ModSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ColorCannonsMod.MOD_ID)
public class ColorCannonsMod {
    public static final String MOD_ID = "colorcannons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ColorCannonsMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ColorCannonsConfig.SPEC);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        com.example.colorcannons.registry.ModCreativeTabs.TABS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("Color Autocannons addon initializing (hard-depends on Create Big Cannons {})", "5.11.7");
    }

    private void commonSetup(final net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        LOGGER.info("Color Autocannons common setup complete.");
    }
}
