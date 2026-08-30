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
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WoundedSheepEntity extends Sheep {
    private static final EntityDataAccessor<Boolean> DATA_HEALED = SynchedEntityData.defineId(WoundedSheepEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_WOUND_TYPE = SynchedEntityData.defineId(WoundedSheepEntity.class, EntityDataSerializers.INT);

    public WoundedSheepEntity(EntityType<? extends Sheep> type, Level level) {
        super(type, level);
        WoundedCreatureHelper.initWounded(this, DATA_HEALED, DATA_WOUND_TYPE, "Mouton Blessé");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Sheep.createAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        WoundedCreatureHelper.defineSynchedData(builder, DATA_HEALED, DATA_WOUND_TYPE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    public boolean isHealed() { return WoundedCreatureHelper.isHealed(this, DATA_HEALED); }
    public void setHealed(boolean h) { WoundedCreatureHelper.setHealed(this, DATA_HEALED, DATA_WOUND_TYPE, h, "Mouton Soigné", 0.4D); }
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

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.level().isClientSide && this.isHealed() && stack.is(Items.SHEARS) && !this.isSheared()) {
            this.setSheared(true);
            int woolCount = 1 + this.random.nextInt(3);
            player.setItemInHand(hand, new net.minecraft.world.item.ItemStack(Items.WHITE_WOOL, woolCount));
            player.displayClientMessage(Component.translatable("message.veterinarium.sheep.sheared"), false);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SHEEP_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SHEEP_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F); }

    @Nullable
    @Override
    public Sheep getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        WoundedSheepEntity baby = new WoundedSheepEntity(com.veterinarium.registry.ModEntities.WOUNDED_SHEEP.get(), level);
        if (this.random.nextFloat() >= 0.3f) baby.setHealed(true);
        return baby;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount % 80 == 0 && !isHealed() && this.getHealth() < this.getMaxHealth() * 0.5f) {
            this.playSound(SoundEvents.SHEEP_AMBIENT, 0.6F, 0.7F);
        }
    }
}
