package com.example.colorcannons.registry;

import com.example.colorcannons.ColorCannonsMod;
import com.example.colorcannons.munitions.LaserRoundItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, ColorCannonsMod.MOD_ID);

    // The one placeable/renamed item -- see lang file: "Laser Autocannon Breech",
    // clearly distinguishing it from CBC's own AP/machine-gun/flak breeches.
    public static final DeferredHolder<Item, BlockItem> LASER_AUTOCANNON_BREECH =
            ITEMS.register("laser_autocannon_breech", () -> new BlockItem(ModBlocks.LASER_AUTOCANNON_BREECH.get(),
                    new Item.Properties()));

    public static final DeferredHolder<Item, BlockItem> FE_FIXED_CANNON_MOUNT =
            ITEMS.register("fe_fixed_cannon_mount", () -> new BlockItem(ModBlocks.FE_FIXED_CANNON_MOUNT.get(),
                    new Item.Properties()));

    // Never obtainable/placeable by a player -- these exist purely so the
    // breech can manufacture a stack of one at fire time (see
    // LaserAutocannonBreechBlockEntity#extractNextInput). Not added to any
    // creative tab.
    public static final DeferredHolder<Item, LaserRoundItem> BLUE_LASER_ROUND =
            ITEMS.register("blue_laser_round", () -> new LaserRoundItem(new Item.Properties(), ModColorModes.BLUE));

    public static final DeferredHolder<Item, LaserRoundItem> RED_LASER_ROUND =
            ITEMS.register("red_laser_round", () -> new LaserRoundItem(new Item.Properties(), ModColorModes.RED));

    public static LaserRoundItem laserRoundFor(ModColorModes mode) {
        return switch (mode) {
            case BLUE -> BLUE_LASER_ROUND.get();
            case RED -> RED_LASER_ROUND.get();
        };
    }
}
