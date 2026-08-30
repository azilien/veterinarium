time set day
weather clear
fill ~10 ~-1 ~-6 ~22 ~-1 ~6 minecraft:grass_block
fill ~10 ~ ~-6 ~22 ~6 ~6 minecraft:air
fill ~12 ~-1 ~-3 ~18 ~-1 ~3 minecraft:oak_planks
fill ~12 ~ ~-3 ~12 ~3 ~3 minecraft:bricks
fill ~18 ~ ~-3 ~18 ~3 ~3 minecraft:bricks
fill ~12 ~ ~-3 ~18 ~3 ~-3 minecraft:bricks
fill ~12 ~ ~3 ~18 ~3 ~3 minecraft:bricks
fill ~12 ~4 ~-3 ~18 ~4 ~3 minecraft:oak_slab
setblock ~12 ~ ~0 minecraft:air
setblock ~12 ~1 ~0 minecraft:air
setblock ~15 ~ ~0 veterinarium:operating_table
setblock ~16 ~ ~0 veterinarium:infirmary
give @p veterinarium:scalpel
give @p veterinarium:suture_kit
give @p veterinarium:syringe
give @p veterinarium:medical_file
summon veterinarium:wounded_wolf ~16 ~ ~1
summon veterinarium:wounded_cat ~14 ~ ~-1
say Maison posee a 15 blocs avec table - F1 + F2
