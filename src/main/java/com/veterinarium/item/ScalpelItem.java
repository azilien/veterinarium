package com.veterinarium.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ScalpelItem extends Item {
    public ScalpelItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (!level.isClientSide) {
            if (target.getHealth() < target.getMaxHealth()) {
                float heal = 2.0f;
                target.heal(heal);
                level.playSound(null, target.blockPosition(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 0.8f, 1.5f);
                player.displayClientMessage(Component.literal("§c[Scalpel] §aIncision précise -> §f" + target.getName().getString() + " §a+" + heal + " HP"), true);
                EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                stack.hurtAndBreak(1, player, slot);
                target.addTag("veterinarium_operated");
                return InteractionResult.SUCCESS;
            } else {
                player.displayClientMessage(Component.literal("§7[Scalpel] §f" + target.getName().getString() + " n'a pas besoin de chirurgie (PV max)"), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7Outil chirurgical de précision"));
        tooltip.add(Component.literal("§8Clic droit sur créature blessée -> soigne 1 coeur"));
        tooltip.add(Component.literal("§8Marque la créature comme 'opérée'"));
    }
}
