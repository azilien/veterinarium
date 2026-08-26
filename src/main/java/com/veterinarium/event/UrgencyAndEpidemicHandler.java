package com.veterinarium.event;

import com.veterinarium.registry.ModEntities;
import com.veterinarium.wound.WoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class UrgencyAndEpidemicHandler {

    // cooldown partagé (ticks) avant prochaine urgence
    private static int ticksUntilNextUrgency = 6000; // 5 min initial

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Level level = event.level;
        if (level.isClientSide) return;
        if (level.getGameTime() % 40 != 0 && level.getGameTime() % 6000 != 0) {
            // on laisse passer mais on veut infection à 40 et urgence à 6000 - on gère séparément
        }
        if (level.getGameTime() % 40 == 0) {
            handleInfectionSpread(level);
            handleUrgentExpiry(level);
        }
        // tick urgence cooldown décrément
        if (!level.players().isEmpty()) {
            ticksUntilNextUrgency--;
            if (ticksUntilNextUrgency <= 0) {
                if (trySpawnUrgency(level)) {
                    ticksUntilNextUrgency = 8000 + level.random.nextInt(6000); // 6-11 min
                } else {
                    ticksUntilNextUrgency = 2000; // retry sooner si pas de spot
                }
            }
        }
    }

    private static void handleInfectionSpread(Level level) {
        // Cherche porteurs infection actifs
        List<LivingEntity> carriers = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000),
                e -> {
                    WoundType wt = getWound(e);
                    return wt == WoundType.INFECTION && (e.getTags().contains("veterinarium_wounded") || e instanceof com.veterinarium.entity.WoundedWolfEntity w && !w.isHealed());
                });
        // limite pour perf : si >20 carriers, on sample 20
        if (carriers.size() > 20) carriers = carriers.subList(0, 20);
        for (LivingEntity carrier : carriers) {
            // si isolé dans hut ? on considère que Hut Lv3+ isole (pas de contagion si dans rayon hut 12)
            if (isInsideHospitalHut(level, carrier.blockPosition())) continue;
            AABB area = new AABB(carrier.blockPosition()).inflate(4, 2, 4);
            List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != carrier && e.isAlive() && !e.getTags().contains("veterinarium_wounded") && e.getHealth() > 0);
            for (LivingEntity target : nearby) {
                // 4% par tick (toutes 2s) => ~48% par min si collés
                if (level.random.nextFloat() < 0.04f) {
                    // filtre animal/villageois uniquement
                    if (!(target instanceof net.minecraft.world.entity.animal.Animal) && !(target instanceof net.minecraft.world.entity.npc.Villager) && !(target instanceof net.minecraft.world.entity.animal.horse.AbstractHorse)) continue;
                    // infecte
                    target.addTag("veterinarium_wounded");
                    target.addTag("veterinarium_needs_scalpel");
                    target.addTag(WoundType.INFECTION.getTag());
                    target.getPersistentData().putInt("VetWound", WoundType.INFECTION.getId());
                    float max = target.getMaxHealth();
                    target.setHealth(max * 0.5f);
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 0));
                    target.setCustomName(Component.literal("§c☠ Infecté §7- " + target.getName().getString().replace("§c☠ Infecté §7- ", "").replace("§c☠ Blessé §7- ", "")));
                    target.setCustomNameVisible(true);
                    if (target instanceof Mob mob) mob.setPersistenceRequired();
                    if (level instanceof ServerLevel sl) {
                        sl.sendParticles(net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER, target.getX(), target.getY()+1, target.getZ(), 2, 0.2,0.2,0.2,0.1);
                    }
                    // alerte joueurs proches
                    for (var p : level.players()) {
                        if (p.distanceToSqr(target) < 5000) { // ~70 blocs
                            p.displayClientMessage(Component.literal("§5☣ Contagion ! §f" + target.getName().getString() + " §7a attrapé §5l'Infection §7près de " + carrier.getName().getString()), false);
                            level.playSound(null, p.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.6f, 0.8f);
                        }
                    }
                    break; // 1 infection max par carrier par tick
                }
            }
        }
    }

    private static boolean isInsideHospitalHut(Level level, BlockPos pos) {
        // si un hut dans 14 blocs, on considère isolé (quarantaine)
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-14, -6, -14), pos.offset(14, 6, 14))) {
            if (level.getBlockState(p).is(com.veterinarium.registry.ModBlocks.HOSPITAL_HUT.get())) {
                // check HutLevel >=3 isolé, sinon pas d'isolement complet
                var be = level.getBlockEntity(p);
                if (be instanceof com.veterinarium.block.entity.HospitalHutBlockEntity hut && hut.getHutLevel() >= 3) return true;
                // sinon stretcher aussi isole si très proche 2.5
                // on laisse passer
            }
            if (level.getBlockState(p).is(com.veterinarium.registry.ModBlocks.STRETCHER.get())) {
                if (p.distManhattan(pos) <= 3) return true;
            }
        }
        return false;
    }

    private static void handleUrgentExpiry(Level level) {
        List<LivingEntity> urgents = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000),
                e -> e.getTags().contains("veterinarium_urgent"));
        for (LivingEntity e : urgents) {
            long expiry = e.getPersistentData().getLong("VetUrgentExpiry");
            if (expiry == 0) continue;
            if (level.getGameTime() > expiry) {
                if (e.getTags().contains("veterinarium_wounded")) {
                    // échec
                    e.hurt(level.damageSources().magic(), 100f);
                    e.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
                    for (var p : level.players()) {
                        p.displayClientMessage(Component.literal("§c☠ URGENCE ÉCHOUÉE ! §7" + e.getName().getString() + " n'a pas été sauvé à temps."), false);
                        if (level instanceof ServerLevel sl) sl.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, e.getX(), e.getY()+1, e.getZ(), 10, 0.5,0.5,0.5,0.02);
                    }
                    e.removeTag("veterinarium_urgent");
                } else {
                    // déjà soigné, on nettoie tag et récompense déjà donnée via healed check ailleurs
                    e.removeTag("veterinarium_urgent");
                }
            } else {
                // tick particules urgentes
                if (level instanceof ServerLevel sl && level.getGameTime() % 60 == 0) {
                    sl.sendParticles(net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER, e.getX(), e.getY()+1.2, e.getZ(), 1, 0.3,0.3,0.3,0.1);
                    // bip si proche
                    if (level.random.nextFloat() < 0.2f) level.playSound(null, e.blockPosition(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.BLOCKS, 0.5f, 2.0f);
                }
            }
        }
    }

    private static boolean trySpawnUrgency(Level level) {
        if (!(level instanceof ServerLevel sl)) return false;
        if (level.players().isEmpty()) return false;
        // choisit un joueur aléatoire qui a un hut ou infirmerie ou au moins 3 diags (début série)
        ServerPlayer targetPlayer = null;
        for (var p : level.players()) {
            var nbt = p.getPersistentData();
            if (nbt.getInt("VetDiagTotal") >= 2 || hasNearbyHospital(level, p.blockPosition(), 128)) {
                targetPlayer = (ServerPlayer) p;
                break;
            }
        }
        if (targetPlayer == null) {
            // pas encore prêt pour urgences (trop tôt)
            targetPlayer = (ServerPlayer) level.players().get(level.random.nextInt(level.players().size()));
            // on laisse quand même après 5 urgences ratées? On spawn quand même avec proba faible 30%
            if (level.random.nextFloat() < 0.7f) return false;
        }
        BlockPos origin = targetPlayer.blockPosition();
        // cherche position spawn à 80-150 blocs, au sol
        for (int attempt=0; attempt<20; attempt++) {
            int dx = level.random.nextInt(140) - 70;
            int dz = level.random.nextInt(140) - 70;
            if (Math.abs(dx) < 60 && Math.abs(dz) < 60) continue; // pas trop proche
            dx += origin.getX();
            dz += origin.getZ();
            int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, dx, dz);
            BlockPos pos = new BlockPos(dx, y, dz);
            // vérifie air
            if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) continue;
            if (!level.getBlockState(pos.below()).isSolidRender(level, pos.below())) continue;

            // choisit entité urgente aléatoire
            LivingEntity spawned = spawnRandomWounded(sl, pos);
            if (spawned == null) continue;
            spawned.addTag("veterinarium_urgent");
            long expiry = level.getGameTime() + 6000 + level.random.nextInt(4000); // 5-8 min
            spawned.getPersistentData().putLong("VetUrgentExpiry", expiry);
            spawned.setCustomName(Component.literal("§c🚨 URGENCE §7- " + spawned.getName().getString().replace("§c☠ Blessé §7- ", "").replace("§c🚨 URGENCE §7- ", "") + " §8(5-8 min)"));
            spawned.setCustomNameVisible(true);
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 1, 0,0,0,0);
            // message
            Component msg = Component.literal("§c🚨 APPEL D'URGENCE ! §f" + spawned.getName().getString().replace("§c🚨 URGENCE §7- ", "") + " §7à " + pos.getX() + " / " + pos.getZ() + " §8(distance " + (int)Math.sqrt(origin.distSqr(pos)) + ") §7— Seringue -> Bloc !");
            for (var p : level.players()) {
                p.displayClientMessage(msg, false);
                p.sendSystemMessage(msg);
                level.playSound(null, p.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.0f, 1.2f);
                level.playSound(null, p.blockPosition(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1.0f, 1.5f);
            }
            return true;
        }
        return false;
    }

    private static boolean hasNearbyHospital(Level level, BlockPos center, int radius) {
        for (BlockPos p : BlockPos.betweenClosed(center.offset(-radius, -10, -radius), center.offset(radius, 10, radius))) {
            if (level.getBlockState(p).is(com.veterinarium.registry.ModBlocks.HOSPITAL_HUT.get()) || level.getBlockState(p).is(com.veterinarium.registry.ModBlocks.INFIRMARY.get()))
                return true;
        }
        return false;
    }

    private static LivingEntity spawnRandomWounded(ServerLevel sl, BlockPos pos) {
        float r = sl.random.nextFloat();
        net.minecraft.world.entity.EntityType<?> type;
        if (r < 0.22f) type = ModEntities.WOUNDED_WOLF.get();
        else if (r < 0.40f) type = ModEntities.WOUNDED_CAT.get();
        else if (r < 0.58f) type = ModEntities.WOUNDED_FOX.get();
        else if (r < 0.75f) type = ModEntities.WOUNDED_HORSE.get();
        else if (r < 0.88f) type = ModEntities.WOUNDED_VILLAGER.get();
        else type = ModEntities.HELLFIRE_RAVAGER.get(); // rare urgence ravager blessé (phase mutation)
        // si drake existe, 5% de chance
        if (sl.random.nextFloat() < 0.08f) {
            try {
                var drakeOpt = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getValue(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("veterinarium", "wounded_drake"));
                if (drakeOpt != null) type = drakeOpt;
            } catch (Exception ignored) {}
        }
        var e = type.create(sl);
        if (e instanceof LivingEntity le) {
            le.moveTo(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, sl.random.nextFloat()*360, 0);
            // health déjà géré par WoundedSpawn? mais on force wounded
            if (le instanceof Mob mob) mob.finalizeSpawn(sl, sl.getCurrentDifficultyAt(pos), net.minecraft.world.entity.MobSpawnType.EVENT, null);
            // force tags si pas déjà
            le.addTag("veterinarium_wounded");
            le.addTag("veterinarium_needs_scalpel");
            WoundType wt = WoundType.random(sl.random);
            le.addTag(wt.getTag());
            le.getPersistentData().putInt("VetWound", wt.getId());
            float max = le.getMaxHealth();
            le.setHealth(max * (0.2f + sl.random.nextFloat()*0.3f));
            le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6000, 1));
            le.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 6000, 0));
            if (le instanceof Mob mob) mob.setPersistenceRequired();
            sl.addFreshEntity(le);
            return le;
        }
        return null;
    }

    private static WoundType getWound(LivingEntity e) {
        if (e instanceof com.veterinarium.entity.WoundedWolfEntity w) return w.getWoundType();
        if (e instanceof com.veterinarium.entity.WoundedCatEntity c) return c.getWoundType();
        if (e instanceof com.veterinarium.entity.WoundedHorseEntity h) return h.getWoundType();
        if (e instanceof com.veterinarium.entity.WoundedFoxEntity f) return f.getWoundType();
        if (e instanceof com.veterinarium.entity.WoundedVillagerEntity v) return v.getWoundType();
        if (e.getPersistentData().contains("VetWound")) return WoundType.fromId(e.getPersistentData().getInt("VetWound"));
        if (e.getTags().contains("veterinarium_wound_brulure")) return WoundType.BRULURE;
        if (e.getTags().contains("veterinarium_wound_infection")) return WoundType.INFECTION;
        if (e.getTags().contains("veterinarium_wound_fracture")) return WoundType.FRACTURE;
        if (e.getTags().contains("veterinarium_wound_hemorragie")) return WoundType.HEMORRAGIE;
        return WoundType.CONTUSION;
    }
}
