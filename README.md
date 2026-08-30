# Veterinarium — Chronicles of the Wounded Beasts

> You no longer tame creatures, you **heal** them.
> Tu ne domptes plus les créatures, tu les **soignes**.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green)
![Forge](https://img.shields.io/badge/Forge-52.1.14-orange)
![Java](https://img.shields.io/badge/Java-21-blue)
![Version](https://img.shields.io/badge/Version-2.0-success)
![Tests](https://img.shields.io/badge/Tests-11/11-passing-brightgreen)
![Lang](https://img.shields.io/badge/Lang-EN/FR-blueviolet)
[![CurseForge](https://img.shields.io/badge/CurseForge-Download-orange)](https://www.curseforge.com/minecraft/mc-mods/veterinarium)

*Ice & Fire meets House M.D. with wounded mobs, surgery and Palworld spheres.*

---

# English

## What You Can Do

### Heal wounded creatures and tame them
- Wander the world and find **wounded animals** (wolves, cats, horses, foxes, cows, sheep, chickens) — they spawn naturally with red particles and a "Wounded" name
- Use the **Syringe** on them to diagnose: you'll see their HP, wound type, and what supplies you need
- Perform **surgery**: Scalpel → Suture Kit (in that order!) to heal them
- Once healed, **tame them** with the usual item (bone for wolf, fish for cat) — or they'll follow you willingly
- **Ride** healed horses, **milk** healed cows, **shear** healed sheep, **feed** healed chickens

### Build a veterinary hospital
- Craft an **Operating Table** — place it, right-click to open the GUI, fill it with Bandages and Anesthetics
- Craft an **Infirmary** — passive healing zone (0.5 HP/2s within 8 blocks)
- Build a **Hospital Hut** (Lv1-5) — heals creatures in a large zone, gives you daily contracts and emerald rewards
- Place a **Stretcher** — portable healing, carry wounded creatures to your hospital

### Perform advanced surgeries
- **General Anesthesia**: use the Syringe near an Operating Table → the creature walks to it automatically → falls asleep for 10 seconds
- **Compression Bandage**: instant 4 HP heal if the creature is below 50% HP
- **Medications**: Antibiotic (cures Poison), Anti-inflammatory (removes Slowness), Adrenaline (emergency +4 HP), Blood Transfusion (+6 HP)

### Cure Zombie Villagers
- Craft an **Antidote** (Golden Apple + Hellfire Serum + Nether Wart)
- Right-click a **Zombie Villager** (tagged "Urgent") to cure it → becomes a normal Villager with Regeneration

### Capture and transport creatures
- Craft a **Vet Sphere** — right-click a healed creature to capture it
- Right-click with a filled sphere to release it anywhere
- Build an army of healed creatures and deploy them

### Create Hellfire Ravagers (Boss mutations)
- Extract **DNA** from a wounded creature with the DNA Syringe
- Craft a **Hellfire Serum** (DNA + Blaze Powder + Nether Wart)
- Use the serum on a healed creature near an Operating Table → it transforms into a **Hellfire Ravager** (40 HP, fire damage)
- The Ravager evolves through **3 mutation stages**: Fire → Acid (10 kills) → Shadow (25 kills)

### Fight emergencies and epidemics
- **Radio calls** every 6-11 minutes: an urgent wounded creature appears 80-150 blocks away
- Rush to heal it before the timer runs out for emerald rewards
- Watch out for **contagion** — infected creatures spread infection to nearby animals
- The **Contaminator** block accelerates infection in a 6-block radius

### Cure the plague
- Craft an **Antibiotic** (Spider Eye + Awkward Potion) to cure Poison
- Craft an **Anti-inflammatory** (Glowstone + Thick Potion) to remove Slowness and Nausea
- Craft **Adrenaline** (Blaze Powder + Nether Wart + Awkward Potion) for emergency healing
- Craft a **Blood Transfusion** (Gold Ingot + Redstone + Bandage) to remove Wither and heal 6 HP

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

## Ce que tu peux faire

### Soigner les créatures blessées et les apprivoiser
- Explore le monde et trouve des **animaux blessés** (loups, chats, chevaux, renards, vaches, moutons, poulets) — ils spawnent naturellement avec des particules rouges et le nom "Blessé"
- Utilise la **Seringue** pour diagnostiquer : tu verras les PV, le type de blessure et les consommables nécessaires
- Effectue une **opération** : Scalpel → Kit de Suture (dans cet ordre !) pour les soigner
- Une fois soigné, **apprivoise-les** avec l'habitué (os pour le loup, poisson pour le chat)
- **Monte** les chevaux soignés, ** traites** les vaches soignées, **tonds** les moutons soignés, **nourris** les poulets soignés

### Construire un hôpital vétérinaire
- Craft un **Bloc Opératoire** — place-le, clic droit pour ouvrir le GUI, remplis-le de Bandages et Anesthésiants
- Craft une **Infirmerie** — zone de heal passif (0.5❤/2s dans 8 blocs)
- Construis une **Hut Hôpital** (Lv1-5) — soigne les créatures dans une large zone, contrats journaliers et récompenses en émeraudes
- Place un **Brancard** — portable, pour transporter les blessés vers ton hôpital

### Effectuer des chirurgies avancées
- **Anesthésie Générale** : utilise la Seringue près d'un Bloc Opératoire → la créature y marche automatiquement → s'endort 10 secondes
- **Bandage de Compression** : soin instantané de 4❤ si la créature est sous 50% PV
- **Médicaments** : Antibiotique (guérit Poison), Anti-inflammatoire (supprime Lenteur), Adrénaline (+4❤ urgence), Transfusion Sanguine (+6❤)

### Guérir les Zombie Villagers
- Craft un **Antidote** (Pomme Dorée + Sérum Hellfire + Nether Wart)
- Clic droit sur un **Zombie Villager** (tagué "Urgent") pour le guérir → devient un Villager normal avec Régénération

### Capturer et transporter des créatures
- Craft une **Sphère Vétérinaire** — clic droit sur une créature soignée pour la capturer
- Clic droit avec une sphère remplie pour la libérer n'importe où
- Construis une armée de créatures soignées et déploie-les

### Créer des Hellfire Ravagers (mutations Boss)
- Extrait de l'**ADN** d'une créature blessée avec la Seringue ADN
- Craft un **Sérum Hellfire** (ADN + Poudre de Blaze + Nether Wart)
- Utilise le sérum sur une créature soignée près d'un Bloc Opératoire → se transforme en **Hellfire Ravager** (40❤, dégâts de feu)
- Le Ravager évolue en **3 stades** : Fire → Acid (10 kills) → Shadow (25 kills)

### Combattre urgences et épidémies
- **Appels radio** toutes les 6-11 minutes : une créature urgente apparaît à 80-150 blocs
- Cours la soigner avant l'expiration du timer pour des récompenses en émeraudes
- Attention à la **contagion** — les créatures infectées contaminent les animaux proches
- Le **Contaminateur** accélère l'infection dans un rayon de 6 blocs

### Soigner la peste
- Craft un **Antibiotique** (Œil d'Araignée + Potion Awkward) pour guérir le Poison
- Craft un **Anti-inflammatoire** (Poudre de Luminite + Potion Thick) pour supprimer Lenteur et Nausée
- Craft de l'**Adrénaline** (Poudre de Blaze + Nether Wart + Potion Awkward) pour urgence
- Craft une **Transfusion Sanguine** (Lingot d'Or + Redstone + Bandage) pour supprimer Wither et soigner 6❤

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
- [x] v1.12 — Scalpel Tiers, Cure Zombie, Cow/Sheep/Chicken, Operating GUI
- [x] v2.0 — Medications, Bestiary unique, GUI fixes, security cleanup

## Licence

Creative Commons 4.0 — Made with ❤️ / Fait avec ❤️
