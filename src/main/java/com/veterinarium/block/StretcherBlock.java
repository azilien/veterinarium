package com.veterinarium.block;

import com.veterinarium.block.entity.StretcherBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

public class StretcherBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<StretcherBlock> CODEC = simpleCodec(StretcherBlock::new);
    @Override protected MapCodec<? extends HorizontalDirectionalBlock> codec() { return CODEC; }
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);
    public StretcherBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) { b.add(FACING); }
    @Nullable @Override public BlockState getStateForPlacement(BlockPlaceContext ctx) { return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()); }
    @Override public VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) { return SHAPE; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new StretcherBlockEntity(pos, state); }
    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl,pos,st,be)->{ if (be instanceof StretcherBlockEntity s) s.tick(lvl,pos,st); };
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof StretcherBlockEntity) {
                player.displayClientMessage(Component.literal("§f[Brancard] §7Soin portable 0.5❤/2s dans 2.5 blocs — pose près d'un patient"), false);
                level.playSound(null,pos,SoundEvents.WOOL_PLACE,SoundSource.BLOCKS,0.7f,1.0f);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
