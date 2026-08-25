# Laser Autocannons — Create Big Cannons Addon

NeoForge 21.1.233 · Minecraft 1.21.1 · hard-depends on Create Big Cannons 5.11.7 (and Create)

Built by decompiling your uploaded `createbigcannons-5_11_7_mc_1_21_1.jar` with
CFR and reading the real source for the Autocannon breech, the Fixed Cannon
Mount, the ammo/round system, and the tracer renderer — everything below
extends CBC's actual classes, not guesses.

## How it actually works in-game

1. Build a normal cast-iron Autocannon (barrel + recoil spring, same as
   stock CBC) but use the new **Laser Autocannon Breech** in place of the
   stock breech.
2. Mount it on the new **FE Fixed Cannon Mount** instead of the stock Fixed
   Cannon Mount.
3. Pipe FE into the mount from any cable/generator (standard NeoForge
   energy capability — 5,000,000 FE cap by default, configurable).
4. Use the mount's new center **Color Mode** slider (sits between Pitch and
   Yaw, built the same way CBC builds those two) to pick Blue or Red.
5. Fire it (redstone signal, same as stock) — no ammo, no magazine, no
   Mechanical Arm needed. Every shot checks and debits FE from the mount
   instead.

There's one physical breech item ("Laser Autocannon Breech" — renamed so
it's unmistakable next to CBC's own AP/machine-gun/flak breeches in your
inventory); which color it fires is decided live by the mount's slider,
per the original spec ("slider... determines tracer color, sound event,
autocannon variant behavior").

## The mechanism, precisely

CBC's own firing sequence (`MountedAutocannonContraption.fireShot`, read
from the decompiled source) is:
```
if (breech.canFire()) {
    ItemStack round = breech.extractNextInput();
    if (round.getItem() instanceof AutocannonAmmoItem ammo) {
        ... spawn ammo.getAutocannonProjectile(...) ...
        breech.handleFiring();
    }
}
```
`LaserAutocannonBreechBlockEntity` (extends CBC's real `AutocannonBreechBlockEntity`)
overrides exactly these three hooks:

- **`canFire()`** — stock cooldown/fire-rate check, AND the connected FE
  mount must have ≥ `fePerShot` stored.
- **`extractNextInput()`** — instead of reading a real ItemStack out of a
  magazine or input buffer (this breech never looks at either), it debits
  FE from the mount and hands back a freshly manufactured
  `LaserRoundItem` stack — `AutocannonAmmoItem` is implemented directly on
  that item (same pattern as CBC's own `MachineGunRoundItem`), so from
  `fireShot`'s point of view a real round was always there.
- **`handleFiring()`** — plays your `AT-AT_Fire_Single.ogg` at the barrel
  position.

The FE mount reference is captured every tick via `tickFromContraption`
(called once/tick per autocannon-family block entity in an assembled
contraption) by checking `poce.getController() instanceof FeFixedCannonMountBlockEntity`.

## The tracer

Both colors reuse CBC's own `APAutocannonProjectile` class unchanged —
its damage/ballistics are already fully JSON-driven via
`CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE`, keyed by
EntityType. So no custom projectile Java class was needed — just two
EntityType registrations ("blue_tracer"/"red_tracer") each with their own
properties JSON (`data/colorcannons/munition_properties/projectiles/`,
currently: no gravity/drag, 8 damage, no squib chance — laser-like) and
their own renderer instance.

CBC's own tracer rendering (`AutocannonProjectileRenderer`) already draws
a glowing elongated box-beam when `isTracer()` is true — but the RGB is
hardcoded yellow/orange inside private static methods we can't call or
override. `ColorTracerRenderer` re-implements that same box-beam technique
(same vertex layout, same two-pass "core + halo" approach) parameterized
on `ModColorModes`' RGB and a size/length multiplier read live from
config, instead of copying CBC's asset.

## The sound

Two things happen when the breech fires:

1. **Your sound plays.** `handleFiring()` plays
   `colorcannons:blue_autocannon_fire` / `red_autocannon_fire`, both
   currently pointing at your `AT-AT_Fire_Single.ogg`.
2. **CBC's own mechanical "clank" is silenced.** `fireShot()` hardcodes a
   single shared sound event (`createbigcannons:fire_autocannon`) for
   every non-machine-gun autocannon round in the game — that selection is
   buried in CBC's private contraption internals and can't be swapped
   per-breech from an addon. Since you asked to remove/mute it, this addon
   ships `assets/createbigcannons/sounds.json` with `"replace": true`,
   pointing that shared event at a near-silent generated file.
   **This is global** — it silences that base clank for every autocannon
   in the world, including stock CBC ones, since it's one shared event.
   There's no way to scope the mute to just this addon's breech without
   duplicating CBC's private `fireShot` method wholesale (fragile, and
   would need re-syncing on every CBC update — not done here).

Net result: firing a Laser Autocannon Breech plays only your AT-AT sound.

## What's genuinely unverified

I don't have Create's own jar (only CBC's, which compiles against Create
but doesn't bundle it), so one file's exact API shape is inferred rather
than confirmed:

- **`mount/ColorModeScrollValueBehaviour.java`** — the Color Mode slider.
  Built by directly mirroring CBC's own real (decompiled)
  `FixedCannonMountBlockEntity.FixedCannonMountScrollValueBehaviour` —
  same base class, same method set, range/formatter swapped for a 2-state
  Blue/Red toggle instead of ±45°. The one piece I couldn't confirm is
  `ValueSettingsBoard`'s exact `(max, granularity, rows)` constructor
  semantics, since that class lives in Create, not CBC. If the popup grid
  looks off compared to the Pitch/Yaw ones in-game, that constructor call
  (marked `ADAPT` in the file) is where to check against Create's real
  source.
- **Ritchie's Projectile Library** — CBC hard-requires it, but as far as I
  can tell nothing in this addon's own code (only `canFire`/`extractNextInput`/
  `handleFiring`/`tickFromContraption` overrides) references its types
  directly, so it's left out of `build.gradle`'s dependencies. If a build
  ever reports a missing `rbasamoyai.ritchiesprojectilelib` class, the
  real Maven coordinates are commented in `build.gradle` ready to
  uncomment (just needs the current build number filled in from
  `https://maven.realrobotix.me/master/com/rbasamoyai/ritchiesprojectilelib/`).

Everything else — the breech, the mount, the FE capability, the ammo
item, the tracer entity/renderer, the block properties/models/blockstates,
the loot tables, the munition-properties JSON, the sound override — is
built directly against your real CBC 5.11.7 jar and should compile as-is
once Create's jar is available (see `build.gradle`).

## To build

1. Get a Create 1.21.1 dev jar for NeoForge and either drop it in `libs/`
   (referenced the same way as the CBC jar) or fill in a real CurseForge
   file ID in `build.gradle`.
2. `./gradlew build`.

The CBC jar you provided is already vendored at
`libs/createbigcannons-5_11_7_mc_1_21_1.jar` and wired into `build.gradle`.

### About the Gradle wrapper (fixes the `Problems.forNamespace` error)

The first version of this project shipped without a `gradlew`/Gradle
wrapper. Without one, Gradle falls back to whatever Gradle version is
already installed on your machine — and if that's newer than what
NeoGradle's `userdev` plugin was built against, you get exactly the error
you hit: `Problems.forNamespace(String)` is a Gradle internal API that
changed shape between Gradle versions, so an old NeoGradle talking to a
too-new Gradle blows up on it immediately.

Fixed now by vendoring the real wrapper from Mojang/NeoForged's own
official `MDK-1.21.1-NeoGradle` template (`gradlew`, `gradlew.bat`,
`gradle/wrapper/gradle-wrapper.{properties,jar}`), pinned to Gradle
9.2.1, and bumping `net.neoforged.gradle.userdev` from `7.0.165` to
`7.1.38` to match — the exact pairing that template currently uses for
1.21.1. **Always run the build via `./gradlew ...` (or `gradlew.bat ...`
on Windows) from now on, never a bare `gradle ...`** — that's what makes
the pinned version actually get used instead of whatever's on your PATH.

### About the `Could not find method neoForge()` error

Bumping to NeoGradle 7.1.38 (above) surfaced a second issue: `build.gradle`
still used 7.0.x's DSL, where NeoForge config lived inside a
`neoForge { version = ...; parchment {...}; runs {...}; mods {...} }`
block. That whole extension was removed in 7.1.x — `runs {}` and
`dependencies {}` are top-level now, and Parchment mapping config moved to
plain `gradle.properties` entries
(`neogradle.subsystems.parchment.minecraftVersion` /
`neogradle.subsystems.parchment.mappingsVersion`). `build.gradle` and
`gradle.properties` are now rewritten to the flat 7.1.x DSL, cross-checked
against both the current official NeoForge 1.21.1 MDK and a real,
currently-building Create addon for the same MC/NeoForge/NeoGradle
combination.

That same reference addon is also where the Create dependency stopped
being a guess: it showed the real, working way to get Create's actual
compile-time classes — `com.simibubi.create:create-1.21.1:<version>:slim`
off Create's own Maven (`https://maven.createmod.net`), `compileOnly` +
non-transitive since Create is supplied at runtime by the Create mod jar
itself, not bundled into this addon. `create_version` in
`gradle.properties` is set to `6.0.10-223`, a real published build that
satisfies CBC 5.11.7's own requirement (`[6.0.7,6.1.0)`, read from CBC's
own `neoforge.mods.toml`). The runtime dependency check in this addon's
own `neoforge.mods.toml` uses that same `[6.0.7,6.1.0)` range directly
(not the Maven artifact string, which includes a build-number suffix
that isn't a real mod version).

## Build 9 assembly and color control fix

Build 9 fixes the two gameplay issues found after Build 8:

- The FE Fixed Cannon Mount now overrides CBC's fixed-mount assembly path. CBC 5.11.7's inherited assembly method checks the exact CBC Fixed Cannon Mount block entry and returns immediately for addon subclasses, so the custom mount could never assemble. Build 9 keeps the CBC assembly flow but removes only that identity check and uses the inherited `attach()` state handling.
- The FE mount's center Color Mode value box now mirrors CBC's own fixed-mount value behaviour contract, with a two-state Blue/Red ValueSettingsBoard and a centered front-face transform.
- The laser breech continues to synthesize a laser round only at firing time; no physical ammo is required. FE is consumed from the mount and the generated round is passed through CBC's normal autocannon firing pipeline.
