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

## 🎮 Concept

1. **Trouve** une créature blessée (< 100% HP)
2. **Diagnostique** avec la `Seringue` (HP + status + stocke pour Table d'Analyse)
3. **Opère** avec le `Scalpel` (+1❤, tag `operated`, anesthésiant requis pour certaines blessures)
4. **Suture** avec le `Kit de Suture` (+6❤ + Régénération, bandage requis pour hémorragie/saignement/infection/brûlure)
5. **Capture** avec la `Sphère Vétérinaire` (Palworld) — **uniquement si soignée**
6. **Assigne** à ton `Hut Hôpital` (heal de zone) ou `Brancard` (portable)

C'est `Ice & Fire + MineColonies + House M.D. + Ark + Palworld` en un seul mod.

---

## 📦 Contenu v1.11.0

### 🩺 Pathologies (6)
| Type | Requis | Risque sans |
|------|--------|-------------|
| **Contusion** | Scalpel→Suture | — |
| **Hémorragie** | Bandage | Rechute 50% |
| **Fracture** | Anesthésiant | Douleur 50% + cri |
| **Infection** | Anesth.+Bandage | Poison |
| **Brûlure** | Anesth.+Bandage | Feu continu + WITHER |
| **Saignement** | Bandage | Saignement 1❤/5s |

### 🐾 Créatures (7)
Loup, Chat, Cheval, Renard, Villageois (spawn naturel 8%) + **Drake Boss** (60HP, vol, rare) + **Hellfire Ravager** (40HP, mutations Fire→Acid→Shadow)

### 🧰 Items (23)
Scalpel, Kit de Suture, Seringue, Seringue ADN, Sérum Hellfire, Sphère Vétérinaire, Dossier Médical, Bandage, Anesthésiant, Bandage de Compression, + spawn eggs

### 🏥 Blocs (6)
| Bloc | Usage |
|------|-------|
| **Bloc Opératoire** | 3 slots (bandage/anesthésiant/compression), auto-fourni à 5 blocs, anesthésie générale |
| **Table d'Analyse** | Diagnostic Bestiaire, 10 pages |
| **Infirmerie** | Heal 0.5❤/2s rayon 8 |
| **Hut Hôpital** Lv1-5 | 1.5→3.5❤/2s, contrats journaliers, ambulancier |
| **Brancard** | Portable, heal zone |
| **Contaminateur** | Accélère l'infection à 6 blocs |

### 🎵 Sons custom (10)
monitor_beep, scalpel_cut, suture, heal_success, mutation, sphere_capture, sphere_release, urgency_bell, epidemic, contaminator_ambient

---

## 🛠️ Installation

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

---

## 🧪 Test Rapide

```
/give @p veterinarium:scalpel
/give @p veterinarium:suture_kit
/give @p veterinarium:syringe
/give @p veterinarium:operating_table
/give @p veterinarium:vet_sphere
/summon veterinarium:wounded_wolf ~ ~ ~
```

---

## 🏗️ Roadmap

- [x] v1.0 — MVP Blocs/Items/Heal zone
- [x] v1.1 — Wounded Entities (loup, chat, cheval, renard, villageois)
- [x] v1.2 — Bestiaire 10 pages, pathologies
- [x] v1.3 — MineColonies compat, Hut Lv1-5
- [x] v1.4 — Ars Nouveau compat
- [x] v1.5 — Mutations Hellfire (Fire→Acid→Shadow)
- [x] v1.6 — Sphères Vétérinaires (Palworld-like)
- [x] v1.7 — Urgences & Épidémies
- [x] v1.11 — Contaminateur, Anesthésie Générale, Bandage Compression, Bilingue EN/FR
- [ ] CurseForge upload
- [ ] Vidéo Asfax

---

## 📜 Licence
MIT — Fait avec ❤️ pour la commu Asfax
