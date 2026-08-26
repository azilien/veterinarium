package com.veterinarium;

import com.mojang.logging.LogUtils;
import com.veterinarium.registry.ModBlockEntities;
import com.veterinarium.registry.ModBlocks;
import com.veterinarium.registry.ModCreativeTabs;
import com.veterinarium.registry.ModEntities;
import com.veterinarium.registry.ModItems;
import com.veterinarium.registry.ModSounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Veterinarium.MODID)
public class Veterinarium {
    public static final String MODID = "veterinarium";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Veterinarium(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, com.veterinarium.config.VeterinariumConfig.COMMON_SPEC, "veterinarium-common.toml");

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Veterinarium chargé - Prêt à soigner les bêtes blessées ! Pour Asfax & les tamers infirmiers.");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM VETERINARIUM COMMON SETUP - Bloc Opératoire prêt !..."); // 1.7.2 timer HUD + wound particles
    }
}
