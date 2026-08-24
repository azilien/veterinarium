package com.veterinarium;

import com.mojang.logging.LogUtils;
import com.veterinarium.registry.ModBlocks;
import com.veterinarium.registry.ModCreativeTabs;
import com.veterinarium.registry.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Veterinarium chargé - Prêt à soigner les bêtes blessées ! Pour Asfax & les tamers infirmiers.");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM VETERINARIUM COMMON SETUP - Bloc Opératoire prêt !");
    }
}
