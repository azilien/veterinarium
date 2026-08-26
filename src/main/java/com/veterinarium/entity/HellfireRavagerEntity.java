package com.veterinarium.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HellfireRavagerEntity extends Wolf {
    public HellfireRavagerEntity(EntityType<? extends Wolf> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§6☢ Hellfire Ravager"));
        this.setCustomNameVisible(true);
        this.setPersistenceRequired();
        this.addTag("veterinarium_mutated");
        this.addTag("veterinarium_healed");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.1D, 10.0F, 2.0F));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.monster.Monster.class, true));
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean ok = super.doHurtTarget(target);
        if (ok && target instanceof net.minecraft.world.entity.LivingEntity le) {
            le.setRemainingFireTicks(80); // 4s feu
            le.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
        }
        return ok;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.tickCount % 20 == 0 && this.isAlive()) {
                // aura feu : légère régé auto + particules
                if (this.getHealth() < this.getMaxHealth()) this.heal(0.5f);
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY()+0.8, this.getZ(), 1, 0.2, 0.2, 0.2, 0.02);
                    if (this.random.nextFloat() < 0.3f) sl.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY()+1.0, this.getZ(), 1, 0.1, 0.1, 0.1, 0.1);
                }
            }
            if (this.isInLava() || this.isOnFire()) {
                this.clearFire();
                this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0));
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // tame via os ou viande
        if (!this.isTame() && (stack.is(Items.BONE) || stack.is(Items.COOKED_BEEF) || stack.is(Items.BLAZE_POWDER))) {
            if (!player.getAbilities().instabuild) stack.shrink(1);
            if (this.random.nextFloat() < 0.5f) {
                this.tame(player);
                this.setOrderedToSit(false);
                this.level().broadcastEntityEvent(this, (byte)7);
                player.displayClientMessage(Component.literal("§6★ Hellfire Ravager apprivoisé ! §7Il te suivra en enfer."), false);
            } else {
                this.level().broadcastEntityEvent(this, (byte)6);
                player.displayClientMessage(Component.literal("§7Le Ravager gronde... réessaie (50% tame)"), true);
            }
            return InteractionResult.SUCCESS;
        }
        if (this.isTame() && this.isOwnedBy(player) && stack.isEmpty() && player.isShiftKeyDown()) {
            this.setOrderedToSit(!this.isOrderedToSit());
            player.displayClientMessage(Component.literal(this.isOrderedToSit() ? "§7Ravager: §eAssis" : "§7Ravager: §aSuit le maître"), true);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.BLAZE_AMBIENT; }
    @Override
    protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.WOLF_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.WOLF_DEATH; }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F); }

    @Nullable
    @Override
    public Wolf getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        // 50% Hellfire, 50% wounded wolf
        if (this.random.nextFloat() < 0.5f) {
            HellfireRavagerEntity baby = new HellfireRavagerEntity(com.veterinarium.registry.ModEntities.HELLFIRE_RAVAGER.get(), level);
            baby.setCustomName(Component.literal("§eHellpup"));
            baby.setCustomNameVisible(true);
            return baby;
        } else {
            WoundedWolfEntity baby = new WoundedWolfEntity(com.veterinarium.registry.ModEntities.WOUNDED_WOLF.get(), level);
            baby.setHealed(true);
            return baby;
        }
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        // pas de data sup
    }
}
