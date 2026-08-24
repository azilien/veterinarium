package com.veterinarium.registry;

import com.veterinarium.Veterinarium;
import com.veterinarium.block.AnalysisTableBlock;
import com.veterinarium.block.HospitalHutBlock;
import com.veterinarium.block.InfirmaryBlock;
import com.veterinarium.block.OperatingTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Veterinarium.MODID);

    public static final RegistryObject<Block> OPERATING_TABLE = BLOCKS.register("operating_table",
            () -> new OperatingTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3.5f).requiresCorrectToolForDrops().noOcclusion()));

    public static final RegistryObject<Block> ANALYSIS_TABLE = BLOCKS.register("analysis_table",
            () -> new AnalysisTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARTOGRAPHY_TABLE).strength(2.5f)));

    public static final RegistryObject<Block> INFIRMARY = BLOCKS.register("infirmary",
            () -> new InfirmaryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).strength(1.5f).noOcclusion()));

    public static final RegistryObject<Block> HOSPITAL_HUT = BLOCKS.register("hospital_hut",
            () -> new HospitalHutBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).strength(3.0f).requiresCorrectToolForDrops()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
