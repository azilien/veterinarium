package com.veterinarium.item;

import com.veterinarium.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class VetSphereItem extends Item {
    public VetSphereItem(Properties props) { super(props); }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (level.isClientSide) return InteractionResult.sidedSuccess(level.isClientSide);

        boolean isHealed = target.getTags().contains("veterinarium_healed")
                || target instanceof com.veterinarium.entity.WoundedWolfEntity w && w.isHealed()
                || target instanceof com.veterinarium.entity.WoundedCatEntity c && c.isHealed()
                || target instanceof com.veterinarium.entity.WoundedHorseEntity h && h.isHealed()
                || target instanceof com.veterinarium.entity.WoundedFoxEntity f && f.isHealed()
                || target instanceof com.veterinarium.entity.WoundedVillagerEntity v && v.isHealed()
                || target instanceof com.veterinarium.entity.WoundedDrakeEntity d && d.isHealed()
                || target instanceof com.veterinarium.entity.HellfireRavagerEntity
                || (target.getHealth() >= target.getMaxHealth()*0.95f && target.getTags().contains("veterinarium_operated"));
        boolean requiresHealed = true;
        try { requiresHealed = com.veterinarium.config.VeterinariumConfig.COMMON.sphereRequiresHealed.get(); } catch (Exception ignored) {}

        boolean isTamableHealed = target instanceof net.minecraft.world.entity.TamableAnimal ta && ta.isTame();

        if (requiresHealed && !isHealed && !isTamableHealed) {
            // Vérifie si c'est un WoundedDrake boss etc
            if (target.getTags().contains("veterinarium_wounded")) {
                player.displayClientMessage(Component.literal("§c[Sphère Vétérinaire] §7Cible encore §cblessée §7! Soigne-la (Scalpel→Suture) d'abord."), true);
                level.playSound(null, target.blockPosition(), SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), SoundSource.PLAYERS, 0.7f, 0.5f);
                return InteractionResult.FAIL;
            }
            // vanilla non blessé
            if (!target.getTags().contains("veterinarium_wounded") && !target.getTags().contains("veterinarium_healed")) {
                player.displayClientMessage(Component.literal("§7[Sphère] §f" + target.getName().getString() + " §7n'est pas un patient Veterinarium. Sphère inutilisable ici."), true);
                return InteractionResult.PASS;
            }
            player.displayClientMessage(Component.literal("§c[Sphère] §7Échec : la créature doit être §aSOIGNÉE§7."), true);
            return InteractionResult.FAIL;
        }

        // Fail chance si PV pas 100% (config)
        try {
            double fc = com.veterinarium.config.VeterinariumConfig.COMMON.sphereFailChanceIfNotFullHealth.get();
            if (fc > 0 && target.getHealth() < target.getMaxHealth() && level.random.nextDouble() < fc) {
                player.displayClientMessage(Component.literal("§c[Sphère] §7La capture échoue : " + target.getName().getString() + " n'est pas à 100% PV !"), true);
                level.playSound(null, target.blockPosition(), SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.PLAYERS, 0.8f, 0.5f);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                return InteractionResult.FAIL;
            }
        } catch (Exception ignored) {}
        // Capturable : soignée ou apprivoisée après soin
        // Interdit de capturer un joueur
        if (target instanceof Player) return InteractionResult.PASS;

        // Capture
        String entityId = "";
        try {
            var key = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
            if (key != null) entityId = key.toString();
        } catch (Exception e) { entityId = target.getType().toString(); }
        String customName = target.hasCustomName() ? target.getCustomName().getString() : target.getName().getString();
        boolean wasTame = target instanceof net.minecraft.world.entity.TamableAnimal ta && ta.isTame();

        // Sauvegarde légère de l'entité (on stocke juste l'id et le nom, pas full NBT pour simplifier DataComponents)
        CompoundTag tag = new CompoundTag();
        tag.putString("vet_entity", entityId);
        tag.putString("vet_name", customName);
        tag.putBoolean("vet_tame", wasTame);
        tag.putFloat("vet_health", target.getMaxHealth());
        // tamed owner ?
        if (target instanceof net.minecraft.world.entity.TamableAnimal ta && ta.getOwnerUUID() != null) {
            tag.putUUID("vet_owner", ta.getOwnerUUID());
        }

        ItemStack filled = new ItemStack(ModItems.VET_SPHERE_FILLED.get());
        // Store via CustomData 1.21.1
        try {
            filled.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            filled.set(DataComponents.CUSTOM_NAME, Component.literal("§aSphère - " + customName.replace("§c☠ Blessé §7- ", "").replace("§a❤ Soigné §7- ", "").replace("§c🚨 URGENCE §7- ", "").replace("§6☢ Hellfire Ravager §7(ADN ", "").replace(")", "")));
        } catch (Exception e) {
            // fallback via display name
            filled.set(DataComponents.CUSTOM_NAME, Component.literal("§aSphère: " + entityId));
        }

        level.playSound(null, target.blockPosition(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 1.0f, 1.2f);
        level.playSound(null, target.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 0.8f);
        if (level instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL, target.getX(), target.getY()+0.8, target.getZ(), 20, 0.3,0.5,0.3,0.1);
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART, target.getX(), target.getY()+1.0, target.getZ(), 3, 0.2,0.2,0.2,0.1);
        }
        player.displayClientMessage(Component.literal("§a[Sphère] §f" + customName.replace("§c☠ Blessé §7- ", "").replace("§a❤ Soigné §7- ", "") + " §acapturé ! §7(Clic droit au sol pour libérer)"), false);

        // Retire l'entité du monde
        target.discard();
        // Conso sphère vide
        if (!player.getAbilities().instabuild) stack.shrink(1);
        if (stack.isEmpty()) {
            player.setItemInHand(hand, filled);
        } else {
            if (!player.addItem(filled)) player.drop(filled, false);
        }
        // bestiaire + contrat
        try { player.getPersistentData().putInt("VetCaptures", player.getPersistentData().getInt("VetCaptures")+1); } catch (Exception ignored) {}
        try {
            String ek = com.veterinarium.data.BestiaryProgress.entityKey(target);
            com.veterinarium.block.entity.HospitalHutBlockEntity.notifyNearbyHuts(level, target.blockPosition(), ek, com.veterinarium.wound.WoundType.CONTUSION, true);
        } catch (Exception ignored) {}
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§aSphère Vétérinaire - Vide"));
        tooltip.add(Component.literal("§8Capture uniquement créature §aSOIGNÉE"));
        tooltip.add(Component.literal("§8→ Seringue -> Scalpel -> Suture d'abord"));
        tooltip.add(Component.literal("§8Échec si blessée ou sauvage"));
        tooltip.add(Component.literal("§7Clic droit sur patient guéri pour capturer"));
    }
}
