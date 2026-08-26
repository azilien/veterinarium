# Veterinarium — Chronicles of the Wounded Beasts

**You no longer tame creatures, you heal them.**
*Ice & Fire meets House M.D. with wounded mobs, surgery and Palworld spheres — inspired by Asfax.*

---

## 🇬🇧 English

### Concept — Perfect for Hardcore Series
1. **Find** a wounded creature (<100% HP, `☠ Wounded` tag + pathology)
2. **Diagnose** with the `Diagnostic Syringe` (HP% + wound type + saves for Analysis Table, applies Slowness 5s)
3. **Operate** with the `Scalpel` (+1 heart, `operated` tag, requires `Anesthetic` for Fracture/Infection/Burn or 50% failure)
4. **Suture** with the `Suture Kit` (+3 hearts + Regeneration II, requires `Bandage` for Hemorrhage/Infection/Burn or 50% relapse, 33% tame chance)
5. **Capture** with the `Vet Sphere` (Palworld-like) — **only if healed** — or assign to your `Hospital Hut` (area heal)

> Success = Wolf tamed **after** care, not with a bone!

### Content 1.6.0

**Pathologies (5)** — 35% Contusion / 22% Hemorrhage / 18% Fracture / 13% Infection / 12% Burn
- Contusion : Scalpel→Suture
- Hemorrhage : Bandage or 50% relapse
- Fracture : Anesthetic or 50% pain
- Infection : Anesthetic + Bandage or Poison
- Burn : Anesthetic + Bandage or continuous fire + WITHER

**Wounded Creatures (6 + variants)**
Wolf, Cat, Horse, Fox, Villager (8% natural spawn) + **Wounded Drake Boss** (Phantom 60HP, flying, rare overworld + urgencies) + **Hellfire Ravager** (40HP 8 dmg fire, Tier Ark mutation via DNA + Serum)

**Items**
- `Scalpel` 250 dur, `Suture Kit` 64, `Syringe` 32, `Medical File` (10-page Bestiary)
- `DNA Syringe` (empty `redstone+glass+nugget` → extract on wounded → `Filled DNA Syringe`)
- `Hellfire Serum` (`Filled DNA + blaze_powder + nether_wart + magma_cream` → mutate healed creature at Operating Table with 1 bandage + 1 anesthetic → Hellfire Ravager 70% tame)
- `Vet Sphere` (`nugget+glass`) captures **only healed** creatures, releases on ground, gives empty sphere back
- `Bandage` / `Anesthetic`, `Infirmary` / `Hut Lv1-5` / `Stretcher` / `Analysis Table` / `Operating Table` (2 slots bandage/anesthetic, auto-provided within 5 blocks)

**Blocks**
- Operating Table, Analysis Table (remembers last diag), Infirmary (0.5 heart/2s radius 8), Hospital Hut Lv1-5 (1.5→3.5 hearts/2s radius 16→32, particles, monitor beep, **daily contracts**), Stretcher (0.5/2s radius 2.5), Abandoned Clinic (rare structure plains/forest/taiga)

**Bestiary 10 pages** (`Medical File`): Cover + 6 creatures + Pathologies + Protocol + Progression (diag/ops/sutures/healed, 0-100% bar)

**Emergencies & Epidemics**
- Radio call every 6-11min: urgent wounded 80-150 blocks, 5-8min timer, 3 emeralds + Hero of the Village if saved
- Infection spreads 4%/2s at 4 blocks, blocked by Hut Lv3+ or Stretcher (quarantine needed)

**Mutations & Boss**
- DNA + Serum + Operating Table → Hellfire Ravager
- Drake Boss 60HP healed → `dragon_breath` + Hero II

**Compat**
- MineColonies : Hut heals citizens 0.5x, `supplycamp` recipe for Operating Table
- Ars Nouveau : `source_gem` → heal +2 + Absorption
- Ice and Fire : 12% dragons (Fire/Ice/Lightning) wounded → scale on heal

**Installation**
1. Forge 1.21.1 - 52.1.14
2. Drop `veterinarium-1.6.0.jar` into `mods/`
3. Launch, Creative tab `Veterinarium - Monster Hospital` or search `@veterinarium`

**Config** `config/veterinarium-common.toml`
```toml
[spawn] woundedSpawnChance=0.08  drakeWeight=4
[urgency] urgencyCooldownMin=8000  urgencyCooldownMax=14000  urgencyTimerMin=6000  urgencyTimerMax=10000
[epidemic] infectionSpreadChance=0.04  infectionSpreadRange=4.0  infectionQuarantineHutLevel=3
[sphere] sphereRequiresHealed=true
```

---

## 🇫🇷 Français

### Concept — Idéal pour série Hardcore
1. **Trouve** une créature blessée (<100% HP, tag `☠ Blessé` + pathologie)
2. **Diagnostique** avec la `Seringue Diagnostique` (HP% + type + sauvegarde pour Table d'Analyse, Lenteur 5s)
3. **Opère** avec le `Scalpel` (+1 cœur, tag `operated`, besoin `Anesthésiant` pour Fracture/Infection/Brûlure sinon 50% échec)
4. **Suture** avec le `Kit de Suture` (+3 cœurs + Régé II, besoin `Bandage` pour Hémorragie/Infection/Brûlure sinon rechute, 33% tame)
5. **Capture** avec la `Sphère Vétérinaire` — **uniquement si soignée** — ou assigne à ton `Hut Hôpital`

> Succès = Loup apprivoisé **après** soins, pas avec un os !

### Contenu 1.6.0

**Pathologies (5)** — mêmes poids, mêmes requis/risques traduits ci-dessus

**Créatures blessées (6)** — Loup/Chat/Cheval/Renard/Villageois (8% naturel) + Drake Boss volant 60PV + Hellfire Ravager 40PV 8dmg feu (mutation ADN)

**Items/Blocs** — mêmes recettes (voir JEI), Bestiaire 10 pages, Hut Lv1-5, Infirmerie, Brancard, Clinique abandonnée rare

**Urgences & Épidémies** — appel radio 6-11min à 80-150 blocs timer 5-8min, contagion 4%/2s bloquée Hut Lv3

**Mutations** — Seringue ADN → Sérum Hellfire → Hellfire Ravager au Bloc Opératoire

**Compat** MineColonies / Ars Nouveau / Ice & Fire (dragons blessés 12%)

**Installation** identique, onglet créatif `Veterinarium - Hôpital des Monstres`

**Config** idem `veterinarium-common.toml`

---

## 📜 Licence MIT — Pour Asfax & les tamers infirmiers
Chaîne : https://www.youtube.com/@Asfax — Pitch : *"Je suis infirmier au bloc IRL comme toi, j'ai fait un mod où on soigne les dragons au bloc avant de les tame."*
