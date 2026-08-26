package com.veterinarium.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WoundedVillagerEntity extends Villager {
    private static final EntityDataAccessor<Boolean> DATA_HEALED = SynchedEntityData.defineId(WoundedVillagerEntity.class, EntityDataSerializers.BOOLEAN);

    public WoundedVillagerEntity(EntityType<? extends Villager> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§c☠ Villageois Blessé"));
        this.setCustomNameVisible(true);
        this.setPersistenceRequired();
        this.addTag("veterinarium_wounded");
        this.addTag("veterinarium_needs_scalpel");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Villager.createAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.45D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HEALED, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals(); // garde l'IA villageoise
        // ajoute un peu de lenteur visuelle quand blessé via tick
    }

    public boolean isHealed() { return this.entityData.get(DATA_HEALED); }
    public void setHealed(boolean h) {
        this.entityData.set(DATA_HEALED, h);
        if (h) {
            this.removeTag("veterinarium_wounded");
            this.removeTag("veterinarium_needs_scalpel");
            this.addTag("veterinarium_healed");
            this.addTag("veterinarium_operated");
            this.setCustomName(Component.literal("§a❤ Villageois Soigné"));
            this.setCustomNameVisible(true);
            this.setHealth(this.getMaxHealth());
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5D);
            // petit bonus: regen
            this.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 100, 1));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("VetHealed", isHealed());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("VetHealed")) this.entityData.set(DATA_HEALED, tag.getBoolean("VetHealed"));
    }

    @Override
    protected SoundEvent getAmbientSound() { return isHealed() ? SoundEvents.VILLAGER_AMBIENT : SoundEvents.VILLAGER_HURT; }
    @Override
    protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.VILLAGER_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.VILLAGER_DEATH; }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.VILLAGER_WORK_FARMER, 0.15F, 1.0F); }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount % 100 == 0 && !isHealed() && this.getHealth() < this.getMaxHealth()*0.5f && this.random.nextFloat()<0.2f) {
            this.playSound(SoundEvents.VILLAGER_HURT, 0.7F, 0.9F);
        }
    }
}
