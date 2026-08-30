# Veterinarium — Chronicles of the Wounded Beasts
**Le mod Minecraft pensé pour Asfax**

> Tu ne domptes plus les créatures, tu les **soignes**.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green)
![Forge](https://img.shields.io/badge/Forge-52.1.14-orange)
![Java](https://img.shields.io/badge/Java-21-blue)
![Version](https://img.shields.io/badge/Version-1.11.0-success)
![Tests](https://img.shields.io/badge/Tests-11/11-passing-brightgreen)
![Lang](https://img.shields.io/badge/Lang-EN/FR-blueviolet)

Inspiré par la chaîne **Asfax** (372k abonnés, infirmier au bloc, fan de Ice & Fire / MineColonies / Ars Nouveau / Ark / Palworld).

---

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

---

## Pathologies (6)

| Type | Prob. | Requis pour soigner | Risque sans soin |
|------|-------|---------------------|------------------|
| **Contusion** | 25% | Scalpel → Suture | — |
| **Hémorragie** | 20% | Bandage | Rechute 50% |
| **Fracture** | 17% | Anesthésiant | Douleur 50% + cri |
| **Infection** | 13% | Anesthésiant + Bandage | Poison |
| **Brûlure** | 12% | Anesthésiant + Bandage | Feu continu + Wither |
| **Saignement** | 13% | Bandage | Saignement 1❤/5s |

---

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

Chaque mutation a sa propre texture.

---

## Items (26)

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

### Spawn Eggs
Loup, Chat, Cheval, Renard, Villageois, Drake, Hellfire Ravager

---

## Blocs (6)

| Bloc | Usage | Détails |
|------|-------|---------|
| **Bloc Opératoire** | Table d'opération | 3 slots (bandage/anesthésiant/compression), auto-fourni à 5 blocs, anesthésie générale, **GUI** |
| **Table d'Analyse** | Diagnostic Bestiaire | 10 pages, affiche pathologie + HP |
| **Infirmerie** | Heal passif | 0.5❤/2s, rayon 8 blocs |
| **Hut Hôpital** Lv1-5 | Heal + contrats | 1.5→3.5❤/2s, contrats journaliers, ambulancier |
| **Brancard** | Portable | 0.5❤/2s, portable, bloque contagion |
| **Contaminateur** | Accélère infection | 6 blocs rayon, 12%/seconde, particles smoke+mycelium |

---

## Mécaniques avancées

### Anesthésie Générale
1. Clic droit sur créature avec `Seringue` → tag `veterinarium_anesthetizing`
2. La créature navigue vers le Bloc Opératoire le plus proche
3. Arrivée à <1.5 blocs → Pose.SWIMMING + effets Resistance/Regeneration/Slowness/Nausea 10s
4. Réveil automatique après 10s (Pose.STANDING)

### Urgences & Épidémies
- **Appel radio** toutes 6-11min : blessé urgent à 80-150 blocs, timer 5-8min
- **Contagion** 4%/2s à 4 blocs, bloquée Hut Lv3+ ou Brancard
- **Contaminateur** : bloc qui accélère l'infection, convertit blessés → infectés

### Sphères Vétérinaires (Palworld-like)
- Clic droit sur créature soignée → capture
- Clic droit avec rempli → libère la créature
- Fonctionne avec toutes les créatures blessées

### Transplantation d'Organes
- Seringue ADN sur blessé → ADN extrait
- ADN + Sérum Hellfire + Bloc Opératoire → Hellfire Ravager

---

## Succès (13)

| Succès | Condition |
|--------|-----------|
| **Bienvenue** | Premier diagnostic |
| **Premier Sang** | Première hémorragie soignée |
| **Opération Réussie** | Premier scalpel utilisé |
| **Cicatrisation** | Premier suture |
| **Soigneur** | 10 créatures guéries |
| **Hôpital** | Placer un Bloc Opératoire |
| **Captured!** | Première capture avec sphère |
| **Urgence** | Première urgence complétée |
| **Mutation** | Premier Hellfire Ravager créé |
| **Dragon Sauvé** | Premier Drake guéri |
| **Second Chance** | Guérir un Zombie Villager avec Antidote |
| **Unlock Recipes** | Craft un Scalpel |

---

## Bestiaire (Dossier Médical)

10 pages interactives (Page de Couverture + 6 créatures + Pathologies + Protocole + Progression).

Progression tracker : diagnostic/opérations/sutures/guérisons, barre 0-100%.

---

## Architecture technique

### Bilingue EN/FR
- 330+ clés `Component.translatable()`
- `en_us.json` + `fr_fr.json` complets
- Langues chargées pour tous les messages in-game

### GameTests automatisés (11/11)
Template `veterinarium:hospital_hut`, testent : spawn entities, diagnostics, operations, sutures, spheres, urgences, hospital hut.

### Config (`config/veterinarium-common.toml`)
```toml
[spawn] woundedSpawnChance=0.08  drakeWeight=4
[urgency] urgencyCooldownMin=8000  urgencyCooldownMax=14000
[epidemic] infectionSpreadChance=0.04  infectionSpreadRange=4.0
[sphere] sphereRequiresHealed=true
```

### Sons custom (10)
monitor_beep, scalpel_cut, suture, heal_success, mutation, sphere_capture, sphere_release, urgency_bell, epidemic, contaminator_ambient

---

## Compatibilité

- **MineColonies** : Hut heal citoyens 0.5x, `supplycamp` recette alternative
- **Ars Nouveau** : `source_gem` → soin +2❤ + absorption
- **Ice & Fire** : 12% dragons blessés → écailles à la guérison

---

## Installation

### Jouer
1. Minecraft Java 1.21.1 + Forge 52.1.14
2. Copie `build/libs/veterinarium-1.11.0.jar` dans `mods/`
3. Lance → Créatif → onglet `Veterinarium`

```bash
cp build/libs/veterinarium-1.11.0.jar ~/.minecraft/mods/
```

### Développer
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew build
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew runClient
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew runGameTestServer  # 11/11 tests
```

### Test Rapide (commandes)
```
/give @p veterinarium:scalpel
/give @p veterinarium:suture_kit
/give @p veterinarium:syringe
/give @p veterinarium:operating_table
/give @p veterinarium:vet_sphere
/give @p veterinarium:antidote
/summon veterinarium:wounded_wolf ~ ~ ~
/summon veterinarium:wounded_cow ~ ~ ~
/summon veterinarium:wounded_sheep ~ ~ ~
```

---

## Roadmap

- [x] v1.0 — MVP Blocs/Items/Heal zone
- [x] v1.1 — Wounded Entities (loup, chat, cheval, renard, villageois)
- [x] v1.2 — Bestiaire 10 pages, pathologies
- [x] v1.3 — MineColonies compat, Hut Lv1-5
- [x] v1.4 — Ars Nouveau compat
- [x] v1.5 — Mutations Hellfire (Fire→Acid→Shadow)
- [x] v1.6 — Sphères Vétérinaires (Palworld-like)
- [x] v1.7 — Urgences & Épidémies
- [x] v1.11 — Contaminateur, Anesthésie Générale, Bandage Compression, Bilingue EN/FR
- [x] v1.12 — Scalpel Tiers, Cure Zombie, Cow/Sheep/Chicken, Operating GUI, Hellfire Renderer
- [ ] CurseForge upload
- [ ] Vidéo Asfax

---

## Licence

MIT — Fait avec ❤️ pour la commu Asfax
