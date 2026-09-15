package com.veterinarium.registry;

import com.veterinarium.Veterinarium;
import com.veterinarium.block.entity.AnalysisTableBlockEntity;
import com.veterinarium.block.entity.ContaminatorBlockEntity;
import com.veterinarium.block.entity.HospitalHutBlockEntity;
import com.veterinarium.block.entity.OperatingTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Veterinarium.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HospitalHutBlockEntity>> HOSPITAL_HUT =
            BLOCK_ENTITIES.register("hospital_hut",
                    () -> BlockEntityType.Builder.of(HospitalHutBlockEntity::new, ModBlocks.HOSPITAL_HUT.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnalysisTableBlockEntity>> ANALYSIS_TABLE =
            BLOCK_ENTITIES.register("analysis_table",
                    () -> BlockEntityType.Builder.of(AnalysisTableBlockEntity::new, ModBlocks.ANALYSIS_TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OperatingTableBlockEntity>> OPERATING_TABLE =
            BLOCK_ENTITIES.register("operating_table",
                    () -> BlockEntityType.Builder.of(OperatingTableBlockEntity::new, ModBlocks.OPERATING_TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.veterinarium.block.entity.StretcherBlockEntity>> STRETCHER =
            BLOCK_ENTITIES.register("stretcher",
                    () -> BlockEntityType.Builder.of(com.veterinarium.block.entity.StretcherBlockEntity::new, ModBlocks.STRETCHER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ContaminatorBlockEntity>> CONTAMINATOR =
            BLOCK_ENTITIES.register("contaminator",
                    () -> BlockEntityType.Builder.of(ContaminatorBlockEntity::new, ModBlocks.CONTAMINATOR.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
