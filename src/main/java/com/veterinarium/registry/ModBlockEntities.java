package com.veterinarium.registry;

import com.veterinarium.Veterinarium;
import com.veterinarium.block.entity.HospitalHutBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Veterinarium.MODID);

    public static final RegistryObject<BlockEntityType<HospitalHutBlockEntity>> HOSPITAL_HUT =
            BLOCK_ENTITIES.register("hospital_hut",
                    () -> BlockEntityType.Builder.of(HospitalHutBlockEntity::new, ModBlocks.HOSPITAL_HUT.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
