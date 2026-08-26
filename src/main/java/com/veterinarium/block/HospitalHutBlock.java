package com.veterinarium.block;

import com.veterinarium.block.entity.HospitalHutBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HospitalHutBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public HospitalHutBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HospitalHutBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof HospitalHutBlockEntity hut) hut.tick(lvl, pos, st);
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            // Sneak-clic = construire le hut 9x9
            if (player.isShiftKeyDown()) {
                if (buildHut(level, pos, player)) {
                    player.displayClientMessage(Component.literal("§a[Hut Hôpital] §fHôpital 9x9 construit ! §7(Murs brique, toit croix, intérieur équipé)"), false);
                    level.playSound(null, pos, SoundEvents.BONE_BLOCK_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.SUCCESS;
                } else {
                    player.displayClientMessage(Component.literal("§c[Hut Hôpital] §7Zone encombrée → dégage un 9x9 plat autour du bloc avant de sneak-clic."), false);
                    return InteractionResult.FAIL;
                }
            }
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof HospitalHutBlockEntity hut) {
                net.minecraft.world.item.ItemStack held = player.getMainHandItem();
                boolean isUpgradeItem = held.is(net.minecraft.world.item.Items.BRICK) || held.is(com.veterinarium.registry.ModItems.BANDAGE.get()) || held.is(net.minecraft.world.item.Items.DIAMOND) || held.is(net.minecraft.world.item.Items.EMERALD) || held.is(net.minecraft.world.item.Items.NETHERITE_INGOT);
                if (isUpgradeItem && hut.getHutLevel() < 5) {
                    if (hut.tryUpgrade(player)) {
                        player.displayClientMessage(Component.literal("§a[Hut Hôpital] §fNiveau " + hut.getHutLevel() + " ! §7Rayon " + hut.getRadius() + " blocs, soin " + hut.getHealAmount() + "❤/2s"), false);
                        level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0f, 1.2f + hut.getHutLevel()*0.1f);
                        return InteractionResult.SUCCESS;
                    } else {
                        int lv = hut.getHutLevel();
                        int needB = switch (lv) { case 1 -> 8; case 2 -> 8; case 3 -> 12; case 4 -> 16; default -> 0; };
                        int needBa = switch (lv) { case 1 -> 4; case 2 -> 8; case 3 -> 12; case 4 -> 16; default -> 0; };
                        int needD = (lv==2)?1:0;
                        int needE = switch (lv) { case 3 -> 2; case 4 -> 4; default -> 0; };
                        int needN = (lv==4)?1:0;
                        String msg = "§c[Hut Hôpital] §7Manque: " + needB + " briques + " + needBa + " bandages";
                        if (needD>0) msg += " + " + needD + " diamant";
                        if (needE>0) msg += " + " + needE + " émeraude" + (needE>1?"s":"");
                        if (needN>0) msg += " + " + needN + " lingot netherite";
                        msg += " pour Lv" + (lv+1);
                        player.displayClientMessage(Component.literal(msg), false);
                        return InteractionResult.FAIL;
                    }
                }
                if (hut.getHutLevel() >= 5 && isUpgradeItem) {
                    player.displayClientMessage(Component.literal("§a[Hut Hôpital] §7Niveau max (5) ! §7Rayon " + hut.getRadius() + " blocs (3.5❤/2s)"), false);
                    return InteractionResult.SUCCESS;
                }
                player.displayClientMessage(Component.literal("§c[Hut Hôpital Lv" + hut.getHutLevel() + "] §7Patients: " + hut.getPatientCount() + " | §aSoignés: " + hut.getHealedCount() + " | §7Rayon " + hut.getRadius() + " blocs §7(Soin " + hut.getHealAmount() + "❤/2s)"), false);
                // Contrat journalier
                player.displayClientMessage(hut.getContractDisplay(), false);
                if (hut.isContractCompleted()) {
                    player.displayClientMessage(Component.literal("§e▶ Clic droit à nouveau pour récupérer la récompense !"), false);
                    if (hut.tryClaimContract(player)) {
                        player.displayClientMessage(Component.literal("§a📋 Contrat validé ! Récompense donnée."), false);
                        level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0f, 1.5f);
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    player.displayClientMessage(Component.literal("§7→ Soigne/capture près du Hut (64 blocs) pour progresser"), false);
                }
                if (hut.getHutLevel() < 5) {
                    int lv = hut.getHutLevel();
                    String next = switch (lv) {
                        case 1 -> "8 briques+4 bandages";
                        case 2 -> "8 briques+8 bandages+1 diamant";
                        case 3 -> "12 briques+12 bandages+2 émeraudes";
                        case 4 -> "16 briques+16 bandages+4 émeraudes+1 netherite";
                        default -> "";
                    };
                    player.displayClientMessage(Component.literal("§7Sneak-clic: 9x9 | Clic upgrade Lv" + (lv+1) + " (" + next + ") | Contrat quotidien"), false);
                } else {
                    player.displayClientMessage(Component.literal("§7Sneak-clic: 9x9 | Niveau max atteint — merci docteur ! | Contrat quotidien"), false);
                }
                level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide && !isMoving) {
            // Auto-construction si le joueur vient de poser et que la zone est vide (optionnel, on laisse le sneak-clic pour éviter grief)
        }
    }

    private boolean buildHut(Level level, BlockPos center, Player player) {
        // Vérifie que la zone 9x9 à y=0..5 est dégagée (air ou remplaçable)
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                for (int dy = 0; dy <= 5; dy++) {
                    BlockPos p = center.offset(dx, dy, dz);
                    if (p.equals(center)) continue; // le hut lui-même
                    BlockState st = level.getBlockState(p);
                    if (!st.isAir() && !st.canBeReplaced() && dy <= 3) {
                        // Tolère l'herbe/fleurs mais pas les blocs solides en bas
                        if (dy == 0 && st.getBlock().toString().contains("grass")) continue;
                        return false;
                    }
                }
            }
        }
        // Place le sol
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos p = center.offset(dx, 0, dz);
                if (p.equals(center)) continue;
                level.setBlock(p, net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
        }
        // Murs y=1..3, périmètre
        for (int dy = 1; dy <= 3; dy++) {
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    boolean isWall = (Math.abs(dx) == 4 || Math.abs(dz) == 4);
                    if (!isWall) continue;
                    BlockPos p = center.offset(dx, dy, dz);
                    // Porte en (0, -4)
                    if (dx == 0 && dz == -4 && (dy == 1 || dy == 2)) {
                        level.setBlock(p, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                        continue;
                    }
                    // Fenêtres en (4,0) ( -4,0) (0,4) à y=2
                    if (dy == 2 && ((Math.abs(dx) == 4 && dz == 0) || (dx == 0 && Math.abs(dz) == 4))) {
                        level.setBlock(p, net.minecraft.world.level.block.Blocks.GLASS.defaultBlockState(), 3);
                        continue;
                    }
                    level.setBlock(p, net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3);
                }
            }
        }
        // Toit y=4 plat + croix rouge
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos p = center.offset(dx, 4, dz);
                boolean isCross = (dx == 0 && Math.abs(dz) <= 2) || (dz == 0 && Math.abs(dx) <= 2);
                if (isCross) {
                    level.setBlock(p, net.minecraft.world.level.block.Blocks.RED_WOOL.defaultBlockState(), 3);
                } else {
                    level.setBlock(p, net.minecraft.world.level.block.Blocks.WHITE_WOOL.defaultBlockState(), 3);
                }
            }
        }
        // Intérieur équipement
        // Operating table à (-2,1,1)
        BlockPos op = center.offset(-2, 1, 1);
        if (level.getBlockState(op).isAir()) level.setBlock(op, com.veterinarium.registry.ModBlocks.OPERATING_TABLE.get().defaultBlockState(), 3);
        // Analysis table à (2,1,1)
        BlockPos an = center.offset(2, 1, 1);
        if (level.getBlockState(an).isAir()) level.setBlock(an, com.veterinarium.registry.ModBlocks.ANALYSIS_TABLE.get().defaultBlockState(), 3);
        // Infirmary à (0,1,2)
        BlockPos inf = center.offset(0, 1, 2);
        if (level.getBlockState(inf).isAir()) level.setBlock(inf, com.veterinarium.registry.ModBlocks.INFIRMARY.get().defaultBlockState(), 3);
        // Coffre à (-2,1,-2)
        BlockPos chestPos = center.offset(-2, 1, -2);
        if (level.getBlockState(chestPos).isAir()) level.setBlock(chestPos, net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState(), 3);
        // Lit blanc à (2,1,-2) + (2,1,-1) (bed is 2 blocks)
        BlockPos bedFoot = center.offset(2, 1, -2);
        BlockPos bedHead = center.offset(2, 1, -1);
        if (level.getBlockState(bedFoot).isAir() && level.getBlockState(bedHead).isAir()) {
            level.setBlock(bedFoot, net.minecraft.world.level.block.Blocks.WHITE_BED.defaultBlockState().setValue(net.minecraft.world.level.block.BedBlock.PART, net.minecraft.world.level.block.state.properties.BedPart.FOOT).setValue(net.minecraft.world.level.block.BedBlock.FACING, net.minecraft.core.Direction.NORTH), 3);
            level.setBlock(bedHead, net.minecraft.world.level.block.Blocks.WHITE_BED.defaultBlockState().setValue(net.minecraft.world.level.block.BedBlock.PART, net.minecraft.world.level.block.state.properties.BedPart.HEAD).setValue(net.minecraft.world.level.block.BedBlock.FACING, net.minecraft.core.Direction.NORTH), 3);
        }
        // Torches aux coins
        for (int dx : new int[]{-3, 3}) {
            for (int dz : new int[]{-3, 3}) {
                BlockPos tp = center.offset(dx, 2, dz);
                if (level.getBlockState(tp).isAir()) level.setBlock(tp, net.minecraft.world.level.block.Blocks.TORCH.defaultBlockState(), 3);
            }
        }
        return true;
    }
}
