package com.veterinarium.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WoundedSpawnHandler {
    // 8% des animaux spawn blessés (pour filmer facilement)
    private static final double WOUND_CHANCE = 0.08;

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof LivingEntity living)) return;
        if (living.getTags().contains("veterinarium_wounded") || living.getTags().contains("veterinarium_healed")) return;
        
        // Ne touche pas les joueurs, ni les déjà blessés, ni les armors stands
        if (living instanceof net.minecraft.world.entity.player.Player) return;
        
        // Filtre: animaux + villageois + golem + quelques monstres doux
        boolean isCandidate = living instanceof Wolf
                || living instanceof IronGolem
                || living instanceof Cow
                || living instanceof Sheep
                || living instanceof Pig
                || living instanceof Horse
                || living instanceof Fox
                || living instanceof Cat
                || living instanceof Villager
                || living instanceof Parrot
                || living instanceof Creeper; // creeper blessé = marrant pour Asfax
        
        if (!isCandidate) return;
        // 8% chance
        if (living.getRandom().nextDouble() > WOUND_CHANCE) return;
        
        // Devient blessé
        float max = living.getMaxHealth();
        float woundedHealth = max * (0.25f + living.getRandom().nextFloat() * 0.35f); // 25-60%
        living.setHealth(woundedHealth);
        living.addTag("veterinarium_wounded");
        living.addTag("veterinarium_needs_scalpel"); // doit être opéré d'abord
        // Nom visible
        living.setCustomName(Component.literal("§c☠ Blessé §7- " + living.getName().getString()));
        living.setCustomNameVisible(true);
        // Effets
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 1));
        living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 0));
        // Pas de despawn pour les animaux blessés (si c'est un Mob)
        if (living instanceof net.minecraft.world.entity.Mob mob) {
            mob.setPersistenceRequired();
        }
    }
}
