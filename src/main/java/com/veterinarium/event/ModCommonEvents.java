package com.veterinarium.event;

import com.veterinarium.Veterinarium;
import com.veterinarium.entity.HellfireRavagerEntity;
import com.veterinarium.entity.WoundedCatEntity;
import com.veterinarium.entity.WoundedFoxEntity;
import com.veterinarium.entity.WoundedHorseEntity;
import com.veterinarium.entity.WoundedVillagerEntity;
import com.veterinarium.entity.WoundedWolfEntity;
import com.veterinarium.registry.ModEntities;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Veterinarium.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommonEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.WOUNDED_WOLF.get(), WoundedWolfEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_CAT.get(), WoundedCatEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_HORSE.get(), WoundedHorseEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_FOX.get(), WoundedFoxEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_VILLAGER.get(), WoundedVillagerEntity.createAttributes().build());
        event.put(ModEntities.HELLFIRE_RAVAGER.get(), HellfireRavagerEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.WOUNDED_WOLF.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.WOUNDED_CAT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.WOUNDED_HORSE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.WOUNDED_FOX.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.WOUNDED_VILLAGER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, lvl, st, pos, rand) -> true,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.HELLFIRE_RAVAGER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
