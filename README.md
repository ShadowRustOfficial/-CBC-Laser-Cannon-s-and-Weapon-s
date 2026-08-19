# Color Autocannons

A NeoForge 1.21.1 addon for Create Big Cannons that adds FE-powered red/blue laser-style autocannon variants and an FE Fixed Cannon Mount with a color selector.

## Compatibility

- Minecraft: **1.21.1**
- NeoForge: **21.1.x** (development build: 21.1.233)
- Create: **6.0.10**
- Create Big Cannons: **5.11.7**
- Ritchie's Projectile Library: **2.1.2+**
- Sodium: **no direct dependency, mixin, or renderer integration**

The addon uses normal NeoForge/Minecraft rendering APIs and does not inject into Sodium. This keeps the mod independent of Sodium's renderer internals. Sodium is a client-side optimization mod; the addon itself remains valid without Sodium as well.

## What it adds

- Laser Autocannon Breech
- FE Fixed Cannon Mount
- Blue and Red laser/tracer projectiles
- Blue and Red firing sounds
- Configurable FE capacity, transfer rate, and FE-per-shot cost
- Blue/Red Color Mode control on the mount
- CBC-compatible JSON projectile properties

## Dependency model

Create, Create Big Cannons, and Ritchie's Projectile Library are external runtime mods. They are **not bundled into the finished Color Autocannons jar**.

CBC 5.11.7 is resolved from CurseMaven for development and is never bundled into the finished addon jar. Create, Ponder, Registrate, and RPL are likewise external dependencies.

## Build

Use the included Gradle wrapper:

```text
./gradlew build
```

On Windows:

```text
gradlew.bat build
```

The wrapper is pinned to the project's Gradle version so the build does not depend on whichever Gradle version happens to be installed globally.

## Sodium safety

There are no Sodium imports, Sodium mixins, Sodium access wideners, or Sodium-specific rendering hooks in this project. `ColorTracerRenderer` uses Minecraft's `RenderType`, `VertexConsumer`, and `PoseStack` APIs and deliberately avoids touching Sodium internals.

The client renderer also returns a real texture location instead of `null`, while its custom `render()` path does not delegate back into CBC's renderer. This avoids a null-texture edge case if the renderer is queried by another rendering path.

## Notes on the original compile failure

The main failure was not a single broken class. The project was missing compile-time APIs that CBC/Create reference:

- `VirtualBlockEntity` → Ponder API
- `Lang` → Create/Catnip API
- `BlockEntry` → Registrate API
- `VecHelper` → Catnip API
- `ValveHandleBlockEntity` → the package is `com.simibubi.create.content.kinetics.crank`, not `foundation.kinetics.crank`

The Color Mode behaviour now mirrors CBC's real `FixedCannonMountScrollValueBehaviour` API shape, including its inherited `blockEntity`, `label`, and `value` fields.

## License

MIT
