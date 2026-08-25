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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for the Color Autocannons addon.
 *
 * This mod is a pure addon on top of Create Big Cannons (CBC): it never
 * modifies CBC's own classes or assets, it only extends / wraps them. The
 * hard dependency declared in META-INF/neoforge.mods.toml (type = "required"
 * for both "create" and "createbigcannons") is what makes NeoForge refuse to
 * start the game if CBC is missing — that check happens before this class is
 * even constructed, so no runtime guard is needed here for the "must not
 * load without CBC" requirement.
 */
@Mod(ColorCannonsMod.MOD_ID)
public class ColorCannonsMod {

    public static final String MOD_ID = "colorcannons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ColorCannonsMod(IEventBus modEventBus, ModContainer modContainer) {
        // Config first: everything below reads defaults from it at class-init time.
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ColorCannonsConfig.SPEC);

        // Deferred registers — see registry/ package. Each register() call
        // hooks its DeferredRegister onto modEventBus; nothing is actually
        // registered until Forge/NeoForge fires RegisterEvent later.
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        com.example.colorcannons.registry.ModCreativeTabs.TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);


        LOGGER.info("Color Autocannons addon initializing (hard-depends on Create Big Cannons {})",
                "5.11.7");
    }

    private void commonSetup(final net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        // Capability registration for the FE Fixed Cannon Mount is done via
        // RegisterCapabilitiesEvent in registry.ModCapabilities — nothing needed here.
        LOGGER.info("Color Autocannons common setup complete.");
    }
}
