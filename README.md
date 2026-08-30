# Veterinarium — Chronicles of the Wounded Beasts

> You no longer tame creatures, you **heal** them.
> Tu ne domptes plus les créatures, tu les **soignes**.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green)
![Forge](https://img.shields.io/badge/Forge-52.1.14-orange)
![Java](https://img.shields.io/badge/Java-21-blue)
![Version](https://img.shields.io/badge/Version-1.12.0-success)
![Tests](https://img.shields.io/badge/Tests-11/11-passing-brightgreen)
![Lang](https://img.shields.io/badge/Lang-EN/FR-blueviolet)
[![CurseForge](https://img.shields.io/badge/CurseForge-Download-orange)](https://www.curseforge.com/minecraft/mc-mods/veterinarium)

*Ice & Fire meets House M.D. with wounded mobs, surgery and Palworld spheres.*

---

# English

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

## Pathologies (6)

| Type | Chance | Requirements | Risk without treatment |
|------|--------|-------------|----------------------|
| **Contusion** | 25% | Scalpel → Suture | — |
| **Hemorrhage** | 20% | Bandage | 50% relapse |
| **Fracture** | 17% | Anesthetic | 50% pain + scream |
| **Infection** | 13% | Anesthetic + Bandage | Poison |
| **Burn** | 12% | Anesthetic + Bandage | Continuous fire + Wither |
| **Bleeding** | 13% | Bandage | 1 HP/5s bleed |

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

## Blocks (6)

| Block | Usage | Details |
|-------|-------|---------|
| **Operating Table** | Surgery table | 3 slots, auto-supplied at 5 blocks, general anesthesia, **GUI** |
| **Analysis Table** | Bestiary diagnostic | 10 pages, shows pathology + HP |
| **Infirmary** | Passive heal | 0.5 HP/2s, 8 block radius |
| **Hospital Hut** Lv1-5 | Heal + contracts | 1.5→3.5 HP/2s, daily contracts, ambulance |
| **Stretcher** | Portable | 0.5 HP/2s, portable, blocks contagion |
| **Contaminator** | Accelerates infection | 6 block radius, 12%/second |

## Advanced Mechanics

### General Anesthesia
1. Right-click creature with `Syringe` → tag `veterinarium_anesthetizing`
2. Creature navigates to nearest Operating Table
3. Arrival at <1.5 blocks → Pose.SWIMMING + Resistance/Regeneration/Slowness/Nausea 10s
4. Automatic wake-up after 10s (Pose.STANDING)

### Emergencies & Epidemics
- **Radio call** every 6-11min: urgent wounded 80-150 blocks, 5-8min timer
- **Contagion** 4%/2s at 4 blocks, blocked by Hut Lv3+ or Stretcher
- **Contaminator**: accelerates infection, converts wounded → infected

### Achievements (13)
Welcome, First Blood, Successful Operation, Cicatrization, Healer, Hospital, Captured!, Emergency, Mutation, Dragon Saved, Second Chance, Unlock Recipes

### Config (`config/veterinarium-common.toml`)
```toml
[spawn] woundedSpawnChance=0.08  drakeWeight=4
[urgency] urgencyCooldownMin=8000  urgencyCooldownMax=14000
[epidemic] infectionSpreadChance=0.04  infectionSpreadRange=4.0
[sphere] sphereRequiresHealed=true
```

### Compatibility
- **MineColonies**: Hut heals citizens 0.5x
- **Ars Nouveau**: `source_gem` → heal +2 HP + absorption
- **Ice & Fire**: 12% wounded dragons → scales on healing

### Installation
**Download on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/veterinarium)**

1. Minecraft Java 1.21.1 + Forge 52.1.14
2. Copy `build/libs/veterinarium-1.12.0.jar` into `mods/`
3. Launch → Creative → `Veterinarium` tab

### Quick Test
```
/give @p veterinarium:scalpel
/give @p veterinarium:suture_kit
/give @p veterinarium:syringe
/give @p veterinarium:operating_table
/give @p veterinarium:vet_sphere
/give @p veterinarium:antidote
/summon veterinarium:wounded_wolf ~ ~ ~
```

---

# Français

## Concept

`Ice & Fire + MineColonies + House M.D. + Ark + Palworld` en un seul mod.

1. **Trouve** une créature blessée (< 100% HP, pathologie aléatoire)
2. **Diagnostique** avec la `Seringue` (HP + status + stocke pour Table d'Analyse)
3. **Opère** avec le `Scalpel` (+1-4❤ selon tier, anesthésiant requis pour Fracture/Infection/Brûlure)
4. **Suture** avec le `Kit de Suture` (+6❤ + Régénération, bandage requis pour Hémorragie/Saignement/Infection/Brûlure)
5. **Anesthésie Générale** : la créature marche vers le Bloc Opératoire → Pose.SWIMMING → réveil 10s
6. **Capture** avec la `Sphère Vétérinaire` — uniquement si soignée
7. **Assigne** à ton `Hut Hôpital` (heal de zone) ou `Brancard` (portable)
8. **Cure** les Zombie Villagers avec l'`Antidote`

## Pathologies (6)

| Type | Prob. | Requis pour soigner | Risque sans soin |
|------|-------|---------------------|------------------|
| **Contusion** | 25% | Scalpel → Suture | — |
| **Hémorragie** | 20% | Bandage | Rechute 50% |
| **Fracture** | 17% | Anesthésiant | Douleur 50% + cri |
| **Infection** | 13% | Anesthésiant + Bandage | Poison |
| **Brûlure** | 12% | Anesthésiant + Bandage | Feu continu + Wither |
| **Saignement** | 13% | Bandage | Saignement 1❤/5s |

## Créatures (10)

### Blessées (spawn naturel 8%)
| Créature | HP | Comportement spécial |
|----------|-----|---------------------|
| **Loup Blessé** | 12 | Apprivoisable après soin (os) |
| **Chat Blessé** | 10 | Apprivoisable après soin (poisson) |
| **Cheval Blessé** | 20 | Montable après soin |
| **Renard Blessé** | 10 | Domestiquable après soin |
| **Villageois Blessé** | 20 | Soignable, trade après guérison |
| **Vache Blessée** | 14 | Traitable après soin (seau) |
| **Mouton Blessé** | 10 | Tondable après soin (ciseaux) |
| **Poulet Blessé** | 6 | Nourrissable après soin (graines) |

### Boss / Special
| Créature | HP | Mécanique |
|----------|-----|-----------|
| **Drake Boss** | 60 | Vol, souffle, rare overworld, urgences |
| **Hellfire Ravager** | 40 | 8 dmg feu, **mutations Fire → Acid → Shadow** via kill counter |

### Mutations Hellfire
- **Fire** (défaut) : dégâts de feu au hit, aura particles
- **Acid** (10 kills) : Weakness + Poison au hit
- **Shadow** (25 kills) : Blindness + Wither au hit

## Items (30)

### Outils médicaux
| Item | Durabilité | Usage |
|------|-----------|-------|
| **Scalpel** | 250 | Opère (+2❤) |
| **Scalpel Diamant** | 500 | Opère (+3❤), Rareté Uncommon |
| **Scalpel Netherite** | 1000 | Opère (+4❤), Rareté Rare, ignifugé |
| **Kit de Suture** | 64 | Soigne (+6❤ + Régénération) |
| **Seringue** | 32 | Diagnostic + Anesthésie Générale |
| **Seringue ADN** | 16 | Extrait ADN sur créature blessée |
| **Sérum Hellfire** | 16 | Mute soigné → Hellfire Ravager |
| **Dossier Médical** | 1 | Bestiaire 10 pages EN/FR |
| **Sphère Vétérinaire** | 16 | Capture (uniquement si soignée) |

### Consommables
| Item | Usage |
|------|-------|
| **Bandage** | Auto-fourni par table pour Hémorragie/Saignement/Infection/Brûlure |
| **Anesthésiant** | Auto-fourni par table pour Fracture/Infection/Brûlure |
| **Bandage de Compression** | Soin rapide si HP < 50% |
| **Antidote** | Guérit Zombie Villager (Golden Apple + Sérum Hellfire + Nether Wart) |
| **Antibiotique** | Guérit Poison, soigne 3❤, Régénération II |
| **Anti-inflammatoire** | Supprime Lenteur/Nausée, soigne 2❤, Résistance |
| **Adrénaline** | Urgence : soigne 4❤, Vitesse II + Force I |
| **Transfusion Sanguine** | Soigne 6❤, Absorption II, supprime Wither |

## Blocs (6)

| Bloc | Usage | Détails |
|------|-------|---------|
| **Bloc Opératoire** | Table d'opération | 3 slots, auto-fourni à 5 blocs, anesthésie générale, **GUI** |
| **Table d'Analyse** | Diagnostic Bestiaire | 10 pages, affiche pathologie + HP |
| **Infirmerie** | Heal passif | 0.5❤/2s, rayon 8 blocs |
| **Hut Hôpital** Lv1-5 | Heal + contrats | 1.5→3.5❤/2s, contrats journaliers, ambulancier |
| **Brancard** | Portable | 0.5❤/2s, portable, bloque contagion |
| **Contaminateur** | Accélère infection | 6 blocs rayon, 12%/seconde |

## Mécaniques avancées

### Anesthésie Générale
1. Clic droit sur créature avec `Seringue` → tag `veterinarium_anesthetizing`
2. La créature navigue vers le Bloc Opératoire le plus proche
3. Arrivée à <1.5 blocs → Pose.SWIMMING + Resistance/Régénération/Lenteur/Nausée 10s
4. Réveil automatique après 10s (Pose.STANDING)

### Urgences & Épidémies
- **Appel radio** toutes 6-11min : blessé urgent à 80-150 blocs, timer 5-8min
- **Contagion** 4%/2s à 4 blocs, bloquée Hut Lv3+ ou Brancard
- **Contaminateur** : accélère l'infection, convertit blessés → infectés

### Succès (13)
Bienvenue, Premier Sang, Opération Réussie, Cicatrisation, Soigneur, Hôpital, Captured!, Urgence, Mutation, Dragon Sauvé, Second Chance, Unlock Recipes

### Config (`config/veterinarium-common.toml`)
```toml
[spawn] woundedSpawnChance=0.08  drakeWeight=4
[urgency] urgencyCooldownMin=8000  urgencyCooldownMax=14000
[epidemic] infectionSpreadChance=0.04  infectionSpreadRange=4.0
[sphere] sphereRequiresHealed=true
```

### Compatibilité
- **MineColonies** : Hut heal citoyens 0.5x
- **Ars Nouveau** : `source_gem` → soin +2❤ + absorption
- **Ice & Fire** : 12% dragons blessés → écailles à la guérison

### Installation
**Télécharger sur [CurseForge](https://www.curseforge.com/minecraft/mc-mods/veterinarium)**

1. Minecraft Java 1.21.1 + Forge 52.1.14
2. Copie `build/libs/veterinarium-1.12.0.jar` dans `mods/`
3. Lance → Créatif → onglet `Veterinarium`

### Test Rapide
```
/give @p veterinarium:scalpel
/give @p veterinarium:suture_kit
/give @p veterinarium:syringe
/give @p veterinarium:operating_table
/give @p veterinarium:vet_sphere
/give @p veterinarium:antidote
/summon veterinarium:wounded_wolf ~ ~ ~
```

---

## Changelog

See [CHANGELOG.md](CHANGELOG.md) / Voir [CHANGELOG.md](CHANGELOG.md)

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
- [x] v1.12 — Scalpel Tiers, Cure Zombie, Cow/Sheep/Chicken, Operating GUI, 4 Medications

## Licence

MIT — Made with ❤️ / Fait avec ❤️
