package com.veterinarium.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class MedicalFileItem extends Item {
    public MedicalFileItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.displayClientMessage(Component.literal("§6═══════ §lDOSSIER MÉDICAL §r§6═══════"), false);
            player.displayClientMessage(Component.literal("§7Patients soignés dans ce monde :"), false);
            player.displayClientMessage(Component.literal(" §8- §fUtilise Seringue sur une créature pour diagnostic"), false);
            player.displayClientMessage(Component.literal(" §8- §fScalpel (opère) -> Kit de Suture (soigne + tame)"), false);
            player.displayClientMessage(Component.literal(" §8- §fTags: veterinarium_healed / operated / sutured"), false);
            player.displayClientMessage(Component.literal("§eProchaine MAJ: Bestiaire complet avec pathologies, élevege, mutations DarkGod."), false);
            level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6Bestiaire Médical - Dossier patient"));
        tooltip.add(Component.literal("§8Clic droit -> ouvre le dossier"));
        tooltip.add(Component.literal("§7Contient l'historique de tes soins"));
    }
}
