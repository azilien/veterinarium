package com.veterinarium.integration;

import com.veterinarium.wound.WoundType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

/**
 * Compat Ice & Fire : wounded dragons rares pour Asfax thumbnail dragon au bloc.
 * Sans dépendance dure (réflexion sur nom de classe).
 */
@Mod.EventBusSubscriber
public class IceAndFireIntegration {
    public static boolean isLoaded() { return ModList.get().isLoaded("iceandfire"); }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!isLoaded()) return;
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof LivingEntity le)) return;
        if (le.getTags().contains("veterinarium_wounded") || le.getTags().contains("veterinarium_healed")) return;
        String cn = le.getClass().getName().toLowerCase();
        // Ice & Fire dragons : EntityFireDragon, EntityIceDragon, EntityLightningDragon + hippogryph etc
        boolean isDragon = cn.contains("firedragon") || cn.contains("icedragon") || cn.contains("lightningdragon") || cn.contains("dragon") && cn.contains("iceandfire");
        boolean isHippogryph = cn.contains("hippogryph");
        boolean isMythic = isDragon || isHippogryph;
        if (!isMythic) return;
        if (le.getRandom().nextFloat() > 0.12f) return; // 12% wounded pour épisodes
        float max = le.getMaxHealth();
        float woundedHealth = max * (0.25f + le.getRandom().nextFloat()*0.35f);
        le.setHealth(woundedHealth);
        le.addTag("veterinarium_wounded");
        le.addTag("veterinarium_needs_scalpel");
        WoundType wt = WoundType.random(le.getRandom());
        // dragons ont plus de brûlure
        if (isDragon && le.getRandom().nextFloat()<0.25f) wt = WoundType.BRULURE;
        le.addTag(wt.getTag());
        le.getPersistentData().putInt("VetWound", wt.getId());
        le.setCustomName(Component.literal("§c☠ Dragon Blessé " + wt.getDisplay() + " §7- " + le.getName().getString()));
        le.setCustomNameVisible(true);
        le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 800, 1));
        le.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 800, 0));
        if (le instanceof Mob mob) mob.setPersistenceRequired();
        // message serveur si proche joueur (pour thumbnail)
        for (var p : event.getLevel().players()) {
            if (p.distanceToSqr(le) < 90000) { // 300 blocs
                p.displayClientMessage(Component.literal("§6☢ Dragon Ice & Fire blessé repéré ! §7" + wt.getDisplay() + " à " + (int)le.getX() + "/" + (int)le.getZ()), false);
            }
        }
    }

    // Bonus soin dragon : après suture, donne écaille
    public static void onDragonHealed(LivingEntity dragon, net.minecraft.world.entity.player.Player player) {
        if (!isLoaded()) return;
        String cn = dragon.getClass().getName().toLowerCase();
        if (!cn.contains("dragon")) return;
        dragon.spawnAtLocation(net.minecraft.world.item.Items.DRAGON_BREATH, 1);
        if (cn.contains("fire") && player.level().random.nextFloat()<0.5f) {
            // essaye de spawn ecaille feu via item registry si présent
            var loc = net.minecraft.resources.ResourceLocation.tryParse("iceandfire:dragon_scale_fire");
            var item = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(loc);
            if (item != null) dragon.spawnAtLocation(item, 1);
        }
        player.displayClientMessage(Component.literal("§6★ Dragon soigné ! Écaille récupérée pour sérum."), false);
    }
}
