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
    private int hutLevel = 1; // 1-5

    public HospitalHutBlockEntity(BlockPos pos, BlockState state) {
        super(com.veterinarium.registry.ModBlockEntities.HOSPITAL_HUT.get(), pos, state);
    }

    public int getRadius() { return 12 + hutLevel * 4; } // 16,20,24,28,32
    public float getHealAmount() { return 1.0f + hutLevel * 0.5f; } // 1.5,2.0,2.5,3.0,3.5
    public int getHutLevel() { return hutLevel; }
    public boolean tryUpgrade(net.minecraft.world.entity.player.Player player) {
        if (hutLevel >= 5) return false;
        int needBricks = switch (hutLevel) { case 1 -> 8; case 2 -> 8; case 3 -> 12; case 4 -> 16; default -> 99; };
        int needBandage = switch (hutLevel) { case 1 -> 4; case 2 -> 8; case 3 -> 12; case 4 -> 16; default -> 99; };
        int needDiamond = (hutLevel == 2) ? 1 : 0;
        int needEmerald = switch (hutLevel) { case 3 -> 2; case 4 -> 4; default -> 0; };
        int needNetherite = (hutLevel == 4) ? 1 : 0;
        int hasBricks = countItem(player, net.minecraft.world.item.Items.BRICK);
        int hasBandage = countItem(player, com.veterinarium.registry.ModItems.BANDAGE.get());
        int hasDiamond = countItem(player, net.minecraft.world.item.Items.DIAMOND);
        int hasEmerald = countItem(player, net.minecraft.world.item.Items.EMERALD);
        int hasNetherite = countItem(player, net.minecraft.world.item.Items.NETHERITE_INGOT);
        if (hasBricks < needBricks || hasBandage < needBandage || hasDiamond < needDiamond || hasEmerald < needEmerald || hasNetherite < needNetherite) return false;
        consumeItem(player, net.minecraft.world.item.Items.BRICK, needBricks);
        consumeItem(player, com.veterinarium.registry.ModItems.BANDAGE.get(), needBandage);
        if (needDiamond>0) consumeItem(player, net.minecraft.world.item.Items.DIAMOND, needDiamond);
        if (needEmerald>0) consumeItem(player, net.minecraft.world.item.Items.EMERALD, needEmerald);
        if (needNetherite>0) consumeItem(player, net.minecraft.world.item.Items.NETHERITE_INGOT, needNetherite);
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
                        || e instanceof com.veterinarium.entity.WoundedHorseEntity h && !h.isHealed()
                        || e instanceof com.veterinarium.entity.WoundedFoxEntity f && !f.isHealed()
                        || e instanceof com.veterinarium.entity.WoundedVillagerEntity v && !v.isHealed());

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
                if (e instanceof com.veterinarium.entity.WoundedFoxEntity f) f.setHealed(true);
                if (e instanceof com.veterinarium.entity.WoundedVillagerEntity v) v.setHealed(true);
            }
        }

        if (com.veterinarium.integration.MineColoniesIntegration.isLoaded()) {
            List<LivingEntity> citizens = level.getEntitiesOfClass(LivingEntity.class, area,
                    e -> e.getClass().getName().toLowerCase().contains("citizen"));
            for (LivingEntity c : citizens) {
                if (c.getHealth() < c.getMaxHealth()) c.heal(heal*0.5f);
            }
        }
        // Effet particules coeur si patients (Lv3+)
        if (!wounded.isEmpty() && hutLevel >= 3 && level instanceof net.minecraft.server.level.ServerLevel sl) {
            if (tickCounter % 20 == 0) {
                for (LivingEntity e : wounded) {
                    sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART, e.getX(), e.getY()+1.2, e.getZ(), 1, 0.2, 0.2, 0.2, 0.1);
                }
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, pos.getX()+0.5, pos.getY()+1.2, pos.getZ()+0.5, 3, 0.5, 0.3, 0.5, 0.1);
            }
        }
        // Son monitor toutes les 10s si des patients (custom si dispo)
        if (!wounded.isEmpty() && tickCounter % 200 == 0) {
            try {
                level.playSound(null, pos, com.veterinarium.registry.ModSounds.MONITOR_BEEP.get(), net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.2f + hutLevel*0.1f);
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
        if (hutLevel <1) hutLevel=1; if (hutLevel>5) hutLevel=5;
    }
}
