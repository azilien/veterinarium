package com.veterinarium.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class StretcherItem extends BlockItem {
    public StretcherItem(Block block, Properties props) { super(block, props); }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (level.isClientSide) return InteractionResult.sidedSuccess(level.isClientSide);

        // Ne prend que blessé
        boolean isWounded = target.getTags().contains("veterinarium_wounded")
                || target instanceof com.veterinarium.entity.WoundedWolfEntity w && !w.isHealed()
                || target instanceof com.veterinarium.entity.WoundedCatEntity c && !c.isHealed()
                || target instanceof com.veterinarium.entity.WoundedHorseEntity h && !h.isHealed()
                || target instanceof com.veterinarium.entity.WoundedFoxEntity f && !f.isHealed()
                || target instanceof com.veterinarium.entity.WoundedVillagerEntity v && !v.isHealed()
                || target instanceof com.veterinarium.entity.WoundedDrakeEntity d && !d.isHealed()
                || target.getPersistentData().contains("VetWound");

        if (!isWounded) {
            player.displayClientMessage(Component.literal("§7[Brancard] §f" + target.getName().getString() + " n'est pas blessé."), true);
            return InteractionResult.PASS;
        }
        if (!player.getPassengers().isEmpty()) {
            player.displayClientMessage(Component.literal("§c[Brancard] §7Tu portes déjà quelqu'un ! Amène-le au Hut (sirène active)."), true);
            return InteractionResult.FAIL;
        }
        // Vérifie que le joueur n'est pas déjà en train de porter (tag)
        if (target.isPassenger()) {
            player.displayClientMessage(Component.literal("§7[Brancard] Cette créature est déjà transportée."), true);
            return InteractionResult.FAIL;
        }
        // Fait monter la cible sur le joueur (ambulance)
        target.startRiding(player, true);
        target.addTag("vet_on_stretcher");
        player.getPersistentData().putLong("VetAmbulanceStart", level.getGameTime());
        player.getPersistentData().putString("VetAmbulanceEntity", target.getName().getString());
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 6000, 0, false, false, true));
        level.playSound(null, target.blockPosition(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1.0f, 1.8f);
        level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.5f, 1.5f);
        player.displayClientMessage(Component.literal("§e🚑 [Brancard] §a" + target.getName().getString() + " §7chargé ! §eCours au Hut (<60s = bonus) — sirène active"), false);
        if (level instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.NOTE, player.getX(), player.getY()+1.5, player.getZ(), 5, 0.3,0.3,0.3,0.1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§fBrancard - Ambulance"));
        tooltip.add(Component.literal("§8Clic sur blessé → le porte (ralenti)"));
        tooltip.add(Component.literal("§8Amène au Hut <60s = bonus heal + émeraude"));
        tooltip.add(Component.literal("§7Sirène active pendant transport"));
    }
}
