package com.veterinarium.entity;

import com.veterinarium.wound.WoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WoundedDrakeEntity extends Phantom {
    private static final EntityDataAccessor<Boolean> DATA_HEALED = SynchedEntityData.defineId(WoundedDrakeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_WOUND = SynchedEntityData.defineId(WoundedDrakeEntity.class, EntityDataSerializers.INT);

    public WoundedDrakeEntity(EntityType<? extends Phantom> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("§c☠ Drake Blessé"));
        this.setCustomNameVisible(true);
        this.setPersistenceRequired();
        this.setWoundType(WoundType.random(this.random));
        this.addTag("veterinarium_wounded");
        this.addTag("veterinarium_needs_scalpel");
        this.addTag("veterinarium_boss");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FLYING_SPEED, 0.4D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HEALED, false);
        builder.define(DATA_WOUND, 0);
    }

    public boolean isHealed() { return this.entityData.get(DATA_HEALED); }
    public void setHealed(boolean h) {
        this.entityData.set(DATA_HEALED, h);
        if (h) {
            this.removeTag("veterinarium_wounded");
            this.removeTag("veterinarium_needs_scalpel");
            this.addTag("veterinarium_healed");
            this.addTag("veterinarium_operated");
            this.setCustomName(Component.literal("§6☢ Drake Soigné §7- Boss"));
            this.setCustomNameVisible(true);
            this.setHealth(this.getMaxHealth());
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        }
    }
    public WoundType getWoundType() { return WoundType.fromId(this.entityData.get(DATA_WOUND)); }
    public void setWoundType(WoundType t) { this.entityData.set(DATA_WOUND, t.getId()); this.addTag(t.getTag()); }

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
        if (tag.contains("VetWound")) this.entityData.set(DATA_WOUND, tag.getInt("VetWound"));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance diff, MobSpawnType reason, @Nullable SpawnGroupData data) {
        // force blessure
        return super.finalizeSpawn(level, diff, reason, data);
    }

    @Override
    protected SoundEvent getAmbientSound() { return isHealed() ? SoundEvents.ENDER_DRAGON_AMBIENT : SoundEvents.PHANTOM_AMBIENT; }
    @Override
    protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.ENDER_DRAGON_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.ENDER_DRAGON_DEATH; }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.PHANTOM_FLAP, 0.3F, 0.8F); }

    @Override
    public void tick() {
        super.tick();
        // Phantom a un phasing, on le nerf si blessé (plus lent, tombe)
        if (!this.level().isClientSide && !isHealed()) {
            if (this.tickCount % 80 == 0 && this.random.nextFloat()<0.3f) {
                this.playSound(SoundEvents.PHANTOM_HURT, 0.8f, 0.7f);
            }
            // léger feu si brulure
            if (getWoundType() == WoundType.BRULURE && this.tickCount % 40 == 0) {
                this.setRemainingFireTicks(40);
            }
        } else if (isHealed() && this.tickCount % 60 == 0) {
            // regen aura boss
            if (this.getHealth() < this.getMaxHealth()) this.heal(1.0f);
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME, this.getX(), this.getY()+1.5, this.getZ(), 2, 0.3,0.3,0.3,0.05);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // si soigné et joueur a sphère vide, laisse VetSphere s'en charger (priorité sphere)
        // sinon interaction vide donne info
        if (!this.level().isClientSide && isHealed() && player.getItemInHand(hand).isEmpty()) {
            player.displayClientMessage(Component.literal("§6Le Drake soigné plane au-dessus de toi... Utilise §aSphère Vétérinaire §7pour le capturer !"), true);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    // Boss : immunité chute, large hitbox déjà via EntityType
    @Override
    public boolean causeFallDamage(float d, float m, DamageSource s) { return false; }
}
