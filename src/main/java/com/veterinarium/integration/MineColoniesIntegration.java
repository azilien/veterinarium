package com.veterinarium.integration;

import com.veterinarium.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * Bridge MineColonies - sans dépendance dure.
 * Si MineColonies est présent, les citoyens ("citizen" dans le className) près d'une Infirmerie
 * se soignent 2x plus vite et le Builder peut crafter nos blocs (via recettes conditionnelles).
 * On détecte les citoyens par réflexion sur le nom de classe pour éviter NoClassDefFoundError.
 */
@Mod.EventBusSubscriber
public class MineColoniesIntegration {

    public static boolean isLoaded() {
        return ModList.get().isLoaded("minecolonies");
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (!isLoaded()) return;
        if (event.phase != TickEvent.Phase.END) return;
        Level level = event.level;
        if (level.isClientSide) return;
        if (level.getGameTime() % 60 != 0) return; // toutes les 3s

        for (var player : level.players()) {
            BlockPos pPos = player.blockPosition();
            boolean nearInfirmary = false;
            for (BlockPos pos : BlockPos.betweenClosed(pPos.offset(-12, -3, -12), pPos.offset(12, 3, 12))) {
                if (level.getBlockState(pos).is(ModBlocks.INFIRMARY.get())) {
                    nearInfirmary = true;
                    break;
                }
            }
            if (!nearInfirmary) continue;

            AABB area = new AABB(pPos).inflate(12);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> {
                String cn = e.getClass().getName().toLowerCase();
                return cn.contains("citizen") || cn.contains("colonist");
            });
            for (LivingEntity citizen : entities) {
                if (citizen.getHealth() < citizen.getMaxHealth()) {
                    citizen.heal(1.5f);
                    // Effet visuel léger
                    if (level.random.nextFloat() < 0.1f) {
                        citizen.addTag("veterinarium_minecolonies_cared");
                    }
                }
            }
            if (!entities.isEmpty() && level.random.nextFloat() < 0.05f) {
                // Message discret
                // player.displayClientMessage(Component.literal("§a[Infirmerie] §7Vos citoyens MineColonies se soignent à proximité."), true);
            }
        }
    }
}
