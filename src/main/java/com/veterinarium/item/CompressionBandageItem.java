package com.veterinarium.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class CompressionBandageItem extends Item {
    public CompressionBandageItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);

        float hpPct = target.getHealth() / target.getMaxHealth();
        if (hpPct >= 0.5f) {
            player.displayClientMessage(Component.translatable("message.veterinarium.compression_bandage.not_needed", target.getName().getString()), true);
            return InteractionResult.PASS;
        }

        // soin rapide: +4❤ en 2s via Regen
        target.heal(4.0f);
        target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0));
        target.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        level.playSound(null, target.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0f, 1.2f);
        player.displayClientMessage(Component.translatable("message.veterinarium.compression_bandage.applied", target.getName().getString()), false);

        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("message.veterinarium.compression_bandage.tooltip.description"));
        tooltip.add(Component.translatable("message.veterinarium.compression_bandage.tooltip.usage"));
        tooltip.add(Component.translatable("message.veterinarium.compression_bandage.tooltip.limit"));
    }
}
