package com.veterinarium.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class AntidoteItem extends Item {
    public AntidoteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (target instanceof ZombieVillager zv && zv.getTags().contains("veterinarium_urgent")) {
            if (!player.getAbilities().instabuild) stack.shrink(1);

            double x = zv.getX(), y = zv.getY(), z = zv.getZ();
            float yRot = zv.getYRot();
            zv.discard();

            Villager cured = new Villager(net.minecraft.world.entity.EntityType.VILLAGER, level);
            cured.moveTo(x, y, z, yRot, 0);
            cured.setCustomName(Component.literal("§a❤ Villageois Guéri"));
            cured.setCustomNameVisible(true);
            cured.setPersistenceRequired();
            cured.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 200, 2));
            level.addFreshEntity(cured);

            if (level instanceof ServerLevel sl) {
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION, x, y+1, z, 5, 0.3, 0.3, 0.3, 0.1);
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, x, y+1.5, z, 10, 0.5, 0.5, 0.5, 0.2);
            }
            level.playSound(null, cured.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1.0f, 0.8f);

            player.displayClientMessage(Component.literal("§a[Antidote] §7Villageois guéri ! Il se souviendra de ton aide."), false);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("message.veterinarium.antidote.tooltip"));
    }
}
