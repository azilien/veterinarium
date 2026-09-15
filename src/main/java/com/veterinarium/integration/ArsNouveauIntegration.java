package com.veterinarium.integration;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Bridge Ars Nouveau - sans dépendance dure.
 * Si Ars Nouveau est présent:
 * - La Table d'Analyse donne un bonus si le joueur a un Source Gem en main
 * - La Syringe + Source = source rapide
 * - Scalpel/Suture ont une chance de ne pas consommer de durabilité si le joueur a de la mana (simulé via effet)
 * On évite d'importer les classes Ars Nouveau pour rester optionnel.
 */
@EventBusSubscriber
public class ArsNouveauIntegration {

    public static boolean isLoaded() {
        return ModList.get().isLoaded("ars_nouveau");
    }

    // Bonus passif: si le joueur tient une source gem, ses soins sont améliorés — disabled for NeoForge port
    // @SubscribeEvent
    // public static void onLivingHeal(LivingEvent.LivingTickEvent event) {
    //     if (!isLoaded()) return;
    public static void onLivingHealDisabled() {
        if (!isLoaded()) return;
        // Léger bonus: on ne fait rien de lourd ici, juste un hook pour future extension
        // L'intégration principale est via les recettes conditionnelles (voir data)
    }

    public static boolean hasSourceGem(Player player) {
        if (!isLoaded()) return false;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) continue;
            String id = stack.getItem().toString().toLowerCase(); // fallback
            // On cherche via registry key plus fiable
            var key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (key != null && key.toString().equals("ars_nouveau:source_gem")) return true;
            if (key != null && key.toString().contains("source_gem")) return true;
        }
        return false;
    }

    public static void applyArsBonus(LivingEntity target, Player player) {
        if (!isLoaded()) return;
        if (!hasSourceGem(player)) return;
        // Bonus Ars: +1 coeur sup + Resistance, son magique
        target.heal(2.0f);
        target.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 0));
        target.level().playSound(null, target.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8f, 1.5f);
        player.displayClientMessage(Component.literal("§5[Ars Nouveau] §dSource Gem détectée → soin magique + absorption !"), true);
    }
}
