# Veterinarium — Chronicles of the Wounded Beasts
**Tu ne domptes plus les créatures, tu les soignes.**
*Le mod Minecraft pensé pour Asfax — Ice & Fire + MineColonies + House M.D. + Ark + Palworld*

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green) ![Forge](https://img.shields.io/badge/Forge-52.1.14-orange) ![Java](https://img.shields.io/badge/Java-21-blue)

---

## 🎮 Concept (parfait pour série Hardcore)

1. **Trouve** une créature blessée (<100% HP, tag `☠ Blessé` + pathologie)
2. **Diagnostique** avec la `Seringue Diagnostique` (HP% + type + stocke pour Table d'Analyse)
3. **Opère** avec le `Scalpel` (+1❤, tag `operated`, besoin `Anesthésiant` pour Fracture/Infection/Brûlure sinon 50% échec)
4. **Suture** avec le `Kit de Suture` (+3❤ + Régénération, besoin `Bandage` pour Hémorragie/Infection/Brûlure sinon rechute, 33% tame)
5. **Capture** avec la `Sphère Vétérinaire` (Palworld) — **uniquement si soignée** — ou assigne à ton `Hut Hôpital` (heal de zone)

> Succès = Loup apprivoisé **après** soins, pas avec un os !

---

## 📦 Contenu 1.6.0

### 🩺 Pathologies (5)
| Type | Requis | Risque sans |
|------|--------|-------------|
| **Contusion** | Scalpel→Suture | — |
| **Hémorragie** | Bandage | Rechute 50% |
| **Fracture** | Anesthésiant | Douleur 50% + cri |
| **Infection** | Anesth.+Bandage | Poison |
| **Brûlure** | Anesth.+Bandage | Feu continu + WITHER |

Poids spawn : 35% / 22% / 18% / 13% / 12%

### 🐾 Créatures blessées (6 + variantes)
- Loup, Chat, Cheval, Renard, Villageois (8% spawn naturel `WoundedSpawnHandler`)
- **Drake Boss** (Phantom 60HP, vol, rare overworld + urgences)
- **Hellfire Ravager** (40HP 8dmg feu, mutation Tier Ark) — évolution via ADN + Sérum

### 🧰 Items
| Item | Recette | Usage |
|------|---------|-------|
| **Scalpel** | `I / I` (iron_ingot) | Opère |
| **Suture Kit** | `S / P / S` | Soigne |
| **Seringue** | ` G / I / I` (glass/nugget) | Diag |
| **Seringue ADN** | ` R / G / I` (redstone/glass/nugget) | Extrait ADN sur blessé → `Seringue ADN Remplie` |
| **Sérum Hellfire** | `ADN remplie + blaze + nether_wart + magma_cream` | Mute soigné → Hellfire Ravager au Bloc Opératoire |
| **Sphère Vétérinaire** | ` I / IGI / I` | Capture seulement soigné, libère au sol |
| **Dossier Médical** | `PP / BB` (paper/book) | Bestiaire 10 pages |
| **Bandage / Anesthésiant** | craft | Consommables bloc |
| **Infirmerie / Hut / Brancard / Table d'Analyse / Bloc Opératoire** | voir JEI | Heal zone |

### 🏥 Blocs
- **Bloc Opératoire** (2 slots bandage/anesthésiant, auto-fourni à 5 blocs)
- **Table d'Analyse** (mémorise dernier diag `VetLastWound`)
- **Infirmerie** (0.5❤/2s rayon 8)
- **Hut Hôpital** Lv1-5 (1.5→3.5❤/2s rayon 16→32, particules, sons monitor, **contrats journaliers**)
- **Brancard** (0.5❤/2s rayon 2.5 portable)
- **Clinique abandonnée** (structure rare plains/forest/taiga, loot médical)

### 📚 Bestiaire 10 pages `MedicalFileScreen`
Cover + 6 créatures + Pathologies + Protocole + Progression (diag/op/suture/guéris, barre 0-100%)

### 🚨 Urgences & Épidémies
- Appel radio toutes 6-11min : blessé urgent à 80-150 blocs, timer 5-8min, 3 émeraudes + Héros si sauvé
- Infection contagion 4%/2s à 4 blocs, bloquée si Hut Lv3+ ou brancard (quarantaine)

### 🧬 Mutations & Boss
- ADN + Sérum + Bloc Opératoire (bandage+anesthésiant) → Hellfire Ravager (tame 70%)
- Drake Boss 60HP guéri → `dragon_breath` + Héros II

---

## 🛠️ Compat
- **MineColonies** : Hut heal citoyens 0.5x, `supplycamp` recette alternative Bloc Opératoire
- **Ars Nouveau** : `source_gem` → soin +2❤ + absorption `ArsNouveauIntegration`
- **Ice & Fire** : 12% dragons blessés (Fire/Ice/Lightning) → écailles à la guérison

---

## 📖 Installation
1. Forge 1.21.1 - 52.1.14
2. Placer `veterinarium-1.6.0.jar` dans `mods/`
3. Lancer, onglet créatif `Veterinarium - Hôpital des Monstres` ou `@veterinarium` en recherche

Prism : le mod se sync auto après chaque `gradlew build` dans `PrismLauncher/instances/Veterinarium/.minecraft/mods`

---

## ⚙️ Config `config/veterinarium-common.toml`
```toml
[spawn] woundedSpawnChance=0.08  drakeWeight=4
[urgency] urgencyCooldownMin=8000  urgencyCooldownMax=14000  urgencyTimerMin=6000  urgencyTimerMax=10000
[epidemic] infectionSpreadChance=0.04  infectionSpreadRange=4.0  infectionQuarantineHutLevel=3
[sphere] sphereRequiresHealed=true
```

---

## 🧪 Test rapide
```
/give @p veterinarium:scalpel
/give @p veterinarium:suture_kit
/give @p veterinarium:syringe
/give @p veterinarium:vet_sphere
/summon veterinarium:wounded_wolf ~ ~ ~
/summon veterinarium:wounded_drake ~ ~ ~
```

---

## 📜 Licence MIT - Pour Asfax & les tamers infirmiers
Chaîne : https://www.youtube.com/@Asfax
