package com.veterinarium.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class SyringeItem extends Item {
    public SyringeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (!level.isClientSide) {
            float hpPct = (target.getHealth() / target.getMaxHealth()) * 100f;
            String status = hpPct > 90 ? "§aBon état" : hpPct > 50 ? "§eBlessé" : hpPct > 0 ? "§cCritique" : "§4Mort";
            
            player.displayClientMessage(Component.literal("§b[Seringue] §fDiagnostic " + target.getName().getString() + " :"), false);
            player.displayClientMessage(Component.literal(" §7PV: " + String.format("%.1f/%.1f (%.0f%%) - %s", target.getHealth(), target.getMaxHealth(), hpPct, status)), false);
            player.displayClientMessage(Component.literal(" §7Tags: " + (target.getTags().contains("veterinarium_healed") ? "§aSoigné " : "§7Non soigné ") + (target.getTags().contains("veterinarium_operated") ? "§aOpéré" : "§7Non opéré")), false);

            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
            level.playSound(null, target.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0f, 1.3f);
            player.displayClientMessage(Component.literal("§7-> Anesthésie injectée (Lenteur 5s)"), true);

            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.hurtAndBreak(1, player, slot);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7Seringue diagnostique + anesthésiante"));
        tooltip.add(Component.literal("§8Clic droit sur créature -> diagnostic HP"));
        tooltip.add(Component.literal("§8-> Applique Lenteur 5s (anesthésie)"));
    }
}
