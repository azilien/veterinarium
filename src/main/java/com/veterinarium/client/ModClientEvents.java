package com.veterinarium.client;

import com.veterinarium.Veterinarium;
import com.veterinarium.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Veterinarium.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.WOUNDED_WOLF.get(), WoundedWolfRenderer::new);
        event.registerEntityRenderer(ModEntities.WOUNDED_CAT.get(), WoundedCatRenderer::new);
        event.registerEntityRenderer(ModEntities.WOUNDED_HORSE.get(), WoundedHorseRenderer::new);
        event.registerEntityRenderer(ModEntities.WOUNDED_FOX.get(), WoundedFoxRenderer::new);
        event.registerEntityRenderer(ModEntities.WOUNDED_VILLAGER.get(), WoundedVillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.HELLFIRE_RAVAGER.get(), HellfireRavagerRenderer::new);
        event.registerEntityRenderer(ModEntities.WOUNDED_DRAKE.get(), WoundedDrakeRenderer::new);
        event.registerEntityRenderer(ModEntities.WOUNDED_COW.get(), WoundedCowRenderer::new);
        event.registerEntityRenderer(ModEntities.WOUNDED_SHEEP.get(), WoundedSheepRenderer::new);
        event.registerEntityRenderer(ModEntities.WOUNDED_CHICKEN.get(), WoundedChickenRenderer::new);
    }
}
