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
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WoundedWolfEntity extends Wolf {
    private static final EntityDataAccessor<Boolean> DATA_HEALED = SynchedEntityData.defineId(WoundedWolfEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_WOUND_TYPE = SynchedEntityData.defineId(WoundedWolfEntity.class, EntityDataSerializers.INT);

    public WoundedWolfEntity(EntityType<? extends Wolf> type, Level level) {
        super(type, level);
        // Name visible
        this.setCustomName(Component.literal("§c☠ Loup Blessé"));
        this.setCustomNameVisible(true);
        this.setPersistenceRequired();
        this.setWoundType(WoundType.random(this.random));
        // Ajoute tags pour compatibilité avec ancien système
        this.addTag("veterinarium_wounded");
        this.addTag("veterinarium_needs_scalpel");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D) // fragile
                .add(Attributes.MOVEMENT_SPEED, 0.25D) // boiteux
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
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        // Pas de BegGoal - il gémit au lieu de quémander
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal(this));
    }

    public boolean isHealed() {
        return this.entityData.get(DATA_HEALED);
    }

    public void setHealed(boolean healed) {
        this.entityData.set(DATA_HEALED, healed);
        if (healed) {
            this.removeTag("veterinarium_wounded");
            this.removeTag("veterinarium_needs_scalpel");
            this.addTag("veterinarium_healed");
            this.addTag("veterinarium_operated");
            this.setCustomName(Component.literal("§a❤ Loup Soigné"));
            this.setCustomNameVisible(true);
            this.setHealth(this.getMaxHealth());
            // Vitesse normale retrouvée
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        }
    }

    public WoundType getWoundType() { return WoundType.fromId(this.entityData.get(DATA_WOUND_TYPE)); }
    public void setWoundType(WoundType t) { this.entityData.set(DATA_WOUND_TYPE, t.getId()); this.addTag(t.getTag()); }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("VetHealed", this.isHealed());
        tag.putInt("VetWound", getWoundType().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("VetHealed")) {
            boolean h = tag.getBoolean("VetHealed");
            // On ne peut pas appeler setHealed directement côté NBT sans synched, on set la data
            this.entityData.set(DATA_HEALED, h);
            if (h) {
                this.setCustomName(Component.literal("§a❤ Loup Soigné"));
            }
        }
        if (tag.contains("VetWound")) this.entityData.set(DATA_WOUND_TYPE, tag.getInt("VetWound"));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Laisse le système de soin via nos items gérer (Scalpel/Suture) - mais on ajoute un feedback
        if (!this.level().isClientSide && this.isHealed() && !this.isTame() && stack.isEmpty()) {
            // Caresse -> léchouille
            player.displayClientMessage(Component.literal("§7Le loup soigné remue la queue... Utilise un os pour l'apprivoiser définitivement !"), true);
            this.playSound(SoundEvents.WOLF_WHINE, 1.0F, 1.2F);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 0.9F);
    }

    @Nullable
    @Override
    public Wolf getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        // Si un des parents est wounded, le bébé a 20% de chance d'être wounded aussi
        WoundedWolfEntity baby = new WoundedWolfEntity(com.veterinarium.registry.ModEntities.WOUNDED_WOLF.get(), level);
        if (this.random.nextFloat() < 0.2f) {
            // reste blessé
        } else {
            baby.setHealed(true);
            baby.setCustomName(Component.literal("§7Louveteau"));
        }
        return baby;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount % 60 == 0 && !this.isHealed() && this.getHealth() < this.getMaxHealth() * 0.5f) {
            // Gémit toutes les 3s si très blessé
            if (this.random.nextFloat() < 0.3f) {
                this.playSound(SoundEvents.WOLF_WHINE, 0.8F, 0.8F);
            }
        }
    }
}
