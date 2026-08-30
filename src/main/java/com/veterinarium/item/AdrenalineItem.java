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

public class AdrenalineItem extends Item {
    public AdrenalineItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide) {
            target.heal(4.0f);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));
            target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0));
            target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 2));
            stack.shrink(1);
            player.playSound(SoundEvents.GENERIC_DRINK, 1.0f, 1.5f);
            player.displayClientMessage(Component.translatable("message.veterinarium.adrenaline.used"), false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.veterinarium.adrenaline.tooltip"));
    }
}
