package com.veterinarium.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import com.veterinarium.wound.WoundType;

public class ContaminatorBlockEntity extends BlockEntity {
    private int infectedCount = 0;
    private static final int SPREAD_RANGE = 6;
    private static final double SPREAD_CHANCE = 0.12;

    public ContaminatorBlockEntity(BlockPos pos, BlockState state) {
        super(com.veterinarium.registry.ModBlockEntities.CONTAMINATOR.get(), pos, state);
    }

    public int getInfectedCount() { return infectedCount; }
    public int getSpreadRange() { return SPREAD_RANGE; }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ContaminatorBlockEntity be) {
        if (level.getGameTime() % 20 != 0) return;

        if (level instanceof ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, pos.getX()+0.5, pos.getY()+1.0, pos.getZ()+0.5, 2, 0.3, 0.3, 0.3, 0.01);
            if (level.random.nextFloat() < 0.3f) {
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.MYCELIUM, pos.getX()+0.5, pos.getY()+0.8, pos.getZ()+0.5, 1, 0.4, 0.4, 0.4, 0.02);
            }
        }
        if (level.random.nextFloat() < 0.1f) {
            level.playSound(null, pos, com.veterinarium.registry.ModSounds.CONTAMINATOR_AMBIENT.get(), SoundSource.BLOCKS, 0.4f, 0.7f);
        }

        AABB area = new AABB(pos).inflate(SPREAD_RANGE);
        for (Entity entity : level.getEntitiesOfClass(Entity.class, area)) {
            if (!(entity instanceof LivingEntity le)) continue;
            if (le instanceof Player) continue;

            boolean isTarget = le instanceof Animal || le instanceof Villager || le instanceof net.minecraft.world.entity.animal.horse.AbstractHorse;
            boolean isWounded = le.getTags().contains("veterinarium_wounded");

            if (isTarget && !isWounded) {
                if (level.random.nextDouble() < SPREAD_CHANCE * 0.3) {
                    le.addTag("veterinarium_wounded");
                    le.addTag("veterinarium_needs_scalpel");
                    le.addTag("veterinarium_wound_infection");
                    le.getPersistentData().putInt("VetWound", WoundType.INFECTION.getId());
                    le.setHealth(le.getMaxHealth() * 0.5f);
                    le.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
                    le.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 0));
                    le.setCustomName(Component.literal("§4☠ Infecté"));
                    le.setCustomNameVisible(true);
                    if (le instanceof Mob mob) mob.setPersistenceRequired();
                    be.infectedCount++;
                    if (level instanceof ServerLevel sl) {
                        sl.sendParticles(net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER, le.getX(), le.getY()+1.5, le.getZ(), 3, 0.3,0.3,0.3,0.1);
                    }
                    level.playSound(null, le.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.HOSTILE, 0.6f, 0.8f);
                }
            } else if (isWounded && !le.getTags().contains("veterinarium_wound_infection")) {
                if (level.random.nextDouble() < SPREAD_CHANCE) {
                    le.removeTag("veterinarium_wound_contusion");
                    le.removeTag("veterinarium_wound_hemorragie");
                    le.removeTag("veterinarium_wound_fracture");
                    le.removeTag("veterinarium_wound_brulure");
                    le.removeTag("veterinarium_wound_saignement");
                    le.addTag("veterinarium_needs_scalpel");
                    le.addTag("veterinarium_wound_infection");
                    le.getPersistentData().putInt("VetWound", WoundType.INFECTION.getId());
                    le.setHealth(le.getMaxHealth() * 0.5f);
                    le.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
                    le.setCustomName(Component.literal("§4☠ Infecté"));
                    le.setCustomNameVisible(true);
                    be.infectedCount++;
                    if (level instanceof ServerLevel sl) {
                        sl.sendParticles(net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER, le.getX(), le.getY()+1.5, le.getZ(), 3, 0.3,0.3,0.3,0.1);
                    }
                    level.playSound(null, le.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.HOSTILE, 0.6f, 0.8f);
                    for (Player p : level.getEntitiesOfClass(Player.class, new AABB(le.blockPosition()).inflate(32))) {
                        p.displayClientMessage(Component.literal("§c[Contaminateur] §f" + le.getName().getString() + " §7évolue vers §4Infection §7près de §e" + pos), false);
                    }
                }
            }
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        // No items stored — just particle/sound feedback
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("InfectedCount", infectedCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        infectedCount = tag.getInt("InfectedCount");
    }
}
