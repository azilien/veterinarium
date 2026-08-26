package com.veterinarium.item;

import com.veterinarium.registry.ModItems;
import com.veterinarium.registry.ModSounds;
import com.veterinarium.wound.WoundType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class SutureKitItem extends Item {
    public SutureKitItem(Properties properties) {
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
        return WoundType.CONTUSION;
    }
    private int countItem(net.minecraft.world.entity.player.Player p, net.minecraft.world.item.Item it) {
        int c=0; for (var s: p.getInventory().items) if (!s.isEmpty() && s.is(it)) c+=s.getCount();
        for (var s: p.getInventory().offhand) if (!s.isEmpty() && s.is(it)) c+=s.getCount();
        return c;
    }
    private void consumeItem(net.minecraft.world.entity.player.Player p, net.minecraft.world.item.Item it) {
        for (var s: p.getInventory().items) if (!s.isEmpty() && s.is(it)) { s.shrink(1); return; }
        for (var s: p.getInventory().offhand) if (!s.isEmpty() && s.is(it)) { s.shrink(1); return; }
    }
    private boolean tryConsumeFromTable(Level level, net.minecraft.core.BlockPos center, WoundType wt, boolean isScalpel) {
        for (int dx=-5;dx<=5;dx++) for (int dy=-2;dy<=2;dy++) for (int dz=-5;dz<=5;dz++) {
            var be = level.getBlockEntity(center.offset(dx,dy,dz));
            if (be instanceof com.veterinarium.block.entity.OperatingTableBlockEntity table) {
                if (table.consumeIfNeeded(wt, isScalpel)) return true;
            }
        }
        return false;
    }
    private boolean hasNearbyTableWithStock(Level level, net.minecraft.core.BlockPos center, WoundType wt, boolean isScalpel) {
        for (int dx=-5;dx<=5;dx++) for (int dy=-2;dy<=2;dy++) for (int dz=-5;dz<=5;dz++) {
            var be = level.getBlockEntity(center.offset(dx,dy,dz));
            if (be instanceof com.veterinarium.block.entity.OperatingTableBlockEntity table) {
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
            boolean isOperated = target.getTags().contains("veterinarium_operated");
            boolean needsScalpel = target.getTags().contains("veterinarium_needs_scalpel") && !isOperated;
            // Infection si on suture sans scalpel alors que la blessure le nécessite
            if (needsScalpel) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
                target.hurt(level.damageSources().magic(), 2.0f);
                level.playSound(null, target.blockPosition(), net.minecraft.sounds.SoundEvents.ZOMBIE_HURT, SoundSource.PLAYERS, 1.0f, 0.8f);
                player.displayClientMessage(Component.literal("§c☠ Infection ! §7Il fallait opérer au §cScalpel §7d'abord ! §f" + target.getName().getString() + " s'aggrave."), true);
                EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                stack.hurtAndBreak(1, player, slot);
                return InteractionResult.SUCCESS;
            }
            if (target.getHealth() < target.getMaxHealth() * 0.9f || isOperated) {
                WoundType wt = getWound(target);
                if (wt.needsBandage()) {
                    boolean fromTable = tryConsumeFromTable(level, target.blockPosition(), wt, false);
                    if (fromTable) {
                        player.displayClientMessage(Component.literal("§c[Bloc Opératoire] §a1 Bandage fourni par la table → suture étanche"), false);
                    } else {
                        boolean hasTableNearby = hasNearbyTableWithStock(level, target.blockPosition(), wt, false);
                        int has = countItem(player, ModItems.BANDAGE.get());
                        if (has < 1) {
                            String src = hasTableNearby ? "dans la table/inventaire" : "dans l'inventaire (ou charge la table à 5 blocs)";
                            player.displayClientMessage(Component.literal("§c⚠ " + wt.getDisplay() + " §7nécessite §e1 Bandage §7" + src + " ! (50% rechute)"), true);
                            if (level.random.nextFloat() < 0.5f) {
                                target.hurt(level.damageSources().magic(), 3.0f);
                                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0));
                                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
                                level.playSound(null, target.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.0f, 0.7f);
                                player.displayClientMessage(Component.literal("§c☠ Hémorragie: sans bandage la plaie se rouvre!"), false);
                                EquipmentSlot sl = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                                stack.hurtAndBreak(1, player, sl);
                                return InteractionResult.SUCCESS;
                            } else {
                                player.displayClientMessage(Component.literal("§e→ Suture réussie de justesse sans bandage..."), false);
                            }
                        } else {
                            consumeItem(player, ModItems.BANDAGE.get());
                            player.displayClientMessage(Component.literal("§e[Bandage] §a1 Bandage consommé → suture étanche"), false);
                        }
                    }
                }
                target.heal(6.0f);
                target.removeEffect(MobEffects.POISON);
                target.removeEffect(MobEffects.WITHER);
                target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0));
                try {
                    level.playSound(null, target.blockPosition(), ModSounds.SUTURE.get(), SoundSource.PLAYERS, 1.0f, 0.9f);
                } catch (Exception e) {
                    level.playSound(null, target.blockPosition(), net.minecraft.sounds.SoundEvents.WOOL_PLACE, SoundSource.PLAYERS, 1.0f, 0.9f);
                }
                player.displayClientMessage(Component.literal("§d[Kit de Suture] §aSuture réussie sur " + target.getName().getString() + " §a+3 coeurs + Régénération"), true);

                // Si c'est nos entités blessées custom, marque healed pour texture
                if (target instanceof com.veterinarium.entity.WoundedWolfEntity woundedWolf) {
                    woundedWolf.setHealed(true);
                }
                if (target instanceof com.veterinarium.entity.WoundedCatEntity woundedCat) {
                    woundedCat.setHealed(true);
                }
                if (target instanceof com.veterinarium.entity.WoundedHorseEntity woundedHorse) {
                    woundedHorse.setHealed(true);
                }
                if (target instanceof com.veterinarium.entity.WoundedFoxEntity woundedFox) {
                    woundedFox.setHealed(true);
                    // confiance renard: tag custom (évite API Fox 1.21.1 instable)
                    woundedFox.addTag("veterinarium_trusted");
                    // tente d'ajouter trust natif via réflexion si dispo
                    try {
                        var m = woundedFox.getClass().getMethod("addTrustedUUID", java.util.UUID.class);
                        m.invoke(woundedFox, player.getUUID());
                    } catch (Exception ignored) {
                        try {
                            var m2 = woundedFox.getClass().getMethod("addTrusted", java.util.UUID.class);
                            m2.invoke(woundedFox, player.getUUID());
                        } catch (Exception ignored2) {}
                    }
                }
                if (target instanceof com.veterinarium.entity.WoundedVillagerEntity woundedVillager) {
                    woundedVillager.setHealed(true);
                    // Réputation villageoise: effet hero + cadeau émeraude
                    player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 600, 0));
                    if (level.random.nextFloat() < 0.5f) {
                        target.spawnAtLocation(net.minecraft.world.item.Items.EMERALD, 1);
                        player.displayClientMessage(Component.literal("§e★ Le villageois reconnaissant vous offre une émeraude !"), false);
                    }
                }

                if (target instanceof TamableAnimal tamable && !tamable.isTame()) {
                    if (level.random.nextFloat() < 0.33f) {
                        tamable.tame(player);
                        tamable.setOrderedToSit(false);
                        player.displayClientMessage(Component.literal("§6★ " + target.getName().getString() + " vous fait confiance après les soins ! (Apprivoisé)"), false);
                        try { level.playSound(null, target.blockPosition(), ModSounds.HEAL_SUCCESS.get(), SoundSource.NEUTRAL, 0.8f, 1.2f); } catch (Exception e) { level.playSound(null, target.blockPosition(), net.minecraft.sounds.SoundEvents.WOLF_WHINE, SoundSource.NEUTRAL, 1.0f, 1.0f); }
                    } else {
                        player.displayClientMessage(Component.literal("§7La créature est soignée mais reste méfiante... Réessayez après un autre soin."), false);
                    }
                } else if (target instanceof net.minecraft.world.entity.animal.horse.AbstractHorse horse && !horse.isTamed()) {
                    if (level.random.nextFloat() < 0.33f) {
                        horse.setTamed(true);
                        horse.setOwnerUUID(player.getUUID());
                        player.displayClientMessage(Component.literal("§6★ " + target.getName().getString() + " vous fait confiance après les soins ! (Apprivoisé)"), false);
                        try { level.playSound(null, target.blockPosition(), ModSounds.HEAL_SUCCESS.get(), SoundSource.NEUTRAL, 0.8f, 1.0f); } catch (Exception e) { level.playSound(null, target.blockPosition(), net.minecraft.sounds.SoundEvents.HORSE_ANGRY, SoundSource.NEUTRAL, 1.0f, 1.0f); }
                    } else {
                        player.displayClientMessage(Component.literal("§7La créature est soignée mais reste méfiante... Réessayez après un autre soin."), false);
                    }
                }

                target.addTag("veterinarium_healed");
                target.addTag("veterinarium_sutured");
                target.removeTag("veterinarium_wounded");
                target.removeTag("veterinarium_needs_scalpel");
                target.removeTag("veterinarium_needs_scalpel");
                try { com.veterinarium.integration.ArsNouveauIntegration.applyArsBonus(target, player); } catch (Exception ignored) {}
                // Enlève le nom "Blessé" et met "Soigné" (sauf si déjà fait par nos entités custom)
                boolean isCustomWounded = target instanceof com.veterinarium.entity.WoundedWolfEntity || target instanceof com.veterinarium.entity.WoundedCatEntity || target instanceof com.veterinarium.entity.WoundedHorseEntity || target instanceof com.veterinarium.entity.WoundedFoxEntity || target instanceof com.veterinarium.entity.WoundedVillagerEntity;
                if (!isCustomWounded && target.getCustomName() != null && target.getCustomName().getString().contains("Blessé")) {
                    target.setCustomName(Component.literal("§a❤ Soigné §7- " + target.getName().getString().replace("§c☠ Blessé §7- ", "").replace("§a❤ Soigné §7- ", "")));
                    target.setCustomNameVisible(true);
                }
                // Bonus: si wolf, enlève lenteur
                target.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                target.removeEffect(MobEffects.WEAKNESS);

                EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                stack.hurtAndBreak(1, player, slot);
                return InteractionResult.SUCCESS;
            } else {
                player.displayClientMessage(Component.literal("§7[Kit de Suture] La créature doit d'abord être opérée au Scalpel ou être blessée (<90% HP)"), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7Kit complet: aiguille + fil stérile"));
        tooltip.add(Component.literal("§8Clic droit sur créature opérée/blessée"));
        tooltip.add(Component.literal("§8-> Soigne 3 coeurs + Régénération"));
        tooltip.add(Component.literal("§8-> 33% de chance d'apprivoiser"));
        tooltip.add(Component.literal("§eNécessite: Scalpel d'abord"));
        tooltip.add(Component.literal("§eHémorragie/Infection → §7nécessite §eBandage"));
    }
}
