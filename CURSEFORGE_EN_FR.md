# Veterinarium — Chronicles of the Wounded Beasts

**You no longer tame creatures, you heal them.**
*Ice & Fire meets House M.D. with wounded mobs, surgery and Palworld spheres.*

![Version](https://img.shields.io/badge/Version-1.11.0-success) ![Tests](https://img.shields.io/badge/Tests-11/11-brightgreen) ![Lang](https://img.shields.io/badge/EN/FR-330%20keys-blueviolet)

---

## 🇬🇧 English

### Concept — Perfect for Hardcore Series
1. **Find** a wounded creature (<100% HP, `☠ Wounded` tag + pathology)
2. **Diagnose** with the `Diagnostic Syringe` (HP% + wound type + saves for Analysis Table)
3. **Operate** with the `Scalpel` (+1 heart, `operated` tag, requires Anesthetic for Fracture/Infection/Burn)
4. **Suture** with the `Suture Kit` (+6 hearts + Regeneration, requires Bandage for Hemorrhage/Saignement/Infection/Burn)
5. **Capture** with the `Vet Sphere` (Palworld-like) — **only if healed**
6. **General Anesthesia** : creature walks to Operating Table → Pose.SWIMMING → wakes up after 10s

> Success = Wolf tamed **after** care, not with a bone!

### Content 1.11.0

**Pathologies (6)** — 25% Contusion / 20% Hemorrhage / 17% Fracture / 13% Infection / 12% Burn / 13% Bleeding
- Contusion : Scalpel→Suture
- Hemorrhage : Bandage or 50% relapse
- Fracture : Anesthetic or 50% pain
- Infection : Anesthetic + Bandage or Poison
- Burn : Anesthetic + Bandage or continuous fire + WITHER
- **Bleeding** : Bandage or 1❤/5s bleed

**Wounded Creatures (7 + variants)**
Wolf, Cat, Horse, Fox, Villager (8% natural spawn) + **Wounded Drake Boss** (60HP, flying) + **Hellfire Ravager** (40HP, mutations Fire→Acid→Shadow via kill counter)

**Items (23)**
- `Scalpel` 250 dur, `Suture Kit` 64, `Syringe` 32, `Medical File` (10-page Bestiary EN/FR)
- `DNA Syringe` → `Filled DNA Syringe` → `Hellfire Serum` → Hellfire Ravager
- `Vet Sphere` captures **only healed** creatures
- `Bandage` / `Anesthetic` / `Compression Bandage` (fast heal HP<50%)

**Blocks (6)**
- **Operating Table** : 3 slots (bandage/anesthetic/compression), auto-provided within 5 blocks, **general anesthesia** (walk-to-table + Pose.SWIMMING)
- **Analysis Table** : Bestiary diagnostic (10 pages)
- **Infirmary** (0.5❤/2s radius 8), **Hospital Hut** Lv1-5 (1.5→3.5❤/2s, daily contracts)
- **Stretcher** (portable), **Contaminator** (accelerates infection at 6 blocks, 12%/second)

**Bestiary 10 pages** (`Medical File` EN/FR): Cover + 6 creatures + 6 Pathologies + Protocol + Progression

**Emergencies & Epidemics**
- Radio call every 6-11min: urgent wounded 80-150 blocks, 5-8min timer
- Infection spreads 4%/2s at 4 blocks, blocked by Hut Lv3+ or Stretcher
- **Contaminator block** accelerates infection spread

**Mutations & Boss**
- DNA + Serum + Operating Table → Hellfire Ravager (70% tame)
- **3 variants**: Fire (default) → Acid (10 kills) → Shadow (25 kills)
- Drake Boss 60HP healed → `dragon_breath` + Hero II

**Sounds (10)** : monitor_beep, scalpel_cut, suture, heal_success, mutation, sphere_capture, sphere_release, urgency_bell, epidemic, contaminator_ambient

**Architecture**
- **11 GameTests** automated (`veterinarium:hospital_hut` template)
- **Bilingual EN/FR** complete (330+ `Component.translatable()` keys)
- **CurseForge ready** : logo 400x400 + banner 800x400

**Compat**
- MineColonies : Hut heals citizens 0.5x
- Ars Nouveau : `source_gem` → heal +2 + Absorption
- Ice and Fire : 12% dragons wounded → scale on heal

**Installation**
1. Forge 1.21.1 - 52.1.14
2. Drop `veterinarium-1.11.0.jar` into `mods/`
3. Launch, Creative tab `Veterinarium - Monster Hospital`

**Config** `config/veterinarium-common.toml`
```toml
[spawn] woundedSpawnChance=0.08  drakeWeight=4
[urgency] urgencyCooldownMin=8000  urgencyCooldownMax=14000
[epidemic] infectionSpreadChance=0.04  infectionSpreadRange=4.0
[sphere] sphereRequiresHealed=true
```

**Tests**
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew runGameTestServer  # 11/11 ✅
```

---

## 🇫🇷 Français

### Concept — Idéal pour série Hardcore
1. **Trouve** une créature blessée (<100% HP, tag `☠ Blessé` + pathologie)
2. **Diagnostique** avec la `Seringue Diagnostique` (HP% + type + sauvegarde pour Table d'Analyse)
3. **Opère** avec le `Scalpel` (+1 cœur, tag `operated`, anesthésiant requis pour Fracture/Infection/Brûlure)
4. **Suture** avec le `Kit de Suture` (+6❤ + Régénération, bandage requis pour Hémorragie/Saignement/Infection/Brûlure)
5. **Capture** avec la `Sphère Vétérinaire` — **uniquement si soignée**
6. **Anesthésie Générale** : créature marche vers Bloc Opératoire → Pose.SWIMMING → réveil 10s

> Succès = Loup apprivoisé **après** soins, pas avec un os !

### Contenu 1.11.0

**Pathologies (6)** — 25% Contusion / 20% Hémorragie / 17% Fracture / 13% Infection / 12% Brûlure / 13% Saignement
- Contusion : Scalpel→Suture
- Hémorragie : Bandage ou rechute 50%
- Fracture : Anesthésiant ou douleur 50%
- Infection : Anesth.+Bandage ou Poison
- Brûlure : Anesth.+Bandage ou feu continu + WITHER
- **Saignement** : Bandage ou saignement 1❤/5s

**Créatures blessées (7)** — Loup/Chat/Cheval/Renard/Villageois (8% naturel) + Drake Boss 60HP volant + Hellfire Ravager 40HP mutations Fire→Acid→Shadow

**Items (23)** — Scalpel, Kit de Suture, Seringue, Seringue ADN, Sérum Hellfire, Sphère Vétérinaire, Dossier Médical, Bandage, Anesthésiant, Bandage de Compression

**BlocOpératoire** : 3 slots, auto-fourni à 5 blocs, **anesthésie générale**. Contaminateur : accélère infection à 6 blocs. Hut Lv1-5, Infirmerie, Brancard, Table d'Analyse.

**Bestiaire 10 pages EN/FR** — Cover + 6 créatures + 6 Pathologies + Protocole + Progression

**Urgences & Épidémies** — appel radio 6-11min, contagion 4%/2s, Contaminateur accélère

**Mutations** — Fire→Acid→Shadow via kill counter, Drake Boss, Sphère Vétérinaire

**Sons (10)** —monitor_beep, scalpel_cut, suture, heal_success, mutation, sphere_capture, sphere_release, urgency_bell, epidemic, contaminator_ambient

**Architecture** — 11 GameTests, bilingue EN/FR 330+ clés, CurseForge ready

**Compat** MineColonies / Ars Nouveau / Ice & Fire

**Installation** identique, onglet créatif `Veterinarium - Hôpital des Monstres`

**Tests** : `./gradlew runGameTestServer` → 11/11 ✅

---

## 📜 Licence MIT
