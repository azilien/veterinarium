package com.veterinarium.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class StretcherBlockEntity extends BlockEntity {
    private int tickCounter = 0;
    public StretcherBlockEntity(BlockPos pos, BlockState state) {
        super(com.veterinarium.registry.ModBlockEntities.STRETCHER.get(), pos, state);
    }
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        tickCounter++;
        if (tickCounter % 40 != 0) return; // 2s
        AABB area = new AABB(pos).inflate(2.5, 1.5, 2.5);
        List<LivingEntity> wounded = level.getEntitiesOfClass(LivingEntity.class, area, e -> e.getTags().contains("veterinarium_wounded") || e instanceof com.veterinarium.entity.WoundedWolfEntity w && !w.isHealed() || e instanceof com.veterinarium.entity.WoundedCatEntity c && !c.isHealed() || e instanceof com.veterinarium.entity.WoundedHorseEntity h && !h.isHealed() || e instanceof com.veterinarium.entity.WoundedFoxEntity f && !f.isHealed() || e instanceof com.veterinarium.entity.WoundedVillagerEntity v && !v.isHealed());
        for (LivingEntity e : wounded) {
            e.heal(1.0f); // 0.5 coeur /2s léger
            e.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
            if (level instanceof net.minecraft.server.level.ServerLevel sl) {
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART, e.getX(), e.getY()+1.0, e.getZ(), 1, 0.2, 0.1, 0.2, 0.1);
            }
            // Brancard aide HP mais ne valide pas le soin (survie: il faut opérer)
        }
    }
}
