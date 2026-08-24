package com.veterinarium.event;

import com.veterinarium.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class InfirmaryHealEvent {

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Level level = event.level;
        if (level.isClientSide) return;
        if (level.getGameTime() % 40 != 0) return; // toutes les 2 sec

        // Cherche joueurs proches d'une infirmerie
        for (var player : level.players()) {
            BlockPos pPos = player.blockPosition();
            // scan 8 de rayon
            boolean nearInfirmary = false;
            for (BlockPos pos : BlockPos.betweenClosed(pPos.offset(-8, -2, -8), pPos.offset(8, 2, 8))) {
                if (level.getBlockState(pos).is(ModBlocks.INFIRMARY.get())) {
                    nearInfirmary = true;
                    break;
                }
            }
            if (!nearInfirmary) continue;

            AABB area = new AABB(pPos).inflate(8);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
                    e -> e.getHealth() < e.getMaxHealth() && e.isAlive());

            for (LivingEntity e : entities) {
                e.heal(1.0f); // 0.5 coeur toutes les 2 sec
                // petite particule implicite via heal
            }
        }
    }
}
