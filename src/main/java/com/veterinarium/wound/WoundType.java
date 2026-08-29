package com.veterinarium.wound;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

public enum WoundType {
    CONTUSION(0, "wound.veterinarium.contusion", "contusion", false, false),
    HEMORRAGIE(1, "wound.veterinarium.hemorragie", "hemorragie", false, true),
    FRACTURE(2, "wound.veterinarium.fracture", "fracture", true, false),
    INFECTION(3, "wound.veterinarium.infection", "infection", true, true),
    BRULURE(4, "wound.veterinarium.brulure", "brulure", true, true),
    SAIGNEMENT(5, "wound.veterinarium.saignement", "saignement", false, false);  // saignement actif: bandage = soin

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
        // pondération: 25% contusion, 20% hémorragie, 17% fracture, 13% infection, 12% brûlure, 13% saignement
        float f = r.nextFloat();
        if (f < 0.25f) return CONTUSION;
        if (f < 0.45f) return HEMORRAGIE;
        if (f < 0.62f) return FRACTURE;
        if (f < 0.75f) return INFECTION;
        if (f < 0.87f) return BRULURE;
        return SAIGNEMENT;
    }
}
