package com.veterinarium.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class VeterinariumConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static class Common {
        // Spawn naturel
        public final ForgeConfigSpec.DoubleValue woundedSpawnChance;
        public final ForgeConfigSpec.IntValue drakeWeight;

        // Urgences
        public final ForgeConfigSpec.IntValue urgencyCooldownMin;
        public final ForgeConfigSpec.IntValue urgencyCooldownMax;
        public final ForgeConfigSpec.IntValue urgencyTimerMin;
        public final ForgeConfigSpec.IntValue urgencyTimerMax;
        public final ForgeConfigSpec.IntValue urgencyDistanceMin;
        public final ForgeConfigSpec.IntValue urgencyDistanceMax;

        // Épidémie
        public final ForgeConfigSpec.DoubleValue infectionSpreadChance;
        public final ForgeConfigSpec.DoubleValue infectionSpreadRange;
        public final ForgeConfigSpec.IntValue infectionQuarantineHutLevel;

        // Hut / Heal (info, pas utilisé direct mais exposé)
        public final ForgeConfigSpec.DoubleValue hutHealBase;
        public final ForgeConfigSpec.IntValue infirmaryRange;

        // Sphère
        public final ForgeConfigSpec.BooleanValue sphereRequiresHealed;
        public final ForgeConfigSpec.DoubleValue sphereFailChanceIfNotFullHealth;

        public Common(ForgeConfigSpec.Builder b) {
            b.push("spawn");
            woundedSpawnChance = b.comment("Chance qu'un animal vanilla spawn blessé (0.0-1.0) - défaut 0.08").defineInRange("woundedSpawnChance", 0.08, 0.0, 1.0);
            drakeWeight = b.comment("Poids spawn WoundedDrake en overworld (0 désactive) - défaut 4").defineInRange("drakeWeight", 4, 0, 100);
            b.pop();

            b.push("urgency");
            urgencyCooldownMin = b.comment("Cooldown min entre urgences (ticks 20=1s) - défaut 8000 ~6min40").defineInRange("urgencyCooldownMin", 8000, 100, 24000);
            urgencyCooldownMax = b.comment("Cooldown max entre urgences - défaut 14000 ~11min40").defineInRange("urgencyCooldownMax", 14000, 100, 48000);
            urgencyTimerMin = b.comment("Timer min pour sauver urgent (ticks) - défaut 6000 ~5min").defineInRange("urgencyTimerMin", 6000, 100, 24000);
            urgencyTimerMax = b.comment("Timer max urgent - défaut 10000 ~8min20 (min+4000) -> on utilise min+4000 aléatoire").defineInRange("urgencyTimerMax", 10000, 100, 48000);
            urgencyDistanceMin = b.comment("Distance min urgences du joueur").defineInRange("urgencyDistanceMin", 60, 10, 500);
            urgencyDistanceMax = b.comment("Distance max urgences").defineInRange("urgencyDistanceMax", 150, 20, 500);
            b.pop();

            b.push("epidemic");
            infectionSpreadChance = b.comment("Chance contagion INFECTION par check 2s à 4 blocs (0-1) - défaut 0.04").defineInRange("infectionSpreadChance", 0.04, 0.0, 1.0);
            infectionSpreadRange = b.comment("Rayon contagion").defineInRange("infectionSpreadRange", 4.0, 1.0, 16.0);
            infectionQuarantineHutLevel = b.comment("Niveau hut requis pour bloquer contagion (1-5, 6=jamais)").defineInRange("infectionQuarantineHutLevel", 3, 1, 6);
            b.pop();

            b.push("heal");
            hutHealBase = b.comment("Soin Hut Lv1 (info) - réel 1.5 +0.5/lv").defineInRange("hutHealBase", 1.5, 0.1, 10.0);
            infirmaryRange = b.comment("Rayon infirmerie").defineInRange("infirmaryRange", 8, 1, 32);
            b.pop();

            b.push("sphere");
            sphereRequiresHealed = b.comment("Sphère ne capture que si soigné").define("sphereRequiresHealed", true);
            sphereFailChanceIfNotFullHealth = b.comment("Chance échec sphère si PV<100% mais >80% (0=jamais)").defineInRange("sphereFailChanceIfNotFullHealth", 0.0, 0.0, 1.0);
            b.pop();
        }
    }
}
