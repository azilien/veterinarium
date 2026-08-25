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
    private int hutLevel = 1; // 1-3

    public HospitalHutBlockEntity(BlockPos pos, BlockState state) {
        super(com.veterinarium.registry.ModBlockEntities.HOSPITAL_HUT.get(), pos, state);
    }

    public int getRadius() { return 12 + hutLevel * 4; } // 16,20,24
    public float getHealAmount() { return 1.0f + hutLevel * 0.5f; } // 1.5,2.0,2.5 -> 0.75-1.25 coeur
    public int getHutLevel() { return hutLevel; }
    public boolean tryUpgrade(net.minecraft.world.entity.player.Player player) {
        if (hutLevel >= 3) return false;
        int needBricks = 8;
        int needBandage = hutLevel == 1 ? 4 : 8;
        boolean needDiamond = hutLevel == 2;
        // Check inventory
        int hasBricks = countItem(player, net.minecraft.world.item.Items.BRICK);
        int hasBandage = countItem(player, com.veterinarium.registry.ModItems.BANDAGE.get());
        int hasDiamond = countItem(player, net.minecraft.world.item.Items.DIAMOND);
        if (hasBricks < needBricks || hasBandage < needBandage || (needDiamond && hasDiamond < 1)) return false;
        // Consume
        consumeItem(player, net.minecraft.world.item.Items.BRICK, needBricks);
        consumeItem(player, com.veterinarium.registry.ModItems.BANDAGE.get(), needBandage);
        if (needDiamond) consumeItem(player, net.minecraft.world.item.Items.DIAMOND, 1);
        hutLevel++;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        return true;
    }
    private int countItem(net.minecraft.world.entity.player.Player p, net.minecraft.world.item.Item item) {
        int c=0; for (var s : p.getInventory().items) if (!s.isEmpty() && s.is(item)) c+=s.getCount();
        for (var s : p.getInventory().offhand) if (!s.isEmpty() && s.is(item)) c+=s.getCount();
        return c;
    }
    private void consumeItem(net.minecraft.world.entity.player.Player p, net.minecraft.world.item.Item item, int need) {
        for (var s : p.getInventory().items) {
            if (s.isEmpty() || !s.is(item)) continue;
            int take = Math.min(s.getCount(), need);
            s.shrink(take); need-=take; if (need<=0) return;
        }
        for (var s : p.getInventory().offhand) {
            if (s.isEmpty() || !s.is(item)) continue;
            int take = Math.min(s.getCount(), need);
            s.shrink(take); need-=take; if (need<=0) return;
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        tickCounter++;
        if (tickCounter % 40 != 0) return; // 2s

        int radius = getRadius();
        float heal = getHealAmount();
        AABB area = new AABB(pos).inflate(radius, 6, radius);
        List<LivingEntity> wounded = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e.getTags().contains("veterinarium_wounded") || e instanceof com.veterinarium.entity.WoundedWolfEntity w && !w.isHealed()
                        || e instanceof com.veterinarium.entity.WoundedCatEntity c && !c.isHealed()
                        || e instanceof com.veterinarium.entity.WoundedHorseEntity h && !h.isHealed());

        for (LivingEntity e : wounded) {
            e.heal(heal);
            e.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
            if (e.getHealth() >= e.getMaxHealth() * 0.95f) {
                e.addTag("veterinarium_healed");
                e.removeTag("veterinarium_wounded");
                e.removeTag("veterinarium_needs_scalpel");
                e.setCustomName(Component.literal("§a❤ Soigné au Hut Lv"+hutLevel));
                e.setCustomNameVisible(true);
                healedCount++;
                if (e instanceof com.veterinarium.entity.WoundedWolfEntity w) w.setHealed(true);
                if (e instanceof com.veterinarium.entity.WoundedCatEntity c) c.setHealed(true);
                if (e instanceof com.veterinarium.entity.WoundedHorseEntity h) h.setHealed(true);
            }
        }

        if (com.veterinarium.integration.MineColoniesIntegration.isLoaded()) {
            List<LivingEntity> citizens = level.getEntitiesOfClass(LivingEntity.class, area,
                    e -> e.getClass().getName().toLowerCase().contains("citizen"));
            for (LivingEntity c : citizens) {
                if (c.getHealth() < c.getMaxHealth()) c.heal(heal*0.5f);
            }
        }
        // Son monitor toutes les 10s si des patients (custom si dispo)
        if (!wounded.isEmpty() && tickCounter % 200 == 0) {
            try {
                level.playSound(null, pos, com.veterinarium.registry.ModSounds.MONITOR_BEEP.get(), net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.2f);
            } catch (Exception e) {
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.NOTE_BLOCK_PLING.value(), net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.5f);
            }
        }
    }

    public int getPatientCount() {
        if (this.level == null) return 0;
        AABB area = new AABB(this.worldPosition).inflate(getRadius(), 6, getRadius());
        return this.level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e.getTags().contains("veterinarium_wounded")).size();
    }

    public int getHealedCount() { return healedCount; }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("HealedCount", healedCount);
        tag.putInt("HutLevel", hutLevel);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        healedCount = tag.getInt("HealedCount");
        hutLevel = tag.contains("HutLevel") ? tag.getInt("HutLevel") : 1;
        if (hutLevel <1) hutLevel=1; if (hutLevel>3) hutLevel=3;
    }
}
