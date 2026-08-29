package com.veterinarium.item;

import com.veterinarium.registry.ModItems;
import com.veterinarium.wound.WoundType;
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

public class DnaSyringeItem extends Item {
    public DnaSyringeItem(Properties props) { super(props); }

    private WoundType getWound(LivingEntity t) {
        if (t instanceof com.veterinarium.entity.WoundedWolfEntity w) return w.getWoundType();
        if (t instanceof com.veterinarium.entity.WoundedCatEntity c) return c.getWoundType();
        if (t instanceof com.veterinarium.entity.WoundedHorseEntity h) return h.getWoundType();
        if (t instanceof com.veterinarium.entity.WoundedFoxEntity f) return f.getWoundType();
        if (t instanceof com.veterinarium.entity.WoundedVillagerEntity v) return v.getWoundType();
        if (t instanceof com.veterinarium.entity.WoundedDrakeEntity d) return d.getWoundType();
        if (t.getPersistentData().contains("VetWound")) return WoundType.fromId(t.getPersistentData().getInt("VetWound"));
        if (t.getTags().contains("veterinarium_wound_brulure")) return WoundType.BRULURE;
        if (t.getTags().contains("veterinarium_wound_hemorragie")) return WoundType.HEMORRAGIE;
        if (t.getTags().contains("veterinarium_wound_fracture")) return WoundType.FRACTURE;
        if (t.getTags().contains("veterinarium_wound_infection")) return WoundType.INFECTION;
        return WoundType.CONTUSION;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (level.isClientSide) return InteractionResult.sidedSuccess(level.isClientSide);
        // Ne marche que sur blessé vivant
        boolean isWounded = target.getTags().contains("veterinarium_wounded")
                || target instanceof com.veterinarium.entity.WoundedWolfEntity w && !w.isHealed()
                || target instanceof com.veterinarium.entity.WoundedCatEntity c && !c.isHealed()
                || target instanceof com.veterinarium.entity.WoundedHorseEntity h && !h.isHealed()
                || target instanceof com.veterinarium.entity.WoundedFoxEntity f && !f.isHealed()
                || target instanceof com.veterinarium.entity.WoundedVillagerEntity v && !v.isHealed()
                || target.getPersistentData().contains("VetWound")
                || target.getHealth() < target.getMaxHealth()*0.7f;

        if (!isWounded) {
            player.displayClientMessage(Component.translatable("message.veterinarium.dna_syringe.not_wounded", target.getName().getString()), true);
            return InteractionResult.PASS;
        }
        WoundType wt = getWound(target);
        // check si déjà rempli? Ce item est vide, on va donner le rempli
        ItemStack filled = new ItemStack(ModItems.DNA_SYRINGE_FILLED.get());
        // petit effet
        target.hurt(level.damageSources().magic(), 1.0f);
        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 100, 0));
        level.playSound(null, target.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0f, 0.8f);
        player.displayClientMessage(Component.translatable("message.veterinarium.dna_syringe.dna_collected", wt.getDisplay(), target.getName().getString()), false);
        player.displayClientMessage(Component.translatable("message.veterinarium.dna_syringe.hint"), false);
        // bestiaire
        try { com.veterinarium.data.BestiaryProgress.recordDiagnose(player, target, wt); } catch (Exception ignored) {}
        // consomme 1 vide et donne remplie
        stack.shrink(1);
        if (stack.isEmpty()) {
            player.setItemInHand(hand, filled);
        } else {
            if (!player.addItem(filled)) player.drop(filled, false);
        }
        // usure? si stack restant on peut hurt?
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("message.veterinarium.dna_syringe.tooltip.description"));
        tooltip.add(Component.translatable("message.veterinarium.dna_syringe.tooltip.usage"));
        tooltip.add(Component.translatable("message.veterinarium.dna_syringe.tooltip.result"));
        tooltip.add(Component.translatable("message.veterinarium.dna_syringe.tooltip.craft_hint"));
    }
}
