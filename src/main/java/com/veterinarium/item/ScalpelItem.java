package com.veterinarium.item;

import com.veterinarium.registry.ModItems;
import com.veterinarium.registry.ModSounds;
import com.veterinarium.wound.WoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
        return WoundType.CONTUSION;
    }
    private int countItem(Player p, net.minecraft.world.item.Item it) {
        int c=0; for (var s: p.getInventory().items) if (!s.isEmpty() && s.is(it)) c+=s.getCount();
        for (var s: p.getInventory().offhand) if (!s.isEmpty() && s.is(it)) c+=s.getCount();
        return c;
    }
    private void consumeItem(Player p, net.minecraft.world.item.Item it) {
        for (var s: p.getInventory().items) if (!s.isEmpty() && s.is(it)) { s.shrink(1); if (s.isEmpty()) p.getInventory().setItem(p.getInventory().selected, net.minecraft.world.item.ItemStack.EMPTY); return; }
        for (var s: p.getInventory().offhand) if (!s.isEmpty() && s.is(it)) { s.shrink(1); return; }
    }
    private boolean tryConsumeFromTable(Level level, BlockPos center, WoundType wt, boolean isScalpel) {
        // cherche OperatingTable dans 5 blocs
        for (int dx=-5;dx<=5;dx++) for (int dy=-2;dy<=2;dy++) for (int dz=-5;dz<=5;dz++) {
            BlockPos p = center.offset(dx,dy,dz);
            var be = level.getBlockEntity(p);
            if (be instanceof com.veterinarium.block.entity.OperatingTableBlockEntity table) {
                if (table.consumeIfNeeded(wt, isScalpel)) return true;
                // si table existe mais stock vide et besoin -> on laisse retomber sur inventaire joueur, mais on indique table vide
            }
        }
        return false;
    }
    private boolean hasNearbyTableWithStock(Level level, BlockPos center, WoundType wt, boolean isScalpel) {
        for (int dx=-5;dx<=5;dx++) for (int dy=-2;dy<=2;dy++) for (int dz=-5;dz<=5;dz++) {
            BlockPos p = center.offset(dx,dy,dz);
            var be = level.getBlockEntity(p);
            if (be instanceof com.veterinarium.block.entity.OperatingTableBlockEntity table) {
                // check if table has required item
                for (int i=0;i<table.getHandler().getSlots();i++) {
                    var s = table.getHandler().getStackInSlot(i);
                    if (isScalpel && s.is(ModItems.ANESTHETIC.get()) && s.getCount()>0) return true;
                    if (!isScalpel && s.is(ModItems.BANDAGE.get()) && s.getCount()>0) return true;
                }
            }
        }
        return false;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (!level.isClientSide) {
            if (target.getHealth() < target.getMaxHealth()) {
                WoundType wt = getWound(target);
                if (wt.needsAnesthetic()) {
                    boolean fromTable = tryConsumeFromTable(level, target.blockPosition(), wt, true);
                    if (fromTable) {
                        player.displayClientMessage(Component.literal("§d[Bloc Opératoire] §a1 Anesthésiant fourni par la table → incision sans douleur"), false);
                        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 200, 0));
                    } else {
                        boolean hasTableNearby = hasNearbyTableWithStock(level, target.blockPosition(), wt, true);
                        int has = countItem(player, ModItems.ANESTHETIC.get());
                        if (has < 1) {
                            String src = hasTableNearby ? "dans la table/inventaire" : "dans l'inventaire (ou charge la table à 5 blocs)";
                            player.displayClientMessage(Component.literal("§c⚠ " + wt.getDisplay() + " §7nécessite §d1 Anesthésiant §7" + src + " ! (50% douleur)"), true);
                            if (level.random.nextFloat() < 0.5f) {
                                target.hurt(level.damageSources().magic(), 2.0f);
                                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 200, 1));
                                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 300, 2));
                                level.playSound(null, target.blockPosition(), net.minecraft.sounds.SoundEvents.VILLAGER_HURT, SoundSource.PLAYERS, 1.0f, 0.7f);
                                player.displayClientMessage(Component.literal("§c☠ Échec anesthésie: la créature hurle, soin réduit!"), false);
                                EquipmentSlot sl = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                                stack.hurtAndBreak(1, player, sl);
                                return InteractionResult.SUCCESS;
                            } else {
                                player.displayClientMessage(Component.literal("§e→ Chanceux: incision réussie malgré la douleur..."), false);
                            }
                        } else {
                            consumeItem(player, ModItems.ANESTHETIC.get());
                            player.displayClientMessage(Component.literal("§d[Anesthésie] §a1 Anesthésiant consommé → incision sans douleur"), false);
                            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 200, 0));
                        }
                    }
                }
                float heal = 2.0f;
                target.heal(heal);
                try {
                    level.playSound(null, target.blockPosition(), ModSounds.SCALPEL_CUT.get(), SoundSource.PLAYERS, 0.8f, 1.2f);
                } catch (Exception e) {
                    level.playSound(null, target.blockPosition(), net.minecraft.sounds.SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 0.8f, 1.5f);
                }
                player.displayClientMessage(Component.literal("§c[Scalpel] §aIncision précise -> §f" + target.getName().getString() + " §a+" + heal + " HP"), true);
                EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                stack.hurtAndBreak(1, player, slot);
                target.addTag("veterinarium_operated");
                target.removeTag("veterinarium_needs_scalpel");
                // Enlève lenteur partielle
                target.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
                // Brûlure: éteint le feu
                if (wt == WoundType.BRULURE) { target.clearFire(); target.removeEffect(net.minecraft.world.effect.MobEffects.WITHER); }
                // Bonus Ars Nouveau si présent
                try { com.veterinarium.integration.ArsNouveauIntegration.applyArsBonus(target, player); } catch (Exception ignored) {}
                try { com.veterinarium.data.BestiaryProgress.recordOperate(player, target); } catch (Exception ignored) {}
                player.displayClientMessage(Component.literal("§7→ Prêt pour suture ! Utilise le §dKit de Suture§7 maintenant."), false);
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
        tooltip.add(Component.literal("§dFracture/Infection → §7nécessite §dAnesthésiant §7dans l'inventaire"));
    }
}
