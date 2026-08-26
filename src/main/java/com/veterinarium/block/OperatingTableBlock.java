package com.veterinarium.block;

import com.veterinarium.block.entity.OperatingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class OperatingTableBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);

    public OperatingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OperatingTableBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof OperatingTableBlockEntity table)) return InteractionResult.PASS;
        // sneak + vide = info
        ItemStack held = player.getMainHandItem();
        if (player.isShiftKeyDown()) {
            int band = 0, anest = 0;
            for (int i=0;i<table.getHandler().getSlots();i++) {
                ItemStack s = table.getHandler().getStackInSlot(i);
                if (s.is(com.veterinarium.registry.ModItems.BANDAGE.get())) band += s.getCount();
                if (s.is(com.veterinarium.registry.ModItems.ANESTHETIC.get())) anest += s.getCount();
            }
            player.displayClientMessage(Component.literal("§c[Bloc Opératoire] §7Stock: §e" + band + " bandage §7| §d" + anest + " anesthésiant §7(Sneak-clic pour vider)"), false);
            if (band==0 && anest==0) player.displayClientMessage(Component.literal("§7→ Clic avec §eBandage§7/§dAnesthésiant §7pour charger la table (accès auto pendant chirurgie à 5 blocs)"), false);
            level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.4f, 1.2f);
            return InteractionResult.SUCCESS;
        }
        // si tient bandage/anesthetic -> insère
        if (!held.isEmpty() && (held.is(com.veterinarium.registry.ModItems.BANDAGE.get()) || held.is(com.veterinarium.registry.ModItems.ANESTHETIC.get()))) {
            ItemStack copy = held.copy();
            copy.setCount(1);
            for (int i=0;i<table.getHandler().getSlots();i++) {
                ItemStack remainder = table.getHandler().insertItem(i, copy, false);
                if (remainder.isEmpty()) {
                    held.shrink(1);
                    player.displayClientMessage(Component.literal("§a[Bloc Opératoire] §7" + copy.getHoverName().getString() + " chargé (§7" + (table.getHandler().getStackInSlot(i).getCount()) + ")"), true);
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.7f, 1.0f);
                    return InteractionResult.SUCCESS;
                }
            }
            player.displayClientMessage(Component.literal("§c[Bloc Opératoire] §7Inventaire plein (2 slots) — Sneak-clic pour voir"), false);
            return InteractionResult.FAIL;
        }
        // main vide -> extrait 1 item si présent
        if (held.isEmpty()) {
            for (int i=table.getHandler().getSlots()-1;i>=0;i--) {
                ItemStack s = table.getHandler().getStackInSlot(i);
                if (!s.isEmpty()) {
                    ItemStack out = table.getHandler().extractItem(i, 1, false);
                    if (!player.addItem(out)) player.drop(out, false);
                    player.displayClientMessage(Component.literal("§7[Bloc Opératoire] §fRetrait: " + out.getHoverName().getString()), true);
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.7f, 0.8f);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        player.displayClientMessage(Component.literal("§c[Bloc Opératoire] §7Prêt. Place §eBandage§7/§dAnesthésiant§7 (clic) puis soigne à 5 blocs — la table fournira auto."), true);
        level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.4f, 1.2f);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof OperatingTableBlockEntity table) {
                for (int i=0;i<table.getHandler().getSlots();i++) {
                    ItemStack s = table.getHandler().getStackInSlot(i);
                    if (!s.isEmpty()) popResource(level, pos, s);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
