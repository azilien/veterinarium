package com.veterinarium;

import com.mojang.logging.LogUtils;
import com.veterinarium.registry.ModBlockEntities;
import com.veterinarium.registry.ModBlocks;
import com.veterinarium.registry.ModCreativeTabs;
import com.veterinarium.registry.ModEntities;
import com.veterinarium.registry.ModItems;
import com.veterinarium.registry.ModSounds;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(Veterinarium.MODID)
public class Veterinarium {
    public static final String MODID = "veterinarium";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Veterinarium(IEventBus modEventBus, ModContainer modContainer) {

        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        com.veterinarium.menu.ModMenuTypes.MENUS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, com.veterinarium.config.VeterinariumConfig.COMMON_SPEC, "veterinarium-common.toml");

        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        LOGGER.info("Veterinarium chargé - Prêt à soigner les bêtes blessées !");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}
