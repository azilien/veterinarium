package com.veterinarium.entity;

import com.veterinarium.wound.WoundType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Shared logic for all Wounded* entities.
 * Each entity holds DATA_HEALED + DATA_WOUND_TYPE and delegates to this helper.
 */
public final class WoundedCreatureHelper {
    private WoundedCreatureHelper() {}

    public static void defineSynchedData(SynchedEntityData.Builder builder, EntityDataAccessor<Boolean> healed, EntityDataAccessor<Integer> wound) {
        builder.define(healed, false);
        builder.define(wound, 0);
    }

    public static void initWounded(LivingEntity self, EntityDataAccessor<Boolean> healed, EntityDataAccessor<Integer> wound, String displayName) {
        self.setCustomName(Component.literal("§c☠ " + displayName));
        self.setCustomNameVisible(true);
        if (self instanceof Mob mob) mob.setPersistenceRequired();
        self.getEntityData().set(wound, WoundType.random(self.getRandom()).getId());
        self.addTag("veterinarium_wounded");
        self.addTag("veterinarium_needs_scalpel");
    }

    public static boolean isHealed(LivingEntity self, EntityDataAccessor<Boolean> healed) {
        return self.getEntityData().get(healed);
    }

    public static void setHealed(LivingEntity self, EntityDataAccessor<Boolean> healed, EntityDataAccessor<Integer> wound, boolean h, String healedName, double normalSpeed) {
        self.getEntityData().set(healed, h);
        if (h) {
            self.removeTag("veterinarium_wounded");
            self.removeTag("veterinarium_needs_scalpel");
            self.addTag("veterinarium_healed");
            self.addTag("veterinarium_operated");
            self.setCustomName(Component.literal("§a❤ " + healedName));
            self.setCustomNameVisible(true);
            self.setHealth(self.getMaxHealth());
            self.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(normalSpeed);
        }
    }

    public static WoundType getWoundType(LivingEntity self, EntityDataAccessor<Integer> wound) {
        return WoundType.fromId(self.getEntityData().get(wound));
    }

    public static void setWoundType(LivingEntity self, EntityDataAccessor<Integer> wound, WoundType t) {
        self.getEntityData().set(wound, t.getId());
        self.addTag(t.getTag());
    }

    public static void save(CompoundTag tag, LivingEntity self, EntityDataAccessor<Boolean> healed, EntityDataAccessor<Integer> wound) {
        tag.putBoolean("VetHealed", self.getEntityData().get(healed));
        tag.putInt("VetWound", self.getEntityData().get(wound));
    }

    public static void load(CompoundTag tag, LivingEntity self, EntityDataAccessor<Boolean> healed, EntityDataAccessor<Integer> wound) {
        if (tag.contains("VetHealed")) {
            boolean h = tag.getBoolean("VetHealed");
            self.getEntityData().set(healed, h);
        }
        if (tag.contains("VetWound")) self.getEntityData().set(wound, tag.getInt("VetWound"));
    }
}
