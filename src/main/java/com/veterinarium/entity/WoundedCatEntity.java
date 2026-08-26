package com.veterinarium.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import com.veterinarium.wound.WoundType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WoundedCatEntity extends Cat {
    private static final EntityDataAccessor<Boolean> DATA_HEALED = SynchedEntityData.defineId(WoundedCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_WOUND_TYPE = SynchedEntityData.defineId(WoundedCatEntity.class, EntityDataSerializers.INT);

    public WoundedCatEntity(EntityType<? extends Cat> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§c☠ Chat Blessé"));
        this.setCustomNameVisible(true);
        this.setPersistenceRequired();
        this.setWoundType(WoundType.random(this.random));
        this.addTag("veterinarium_wounded");
        this.addTag("veterinarium_needs_scalpel");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Cat.createAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HEALED, false);
        builder.define(DATA_WOUND_TYPE, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new OcelotAttackGoal(this));
        this.goalSelector.addGoal(6, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal(this));
    }

    public boolean isHealed() { return this.entityData.get(DATA_HEALED); }
    public void setHealed(boolean h) {
        this.entityData.set(DATA_HEALED, h);
        if (h) {
            this.removeTag("veterinarium_wounded");
            this.removeTag("veterinarium_needs_scalpel");
            this.addTag("veterinarium_healed");
            this.addTag("veterinarium_operated");
            this.setCustomName(Component.literal("§a❤ Chat Soigné"));
            this.setHealth(this.getMaxHealth());
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        }
    }

    public WoundType getWoundType() { return WoundType.fromId(this.entityData.get(DATA_WOUND_TYPE)); }
    public void setWoundType(WoundType t) { this.entityData.set(DATA_WOUND_TYPE, t.getId()); this.addTag(t.getTag()); }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("VetHealed", isHealed());
        tag.putInt("VetWound", getWoundType().getId());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("VetHealed")) this.entityData.set(DATA_HEALED, tag.getBoolean("VetHealed"));
        if (tag.contains("VetWound")) this.entityData.set(DATA_WOUND_TYPE, tag.getInt("VetWound"));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.CAT_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.CAT_DEATH; }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.CAT_STRAY_AMBIENT, 0.15F, 1.0F); }

    @Override
    public Cat getBreedOffspring(ServerLevel level, AgeableMob other) {
        WoundedCatEntity baby = new WoundedCatEntity(com.veterinarium.registry.ModEntities.WOUNDED_CAT.get(), level);
        if (this.random.nextFloat() >= 0.2f) baby.setHealed(true);
        return baby;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount % 60 == 0 && !isHealed() && this.getHealth() < this.getMaxHealth()*0.5f && this.random.nextFloat()<0.3f) {
            this.playSound(SoundEvents.CAT_HURT, 0.7F, 0.9F);
        }
    }
}
