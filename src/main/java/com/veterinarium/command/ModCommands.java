package com.veterinarium.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "veterinarium")
public class ModCommands {
    @SubscribeEvent
    public static void onRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("veterinarium")
            .then(Commands.literal("photo").executes(ModCommands::photo)));
        d.register(Commands.literal("photo").executes(ModCommands::photo));
    }

    private static int photo(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        ServerLevel level = src.getLevel();
        BlockPos origin = BlockPos.containing(src.getPosition());
        // trouve le sol à 15 blocs (évite gouffre)
        BlockPos hutPosTmp = origin.offset(15, 0, 0);
        int groundY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, hutPosTmp.getX(), hutPosTmp.getZ());
        // si gouffre profond, téléporte le joueur sur terrain plat
        if (origin.getY() < groundY - 10 || level.getBlockState(origin).isAir() && level.getBlockState(origin.below()).isAir()) {
            if (src.getEntity() instanceof ServerPlayer pl) {
                pl.teleportTo((double)hutPosTmp.getX() - 15, (double)groundY, (double)hutPosTmp.getZ());
                origin = BlockPos.containing(pl.position());
                hutPosTmp = origin.offset(15, 0, 0);
                groundY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, hutPosTmp.getX(), hutPosTmp.getZ());
            }
        }
        // aplanit 16x16 à hauteur du sol
        BlockPos hutPos = new BlockPos(hutPosTmp.getX(), groundY, hutPosTmp.getZ());
        BlockPos p1 = hutPos.offset(-8, -1, -8);
        BlockPos p2 = hutPos.offset(8, -1, 8);
        for (BlockPos p : BlockPos.betweenClosed(p1, p2)) level.setBlock(p, net.minecraft.world.level.block.Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        p1 = hutPos.offset(-8, 0, -8);
        p2 = hutPos.offset(8, 6, 8);
        for (BlockPos p : BlockPos.betweenClosed(p1, p2)) level.setBlock(p, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(hutPos, com.veterinarium.registry.ModBlocks.HOSPITAL_HUT.get().defaultBlockState(), 3);
        // force construction (normalement sneak-clic)
        if (level.getBlockState(hutPos).getBlock() instanceof com.veterinarium.block.HospitalHutBlock hutBlock) {
            net.minecraft.world.entity.player.Player pl = null;
            if (src.getEntity() instanceof net.minecraft.world.entity.player.Player p) pl = p;
            else if (level.getNearestPlayer(hutPos.getX(), hutPos.getY(), hutPos.getZ(), 64, false) != null) pl = level.getNearestPlayer(hutPos.getX(), hutPos.getY(), hutPos.getZ(), 64, false);
            if (pl != null) {
                boolean ok = hutBlock.buildHut(level, hutPos, pl);
                if (!ok) src.sendFailure(Component.literal("Zone encombrée, dégage un 9x9 plat !"));
                else {
                    // étend à 15x15 : sol et murs extérieurs
                    for (int dx=-7; dx<=7; dx++) for (int dz=-7; dz<=7; dz++) {
                        if (Math.abs(dx)==7 || Math.abs(dz)==7) {
                            level.setBlock(hutPos.offset(dx, -1, dz), net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(), 3);
                            for (int y=0; y<=3; y++) {
                                if (dx==0 && dz==-7 && (y==0||y==1)) continue; // porte
                                if (y==2 && Math.abs(dx)==7 && dz%2==0) { level.setBlock(hutPos.offset(dx, y, dz), net.minecraft.world.level.block.Blocks.GLASS.defaultBlockState(), 3); continue; }
                                if (y==2 && Math.abs(dz)==7 && dx%2==0) { level.setBlock(hutPos.offset(dx, y, dz), net.minecraft.world.level.block.Blocks.GLASS.defaultBlockState(), 3); continue; }
                                level.setBlock(hutPos.offset(dx, y, dz), net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3);
                            }
                        }
                    }
                    // toit 15x15
                    for (int dx=-7; dx<=7; dx++) for (int dz=-7; dz<=7; dz++) {
                        BlockPos tp = hutPos.offset(dx, 4, dz);
                        boolean isCross = (dx==0 && Math.abs(dz)<=2) || (dz==0 && Math.abs(dx)<=2);
                        if (level.getBlockState(tp).isAir()) level.setBlock(tp, (isCross ? net.minecraft.world.level.block.Blocks.RED_WOOL : net.minecraft.world.level.block.Blocks.WHITE_WOOL).defaultBlockState(), 3);
                    }
                    // fenetres supplémentaires internes 9x9
                    for (int dx : new int[]{-2, 2}) { level.setBlock(hutPos.offset(dx, 2, -4), net.minecraft.world.level.block.Blocks.GLASS.defaultBlockState(), 3); level.setBlock(hutPos.offset(dx, 2, 4), net.minecraft.world.level.block.Blocks.GLASS.defaultBlockState(), 3); }
                    for (int dz : new int[]{-2, 2}) { level.setBlock(hutPos.offset(-4, 2, dz), net.minecraft.world.level.block.Blocks.GLASS.defaultBlockState(), 3); level.setBlock(hutPos.offset(4, 2, dz), net.minecraft.world.level.block.Blocks.GLASS.defaultBlockState(), 3); }
                    // lanternes
                    for (int dx : new int[]{-2, 2}) for (int dz : new int[]{-2, 2}) level.setBlock(hutPos.offset(dx, 3, dz), net.minecraft.world.level.block.Blocks.LANTERN.defaultBlockState(), 3);
                    level.setBlock(hutPos.offset(0, 3, 0), net.minecraft.world.level.block.Blocks.LANTERN.defaultBlockState(), 3);
                    // lanternes extérieures 15x15
                    for (int dx : new int[]{-6, 6}) for (int dz : new int[]{-6, 6}) level.setBlock(hutPos.offset(dx, 3, dz), net.minecraft.world.level.block.Blocks.LANTERN.defaultBlockState(), 3);
                    // pots de fleurs
                    level.setBlock(hutPos.offset(-3, 1, 0), net.minecraft.world.level.block.Blocks.POTTED_OXEYE_DAISY.defaultBlockState(), 3);
                    level.setBlock(hutPos.offset(3, 1, 0), net.minecraft.world.level.block.Blocks.POTTED_BLUE_ORCHID.defaultBlockState(), 3);
                    level.setBlock(hutPos.offset(-6, 1, -6), net.minecraft.world.level.block.Blocks.POTTED_AZURE_BLUET.defaultBlockState(), 3);
                    level.setBlock(hutPos.offset(6, 1, -6), net.minecraft.world.level.block.Blocks.POTTED_RED_TULIP.defaultBlockState(), 3);
                    // chemin
                    for (int dz=-9; dz<=-7; dz++) level.setBlock(hutPos.offset(0, -1, dz), net.minecraft.world.level.block.Blocks.DIRT_PATH.defaultBlockState(), 3);
                    // matériel du mod exposé à l'intérieur (tables déjà posées par buildHut, ajoute le reste)
                    level.setBlock(hutPos.offset(-5, 1, 0), com.veterinarium.registry.ModBlocks.CONTAMINATOR.get().defaultBlockState(), 3);
                    level.setBlock(hutPos.offset(5, 1, 0), net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState(), 3);
                }
            }
        }
        // donne tout le matériel du mod (30 items)
        if (src.getEntity() instanceof ServerPlayer pl) {
            for (var it : new net.minecraft.world.item.Item[]{
                com.veterinarium.registry.ModItems.SCALPEL.get(), com.veterinarium.registry.ModItems.SCALPEL_DIAMOND.get(), com.veterinarium.registry.ModItems.SCALPEL_NETHERITE.get(),
                com.veterinarium.registry.ModItems.SUTURE_KIT.get(), com.veterinarium.registry.ModItems.SYRINGE.get(), com.veterinarium.registry.ModItems.MEDICAL_FILE.get(),
                com.veterinarium.registry.ModItems.BANDAGE.get(), com.veterinarium.registry.ModItems.ANESTHETIC.get(), com.veterinarium.registry.ModItems.COMPRESSION_BANDAGE.get(),
                com.veterinarium.registry.ModItems.ANTIBIOTIC.get(), com.veterinarium.registry.ModItems.ANTI_INFLAMMATORY.get(), com.veterinarium.registry.ModItems.ADRENALINE.get(), com.veterinarium.registry.ModItems.BLOOD_TRANSFUSION.get(),
                com.veterinarium.registry.ModItems.ANTIDOTE.get(), com.veterinarium.registry.ModItems.VET_SPHERE.get(), com.veterinarium.registry.ModItems.DNA_SYRINGE.get(), com.veterinarium.registry.ModItems.HELLFIRE_SERUM.get(),
                com.veterinarium.registry.ModItems.OPERATING_TABLE.get(), com.veterinarium.registry.ModItems.ANALYSIS_TABLE.get(), com.veterinarium.registry.ModItems.INFIRMARY.get(), com.veterinarium.registry.ModItems.HOSPITAL_HUT.get(), com.veterinarium.registry.ModItems.STRETCHER.get(), com.veterinarium.registry.ModItems.CONTAMINATOR.get()
            }) pl.getInventory().add(new ItemStack(it));
        }
        // summons à hauteur du sol du hut
        level.getServer().getCommands().performPrefixedCommand(src.withSuppressedOutput().withMaximumPermission(4), "summon veterinarium:wounded_wolf " + (hutPos.getX()+1) + " " + hutPos.getY() + " " + (hutPos.getZ()+1));
        level.getServer().getCommands().performPrefixedCommand(src.withSuppressedOutput().withMaximumPermission(4), "summon veterinarium:wounded_cat " + (hutPos.getX()-1) + " " + hutPos.getY() + " " + (hutPos.getZ()-1));
        src.sendSuccess(() -> Component.literal("Maison posee a 15 blocs - recule et F1 + F2"), false);
        return 1;
    }
}
