package com.veterinarium.wound;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

public enum WoundType {
    CONTUSION(0, "§7Contusion légère", "contusion", false, false),
    HEMORRAGIE(1, "§cHémorragie", "hemorragie", false, true),      // besoin bandage après suture sinon resaignement
    FRACTURE(2, "§6Fracture", "fracture", true, false),            // besoin anesthésie AVANT scalpel
    INFECTION(3, "§5Infection", "infection", true, true);          // anesthésie + bandage recommandés

    private final int id;
    private final String display;
    private final String tag;
    private final boolean needsAnesthetic;
    private final boolean needsBandage;

    WoundType(int id, String display, String tag, boolean needsAnesthetic, boolean needsBandage) {
        this.id = id;
        this.display = display;
        this.tag = tag;
        this.needsAnesthetic = needsAnesthetic;
        this.needsBandage = needsBandage;
    }

    public int getId() { return id; }
    public String getDisplay() { return display; }
    public String getTag() { return "veterinarium_wound_" + tag; }
    public boolean needsAnesthetic() { return needsAnesthetic; }
    public boolean needsBandage() { return needsBandage; }
    public Component getDescription() {
        return switch (this) {
            case CONTUSION -> Component.literal("§7Contusion: soin standard Scalpel→Suture");
            case HEMORRAGIE -> Component.literal("§cHémorragie: saignement! Suture+Bandage requis sinon rechute");
            case FRACTURE -> Component.literal("§6Fracture: Anesthésie AVANT Scalpel ou échec 50% + cri");
            case INFECTION -> Component.literal("§5Infection: Anesthésie+Suture+Bandage, sinon poison");
        };
    }
    public static WoundType fromId(int id) {
        for (var v : values()) if (v.id == id) return v;
        return CONTUSION;
    }
    public static WoundType random(RandomSource r) {
        // pondération: 40% contusion, 25% hémorragie, 20% fracture, 15% infection
        float f = r.nextFloat();
        if (f < 0.40f) return CONTUSION;
        if (f < 0.65f) return HEMORRAGIE;
        if (f < 0.85f) return FRACTURE;
        return INFECTION;
    }
}
