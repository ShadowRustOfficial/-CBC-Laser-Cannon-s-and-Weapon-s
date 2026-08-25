package com.example.colorcannons.registry;

import com.example.colorcannons.ColorCannonsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ColorCannonsMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.colorcannons.main"))
                    .icon(() -> new ItemStack(ModItems.LASER_AUTOCANNON_BREECH.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.LASER_AUTOCANNON_BREECH.get());
                        output.accept(ModItems.FE_FIXED_CANNON_MOUNT.get());
                        // Blue/red laser round items are intentionally NOT
                        // added here -- they're synthesized at fire time
                        // only, never meant to be held or placed by a player.
                    })
                    .build());
}
