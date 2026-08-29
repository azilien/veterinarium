package com.veterinarium.block;

import com.veterinarium.block.entity.AnalysisTableBlockEntity;
import com.veterinarium.wound.WoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnalysisTableBlock extends Block implements EntityBlock {
    public AnalysisTableBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnalysisTableBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof AnalysisTableBlockEntity table)) {
            return InteractionResult.PASS;
        }
        // Si le joueur vient de diagnostiquer avec Seringue, la target est dans ses persistentData
        if (player.getPersistentData().contains("VetLastWound")) {
            int id = player.getPersistentData().getInt("VetLastWound");
            String target = player.getPersistentData().getString("VetLastTarget");
            WoundType wt = WoundType.fromId(id);
            table.setAnalysis(wt, target);
            try { com.veterinarium.data.BestiaryProgress.recordAnalysis(player, wt, target); } catch (Exception ignored) {}
            player.displayClientMessage(Component.translatable("message.veterinarium.analysis.recorded", target, wt.getDisplay()), false);
            player.displayClientMessage(wt.getDescription(), false);
            if (wt.needsAnesthetic() || wt.needsBandage()) {
                net.minecraft.network.chat.MutableComponent req = (net.minecraft.network.chat.MutableComponent) Component.empty();
                if (wt.needsAnesthetic()) req = req.append(Component.translatable("message.veterinarium.analysis.requires.anesthetic"));
                if (wt.needsBandage()) req = req.append(Component.translatable("message.veterinarium.analysis.requires.bandage"));
                player.displayClientMessage(Component.translatable("message.veterinarium.analysis.bring_to_table", req), false);
            } else {
                player.displayClientMessage(Component.translatable("message.veterinarium.analysis.standard_care"), false);
            }
            level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0f, 1.2f);
            return InteractionResult.SUCCESS;
        }
        // Sinon affiche la dernière analyse mémorisée
        if (!table.getLastTarget().isEmpty()) {
            player.displayClientMessage(table.getAnalysisText(), false);
            player.displayClientMessage(table.getLastWound().getDescription(), false);
            level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        // Sinon scan le plus proche blessé dans 8 blocs
        AABB area = new AABB(pos).inflate(8, 4, 8);
        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, area, e -> e.getTags().contains("veterinarium_wounded") || e instanceof com.veterinarium.entity.WoundedWolfEntity || e instanceof com.veterinarium.entity.WoundedCatEntity || e instanceof com.veterinarium.entity.WoundedHorseEntity || e instanceof com.veterinarium.entity.WoundedFoxEntity || e instanceof com.veterinarium.entity.WoundedVillagerEntity);
        if (!nearby.isEmpty()) {
            LivingEntity target = nearby.get(0);
            WoundType wt = WoundType.CONTUSION;
            if (target instanceof com.veterinarium.entity.WoundedWolfEntity w) wt = w.getWoundType();
            else if (target instanceof com.veterinarium.entity.WoundedCatEntity c) wt = c.getWoundType();
            else if (target instanceof com.veterinarium.entity.WoundedHorseEntity h) wt = h.getWoundType();
            else if (target instanceof com.veterinarium.entity.WoundedFoxEntity f) wt = f.getWoundType();
            else if (target instanceof com.veterinarium.entity.WoundedVillagerEntity v) wt = v.getWoundType();
            else if (target.getPersistentData().contains("VetWound")) wt = WoundType.fromId(target.getPersistentData().getInt("VetWound"));
            table.setAnalysis(wt, target.getName().getString());
            player.displayClientMessage(Component.translatable("message.veterinarium.analysis.auto_scan", target.getName().getString(), wt.getDisplay()), false);
            player.displayClientMessage(wt.getDescription(), false);
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.6f, 1.5f);
            return InteractionResult.SUCCESS;
        }
        player.displayClientMessage(Component.translatable("message.veterinarium.analysis.no_target"), false);
        player.displayClientMessage(Component.translatable("message.veterinarium.analysis.total_count", String.valueOf(table.getAnalysesDone())), false);
        level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0f, 0.8f);
        return InteractionResult.SUCCESS;
    }
}
