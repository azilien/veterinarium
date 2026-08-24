package com.veterinarium.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HospitalHutBlockEntity extends BlockEntity {
    private int healedCount = 0;
    private int tickCounter = 0;

    public HospitalHutBlockEntity(BlockPos pos, BlockState state) {
        super(com.veterinarium.registry.ModBlockEntities.HOSPITAL_HUT.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        tickCounter++;
        if (tickCounter % 40 != 0) return; // 2s

        AABB area = new AABB(pos).inflate(16, 6, 16);
        List<LivingEntity> wounded = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e.getTags().contains("veterinarium_wounded") || e instanceof com.veterinarium.entity.WoundedWolfEntity w && !w.isHealed());

        for (LivingEntity e : wounded) {
            e.heal(2.0f); // 1 coeur /2s
            e.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
            // Si full HP, marque soigné
            if (e.getHealth() >= e.getMaxHealth() * 0.95f) {
                e.addTag("veterinarium_healed");
                e.removeTag("veterinarium_wounded");
                e.removeTag("veterinarium_needs_scalpel");
                e.setCustomName(Component.literal("§a❤ Soigné au Hut"));
                e.setCustomNameVisible(true);
                healedCount++;
                if (e instanceof com.veterinarium.entity.WoundedWolfEntity w) {
                    w.setHealed(true);
                }
            }
        }

        // Heal MineColonies citoyens aussi (via string check)
        if (com.veterinarium.integration.MineColoniesIntegration.isLoaded()) {
            List<LivingEntity> citizens = level.getEntitiesOfClass(LivingEntity.class, area,
                    e -> e.getClass().getName().toLowerCase().contains("citizen"));
            for (LivingEntity c : citizens) {
                if (c.getHealth() < c.getMaxHealth()) c.heal(1.0f);
            }
        }
    }

    public int getPatientCount() {
        if (this.level == null) return 0;
        AABB area = new AABB(this.worldPosition).inflate(16, 6, 16);
        return this.level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e.getTags().contains("veterinarium_wounded")).size();
    }

    public int getHealedCount() { return healedCount; }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("HealedCount", healedCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        healedCount = tag.getInt("HealedCount");
    }
}
