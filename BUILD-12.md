# Color Autocannons — Build 12

Based directly on Build 11.

## Patch
- Laser projectile records its world-space spawn/muzzle position and synchronizes it to the client.
- Laser tracer rendering now spans from the recorded muzzle to the projectile's current position instead of using a short previous-tick trail.
- Tracer thickness defaults to 1.0x and no longer grows into the oversized CBC-style halo.
- Laser projectile keeps zero gravity/drag and zero deflection so it does not ricochet.
- CBC/Ritchie's Projectile Library projectile pipeline remains authoritative for collision and penetration.
- Laser projectile damage/penetration properties were strengthened for armour penetration.
- Impact explosion remains routed through CBC's ProjectileContext.
- The addon's custom blue/red OGG firing sounds remain the only firing sounds supplied by this addon.
- Removed the previous global `createbigcannons:sounds.json` override so stock CBC firing sounds are not globally replaced.
- Added the actual Ritchie's Projectile Library 2.1.2 jar to `libs/` and Gradle compile/runtime inputs.
- No Sodium/Iris/Veil mixins are added by this mod. The tracer uses vanilla Minecraft/CBC rendering primitives, which avoids direct Sodium renderer hooks.

## Important verification note
The source tree and archive contents were checked locally. A full Gradle build could not be executed in this isolated environment because the Gradle 9.2.1 wrapper distribution is not cached and external network access is unavailable. Run `gradlew.bat clean build` in IntelliJ/PowerShell on a machine with Gradle distribution access.
