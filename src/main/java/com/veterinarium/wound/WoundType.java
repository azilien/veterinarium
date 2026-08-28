package com.veterinarium.wound;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

public enum WoundType {
    CONTUSION(0, "wound.veterinarium.contusion", "contusion", false, false),
    HEMORRAGIE(1, "wound.veterinarium.hemorragie", "hemorragie", false, true),      // besoin bandage après suture sinon resaignement
    FRACTURE(2, "wound.veterinarium.fracture", "fracture", true, false),            // besoin anesthésie AVANT scalpel
    INFECTION(3, "wound.veterinarium.infection", "infection", true, true),          // anesthésie + bandage recommandés
    BRULURE(4, "wound.veterinarium.brulure", "brulure", true, true);                // brûlure grave: anesthésie + bandage + effet feu

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
    public String getDisplay() { return Component.translatable(display).getString(); }
    public String getTag() { return "veterinarium_wound_" + tag; }
    public boolean needsAnesthetic() { return needsAnesthetic; }
    public boolean needsBandage() { return needsBandage; }
    public Component getDescription() {
        return Component.translatable(display + ".desc");
    }
    public static WoundType fromId(int id) {
        for (var v : values()) if (v.id == id) return v;
        return CONTUSION;
    }
    public static WoundType random(RandomSource r) {
        // pondération: 35% contusion, 22% hémorragie, 18% fracture, 13% infection, 12% brûlure
        float f = r.nextFloat();
        if (f < 0.35f) return CONTUSION;
        if (f < 0.57f) return HEMORRAGIE;
        if (f < 0.75f) return FRACTURE;
        if (f < 0.88f) return INFECTION;
        return BRULURE;
    }
}
