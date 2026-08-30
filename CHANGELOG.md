# Changelog — Veterinarium

All notable changes to this project will be documented in this file.

---

## [1.12.0] — 2026-08-30

### Added
- **WoundedCreatureHelper**: shared utility for all Wounded* entities (~80 lines removed)
- **Scalpel Tiers**: Diamond (+3 HP, 500 dur), Netherite (+4 HP, 1000 dur, fireproof)
- **Antidote Item**: cures Zombie Villagers (Golden Apple + Hellfire Serum + Nether Wart)
- **Wounded Cow**: milkable after healing (bucket)
- **Wounded Sheep**: shearable after healing (shears)
- **Wounded Chicken**: feedable after healing (seeds)
- **Operating Table GUI**: container menu + screen, opens on right-click
- **Hellfire Renderer**: per-mutation textures (Fire/Acid/Shadow)
- **4 Medications**: Antibiotic (cures Poison, +3 HP), Anti-inflammatory (removes Slowness/Nausea, +2 HP), Adrenaline (+4 HP, Speed II + Strength I), Blood Transfusion (+6 HP, Absorption II)
- **Advancement**: "Second Chance" (cure zombie villager)
- **Recipes**: Scalpel Diamond (shapeless), Scalpel Netherite (smithing), Antidote, Antibiotic, Anti-inflammatory, Adrenaline, Blood Transfusion

### Fixed
- Evolution skip: `==` → `>=` for Hellfire mutation thresholds
- Debug log removed from `Veterinarium.java`
- GUI overlap: pagination dots + buttons no longer overlap
- Page order: Recipes now appear first in Bestiary

### Removed
- All Asfax references from code, docs, and lang files

---

## [1.11.0] — 2026-08-28

### Added
- **WoundType SAIGNEMENT** (Bleeding): 13% chance, bandage required, 1 HP/5s bleed risk
- **Compression Bandage**: quick heal if HP < 50% (+4 HP, Regeneration)
- **Contaminator Block**: infection spread 6 blocks, 12%/second, smoke+mycelium particles
- **General Anesthesia**: Syringe → Operating Table → creature walks → Pose.SWIMMING → 10s effects
- **Hellfire Ravager**: 3 mutation variants (Fire/Acid/Shadow) with kill counter evolution
- **Custom Sounds**: EPIDEMIC, CONTAMINATOR_AMBIENT, URGENCY_BELL, MUTATION
- **Advancement**: "Premier Sang" (first bleed healed)
- **Bilingual EN/FR**: 340+ translation keys, complete en_us.json + fr_fr.json

### Changed
- Operating Table: 3 slots (bandage/anesthetic/compression), stock display on sneak-click
- Bestiary risk.bleed now translatable (was hardcoded)

### Fixed
- Hellfire evolution threshold bug (was `==`, now `>=`)

---

## [1.7.0] — 2026-08-25

### Added
- **Emergencies & Epidemics system**: radio calls every 6-11min, urgent wounded at 80-150 blocks
- **Contagion mechanic**: 4%/2s spread at 4 blocks, blocked by Hut Lv3+ or Stretcher
- **Urgency sounds**: URGENCY_BELL, EPIDEMIC

---

## [1.6.0] — 2026-08-23

### Added
- **Vet Sphere** (Palworld-like): capture healed creatures, release on right-click
- **Vet Sphere Filled**: stores entity data, restores health on release

---

## [1.5.0] — 2026-08-21

### Added
- **Hellfire Mutations**: Fire → Acid (10 kills) → Shadow (25 kills)
- **Hellfire Serum**: mutates healed creature into Hellfire Ravager
- **DNA Syringe**: extracts DNA from wounded creatures
- **Kill counter**: persistent NBT evolution system

---

## [1.4.0] — 2026-08-19

### Added
- **Ars Nouveau integration**: source_gem → heal +2 HP + absorption

---

## [1.3.0] — 2026-08-17

### Added
- **Hospital Hut Lv1-5**: zone heal 1.5→3.5 HP/2s, daily contracts, ambulance role
- **MineColonies integration**: Hut heals citizens 0.5x

---

## [1.2.0] — 2026-08-15

### Added
- **Bestiary (Medical File)**: 10 interactive pages, progression tracker
- **6 Pathologies**: Contusion, Hemorrhage, Fracture, Infection, Burn, Bleeding
- **Pathology system**: weighted random, specific requirements per type

---

## [1.1.0] — 2026-08-13

### Added
- **Wounded Entities**: Wolf, Cat, Horse, Fox, Villager
- **8% natural spawn** via WoundedSpawnHandler
- **Tameable after healing**: wolf (bone), cat (fish)
- **Wounded textures**: 4 variants per entity (wounded/healed × normal/tame)

---

## [1.0.0] — 2026-08-11

### Added
- **MVP**: Operating Table, Analysis Table, Infirmary
- **Scalpel**: +1 HP, operates wounded entities
- **Suture Kit**: +6 HP, Regeneration, 33% tame chance
- **Syringe**: diagnostic tool
- **Medical File**: basic help screen
- **Bandage / Anesthetic**: crafting ingredients
- **Creative Tab**: Veterinarium

---

## Legend

- **Added**: new features
- **Changed**: modifications to existing functionality
- **Fixed**: bug fixes
- **Removed**: removed features
