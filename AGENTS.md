# AGENTS.md — Veterinarium

> Fichier de référence pour les agents IA et développeurs travaillant sur ce projet.
> Le mod est bilingue EN/FR : toute feature gameplay inclut ses clés `en_us.json` + `fr_fr.json`.

## Projet

- **Veterinarium — Chronicles of the Wounded Beasts** : mod Minecraft qui soigne les créatures blessées au lieu de les dompter (`Ice & Fire + MineColonies + House M.D. + Ark + Palworld`).
- **Minecraft 1.21.1, Java 21**, mappings `official`.
- **Repo GitHub** : `azilien/veterinarium`. **CurseForge** : projet `veterinarium` (upload manuel, voir § Release).
- **Licence : CC-BY-4.0** (README + `mods.toml` / `neoforge.mods.toml`). Ne jamais remettre MIT.
- **Aucune référence Asfax** dans le code, les docs, les lang files et les logos (règle explicite du mainteneur).

## Double loader : Forge + NeoForge (2 branches)

| Branche | Loader | Fichier version | Jar produit |
|---|---|---|---|
| `master` | Forge 52.1.14 (`net.minecraftforge.gradle` 7.x, Gradle 9.3.1) | `version` dans `build.gradle`, `version=` dans `src/main/resources/META-INF/mods.toml` | `build/libs/veterinarium-<ver>-forge.jar` (`archiveClassifier = 'forge'`) |
| `neoforge-1.21.1` | NeoForge 21.1.133 (`net.neoforged.gradle.userdev` 7.1.38, Gradle 8.14) | `mod_version` dans `gradle.properties` (+ template `src/main/templates/META-INF/neoforge.mods.toml`) | `build/libs/veterinarium-<ver>-neoforge.jar` (`archiveClassifier = 'neoforge'`) |

- **Un seul dossier local** : `git checkout master` = code Forge, `git checkout neoforge-1.21.1` = code NeoForge (mêmes chemins `src/main`, contenus différents).
- **Ne jamais merger une branche dans l'autre** (imports incompatibles). Porter les fixes à la main dans chaque branche en adaptant l'API (voir § Différences Forge/NeoForge).
- **Les 2 jars sont trackés sur `master`** (`build/` est gitignoré → `git add -f`) pour l'upload CurseForge. Sur la branche NeoForge, seul le jar `-neoforge` est tracké.
- Sur CurseForge : **un seul projet, 2 fichiers uploadés** pour la même version — `*-forge.jar` coché **Forge**, `*-neoforge.jar` coché **NeoForge**. Ne jamais cocher les 2 loaders sur un seul jar (crash au lancement côté opposé).

## Différences Forge → NeoForge (port v2.2, commit `a76d506`)

- Imports : `net.minecraftforge.*` → `net.neoforged.*` / `net.neoforged.neoforge.*` (`bus.api`, `fml.common.Mod`, `ModList`, `ModConfig`, `api.distmarker`, `neoforge.client.event`, `neoforge.common.NeoForge`, `neoforge.common.ModConfigSpec`).
- Events : `TickEvent.LevelTickEvent` → `LevelTickEvent.Post` (plus de `event.phase`, `event.level` → `event.getLevel()`), `EntityJoinLevelEvent`, `PlayerEvent`, `LivingEvent` sous `neoforge.event.*`.
- `Veterinarium(IEventBus, ModContainer)` — `FMLJavaModLoadingContext` et `ModLoadingContext.get().registerConfig()` supprimés ; `MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`.
- Registres : `DeferredRegister.create(ForgeRegistries.X)` → `DeferredRegister.create(Registries.X)` ; `RegistryObject<T>` → `DeferredHolder<R, T>` (ex: `DeferredHolder<EntityType<?>, EntityType<Wolf>>`) ; `ForgeRegistries.ITEMS.getValue()` → `BuiltInRegistries.ITEM.get()`.
- `ForgeSpawnEggItem` → `DeferredSpawnEggItem` ; `IForgeMenuType` → `IMenuTypeExtension` ; `MenuScreens.register()` (privé) → event `RegisterMenuScreensEvent`.
- **Capabilities supprimées** : `LazyOptional`, `getCapability()`, `invalidateCaps()`, `ForgeCapabilities.ITEM_HANDLER` ont été **retirés** (pas réécrits) dans `OperatingTableBlockEntity` et `HospitalHutBlockEntity`. Le `Menu` accède au handler via `getHandler()` direct. Le code "chest à proximité" du Hut est désactivé (`// Chest handling disabled for NeoForge port`).
- `SpawnPlacementRegisterEvent` et `LivingEvent.LivingTickEvent` désactivés côté NeoForge (TODO en commentaire).
- GameTests : `@GameTestHolder("veterinarium")` sur la classe + `@GameTest(template = ...)` sur les méthodes.
- Resources : `data/veterinarium/forge/biome_modifier/` → `data/veterinarium/neoforge/biome_modifier/` (6 JSON).
- `settings.gradle` NeoForge exige le bloc `pluginManagement { maven neoforged + gradlePluginPortal }`, sinon le plugin `userdev` est introuvable. NeoGradle 7.1.38 exige **Gradle 8.14** (wrapper downgradé sur cette branche uniquement).

## Build, tests, sync local

```bash
# Forge (master) — Gradle 9.3.1
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew build runGameTestServer
# NeoForge (neoforge-1.21.1) — Gradle 8.14
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew build
```

- **GameTests : 11/11 requis** (`All 11 required tests passed`). Template `veterinarium:hospital_hut`.
- `copyToPrism` (via `local-dev.gradle`, **gitignoré, ne jamais committer**) sync le jar vers `~/.local/share/PrismLauncher/instances/Veterinarium/.minecraft/mods/`.
- **Ne jamais pusher du code qui ne compile pas.** Vérifier `BUILD SUCCESSFUL` avant tout commit/push.

## Bug historique : déplacement saccadé frame-par-frame (v2.3)

- **Symptôme** : animaux/villageois qui avancent par saccades.
- **Cause** (`UrgencyAndEpidemicHandler.handleAnesthesiaWalk`) : `moveTo()` relancé **à chaque tick** → le pathfinding restart en boucle ; + scans monde entier (`AABB -30M`) à chaque tick → TPS en chute.
- **Fix (les 2 branches)** : handler appelé toutes les **10 ticks** ; repath seulement si `navigation.isDone()` ou toutes les **40 ticks** (`VetRepath` en persistent data, nettoyé dans `clearAnesthesiaState`) ; early-out `players().isEmpty()` sur anesthésie/infection/particules ; lookup drake via `ModEntities.WOUNDED_DRAKE.get()` direct.
- Si le symptôme revient : chercher les scans `getEntitiesOfClass` à large AABB et les `moveTo()` par tick.

## Architecture code (points d'attention)

- `WoundedCreatureHelper` : logique partagée des entités `Wounded*` (pas de classe abstraite — Java mono-héritage). Chaque entité garde `DATA_HEALED` + `DATA_WOUND_TYPE`.
- Protocole de soin : Seringue (diagnostic + anesthésie générale via tag `veterinarium_anesthetizing` + `Pose.SWIMMING` 10s) → Scalpel (+anesthésiant si requis) → Suture Kit (+bandage si requis, +6❤).
- `WoundType` (6) : Contusion 25% / Hémorragie 20% / Fracture 17% / Infection 13% / Brûlure 12% / Saignement 13%, flags `needsAnesthetic` / `needsBandage`.
- GameTest : `makeMockPlayer(GameType.SURVIVAL)` (pas `FakePlayerFactory`), pas de `@GameTestDontPrefix`, lang files non chargées côté serveur (`translatable()` retourne la clé).
- 1.21.1 quirks : pas de `MobEffects.NAUSEA`, `getEntityData()` (pas `entityData`), `setPersistenceRequired()` sur `Mob`, `ParticleTypes.ITEM` exige un `ItemStack`, pas de `SoundType.NETHER_WART_BLOCK`.

## Release (procédure v2.3, à répéter)

1. `master` : bump `build.gradle` + `mods.toml` + logos (`vX.Y • MC 1.21.1`, PIL) + README (badge, jars, roadmap) + CHANGELOG. Build + 11/11 tests. Commit, tag `vX.Y`, push + tags.
2. `neoforge-1.21.1` : porter les fixes, bump `mod_version` (`gradle.properties`). Build. Commit, push.
3. Récupérer les 2 jars sur `master` (`git checkout neoforge-1.21.1 -- build/libs/...` si besoin), `git add -f`, commit, push.
4. CurseForge : upload `*-forge.jar` (cocher Forge) + `*-neoforge.jar` (cocher NeoForge), coller le changelog EN du CHANGELOG.
5. `screenshot.png` (racine) illustre le README GitHub.

## Fichiers sensibles / gitignore

- `build/`, `.gradle/`, `run/`, `local-dev.gradle`, `gitignore/` (planning privé, ex-`Série 10 épisodes`) : **ne jamais committer**.
- Jars : exception volontaire via `git add -f` (binaire de release pour CurseForge).
- Aucun token/clé/IP/mot de passe dans le repo (audit fait : seuls chemins locaux `build.gradle`/`gradle.properties` ont été extraits vers `local-dev.gradle`).
