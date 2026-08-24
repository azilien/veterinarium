# Veterinarium — Chronicles of the Wounded Beasts
**Le mod Minecraft pensé pour Asfax**

> Tu ne domptes plus les créatures, tu les **soignes**.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green)
![Forge](https://img.shields.io/badge/Forge-52.1.14-orange)
![Java](https://img.shields.io/badge/Java-21-blue)
![Status](https://img.shields.io/badge/Status-MVP%20Fonctionnel-success)

Inspiré par la chaîne **Asfax** (372k abonnés, infirmier au bloc, fan de Ice & Fire / MineColonies / Ars Nouveau / Ark / Palworld).

---

## 🎮 Concept

1.  **Trouve** une créature blessée (< 100% HP)
2.  **Diagnostique** avec la `Seringue` (HP + status)
3.  **Opère** avec le `Scalpel` (+1 coeur, marque "opérée")
4.  **Suture** avec le `Kit de Suture` (+3 coeurs + Régénération + 33% chance de tame)
5.  **Assigne**-la à ton enclos près de l'`Infirmerie` (heal de zone passif)

C'est `Ice & Fire + MineColonies + House M.D.` en un seul mod.

---

## 📦 Contenu MVP (v1.0.0)

### Blocs
| Bloc | Craft | Usage |
|------|-------|-------|
| **Bloc Opératoire** | `III / W W / I I` (I=Iron Block, W=Wool) | Clic droit = info chirurgie |
| **Table d'Analyse** | `PGP / WWW / PPP` (P=Planks, G=Glass) | Diagnostic Bestiaire (à venir) |
| **Infirmerie** | `WWW / R R / WWW` | Heal 0.5 coeur / 2s dans 8 blocs |

### Items
| Item | Durabilité | Effet |
|------|------------|-------|
| **Scalpel** | 250 | Clic sur entité blessée → +1 coeur, tag `operated` |
| **Kit de Suture** | 64 | Sur opérée/blessée → +3 coeurs, Régénération II, 33% tame si Tamable |
| **Seringue** | 32 | Diagnostic HP + Anesthésie (Lenteur 5s) |
| **Dossier Médical** | 1 | Clic droit → affiche tuto Bestiaire |
| **Bandage** | - | Craft uniquement (futur: craft intermédiaire) |
| **Anesthésiant** | - | Placeholder |

### Onglet Créatif
`Veterinarium - Hôpital des Monstres` (icône Scalpel)

---

## 🛠️ Installation

### Pour tester tout de suite (comme Asfax)
1. Minecraft Java 1.21.1 + Forge 52.1.14 installé (déjà présent sur ton launcher `forge`)
2. Copie `build/libs/veterinarium-1.0.0.jar` dans `~/.minecraft/mods/`
3. Lance le profil `forge` → Nouveau monde en Créatif
4. Tape `@veterinarium` dans l'inventaire ou cherche l'onglet

```bash
cp build/libs/veterinarium-1.0.0.jar ~/.minecraft/mods/
```

### Pour développer
```bash
cd ~/Documents/Projets/veterinarium
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew build
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew runClient # lance le jeu dev
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew runServer # lance un serveur test
```

---

## 🧪 Test Rapide (Hardcore Asfax Style)

1.  `/give @p veterinarium:scalpel`
2.  `/give @p veterinarium:suture_kit`
3.  `/give @p veterinarium:syringe`
4.  `/give @p veterinarium:operating_table`
5.  `/summon wolf ~ ~ ~ {Health:5.0f}` → loup blessé
6.  Clic droit Seringue → diagnostic
7.  Clic droit Scalpel → opère
8.  Clic droit Suture Kit → soigne + tame aléatoire
9.  Pose Infirmerie à côté → regen de zone

**Succès = Loup apprivoisé après soins, pas après os !**

---

## 🚀 Roadmap (pour séduire Asfax en série)

- [ ] **v1.1 - Wounded Entities**: loup blessé qui spawn naturellement, dragon blessé rare (Ice & Fire compat)
- [ ] **v1.2 - Bestiaire Médical GUI**: livre avec pages par créature, pathologies (infection, fracture, brûlure)
- [ ] **v1.3 - MineColonies Integration**: le Builder peut crafter les items, l'Infirmerie devient un Hut officiel, les créatures soignées deviennent `Guard`/`Miner`
- [ ] **v1.4 - Ars Nouveau**: sort `Heal Wound`, glyphe `Suture`, familier soigneur
- [ ] **v1.5 - Mutations DarkGod**: Seringue d'ADN + Bloc Opératoire → faire évoluer un raptor en Hellfire Ravager (tier Ark)
- [ ] **v1.6 - Palworld-like Spheres**: Sphère vétérinaire pour capturer seulement après soin

---

## 📁 Structure
```
src/main/java/com/veterinarium/
  Veterinarium.java (mod main)
  block/OperatingTableBlock.java, AnalysisTableBlock.java, InfirmaryBlock.java
  item/ScalpelItem.java, SutureKitItem.java, SyringeItem.java, MedicalFileItem.java
  registry/ModBlocks.java, ModItems.java, ModCreativeTabs.java
  event/InfirmaryHealEvent.java (heal de zone)
src/main/resources/
  assets/veterinarium/{blockstates,models,lang}
  data/veterinarium/{recipes,loot_tables}
```

---

## 🤝 Pour contacter Asfax
- Chaîne: https://www.youtube.com/@Asfax
- Pitch DM: "Je suis infirmier au bloc IRL comme toi, j'ai fait un mod où on soigne les dragons au bloc opératoire avant de les tame. Ça combine Ice & Fire + MineColonies. Tu veux tester la bêta en Hardcore ?"

---

## 📜 Licence
MIT - Fait avec ❤️ pour la commu Asfax
