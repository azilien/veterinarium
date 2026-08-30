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
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WoundedHorseEntity extends Horse {
    private static final EntityDataAccessor<Boolean> DATA_HEALED = SynchedEntityData.defineId(WoundedHorseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_WOUND_TYPE = SynchedEntityData.defineId(WoundedHorseEntity.class, EntityDataSerializers.INT);

    public WoundedHorseEntity(EntityType<? extends Horse> type, Level level) {
        super(type, level);
        WoundedCreatureHelper.initWounded(this, DATA_HEALED, DATA_WOUND_TYPE, "Cheval Blessé");
        this.setTamed(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Horse.createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.JUMP_STRENGTH, 0.5D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        WoundedCreatureHelper.defineSynchedData(builder, DATA_HEALED, DATA_WOUND_TYPE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.25D, stack -> stack.is(net.minecraft.world.item.Items.GOLDEN_CARROT), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public boolean isHealed() { return WoundedCreatureHelper.isHealed(this, DATA_HEALED); }
    public void setHealed(boolean h) { WoundedCreatureHelper.setHealed(this, DATA_HEALED, DATA_WOUND_TYPE, h, "Cheval Soigné", 0.2D); }
    public WoundType getWoundType() { return WoundedCreatureHelper.getWoundType(this, DATA_WOUND_TYPE); }
    public void setWoundType(WoundType t) { WoundedCreatureHelper.setWoundType(this, DATA_WOUND_TYPE, t); }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        WoundedCreatureHelper.save(tag, this, DATA_HEALED, DATA_WOUND_TYPE);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        WoundedCreatureHelper.load(tag, this, DATA_HEALED, DATA_WOUND_TYPE);
    }

    @Override protected SoundEvent getAmbientSound() { return isHealed() ? SoundEvents.HORSE_AMBIENT : SoundEvents.HORSE_HURT; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.HORSE_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.HORSE_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.HORSE_STEP, 0.15F, 1.0F); }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && isHealed() && !this.isTamed() && player.getItemInHand(hand).isEmpty()) {
            player.displayClientMessage(Component.literal("§7Le cheval soigné hennit... Utilise une pomme dorée pour l'apprivoiser !"), true);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        WoundedHorseEntity baby = new WoundedHorseEntity(com.veterinarium.registry.ModEntities.WOUNDED_HORSE.get(), level);
        if (this.random.nextFloat() >= 0.3f) baby.setHealed(true);
        return baby;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount % 80 == 0 && !isHealed() && this.getHealth() < this.getMaxHealth()*0.5f && this.random.nextFloat()<0.25f) {
            this.playSound(SoundEvents.HORSE_HURT, 0.6F, 0.9F);
        }
    }
}
