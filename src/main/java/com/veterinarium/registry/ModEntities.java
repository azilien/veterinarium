package com.veterinarium.registry;

import com.veterinarium.Veterinarium;
import com.veterinarium.entity.HellfireRavagerEntity;
import com.veterinarium.entity.WoundedCatEntity;
import com.veterinarium.entity.WoundedDrakeEntity;
import com.veterinarium.entity.WoundedFoxEntity;
import com.veterinarium.entity.WoundedHorseEntity;
import com.veterinarium.entity.WoundedVillagerEntity;
import com.veterinarium.entity.WoundedWolfEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Veterinarium.MODID);

    public static final RegistryObject<EntityType<WoundedWolfEntity>> WOUNDED_WOLF =
            ENTITY_TYPES.register("wounded_wolf",
                    () -> EntityType.Builder.of(WoundedWolfEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 0.85F)
                            .eyeHeight(0.68F)
                            .clientTrackingRange(10)
                            .build("wounded_wolf"));

    public static final RegistryObject<EntityType<WoundedCatEntity>> WOUNDED_CAT =
            ENTITY_TYPES.register("wounded_cat",
                    () -> EntityType.Builder.of(WoundedCatEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 0.7F)
                            .eyeHeight(0.5F)
                            .clientTrackingRange(10)
                            .build("wounded_cat"));

    public static final RegistryObject<EntityType<WoundedHorseEntity>> WOUNDED_HORSE =
            ENTITY_TYPES.register("wounded_horse",
                    () -> EntityType.Builder.of(WoundedHorseEntity::new, MobCategory.CREATURE)
                            .sized(1.4F, 1.6F)
                            .eyeHeight(1.5F)
                            .clientTrackingRange(10)
                            .build("wounded_horse"));

    public static final RegistryObject<EntityType<WoundedFoxEntity>> WOUNDED_FOX =
            ENTITY_TYPES.register("wounded_fox",
                    () -> EntityType.Builder.of(WoundedFoxEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 0.7F)
                            .eyeHeight(0.5F)
                            .clientTrackingRange(10)
                            .build("wounded_fox"));

    public static final RegistryObject<EntityType<WoundedVillagerEntity>> WOUNDED_VILLAGER =
            ENTITY_TYPES.register("wounded_villager",
                    () -> EntityType.Builder.of(WoundedVillagerEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F)
                            .eyeHeight(1.62F)
                            .clientTrackingRange(10)
                            .build("wounded_villager"));

    public static final RegistryObject<EntityType<HellfireRavagerEntity>> HELLFIRE_RAVAGER =
            ENTITY_TYPES.register("hellfire_ravager",
                    () -> EntityType.Builder.of(HellfireRavagerEntity::new, MobCategory.CREATURE)
                            .sized(0.9F, 1.1F)
                            .eyeHeight(0.9F)
                            .fireImmune()
                            .clientTrackingRange(10)
                            .build("hellfire_ravager"));

    public static final RegistryObject<EntityType<WoundedDrakeEntity>> WOUNDED_DRAKE =
            ENTITY_TYPES.register("wounded_drake",
                    () -> EntityType.Builder.of(WoundedDrakeEntity::new, MobCategory.CREATURE)
                            .sized(1.8F, 1.2F)
                            .eyeHeight(1.0F)
                            .fireImmune()
                            .clientTrackingRange(10)
                            .build("wounded_drake"));

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
