package com.veterinarium.item;

import com.veterinarium.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class HellfireSerumItem extends Item {
    public HellfireSerumItem(Properties props) { super(props); }

    private boolean hasNearbyOperatingTable(Level level, BlockPos center) {
        for (int dx=-5;dx<=5;dx++) for(int dy=-2;dy<=2;dy++) for(int dz=-5;dz<=5;dz++) {
            BlockPos p = center.offset(dx,dy,dz);
            var be = level.getBlockEntity(p);
            if (be instanceof com.veterinarium.block.entity.OperatingTableBlockEntity) return true;
        }
        return false;
    }
    private boolean consumeFromTable(Level level, BlockPos center, boolean needBandage, boolean needAnesthetic) {
        for (int dx=-5;dx<=5;dx++) for(int dy=-2;dy<=2;dy++) for(int dz=-5;dz<=5;dz++) {
            var be = level.getBlockEntity(center.offset(dx,dy,dz));
            if (be instanceof com.veterinarium.block.entity.OperatingTableBlockEntity table) {
                // mutation nécessite 1 bandage + 1 anesthésiant dans la table (simule bloc opératoire équipé)
                boolean hasBand = false, hasAnest = false;
                int bandSlot=-1, anestSlot=-1;
                for(int i=0;i<table.getHandler().getSlots();i++) {
                    var s = table.getHandler().getStackInSlot(i);
                    if (!s.isEmpty()) {
                        if (s.is(com.veterinarium.registry.ModItems.BANDAGE.get()) && s.getCount()>0 && !hasBand) { hasBand=true; bandSlot=i; }
                        if (s.is(com.veterinarium.registry.ModItems.ANESTHETIC.get()) && s.getCount()>0 && !hasAnest) { hasAnest=true; anestSlot=i; }
                    }
                }
                if (hasBand && hasAnest) {
                    table.getHandler().extractItem(bandSlot, 1, false);
                    table.getHandler().extractItem(anestSlot, 1, false);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (level.isClientSide) return InteractionResult.sidedSuccess(level.isClientSide);

        // cible doit être soignée / apprivoisable / vivante
        boolean isHealed = target.getTags().contains("veterinarium_healed")
                || target instanceof com.veterinarium.entity.WoundedWolfEntity w && w.isHealed()
                || target instanceof com.veterinarium.entity.WoundedCatEntity c && c.isHealed()
                || target instanceof com.veterinarium.entity.WoundedHorseEntity h && h.isHealed()
                || target instanceof com.veterinarium.entity.WoundedFoxEntity f && f.isHealed()
                || target instanceof com.veterinarium.entity.WoundedVillagerEntity v && v.isHealed()
                || (target.getHealth() >= target.getMaxHealth()*0.95f && (target.getTags().contains("veterinarium_operated") || target.getTags().contains("veterinarium_sutured")));

        // aussi accepte loup/chat/cheval vanilla soignés (healed via hut)
        boolean isEligibleVanilla = target instanceof net.minecraft.world.entity.animal.Wolf
                || target instanceof net.minecraft.world.entity.animal.Cat
                || target instanceof net.minecraft.world.entity.animal.horse.Horse
                || target instanceof net.minecraft.world.entity.animal.Fox;

        if (!isHealed) {
            // tolère aussi si le joueur insiste: si la créature est >90% HP et déjà opérée, on laisse muter (pour serie)
            if (!(target.getHealth() >= target.getMaxHealth()*0.8f && (target.getTags().contains("veterinarium_operated") || target instanceof net.minecraft.world.entity.TamableAnimal ta && ta.isTame()))) {
                player.displayClientMessage(Component.literal("§c[Sérum Hellfire] §7La cible doit être §aSOIGNÉE§7 d'abord (Scalpel→Suture ou Hut). §f" + target.getName().getString() + " n'est pas prêt."), true);
                return InteractionResult.PASS;
            }
        }

        if (!hasNearbyOperatingTable(level, target.blockPosition())) {
            player.displayClientMessage(Component.literal("§c[Sérum Hellfire] §7Pas de §cBloc Opératoire §7équipé à 5 blocs ! Charge 1 bandage + 1 anesthésiant."), true);
            return InteractionResult.FAIL;
        }
        // tente conso table, sinon inventaire
        boolean tableOk = consumeFromTable(level, target.blockPosition(), true, true);
        if (!tableOk) {
            // fallback inventaire joueur
            int band = 0, anest = 0;
            for(var s: player.getInventory().items) {
                if (s.is(com.veterinarium.registry.ModItems.BANDAGE.get())) band+=s.getCount();
                if (s.is(com.veterinarium.registry.ModItems.ANESTHETIC.get())) anest+=s.getCount();
            }
            for(var s: player.getInventory().offhand) {
                if (s.is(com.veterinarium.registry.ModItems.BANDAGE.get())) band+=s.getCount();
                if (s.is(com.veterinarium.registry.ModItems.ANESTHETIC.get())) anest+=s.getCount();
            }
            if (band<1 || anest<1) {
                player.displayClientMessage(Component.literal("§c[Sérum] §7Il faut §e1 Bandage + §d1 Anesthésiant §7(dans table ou inventaire) pour la mutation."), true);
                return InteractionResult.FAIL;
            }
            // consomme
            consumeInv(player, com.veterinarium.registry.ModItems.BANDAGE.get());
            consumeInv(player, com.veterinarium.registry.ModItems.ANESTHETIC.get());
            player.displayClientMessage(Component.literal("§e[Consommé] §71 Bandage + 1 Anesthésiant (inventaire) → mutation..."), false);
        } else {
            player.displayClientMessage(Component.literal("§c[Bloc Opératoire] §a1 Bandage + 1 Anesthésiant fournis → mutation..."), false);
        }

        // mutation !
        if (level instanceof ServerLevel sl) {
            // détermine source ADN si présent dans sérum NBT (sinon aléatoire)
            String dnaInfo = target.getName().getString();
            // spawn Hellfire Ravager à la position de la cible
            var ravager = ModEntities.HELLFIRE_RAVAGER.get().create(sl);
            if (ravager == null) {
                player.displayClientMessage(Component.literal("§cErreur spawn Hellfire"), false);
                return InteractionResult.FAIL;
            }
            ravager.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
            ravager.setCustomName(Component.literal("§6☢ Hellfire Ravager §7(ADN " + (dnaInfo.isEmpty()? target.getName().getString(): dnaInfo) + ")"));
            ravager.setCustomNameVisible(true);
            // tente tame direct si target était tame ou si joueur a chance
            if (target instanceof net.minecraft.world.entity.TamableAnimal tam && tam.isTame() && tam.isOwnedBy(player)) {
                ravager.tame(player);
            } else if (level.random.nextFloat() < 0.7f) {
                ravager.tame(player);
                player.displayClientMessage(Component.literal("§6★ Le Ravager te reconnaît comme son maître !"), false);
            }
            // copie santé relative?
            sl.addFreshEntity(ravager);
            // effets
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION, target.getX(), target.getY()+1, target.getZ(), 1, 0,0,0,0);
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.LAVA, target.getX(), target.getY()+1, target.getZ(), 10, 0.5,0.5,0.5,0.1);
            level.playSound(null, target.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.0f, 0.8f);
            level.playSound(null, target.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 1.2f);
            // supprime ancienne entité (garde si c'est un Wounded non healed? mais ici on supprime)
            target.discard();
            // stats bestiaire
            try {
                var nbt = player.getPersistentData();
                nbt.putInt("VetMutations", nbt.getInt("VetMutations")+1);
                nbt.putBoolean("VetSeen_hellfire_ravager", true);
            } catch (Exception ignored) {}
            player.displayClientMessage(Component.literal("§6☢ MUTATION RÉUSSIE ! §f" + ravager.getName().getString() + " §7est né du bloc opératoire."), false);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void consumeInv(Player p, net.minecraft.world.item.Item it) {
        for(var s: p.getInventory().items) if (!s.isEmpty() && s.is(it)) { s.shrink(1); return; }
        for(var s: p.getInventory().offhand) if (!s.isEmpty() && s.is(it)) { s.shrink(1); return; }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6Sérum Hellfire - Mutagène"));
        tooltip.add(Component.literal("§8Clic sur créature §aSOIGNÉE §8près Bloc Opé"));
        tooltip.add(Component.literal("§8→ Transforme en §6Hellfire Ravager"));
        tooltip.add(Component.literal("§8Requiert: 1 bandage + 1 anesthésiant (table)"));
        tooltip.add(Component.literal("§c☢ Tier Ark - 40❤ 8 dmg feu"));
    }
}
