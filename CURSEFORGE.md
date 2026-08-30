# Veterinarium — Chronicles of the Wounded Beasts
**Tu ne domptes plus les créatures, tu les soignes.**
*Le mod Minecraft inspiré de Ice & Fire + MineColonies + House M.D. + Ark + Palworld*

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green) ![Forge](https://img.shields.io/badge/Forge-52.1.14-orange) ![Java](https://img.shields.io/badge/Java-21-blue) ![Version](https://img.shields.io/badge/Version-1.11.0-success) ![Tests](https://img.shields.io/badge/Tests-11/11-brightgreen) ![Lang](https://img.shields.io/badge/EN/FR-330%20clés-blueviolet)

---

## 🎮 Concept (parfait pour série Hardcore)

1. **Trouve** une créature blessée (<100% HP, tag `☠ Blessé` + pathologie)
2. **Diagnostique** avec la `Seringue Diagnostique` (HP% + type + stocke pour Table d'Analyse)
3. **Opère** avec le `Scalpel` (+1❤, tag `operated`, anesthésiant requis pour Fracture/Infection/Brûlure)
4. **Suture** avec le `Kit de Suture` (+6❤ + Régénération, bandage requis pour Hémorragie/Saignement/Infection/Brûlure)
5. **Capture** avec la `Sphère Vétérinaire` (Palworld) — **uniquement si soignée**
6. **Anesthésie Générale** : créature marche vers Bloc Opératoire → Pose.SWIMMING → réveil 10s

> Succès = Loup apprivoisé **après** soins, pas avec un os !

---

## 📦 Contenu 1.11.0

### 🩺 Pathologies (6)
| Type | Requis | Risque sans | Poids |
|------|--------|-------------|-------|
| **Contusion** | Scalpel→Suture | — | 25% |
| **Hémorragie** | Bandage | Rechute 50% | 20% |
| **Fracture** | Anesthésiant | Douleur 50% | 17% |
| **Infection** | Anesth.+Bandage | Poison | 13% |
| **Brûlure** | Anesth.+Bandage | Feu continu + WITHER | 12% |
| **Saignement** | Bandage | Saignement 1❤/5s | 13% |

### 🐾 Créatures (7 + variantes)
- Loup, Chat, Cheval, Renard, Villageois (8% spawn naturel `WoundedSpawnHandler`)
- **Drake Boss** (60HP, vol, souffle, rare overworld + urgences)
- **Hellfire Ravager** (40HP 8dmg feu, mutations **Fire→Acid→Shadow** via kill counter)

### 🧰 Items (23)
| Item | Usage |
|------|-------|
| **Scalpel** 250 dur | Opère (+1❤) |
| **Kit de Suture** 64 dur | Soigne (+6❤, Régénération) |
| **Seringue** 32 dur | Diagnostic + Anesthésie Générale |
| **Seringue ADN** 16 | Extrait ADN sur blessé |
| **Sérum Hellfire** 16 | Mute soigné → Hellfire Ravager |
| **Sphère Vétérinaire** 16 | Capture seulement soigné |
| **Dossier Médical** 1 | Bestiaire 10 pages EN/FR |
| **Bandage / Anesthésiant** | Consommables auto-fournis par table |
| **Bandage de Compression** 16 | Soin rapide si HP<50% |

### 🏥 Blocs (6)
| Bloc | Usage |
|------|-------|
| **Bloc Opératoire** | 3 slots (bandage/anesthésiant/compression), auto-fourni à 5 blocs, **anesthésie générale** |
| **Table d'Analyse** | Diagnostic Bestiaire (10 pages) |
| **Infirmerie** | Heal 0.5❤/2s rayon 8 |
| **Hut Hôpital** Lv1-5 | 1.5→3.5❤/2s, contrats journaliers, ambulancier |
| **Brancard** | Portable 0.5❤/2s |
| **Contaminateur** | Accélère infection à 6 blocs (12%/seconde) |

### 📚 Bestiaire 10 pages `MedicalFileScreen`
Cover + 6 créatures + 6 Pathologies + Protocole + Progression (diag/op/suture/guéris, barre 0-100%)

### 🚨 Urgences & Épidémies
- Appel radio toutes 6-11min : blessé urgent à 80-150 blocs, timer 5-8min
- Contagion 4%/2s à 4 blocs, bloquée Hut Lv3+ ou Brancard
- **Contaminateur** : bloc qui accélère l'infection, convertit blessés → infectés

### 🧬 Mutations & Boss
- ADN + Sérum + Bloc Opératoire → Hellfire Ravager (tame 70%)
- **3 variants** : Fire (défaut) → Acid (10 kills) → Shadow (25 kills)
- Drake Boss 60HP guéri → `dragon_breath` + Héros II

### 🎵 Sons custom (10)
monitor_beep, scalpel_cut, suture, heal_success, mutation, sphere_capture, sphere_release, urgency_bell, epidemic, contaminator_ambient

### 🏗️ Architecture
- **11 GameTests** automatisés (template `veterinarium:hospital_hut`)
- **Bilingue EN/FR** complet (330+ clés `Component.translatable()`)
- **Config** `config/veterinarium-common.toml`
- **CurseForge ready** : logo 400x400 + banner 800x400

---

## 🛠️ Compat
- **MineColonies** : Hut heal citoyens 0.5x, `supplycamp` recette alternative
- **Ars Nouveau** : `source_gem` → soin +2❤ + absorption
- **Ice & Fire** : 12% dragons blessés → écailles à la guérison

---

## 📖 Installation
1. Forge 1.21.1 - 52.1.14
2. Placer `veterinarium-1.11.0.jar` dans `mods/`
3. Lancer, onglet créatif `Veterinarium - Hôpital des Monstres`

---

## ⚙️ Config `config/veterinarium-common.toml`
```toml
[spawn] woundedSpawnChance=0.08  drakeWeight=4
[urgency] urgencyCooldownMin=8000  urgencyCooldownMax=14000  urgencyTimerMin=6000  urgencyTimerMax=10000
[epidemic] infectionSpreadChance=0.04  infectionSpreadRange=4.0  infectionQuarantineHutLevel=3
[sphere] sphereRequiresHealed=true
```

---

## 🧪 Tests
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew runGameTestServer  # 11/11 ✅
```

---

## 📜 Licence MIT
