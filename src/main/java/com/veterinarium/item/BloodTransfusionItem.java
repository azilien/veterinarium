package com.veterinarium.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class BloodTransfusionItem extends Item {
    public BloodTransfusionItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide) {
            target.heal(6.0f);
            target.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 1));
            target.removeEffect(net.minecraft.world.effect.MobEffects.WITHER);
            stack.shrink(1);
            player.playSound(SoundEvents.GENERIC_DRINK, 1.0f, 0.8f);
            player.displayClientMessage(Component.translatable("message.veterinarium.blood_transfusion.used"), false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.veterinarium.blood_transfusion.tooltip"));
    }
}
