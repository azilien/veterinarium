package com.veterinarium.event;

import com.veterinarium.Veterinarium;
import com.veterinarium.entity.HellfireRavagerEntity;
import com.veterinarium.entity.WoundedCatEntity;
import com.veterinarium.entity.WoundedChickenEntity;
import com.veterinarium.entity.WoundedCowEntity;
import com.veterinarium.entity.WoundedDrakeEntity;
import com.veterinarium.entity.WoundedFoxEntity;
import com.veterinarium.entity.WoundedHorseEntity;
import com.veterinarium.entity.WoundedSheepEntity;
import com.veterinarium.entity.WoundedVillagerEntity;
import com.veterinarium.entity.WoundedWolfEntity;
import com.veterinarium.registry.ModEntities;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = Veterinarium.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModCommonEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.WOUNDED_WOLF.get(), WoundedWolfEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_CAT.get(), WoundedCatEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_HORSE.get(), WoundedHorseEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_FOX.get(), WoundedFoxEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_VILLAGER.get(), WoundedVillagerEntity.createAttributes().build());
        event.put(ModEntities.HELLFIRE_RAVAGER.get(), HellfireRavagerEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_DRAKE.get(), WoundedDrakeEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_COW.get(), WoundedCowEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_SHEEP.get(), WoundedSheepEntity.createAttributes().build());
        event.put(ModEntities.WOUNDED_CHICKEN.get(), WoundedChickenEntity.createAttributes().build());
    }

    // Spawn placements disabled for NeoForge port — TODO: use RegisterSpawnPlacementsEvent correctly
    // @SubscribeEvent
    // public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) { ... }
}
