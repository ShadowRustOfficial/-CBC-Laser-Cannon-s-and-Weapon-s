# Color Autocannons

NeoForge 1.21.1 addon for Create Big Cannons adding colour-coded laser/autocannon content and an FE-powered fixed cannon mount.

## Compatibility

- Minecraft 1.21.1
- NeoForge 21.1.x
- Create 6.0.10
- Create Big Cannons 5.11.7
- Ritchie's Projectile Library 2.1.2+
- Sodium: no direct dependency, mixin, or API usage

The addon deliberately uses Minecraft/NeoForge/Create rendering APIs and does not couple to Sodium internals.

## Build

Use Java 21 and run `gradlew.bat build` on Windows or `./gradlew build` on Linux/macOS.

## Original compile failures addressed

The dependency setup explicitly supplies Ponder, Registrate, Create/Catnip and Ritchie's Projectile Library. The mount behaviour uses CBC's actual Create package for `ValveHandleBlockEntity` and inherits the required `blockEntity`, `label`, and `value` behaviour from CBC's implementation.
