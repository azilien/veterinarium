package com.veterinarium.data;

import com.veterinarium.wound.WoundType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Suivi de progression bestiaire.
 * Stocké en player persistentData (survit déco/reco, pas de cap).
 * Clés: VetDiagTotal, VetOpsTotal, VetSutureTotal, VetHealedTotal,
 *       VetSeen_<entityId>, VetSeenWound_<id>, VetHealed_<entityId>
 */
public class BestiaryProgress {

    public static String entityKey(LivingEntity e) {
        if (e instanceof com.veterinarium.entity.WoundedWolfEntity) return "wounded_wolf";
        if (e instanceof com.veterinarium.entity.WoundedCatEntity) return "wounded_cat";
        if (e instanceof com.veterinarium.entity.WoundedHorseEntity) return "wounded_horse";
        if (e instanceof com.veterinarium.entity.WoundedFoxEntity) return "wounded_fox";
        if (e instanceof com.veterinarium.entity.WoundedVillagerEntity) return "wounded_villager";
        if (e instanceof com.veterinarium.entity.WoundedDrakeEntity) return "wounded_drake";
        if (e instanceof com.veterinarium.entity.HellfireRavagerEntity) return "hellfire_ravager";
        // generic wounded (vanilla tag)
        if (e.getTags().contains("veterinarium_wounded") || e.getPersistentData().contains("VetWound")) {
            String name = e.getType().toString(); // fallback
            // essaye registry key
            try {
                var key = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(e.getType());
                if (key != null) return key.getPath(); // ex: wolf, horse
            } catch (Exception ignored) {}
            return name;
        }
        return "generic";
    }

    public static void recordDiagnose(Player p, LivingEntity target, WoundType wt) {
        CompoundTag nbt = p.getPersistentData();
        nbt.putInt("VetDiagTotal", nbt.getInt("VetDiagTotal") + 1);
        nbt.putBoolean("VetSeen_" + entityKey(target), true);
        nbt.putBoolean("VetSeenWound_" + wt.getId(), true);
        nbt.putInt("VetLastWound", wt.getId());
        nbt.putString("VetLastTarget", target.getName().getString());
        // aussi compteur par wound
        String wk = "VetDiagWound_" + wt.getId();
        nbt.putInt(wk, nbt.getInt(wk) + 1);
    }

    public static void recordOperate(Player p, LivingEntity target) {
        CompoundTag nbt = p.getPersistentData();
        nbt.putInt("VetOpsTotal", nbt.getInt("VetOpsTotal") + 1);
        nbt.putBoolean("VetSeen_" + entityKey(target), true);
    }

    public static void recordSuture(Player p, LivingEntity target, boolean healed) {
        CompoundTag nbt = p.getPersistentData();
        nbt.putInt("VetSutureTotal", nbt.getInt("VetSutureTotal") + 1);
        nbt.putBoolean("VetSeen_" + entityKey(target), true);
        if (healed || target.getHealth() >= target.getMaxHealth() * 0.9f) {
            nbt.putInt("VetHealedTotal", nbt.getInt("VetHealedTotal") + 1);
            String ek = entityKey(target);
            nbt.putInt("VetHealed_" + ek, nbt.getInt("VetHealed_" + ek) + 1);
            // débloque brulure progression si heal beaucoup
            if (nbt.getInt("VetHealedTotal") >= 5) nbt.putBoolean("VetSeenWound_" + WoundType.BRULURE.getId(), true);
        }
    }

    public static void recordAnalysis(Player p, WoundType wt, String targetName) {
        CompoundTag nbt = p.getPersistentData();
        nbt.putInt("VetAnalysisTotal", nbt.getInt("VetAnalysisTotal") + 1);
        nbt.putBoolean("VetSeenWound_" + wt.getId(), true);
    }

    public static int getDiagTotal(Player p) { return p.getPersistentData().getInt("VetDiagTotal"); }
    public static int getOpsTotal(Player p) { return p.getPersistentData().getInt("VetOpsTotal"); }
    public static int getSutureTotal(Player p) { return p.getPersistentData().getInt("VetSutureTotal"); }
    public static int getHealedTotal(Player p) { return p.getPersistentData().getInt("VetHealedTotal"); }
    public static int getHealedFor(Player p, String key) { return p.getPersistentData().getInt("VetHealed_" + key); }
    public static boolean hasSeen(Player p, String key) { return p.getPersistentData().getBoolean("VetSeen_" + key); }
    public static boolean hasSeenWound(Player p, int woundId) { return p.getPersistentData().getBoolean("VetSeenWound_" + woundId); }
    public static int getAnalysisTotal(Player p) { return p.getPersistentData().getInt("VetAnalysisTotal"); }

    public static int getUnlockedCreatureCount(Player p) {
        int c = 0;
        for (String k : new String[]{"wounded_wolf","wounded_cat","wounded_horse","wounded_fox","wounded_villager","wounded_drake"}) if (hasSeen(p,k)) c++;
        if (hasSeen(p,"hellfire_ravager")) c = Math.min(c+1, 6); // hellfire compte comme bonus mais cap 6
        return Math.min(c,6);
    }
    public static int getUnlockedWoundCount(Player p) {
        int c=0;
        for (WoundType wt: WoundType.values()) if (hasSeenWound(p, wt.getId())) c++;
        return c;
    }
    public static float getCompletionPercent(Player p) {
        int totalCreatures = 6;
        int totalWounds = WoundType.values().length;
        int seenC = getUnlockedCreatureCount(p);
        int seenW = getUnlockedWoundCount(p);
        int healed = Math.min(getHealedTotal(p), 20); // cap 20 pour 100%
        return ( (seenC/(float)totalCreatures)*0.3f + (seenW/(float)totalWounds)*0.3f + (healed/20f)*0.4f )*100f;
    }
}
