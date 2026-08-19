package com.example.colorcannons.munitions;

import com.example.colorcannons.registry.ModColorModes;
import com.example.colorcannons.registry.ModEntities;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoType;
import rbasamoyai.createbigcannons.munitions.autocannon.config.AutocannonProjectilePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.autocannon.config.InertAutocannonProjectileProperties;

public class LaserRoundItem extends Item implements AutocannonAmmoItem {
    private final ModColorModes colorMode;
    public LaserRoundItem(Properties properties, ModColorModes colorMode) { super(properties); this.colorMode = colorMode; }
    public ModColorModes getColorMode() { return colorMode; }

    @Override @Nullable
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        EntityType<?> type = ModEntities.tracerFor(colorMode);
        Object entity = type.create(level);
        return entity instanceof AbstractAutocannonProjectile ? (AbstractAutocannonProjectile) entity : null;
    }

    @Override @Nullable public EntityType<?> getEntityType(ItemStack stack) { return ModEntities.tracerFor(colorMode); }

    @Override
    public AutocannonProjectilePropertiesComponent getAutocannonProperties(ItemStack stack) {
        InertAutocannonProjectileProperties props = CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE.getPropertiesOf(getEntityType(stack));
        return props.autocannonProperties();
    }

    @Override public boolean isTracer(ItemStack stack) { return true; }
    @Override public void setTracer(ItemStack stack, boolean value) { }
    @Override public ItemStack getSpentItem(ItemStack stack) { return ItemStack.EMPTY; }
    @Override public AutocannonAmmoType getType() { return AutocannonAmmoType.AUTOCANNON; }
}
