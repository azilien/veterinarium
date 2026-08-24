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
        if (level.isClientSide) {
            // Ouverture GUI côté client via reflection pour éviter le chargement côté serveur (DEDICATED_SERVER)
            try {
                Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
                Object mc = mcClass.getMethod("getInstance").invoke(null);
                Class<?> screenClass = Class.forName("com.veterinarium.client.MedicalFileScreen");
                Object screen = screenClass.getDeclaredConstructor().newInstance();
                Class<?> screenBase = Class.forName("net.minecraft.client.gui.screens.Screen");
                mcClass.getMethod("setScreen", screenBase).invoke(mc, screen);
            } catch (Exception e) {
                // Fallback: message si GUI échoue
                player.displayClientMessage(Component.literal("§c[Erreur GUI] " + e.getMessage()), false);
            }
            level.playSound(player, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);
        } else {
            player.displayClientMessage(Component.literal("§6[Dossier Médical] §7Ouverture du dossier... (GUI côté client)"), false);
            player.displayClientMessage(Component.literal("§7Utilise Seringue→Scalpel→Suture. Infirmerie = heal de zone."), false);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6Bestiaire Médical - Dossier patient"));
        tooltip.add(Component.literal("§8Clic droit -> ouvre le dossier (GUI)"));
        tooltip.add(Component.literal("§7Contient l'historique de tes soins"));
    }
}
