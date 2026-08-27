package com.veterinarium.event;

import com.veterinarium.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class StarterHandler {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getEntity();
        if (player.level().isClientSide) return;
        var tag = player.getPersistentData();
        if (tag.getBoolean("VetStarterGiven")) return;
        tag.putBoolean("VetStarterGiven", true);
        // Donne Dossier Médical + 2 bandages + seringue si inventaire pas plein
        var file = new ItemStack(ModItems.MEDICAL_FILE.get());
        if (!player.addItem(file)) player.drop(file, false);
        var band = new ItemStack(ModItems.BANDAGE.get(), 3);
        if (!player.addItem(band)) player.drop(band, false);
        var syringe = new ItemStack(ModItems.SYRINGE.get());
        // abîme pas
        if (!player.addItem(syringe)) player.drop(syringe, false);
        player.displayClientMessage(Component.literal("§6📖 [Veterinarium] §fDossier Médical offert ! §7Ouvre-le (clic droit) → page 9 Recettes"), false);
        player.displayClientMessage(Component.literal("§7→ Trouve la §aClinique abandonnée §7(rare) pour le kit complet"), false);
    }
}
