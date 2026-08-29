package com.veterinarium.item;

import com.veterinarium.wound.WoundType;
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

    private WoundType getWound(LivingEntity target) {
        if (target instanceof com.veterinarium.entity.WoundedWolfEntity w) return w.getWoundType();
        if (target instanceof com.veterinarium.entity.WoundedCatEntity c) return c.getWoundType();
        if (target instanceof com.veterinarium.entity.WoundedHorseEntity h) return h.getWoundType();
        if (target instanceof com.veterinarium.entity.WoundedFoxEntity f) return f.getWoundType();
        if (target instanceof com.veterinarium.entity.WoundedVillagerEntity v) return v.getWoundType();
        if (target instanceof com.veterinarium.entity.WoundedDrakeEntity d) return d.getWoundType();
        if (target.getPersistentData().contains("VetWound")) return WoundType.fromId(target.getPersistentData().getInt("VetWound"));
        if (target.getTags().contains("veterinarium_wound_hemorragie")) return WoundType.HEMORRAGIE;
        if (target.getTags().contains("veterinarium_wound_fracture")) return WoundType.FRACTURE;
        if (target.getTags().contains("veterinarium_wound_infection")) return WoundType.INFECTION;
        if (target.getTags().contains("veterinarium_wound_brulure")) return WoundType.BRULURE;
        if (target.getTags().contains("veterinarium_wounded")) return WoundType.CONTUSION;
        return WoundType.CONTUSION;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (!level.isClientSide) {
            float hpPct = (target.getHealth() / target.getMaxHealth()) * 100f;
            String statusKey = hpPct > 90 ? "message.veterinarium.syringe.status.good" : hpPct > 50 ? "message.veterinarium.syringe.status.wounded" : hpPct > 0 ? "message.veterinarium.syringe.status.critical" : "message.veterinarium.syringe.status.dead";
            String status = Component.translatable(statusKey).getString();
            WoundType wt = getWound(target);
            
            player.displayClientMessage(Component.translatable("message.veterinarium.syringe.diagnostic.header", target.getName().getString()), false);
            player.displayClientMessage(Component.translatable("message.veterinarium.syringe.diagnostic.hp", String.format("%.1f", target.getHealth()), String.format("%.1f", target.getMaxHealth()), hpPct, status), false);
            if (target.getTags().contains("veterinarium_wounded") || target instanceof com.veterinarium.entity.WoundedWolfEntity || target instanceof com.veterinarium.entity.WoundedCatEntity || target instanceof com.veterinarium.entity.WoundedHorseEntity || target instanceof com.veterinarium.entity.WoundedFoxEntity || target instanceof com.veterinarium.entity.WoundedVillagerEntity) {
                player.displayClientMessage(Component.translatable("message.veterinarium.syringe.diagnostic.wound", wt.getDisplay(), wt.getTag().replace("veterinarium_wound_","")), false);
                player.displayClientMessage(wt.getDescription(), false);
                String reqKey;
                if (wt.needsAnesthetic() && wt.needsBandage()) reqKey = "message.veterinarium.syringe.diagnostic.requires_both";
                else if (wt.needsAnesthetic()) reqKey = "message.veterinarium.syringe.diagnostic.requires_anesthetic";
                else if (wt.needsBandage()) reqKey = "message.veterinarium.syringe.diagnostic.requires_bandage";
                else reqKey = "message.veterinarium.syringe.diagnostic.requires_none";
                player.displayClientMessage(Component.translatable(reqKey).append(Component.translatable("message.veterinarium.syringe.diagnostic.see_analysis")), false);
                // sauve le dernier diagnostic pour la Table d'Analyse
                player.getPersistentData().putInt("VetLastWound", wt.getId());
                player.getPersistentData().putString("VetLastTarget", target.getName().getString());
            } else {
                player.displayClientMessage(Component.literal(" §7Tags: ").append(Component.translatable(target.getTags().contains("veterinarium_healed") ? "message.veterinarium.syringe.tags.healed" : "message.veterinarium.syringe.tags.not_healed")).append(Component.translatable(target.getTags().contains("veterinarium_operated") ? "message.veterinarium.syringe.tags.operated" : "message.veterinarium.syringe.tags.not_operated")), false);
            }

            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
            level.playSound(null, target.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0f, 1.3f);
            // Anesthésie générale si table à proximité avec stock
            boolean hasTableAnesthetic = false;
            for (int dx=-5;dx<=5 && !hasTableAnesthetic;dx++)
                for (int dy=-2;dy<=2 && !hasTableAnesthetic;dy++)
                    for (int dz=-5;dz<=5 && !hasTableAnesthetic;dz++) {
                        var be = level.getBlockEntity(target.blockPosition().offset(dx,dy,dz));
                        if (be instanceof com.veterinarium.block.entity.OperatingTableBlockEntity table) {
                            for (int i=0;i<table.getHandler().getSlots();i++) {
                                var s = table.getHandler().getStackInSlot(i);
                                if (s.is(com.veterinarium.registry.ModItems.ANESTHETIC.get()) && s.getCount()>0) { hasTableAnesthetic = true; break; }
                            }
                        }
                    }
            if (hasTableAnesthetic) {
                // Anesthésie générale: endormissement profond 10s
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 3));
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
                target.addTag("veterinarium_anesthetized");
                target.getPersistentData().putLong("VetAnesthesiaExpiry", level.getGameTime() + 200);
                target.setCustomName(net.minecraft.network.chat.Component.literal("§dEndormi..."));
                target.setCustomNameVisible(true);
                level.playSound(null, target.blockPosition(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 0.6f, 0.5f);
                player.displayClientMessage(Component.literal("§d[Anesthésie Générale] §aCréature endormie 10s — la table a fourni l'anesthésiant."), false);
            } else {
                player.displayClientMessage(Component.translatable("message.veterinarium.syringe.anesthesia_injected"), true);
            }
            try { com.veterinarium.data.BestiaryProgress.recordDiagnose(player, target, wt); } catch (Exception ignored) {}

            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.hurtAndBreak(1, player, slot);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("message.veterinarium.syringe.tooltip.description"));
        tooltip.add(Component.translatable("message.veterinarium.syringe.tooltip.usage"));
        tooltip.add(Component.translatable("message.veterinarium.syringe.tooltip.effect"));
    }
}
