# Changelog — Veterinarium

All notable changes to this project will be documented in this file.
Les modifications notables de ce projet seront documentées dans ce fichier.

---

## [1.12.0] — 2026-08-30

### Added / Ajouté
- **WoundedCreatureHelper**: shared utility for all Wounded* entities (~80 lines removed) / Utilitaire partagé pour toutes les entités Wounded* (~80 lignes supprimées)
- **Scalpel Tiers**: Diamond (+3 HP, 500 dur), Netherite (+4 HP, 1000 dur, fireproof) / Tiers Scalpel : Diamant (+3❤, 500 dur), Netherite (+4❤, 1000 dur, ignifugé)
- **Antidote Item**: cures Zombie Villagers (Golden Apple + Hellfire Serum + Nether Wart) / Item Antidote : guérit les Zombie Villagers
- **Wounded Cow**: milkable after healing (bucket) / Vache Blessée : traitable après soin (seau)
- **Wounded Sheep**: shearable after healing (shears) / Mouton Blessé : tondable après soin (ciseaux)
- **Wounded Chicken**: feedable after healing (seeds) / Poulet Blessé : nourrissable après soin (graines)
- **Operating Table GUI**: container menu + screen, opens on right-click / GUI Bloc Opératoire : menu container + écran, clic droit
- **Hellfire Renderer**: per-mutation textures (Fire/Acid/Shadow) / Renderer Hellfire : textures par mutation
- **4 Medications**: Antibiotic (+3 HP, cures Poison), Anti-inflammatory (+2 HP, removes Slowness/Nausea), Adrenaline (+4 HP, Speed II + Strength I), Blood Transfusion (+6 HP, Absorption II) / 4 Médicaments : Antibiotique, Anti-inflammatoire, Adrénaline, Transfusion Sanguine
- **Advancement**: "Second Chance" (cure zombie villager) / Avancement : "Second Chance"
- **Recipes**: Scalpel Diamond, Scalpel Netherite, Antidote, Antibiotic, Anti-inflammatory, Adrenaline, Blood Transfusion / Recettes

### Fixed / Corrigé
- Evolution skip: `==` → `>=` for Hellfire mutation thresholds / Seuil d'évolution Hellfire corrigé
- Debug log removed / Log de debug supprimé
- GUI overlap fixed / Chevauchement GUI corrigé
- Page order: Recipes first in Bestiary / Pages réordonnées : Recettes en 1er

---

## [1.11.0] — 2026-08-28

### Added / Ajouté
- **WoundType SAIGNEMENT** (Bleeding): 13% chance, bandage required / Type de blessure SAIGNEMENT : 13%, bandage requis
- **Compression Bandage**: quick heal if HP < 50% / Bandage de Compression : soin rapide si HP < 50%
- **Contaminator Block**: infection spread 6 blocks, 12%/second / Bloc Contaminateur : infection 6 blocs, 12%/seconde
- **General Anesthesia**: Syringe → Operating Table → creature walks → Pose.SWIMMING → 10s / Anesthésie Générale
- **Hellfire Ravager**: 3 mutation variants (Fire/Acid/Shadow) / Hellfire Ravager : 3 variants de mutation
- **Custom Sounds**: EPIDEMIC, CONTAMINATOR_AMBIENT, URGENCY_BELL, MUTATION / Sons custom
- **Bilingual EN/FR**: 340+ translation keys / Bilingue EN/FR : 340+ clés traduites

### Changed / Modifié
- Operating Table: 3 slots, stock display on sneak-click / Bloc Opératoire : 3 slots, affichage stock

### Fixed / Corrigé
- Hellfire evolution threshold bug / Bug de seuil d'évolution Hellfire

---

## [1.7.0] — 2026-08-25

### Added / Ajouté
- **Emergencies & Epidemics**: radio calls every 6-11min / Urgences & Épidémies : appels radio toutes 6-11min
- **Contagion mechanic**: 4%/2s at 4 blocks / Mécanique de contagion : 4%/2s à 4 blocs

---

## [1.6.0] — 2026-08-23

### Added / Ajouté
- **Vet Sphere** (Palworld-like): capture healed creatures / Sphère Vétérinaire : capture créatures soignées

---

## [1.5.0] — 2026-08-21

### Added / Ajouté
- **Hellfire Mutations**: Fire → Acid (10 kills) → Shadow (25 kills) / Mutations Hellfire
- **Hellfire Serum**: mutates healed creature / Sérum Hellfire : mute créature soignée
- **DNA Syringe**: extracts DNA / Seringue ADN : extrait ADN
- **Kill counter**: persistent NBT evolution / Compteur de kills : évolution NBT persistante

---

## [1.4.0] — 2026-08-19

### Added / Ajouté
- **Ars Nouveau integration**: source_gem → heal +2 HP + absorption / Compatibilité Ars Nouveau

---

## [1.3.0] — 2026-08-17

### Added / Ajouté
- **Hospital Hut Lv1-5**: zone heal 1.5→3.5 HP/2s / Hut Hôpital Lv1-5 : heal de zone
- **MineColonies integration**: Hut heals citizens 0.5x / Compatibilité MineColonies

---

## [1.2.0] — 2026-08-15

### Added / Ajouté
- **Bestiary (Medical File)**: 10 interactive pages / Bestiaire (Dossier Médical) : 10 pages interactives
- **6 Pathologies**: weighted random, specific requirements / 6 Pathologies : pondération, requis spécifiques

---

## [1.1.0] — 2026-08-13

### Added / Ajouté
- **Wounded Entities**: Wolf, Cat, Horse, Fox, Villager / Entités Blessées
- **8% natural spawn** / Spawn naturel 8%
- **Tameable after healing** / Apprivoisable après soin

---

## [1.0.0] — 2026-08-11

### Added / Ajouté
- **MVP**: Operating Table, Analysis Table, Infirmary / MVP : Bloc Opératoire, Table d'Analyse, Infirmerie
- **Scalpel + Suture Kit + Syringe** / Scalpel + Kit de Suture + Seringue
- **Creative Tab**: Veterinarium / Onglet Créatif

---

## Légende / Legend

- **Added / Ajouté**: new features / nouvelles fonctionnalités
- **Changed / Modifié**: modifications / modifications
- **Fixed / Corrigé**: bug fixes / corrections de bugs
- **Removed / Supprimé**: removed features / fonctionnalités supprimées
