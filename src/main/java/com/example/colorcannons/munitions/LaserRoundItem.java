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

/**
 * The "round" our custom breech manufactures out of thin air (see
 * {@link com.example.colorcannons.breech.LaserAutocannonBreechBlockEntity#extractNextInput()})
 * whenever the connected FE Fixed Cannon Mount has enough energy. It is
 * never actually held in an inventory or consumed from a magazine — it
 * exists only for the one tick it takes {@code fireShot} to read it, which
 * is why {@link #getSpentItem} returns EMPTY (nothing is ejected) and
 * {@link #isTracer}/{@link #setTracer} are hardcoded rather than
 * stack-backed.
 *
 * Modeled directly on CBC's own {@code MachineGunRoundItem}, which also
 * implements {@link AutocannonAmmoItem} directly instead of going through
 * the cartridge-wrapper system — the simplest working pattern for a round
 * that doesn't need to exist as a physical, storable item.
 */
public class LaserRoundItem extends Item implements AutocannonAmmoItem {

    private final ModColorModes colorMode;

    public LaserRoundItem(Properties properties, ModColorModes colorMode) {
        super(properties);
        this.colorMode = colorMode;
    }

    public ModColorModes getColorMode() {
        return colorMode;
    }

    @Override
    @Nullable
    public AbstractAutocannonProjectile getAutocannonProjectile(ItemStack stack, Level level) {
        EntityType<?> type = ModEntities.tracerFor(colorMode);
        Object entity = type.create(level);
        return entity instanceof AbstractAutocannonProjectile ? (AbstractAutocannonProjectile) entity : null;
    }

    @Override
    @Nullable
    public EntityType<?> getEntityType(ItemStack stack) {
        return ModEntities.tracerFor(colorMode);
    }

    @Override
    public AutocannonProjectilePropertiesComponent getAutocannonProperties(ItemStack stack) {
        // Reuses CBC's own JSON-driven ballistics/damage system — see
        // data/colorcannons/munition_properties/projectiles/{blue,red}_tracer.json,
        // read by the same InertAutocannonProjectilePropertiesHandler CBC's
        // own AP/inert rounds use, keyed by our tracer EntityType id.
        InertAutocannonProjectileProperties props =
                CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE.getPropertiesOf(getEntityType(stack));
        return props.autocannonProperties();
    }

    @Override
    public boolean isTracer(ItemStack stack) {
        return true; // always renders as a bright bolt — that's the entire point of this round
    }

    @Override
    public void setTracer(ItemStack stack, boolean value) {
        // no-op: this round is never a persisted stack, so there's nothing to flip
    }

    @Override
    public ItemStack getSpentItem(ItemStack stack) {
        return ItemStack.EMPTY; // no casing, magazine, or waste item — it was FE, not a physical round
    }

    @Override
    public AutocannonAmmoType getType() {
        return AutocannonAmmoType.AUTOCANNON;
    }
}
