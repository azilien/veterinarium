package com.veterinarium.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
    private int killCount = 0;
    private String mutationType = "fire"; // fire, acid, shadow

    // Evolution thresholds
    private static final int EVOLVE_ACID = 10;
    private static final int EVOLVE_SHADOW = 25;
    private static final int MAX_EVOLUTION = 50;

    public HellfireRavagerEntity(EntityType<? extends Wolf> type, Level level) {
        super(type, level);
        this.setCustomName(getMutationDisplayName());
        this.setCustomNameVisible(true);
        this.setPersistenceRequired();
        this.addTag("veterinarium_mutated");
        this.addTag("veterinarium_healed");
    }

    private Component getMutationDisplayName() {
        return switch (mutationType) {
            case "acid" -> Component.literal("§2☢ Hellfire Acid");
            case "shadow" -> Component.literal("§5☢ Hellfire Shadow");
            default -> Component.literal("§6☢ Hellfire Ravager");
        };
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
            // Effet selon le type de mutation
            switch (mutationType) {
                case "acid" -> {
                    le.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1)); // Poison II 5s
                    le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1)); // Slowness II 3s
                    if (this.level() instanceof ServerLevel sl) {
                        sl.sendParticles(ParticleTypes.ITEM_SLIME, le.getX(), le.getY()+1.0, le.getZ(), 5, 0.3,0.3,0.3, 0.1);
                    }
                }
                case "shadow" -> {
                    le.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0)); // Blindness 2s
                    le.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0)); // Wither 4s
                    le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0)); // Speed to self
                    this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0));
                    if (this.level() instanceof ServerLevel sl) {
                        sl.sendParticles(ParticleTypes.SCULK_SOUL, le.getX(), le.getY()+1.0, le.getZ(), 5, 0.3,0.3,0.3, 0.1);
                    }
                }
                default -> { // fire
                    le.setRemainingFireTicks(80); // 4s feu
                    le.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
                }
            }
            // Kill counter
            if (!le.isAlive()) {
                onKill();
            }
        }
        return ok;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.tickCount % 20 == 0 && this.isAlive()) {
                // Aura selon type
                switch (mutationType) {
                    case "acid" -> {
                        if (this.getHealth() < this.getMaxHealth()) this.heal(0.5f);
                        if (this.level() instanceof ServerLevel sl) {
                            sl.sendParticles(ParticleTypes.ITEM_SLIME, this.getX(), this.getY()+0.8, this.getZ(), 1, 0.2, 0.2, 0.2, 0.02);
                        }
                    }
                    case "shadow" -> {
                        if (this.getHealth() < this.getMaxHealth()) this.heal(0.3f);
                        if (this.level() instanceof ServerLevel sl) {
                            sl.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getY()+0.8, this.getZ(), 1, 0.2, 0.2, 0.2, 0.02);
                        }
                        // Invisibility toggle when low HP
                        if (this.getHealth() < this.getMaxHealth() * 0.3f) {
                            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40, 0));
                        }
                    }
                    default -> { // fire
                        if (this.getHealth() < this.getMaxHealth()) this.heal(0.5f);
                        if (this.level() instanceof ServerLevel sl) {
                            sl.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY()+0.8, this.getZ(), 1, 0.2, 0.2, 0.2, 0.02);
                            if (this.random.nextFloat() < 0.3f) sl.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY()+1.0, this.getZ(), 1, 0.1, 0.1, 0.1, 0.1);
                        }
                    }
                }
            }
            if (this.isInLava() || this.isOnFire()) {
                this.clearFire();
                this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0));
            }
        }
    }

    public void onKill() {
        killCount++;
        // Evolution automatique
        if (killCount == EVOLVE_ACID && "fire".equals(mutationType)) {
            mutateTo("acid");
        } else if (killCount >= EVOLVE_SHADOW && "fire".equals(mutationType)) {
            mutateTo("shadow");
        } else if (killCount >= EVOLVE_SHADOW && "acid".equals(mutationType)) {
            mutateTo("shadow");
        }
        // Stats boost every 10 kills
        if (killCount % 10 == 0) {
            var attrs = this.getAttributes();
            var maxHp = attrs.getInstance(Attributes.MAX_HEALTH);
            var dmg = attrs.getInstance(Attributes.ATTACK_DAMAGE);
            if (maxHp != null) maxHp.setBaseValue(maxHp.getBaseValue() + 2.0);
            if (dmg != null) dmg.setBaseValue(dmg.getBaseValue() + 1.0);
            this.heal(this.getMaxHealth());
        }
    }

    private void mutateTo(String type) {
        this.mutationType = type;
        this.setCustomName(getMutationDisplayName());
        this.setCustomNameVisible(true);
        // Heal fully on evolution
        this.heal(this.getMaxHealth());
        // Effects
        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0));
        // Sound + particles
        this.playSound(com.veterinarium.registry.ModSounds.MUTATION.get(), 1.0f, 0.8f);
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY()+1.0, this.getZ(), 1, 0.5, 0.5, 0.5, 0.1);
        }
        // Notify nearby players
        for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(16))) {
            p.displayClientMessage(Component.literal("§6★ Hellfire Ravager §7évolue en §" + switch(type) {
                case "acid" -> "2Acid";
                case "shadow" -> "5Shadow";
                default -> "6Fire";
            } + " §7(kills: " + killCount + ")"), false);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Tame via bone/beef/blaze powder
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
        // Info with bone meal
        if (this.isOwnedBy(player) && stack.is(Items.BONE_MEAL)) {
            player.displayClientMessage(Component.literal("§6[Ravager] §7Type: §" + switch(mutationType) {
                case "acid" -> "2Acid";
                case "shadow" -> "5Shadow";
                default -> "6Fire";
            } + " §7| Kills: §e" + killCount + " §7| Next evolve: §e" + (killCount < EVOLVE_ACID ? EVOLVE_ACID : killCount < EVOLVE_SHADOW ? EVOLVE_SHADOW : MAX_EVOLUTION)), false);
            return InteractionResult.SUCCESS;
        }
        // Sit with empty hand
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
        if (this.random.nextFloat() < 0.5f) {
            HellfireRavagerEntity baby = new HellfireRavagerEntity(com.veterinarium.registry.ModEntities.HELLFIRE_RAVAGER.get(), level);
            baby.mutationType = this.mutationType;
            baby.setCustomName(baby.getMutationDisplayName());
            baby.setCustomNameVisible(true);
            return baby;
        } else {
            WoundedWolfEntity baby = new WoundedWolfEntity(com.veterinarium.registry.ModEntities.WOUNDED_WOLF.get(), level);
            baby.setHealed(true);
            return baby;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("KillCount", killCount);
        tag.putString("MutationType", mutationType);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        killCount = tag.getInt("KillCount");
        mutationType = tag.contains("MutationType") ? tag.getString("MutationType") : "fire";
        setCustomName(getMutationDisplayName());
    }

    public int getKillCount() { return killCount; }
    public String getMutationType() { return mutationType; }
}
