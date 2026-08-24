package com.veterinarium.registry;

import com.veterinarium.Veterinarium;
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

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
