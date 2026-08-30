package com.veterinarium.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

public class AntibioticItem extends Item {
    public AntibioticItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide) {
            target.removeEffect(net.minecraft.world.effect.MobEffects.POISON);
            target.heal(3.0f);
            target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
            stack.shrink(1);
            player.playSound(SoundEvents.GENERIC_DRINK, 1.0f, 1.2f);
            player.displayClientMessage(Component.translatable("message.veterinarium.antibiotic.used"), false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.veterinarium.antibiotic.tooltip"));
    }
}
