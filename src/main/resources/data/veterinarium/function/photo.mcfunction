time set day
weather clear
fill ~10 ~-1 ~-6 ~22 ~-1 ~6 minecraft:grass_block
fill ~10 ~ ~-6 ~22 ~6 ~6 minecraft:air
setblock ~15 ~ ~ veterinarium:hospital_hut
give @p veterinarium:operating_table
give @p veterinarium:infirmary
give @p veterinarium:scalpel
give @p veterinarium:suture_kit
give @p veterinarium:syringe
give @p veterinarium:medical_file
give @p veterinarium:bandage 16
give @p veterinarium:anesthetic 16
summon veterinarium:wounded_wolf ~16 ~ ~1
summon veterinarium:wounded_cat ~14 ~ ~-1
summon veterinarium:wounded_villager ~15 ~ ~3
tellraw @p {"text":"Hut posé à 15 blocs sur terrain aplani — recule et F1 + F2","color":"gold"}
