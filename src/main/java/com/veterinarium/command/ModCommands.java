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
        // aplanit 15x13 a 15 blocs
        BlockPos p1 = origin.offset(10, -1, -6);
        BlockPos p2 = origin.offset(22, -1, 6);
        for (BlockPos p : BlockPos.betweenClosed(p1, p2)) level.setBlock(p, net.minecraft.world.level.block.Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        p1 = origin.offset(10, 0, -6);
        p2 = origin.offset(22, 6, 6);
        for (BlockPos p : BlockPos.betweenClosed(p1, p2)) level.setBlock(p, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        // pose le hut et construit la vraie maison 9x9 via buildHut
        BlockPos hutPos = origin.offset(15, 0, 0);
        level.setBlock(hutPos, com.veterinarium.registry.ModBlocks.HOSPITAL_HUT.get().defaultBlockState(), 3);
        // force construction (normalement sneak-clic)
        if (level.getBlockState(hutPos).getBlock() instanceof com.veterinarium.block.HospitalHutBlock hutBlock) {
            net.minecraft.world.entity.player.Player pl = null;
            if (src.getEntity() instanceof net.minecraft.world.entity.player.Player p) pl = p;
            else if (level.getNearestPlayer(hutPos.getX(), hutPos.getY(), hutPos.getZ(), 64, false) != null) pl = level.getNearestPlayer(hutPos.getX(), hutPos.getY(), hutPos.getZ(), 64, false);
            if (pl != null) {
                boolean ok = hutBlock.buildHut(level, hutPos, pl);
                if (!ok) src.sendFailure(Component.literal("Zone encombrée, dégage un 9x9 plat !"));
            }
        }
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
