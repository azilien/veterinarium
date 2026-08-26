package com.veterinarium.item;

import com.veterinarium.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class VetSphereFilledItem extends Item {
    public VetSphereFilledItem(Properties props) { super(props); }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);
        // spawn au dessus du bloc cliqué si solide
        BlockPos spawnPos = state.isCollisionShapeFullBlock(level, pos) ? pos.above() : pos;
        if (!level.getBlockState(spawnPos).isAir() && !level.getBlockState(spawnPos).canBeReplaced()) {
            spawnPos = spawnPos.above();
        }
        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        CompoundTag tag = null;
        try {
            CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
            if (cd != null) tag = cd.copyTag();
        } catch (Exception ignored) {}

        String entityId = tag != null ? tag.getString("vet_entity") : "";
        if (entityId.isEmpty()) {
            // fallback : spawn wolf healed
            entityId = "veterinarium:wounded_wolf";
            tag = new CompoundTag();
            tag.putString("vet_entity", entityId);
        }

        EntityType<?> type = null;
        try {
            var key = net.minecraft.resources.ResourceLocation.tryParse(entityId);
            if (key != null) type = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getValue(key);
            if (type == null) type = EntityType.byString(entityId).orElse(null);
        } catch (Exception ignored) {}
        if (type == null) {
            if (player != null) player.displayClientMessage(Component.literal("§c[Sphère] Entité inconnue: " + entityId), true);
            return InteractionResult.FAIL;
        }

        var entity = type.create((ServerLevel) level);
        if (entity == null) {
            if (player != null) player.displayClientMessage(Component.literal("§c[Sphère] Échec spawn"), true);
            return InteractionResult.FAIL;
        }
        entity.moveTo(spawnPos.getX()+0.5, spawnPos.getY(), spawnPos.getZ()+0.5, level.random.nextFloat()*360, 0);
        if (entity instanceof net.minecraft.world.entity.Mob mob) {
            mob.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.SPAWN_EGG, null);
        }
        // restaure nom et tame si présent
        if (tag != null) {
            String name = tag.getString("vet_name");
            if (!name.isEmpty()) {
                entity.setCustomName(Component.literal(name.replace("§c☠ Blessé §7- ", "§a❤ Soigné §7- ")));
                entity.setCustomNameVisible(true);
            }
            if (tag.getBoolean("vet_tame") && entity instanceof net.minecraft.world.entity.TamableAnimal ta && player != null) {
                ta.tame(player);
            }
            if (tag.contains("vet_owner") && entity instanceof net.minecraft.world.entity.TamableAnimal ta) {
                try { ta.setOwnerUUID(tag.getUUID("vet_owner")); } catch (Exception ignored) {}
            }
        }
        entity.addTag("veterinarium_healed");
        entity.addTag("veterinarium_captured");
        if (entity instanceof net.minecraft.world.entity.LivingEntity le) le.setHealth(le.getMaxHealth());

        level.addFreshEntity(entity);
        level.playSound(null, spawnPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0f, 0.9f);
        if (level instanceof ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL, spawnPos.getX()+0.5, spawnPos.getY()+0.8, spawnPos.getZ()+0.5, 15, 0.3,0.3,0.3,0.1);
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, spawnPos.getX()+0.5, spawnPos.getY()+1.0, spawnPos.getZ()+0.5, 5, 0.3,0.3,0.3,0.1);
        }
        if (player != null && !player.getAbilities().instabuild) {
            // consomme sphère remplie et rend vide
            stack.shrink(1);
            ItemStack empty = new ItemStack(ModItems.VET_SPHERE.get());
            if (stack.isEmpty()) {
                player.setItemInHand(ctx.getHand(), empty);
            } else {
                if (!player.addItem(empty)) player.drop(empty, false);
            }
            player.displayClientMessage(Component.literal("§a[Sphère] §f" + entity.getName().getString() + " §alibéré !"), false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§aSphère Vétérinaire - Remplie"));
        try {
            CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
            if (cd != null) {
                CompoundTag tag = cd.copyTag();
                String id = tag.getString("vet_entity");
                String name = tag.getString("vet_name");
                if (!id.isEmpty()) tooltip.add(Component.literal("§7Contient: §f" + (name.isEmpty()? id : name)));
                else tooltip.add(Component.literal("§7Contient: §fCréature guérie"));
            } else {
                tooltip.add(Component.literal("§7Contient: §fCréature"));
            }
        } catch (Exception e) {
            tooltip.add(Component.literal("§7Contient: §fCréature"));
        }
        tooltip.add(Component.literal("§7Clic droit au sol → libère"));
        tooltip.add(Component.literal("§8Rend une sphère vide après libération"));
    }
}
