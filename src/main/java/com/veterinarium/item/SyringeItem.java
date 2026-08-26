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
            String status = hpPct > 90 ? "§aBon état" : hpPct > 50 ? "§eBlessé" : hpPct > 0 ? "§cCritique" : "§4Mort";
            WoundType wt = getWound(target);
            
            player.displayClientMessage(Component.literal("§b[Seringue] §fDiagnostic " + target.getName().getString() + " :"), false);
            player.displayClientMessage(Component.literal(" §7PV: " + String.format("%.1f/%.1f (%.0f%%) - %s", target.getHealth(), target.getMaxHealth(), hpPct, status)), false);
            if (target.getTags().contains("veterinarium_wounded") || target instanceof com.veterinarium.entity.WoundedWolfEntity || target instanceof com.veterinarium.entity.WoundedCatEntity || target instanceof com.veterinarium.entity.WoundedHorseEntity || target instanceof com.veterinarium.entity.WoundedFoxEntity || target instanceof com.veterinarium.entity.WoundedVillagerEntity) {
                player.displayClientMessage(Component.literal(" §c⚠ Blessure: " + wt.getDisplay() + " §7(" + wt.getTag().replace("veterinarium_wound_","") + ")"), false);
                player.displayClientMessage(wt.getDescription(), false);
                String req = " §7Requis: ";
                if (wt.needsAnesthetic()) req += "§dAnesthésiant §7";
                if (wt.needsBandage()) req += "§eBandage §7";
                if (!wt.needsAnesthetic() && !wt.needsBandage()) req += "§aScalpel→Suture standard";
                else req += "→ Scalpel → Suture";
                player.displayClientMessage(Component.literal(req + " §7(voir Table d'Analyse)"), false);
                // sauve le dernier diagnostic pour la Table d'Analyse
                player.getPersistentData().putInt("VetLastWound", wt.getId());
                player.getPersistentData().putString("VetLastTarget", target.getName().getString());
            } else {
                player.displayClientMessage(Component.literal(" §7Tags: " + (target.getTags().contains("veterinarium_healed") ? "§aSoigné " : "§7Non soigné ") + (target.getTags().contains("veterinarium_operated") ? "§aOpéré" : "§7Non opéré")), false);
            }

            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
            level.playSound(null, target.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0f, 1.3f);
            player.displayClientMessage(Component.literal("§7-> Anesthésie injectée (Lenteur 5s)"), true);
            try { com.veterinarium.data.BestiaryProgress.recordDiagnose(player, target, wt); } catch (Exception ignored) {}

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
