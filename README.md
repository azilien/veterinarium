# Veterinarium — Chronicles of the Wounded Beasts

> You no longer tame creatures, you **heal** them.
> Tu ne domptes plus les créatures, tu les **soignes**.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green)
![Forge](https://img.shields.io/badge/Forge-52.1.14-orange)
![Java](https://img.shields.io/badge/Java-21-blue)
![Version](https://img.shields.io/badge/Version-1.12.0-success)
![Tests](https://img.shields.io/badge/Tests-11/11-passing-brightgreen)
![Lang](https://img.shields.io/badge/Lang-EN/FR-blueviolet)

*Ice & Fire meets House M.D. with wounded mobs, surgery and Palworld spheres.*

---

## Concept

`Ice & Fire + MineColonies + House M.D. + Ark + Palworld` in one mod.

1. **Find** a wounded creature (< 100% HP, random pathology)
2. **Diagnose** with the `Syringe` (HP + status + saves for Analysis Table)
3. **Operate** with the `Scalpel` (+1-4 HP depending on tier, anesthetic required for Fracture/Infection/Burn)
4. **Suture** with the `Suture Kit` (+6 HP + Regeneration, bandage required for Hemorrhage/Bleeding/Infection/Burn)
5. **General Anesthesia**: creature walks to Operating Table → Pose.SWIMMING → wakes up after 10s
6. **Capture** with the `Vet Sphere` — only if healed
7. **Assign** to your `Hospital Hut` (zone heal) or `Stretcher` (portable)
8. **Cure** Zombie Villagers with the `Antidote`

---

## Pathologies (6)

| Type | Chance | Requirements | Risk without treatment |
|------|--------|-------------|----------------------|
| **Contusion** | 25% | Scalpel → Suture | — |
| **Hemorrhage** | 20% | Bandage | 50% relapse |
| **Fracture** | 17% | Anesthetic | 50% pain + scream |
| **Infection** | 13% | Anesthetic + Bandage | Poison |
| **Burn** | 12% | Anesthetic + Bandage | Continuous fire + Wither |
| **Bleeding** | 13% | Bandage | 1 HP/5s bleed |

---

## Creatures (10)

### Wounded (8% natural spawn)
| Creature | HP | Special behavior |
|----------|-----|-----------------|
| **Wounded Wolf** | 12 | Tameable after healing (bone) |
| **Wounded Cat** | 10 | Tameable after healing (fish) |
| **Wounded Horse** | 20 | Rideable after healing |
| **Wounded Fox** | 10 | Domesticable after healing |
| **Wounded Villager** | 20 | Healeable, trades after cure |
| **Wounded Cow** | 14 | Milkable after healing (bucket) |
| **Wounded Sheep** | 10 | Shearable after healing (shears) |
| **Wounded Chicken** | 6 | Feedable after healing (seeds) |

### Boss / Special
| Creature | HP | Mechanic |
|----------|-----|----------|
| **Drake Boss** | 60 | Flying, breath attack, rare overworld, emergencies |
| **Hellfire Ravager** | 40 | 8 fire damage, **mutations Fire → Acid → Shadow** via kill counter |

### Hellfire Mutations
- **Fire** (default): fire damage on hit, aura particles
- **Acid** (10 kills): Weakness + Poison on hit
- **Shadow** (25 kills): Blindness + Wither on hit

Each mutation has its own texture.

---

## Items (30)

### Medical Tools
| Item | Durability | Usage |
|------|-----------|-------|
| **Scalpel** | 250 | Operates (+2 HP) |
| **Diamond Scalpel** | 500 | Operates (+3 HP), Uncommon rarity |
| **Netherite Scalpel** | 1000 | Operates (+4 HP), Rare rarity, fireproof |
| **Suture Kit** | 64 | Heals (+6 HP + Regeneration) |
| **Syringe** | 32 | Diagnosis + General Anesthesia |
| **DNA Syringe** | 16 | Extracts DNA from wounded creature |
| **Hellfire Serum** | 16 | Mutates healed → Hellfire Ravager |
| **Medical File** | 1 | Bestiary 10 pages EN/FR |
| **Vet Sphere** | 16 | Capture (only if healed) |

### Consumables
| Item | Usage |
|------|-------|
| **Bandage** | Auto-supplied by table for Hemorrhage/Bleeding/Infection/Burn |
| **Anesthetic** | Auto-supplied by table for Fracture/Infection/Burn |
| **Compression Bandage** | Quick heal if HP < 50% |
| **Antidote** | Cures Zombie Villager (Golden Apple + Hellfire Serum + Nether Wart) |
| **Antibiotic** | Cures Poison, heals 3 HP, Regeneration II |
| **Anti-inflammatory** | Removes Slowness/Nausea, heals 2 HP, Resistance |
| **Adrenaline** | Emergency heal 4 HP, Speed II + Strength I |
| **Blood Transfusion** | Heals 6 HP, Absorption II, removes Wither |

### Spawn Eggs
Wolf, Cat, Horse, Fox, Villager, Drake, Hellfire Ravager, Cow, Sheep, Chicken

---

## Blocks (6)

| Block | Usage | Details |
|-------|-------|---------|
| **Operating Table** | Surgery table | 3 slots (bandage/anesthetic/compression), auto-supplied at 5 blocks, general anesthesia, **GUI** |
| **Analysis Table** | Bestiary diagnostic | 10 pages, shows pathology + HP |
| **Infirmary** | Passive heal | 0.5 HP/2s, 8 block radius |
| **Hospital Hut** Lv1-5 | Heal + contracts | 1.5→3.5 HP/2s, daily contracts, ambulance |
| **Stretcher** | Portable | 0.5 HP/2s, portable, blocks contagion |
| **Contaminator** | Accelerates infection | 6 block radius, 12%/second, smoke+mycelium particles |

---

## Advanced Mechanics

### General Anesthesia
1. Right-click creature with `Syringe` → tag `veterinarium_anesthetizing`
2. Creature navigates to nearest Operating Table
3. Arrival at <1.5 blocks → Pose.SWIMMING + Resistance/Regeneration/Slowness/Nausea 10s
4. Automatic wake-up after 10s (Pose.STANDING)

### Emergencies & Epidemics
- **Radio call** every 6-11min: urgent wounded 80-150 blocks, 5-8min timer
- **Contagion** 4%/2s at 4 blocks, blocked by Hut Lv3+ or Stretcher
- **Contaminator**: block that accelerates infection, converts wounded → infected

### Vet Spheres (Palworld-like)
- Right-click healed creature → capture
- Right-click with filled → release creature
- Works with all wounded creatures

### Organ Transplant
- DNA Syringe on wounded → DNA extracted
- DNA + Hellfire Serum + Operating Table → Hellfire Ravager

---

## Achievements (13)

| Achievement | Condition |
|-------------|-----------|
| **Welcome** | First diagnosis |
| **First Blood** | First hemorrhage healed |
| **Successful Operation** | First scalpel used |
| **Cicatrization** | First suture |
| **Healer** | 10 creatures healed |
| **Hospital** | Place an Operating Table |
| **Captured!** | First capture with sphere |
| **Emergency** | First emergency completed |
| **Mutation** | First Hellfire Ravager created |
| **Dragon Saved** | First Drake healed |
| **Second Chance** | Cure a Zombie Villager with Antidote |
| **Unlock Recipes** | Craft a Scalpel |

---

## Bestiary (Medical File)

10 interactive pages (Cover + 6 creatures + Pathologies + Protocol + Recipes + Progression).

Progression tracker: diagnosis/operations/sutures/heals, 0-100% bar.

---

## Technical Architecture

### Bilingual EN/FR
- 340+ `Component.translatable()` keys
- Complete `en_us.json` + `fr_fr.json`
- All in-game messages translated

### Automated GameTests (11/11)
Template `veterinarium:hospital_hut`, tests: entity spawn, diagnostics, operations, sutures, spheres, emergencies, hospital hut.

### Config (`config/veterinarium-common.toml`)
```toml
[spawn] woundedSpawnChance=0.08  drakeWeight=4
[urgency] urgencyCooldownMin=8000  urgencyCooldownMax=14000
[epidemic] infectionSpreadChance=0.04  infectionSpreadRange=4.0
[sphere] sphereRequiresHealed=true
```

### Custom Sounds (10)
monitor_beep, scalpel_cut, suture, heal_success, mutation, sphere_capture, sphere_release, urgency_bell, epidemic, contaminator_ambient

---

## Compatibility

- **MineColonies**: Hut heals citizens 0.5x, `supplycamp` alternative recipe
- **Ars Nouveau**: `source_gem` → heal +2 HP + absorption
- **Ice & Fire**: 12% wounded dragons → scales on healing

---

## Installation

### Play
1. Minecraft Java 1.21.1 + Forge 52.1.14
2. Copy `build/libs/veterinarium-1.12.0.jar` into `mods/`
3. Launch → Creative → `Veterinarium` tab

```bash
cp build/libs/veterinarium-1.12.0.jar ~/.minecraft/mods/
```

### Develop
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew build
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew runClient
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew runGameTestServer  # 11/11 tests
```

### Quick Test (commands)
```
/give @p veterinarium:scalpel
/give @p veterinarium:suture_kit
/give @p veterinarium:syringe
/give @p veterinarium:operating_table
/give @p veterinarium:vet_sphere
/give @p veterinarium:antidote
/give @p veterinarium:antibiotic
/summon veterinarium:wounded_wolf ~ ~ ~
/summon veterinarium:wounded_cow ~ ~ ~
/summon veterinarium:wounded_sheep ~ ~ ~
```

---

## Roadmap

- [x] v1.0 — MVP Blocks/Items/Zone heal
- [x] v1.1 — Wounded Entities (wolf, cat, horse, fox, villager)
- [x] v1.2 — Bestiary 10 pages, pathologies
- [x] v1.3 — MineColonies compat, Hut Lv1-5
- [x] v1.4 — Ars Nouveau compat
- [x] v1.5 — Hellfire Mutations (Fire→Acid→Shadow)
- [x] v1.6 — Vet Spheres (Palworld-like)
- [x] v1.7 — Emergencies & Epidemics
- [x] v1.11 — Contaminator, General Anesthesia, Compression Bandage, Bilingual EN/FR
- [x] v1.12 — Scalpel Tiers, Cure Zombie, Cow/Sheep/Chicken, Operating GUI, Hellfire Renderer, 4 Medications
- [ ] CurseForge upload

---

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for full version history.

---

## Licence

MIT — Made with ❤️
