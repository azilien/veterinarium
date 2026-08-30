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
        // aplanit
        BlockPos p1 = origin.offset(10, -1, -6);
        BlockPos p2 = origin.offset(22, -1, 6);
        for (BlockPos p : BlockPos.betweenClosed(p1, p2)) level.setBlock(p, net.minecraft.world.level.block.Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        p1 = origin.offset(10, 0, -6);
        p2 = origin.offset(22, 6, 6);
        for (BlockPos p : BlockPos.betweenClosed(p1, p2)) level.setBlock(p, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        // maison
        p1 = origin.offset(12, -1, -3); p2 = origin.offset(18, -1, 3);
        for (BlockPos p : BlockPos.betweenClosed(p1, p2)) level.setBlock(p, net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(), 3);
        // murs briques
        for (int y=0;y<=3;y++) {
            for (int z=-3;z<=3;z++) { level.setBlock(origin.offset(12, y, z), net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3); level.setBlock(origin.offset(18, y, z), net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3); }
            for (int x=12;x<=18;x++) { level.setBlock(origin.offset(x, y, -3), net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3); level.setBlock(origin.offset(x, y, 3), net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3); }
        }
        // toit
        p1 = origin.offset(12, 4, -3); p2 = origin.offset(18, 4, 3);
        for (BlockPos p : BlockPos.betweenClosed(p1, p2)) level.setBlock(p, net.minecraft.world.level.block.Blocks.OAK_SLAB.defaultBlockState(), 3);
        // porte
        level.setBlock(origin.offset(12, 0, 0), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(origin.offset(12, 1, 0), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        // table et infirmerie
        level.setBlock(origin.offset(15, 0, 0), com.veterinarium.registry.ModBlocks.OPERATING_TABLE.get().defaultBlockState(), 3);
        level.setBlock(origin.offset(16, 0, 0), com.veterinarium.registry.ModBlocks.INFIRMARY.get().defaultBlockState(), 3);
        // donne items
        if (src.getEntity() instanceof ServerPlayer pl) {
            pl.getInventory().add(new ItemStack(com.veterinarium.registry.ModItems.SCALPEL.get()));
            pl.getInventory().add(new ItemStack(com.veterinarium.registry.ModItems.SUTURE_KIT.get()));
            pl.getInventory().add(new ItemStack(com.veterinarium.registry.ModItems.SYRINGE.get()));
            pl.getInventory().add(new ItemStack(com.veterinarium.registry.ModItems.MEDICAL_FILE.get()));
            pl.getInventory().add(new ItemStack(com.veterinarium.registry.ModItems.BANDAGE.get(), 16));
            pl.getInventory().add(new ItemStack(com.veterinarium.registry.ModItems.ANESTHETIC.get(), 16));
        }
        // summons via command
        level.getServer().getCommands().performPrefixedCommand(src.withSuppressedOutput().withMaximumPermission(4), "summon veterinarium:wounded_wolf " + (origin.getX()+16) + " " + origin.getY() + " " + (origin.getZ()+1));
        level.getServer().getCommands().performPrefixedCommand(src.withSuppressedOutput().withMaximumPermission(4), "summon veterinarium:wounded_cat " + (origin.getX()+14) + " " + origin.getY() + " " + (origin.getZ()-1));
        src.sendSuccess(() -> Component.literal("Maison posee a 15 blocs - recule et F1 + F2"), false);
        return 1;
    }
}
