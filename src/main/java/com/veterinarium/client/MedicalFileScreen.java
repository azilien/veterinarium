package com.veterinarium.client;

import com.veterinarium.data.BestiaryProgress;
import com.veterinarium.registry.ModItems;
import com.veterinarium.wound.WoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class MedicalFileScreen extends Screen {
    private int currentPage = 0;
    private static final int TOTAL_PAGES = 9; // 0 cover, 1-5 creatures, 6 pathologies, 7 protocole, 8 progression
    private int worldWounded = 0, worldHealed = 0, worldOperated = 0;
    private int tickCounter = 0;

    // creature defs
    private static class CreatureEntry {
        String id; String name; String sci; String habitat; String trait; ItemStack egg; int color;
        CreatureEntry(String id, String name, String sci, String habitat, String trait, ItemStack egg, int color) {
            this.id=id; this.name=name; this.sci=sci; this.habitat=habitat; this.trait=trait; this.egg=egg; this.color=color;
        }
    }
    private final CreatureEntry[] creatures = new CreatureEntry[]{
        new CreatureEntry("wounded_wolf","Loup Blessé","Canis lupus - Alpha","Forêts / Plaines","Hurle si <50% HP", new ItemStack(ModItems.WOUNDED_WOLF_SPAWN_EGG.get()), 0xFFD7C9B5),
        new CreatureEntry("wounded_cat","Chat Blessé","Felis catus - Agilité","Villages / Jungle","Miaulement discret", new ItemStack(ModItems.WOUNDED_CAT_SPAWN_EGG.get()), 0xFFE6C8A0),
        new CreatureEntry("wounded_horse","Cheval Blessé","Equus ferus - Monture","Plaines / Savane","Galop ralenti", new ItemStack(ModItems.WOUNDED_HORSE_SPAWN_EGG.get()), 0xFFF0E6D2),
        new CreatureEntry("wounded_fox","Renard Blessé","Vulpes vulpes - Rusé","Taïga / Neige","Confiance à gagner", new ItemStack(ModItems.WOUNDED_FOX_SPAWN_EGG.get()), 0xFFD76F2D),
        new CreatureEntry("wounded_villager","Villageois Blessé","Homo sapiens - Civil","Villages","Donne émeraude si soigné", new ItemStack(ModItems.WOUNDED_VILLAGER_SPAWN_EGG.get()), 0xFF8ACB8A)
    };

    public MedicalFileScreen() {
        super(Component.literal("Dossier Médical"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int by = this.height / 2 + 78;
        this.addRenderableWidget(Button.builder(Component.literal("◀"), b -> { if(currentPage>0) currentPage--; })
                .bounds(cx - 115, by, 20, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("▶"), b -> { if(currentPage<TOTAL_PAGES-1) currentPage++; })
                .bounds(cx + 95, by, 20, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Fermer"), b -> this.onClose())
                .bounds(cx - 40, by, 80, 20).build());
        // clickable dots are rendered manually, no buttons
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0 && currentPage > 0) currentPage--;
        else if (scrollY < 0 && currentPage < TOTAL_PAGES-1) currentPage++;
        return true;
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 262 || keyCode == 258 || keyCode == 32) { // right, tab, space
            if (currentPage < TOTAL_PAGES-1) { currentPage++; return true; }
        }
        if (keyCode == 263) { if (currentPage>0) { currentPage--; return true; } }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        tickCounter++;
        if (tickCounter % 20 == 0) refreshWorldStats();
    }

    private void refreshWorldStats() {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        // scan limité 128 blocs autour joueur pour perf, fallback global si null player
        Player p = Minecraft.getInstance().player;
        List<LivingEntity> all;
        if (p != null) {
            net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(p.blockPosition()).inflate(128, 64, 128);
            all = level.getEntitiesOfClass(LivingEntity.class, area);
            // aussi compte global rapide via tags -> on scan area large uniquement
        } else {
            all = level.getEntitiesOfClass(LivingEntity.class, new net.minecraft.world.phys.AABB(-30000000,-64,-30000000,30000000,320,30000000));
        }
        int w=0,h=0,o=0;
        for (LivingEntity e: all) {
            if (e.getTags().contains("veterinarium_wounded")) w++;
            if (e.getTags().contains("veterinarium_healed")) h++;
            if (e.getTags().contains("veterinarium_operated")) o++;
        }
        worldWounded=w; worldHealed=h; worldOperated=o;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gfx, mouseX, mouseY, partialTick);
        int x = this.width / 2 - 120;
        int y = this.height / 2 - 85;
        int w = 240;
        int h = 170;

        // ombre
        gfx.fill(x+4, y+4, x+w+4, y+h+4, 0x44000000);
        // bord cuir
        gfx.fill(x, y, x+w, y+h, 0xFF2B1B0F);
        // page papier
        gfx.fill(x+2, y+2, x+w-2, y+h-2, 0xFFF5E6C8);
        // header marron
        gfx.fill(x+4, y+4, x+w-4, y+18, 0xFF8B4513);
        // header text
        String header = switch(currentPage) {
            case 0 -> "DOSSIER MEDICAL - COUVERTURE";
            case 1,2,3,4,5 -> "BESTIAIRE - " + creatures[currentPage-1].name.toUpperCase();
            case 6 -> "PATHOLOGIES - 5 BLESSURES";
            case 7 -> "PROTOCOLE ASFAX - BLOQU+";
            case 8 -> "PROGRESSION - STATS";
            default -> "DOSSIER MEDICAL";
        };
        gfx.drawCenteredString(this.font, "§l"+header, this.width/2, y+8, 0xFFFFFF);
        gfx.drawCenteredString(this.font, "Veterinarium - Hopital des Monstres", this.width/2, y+20, 0x6B3A1F);

        // contenu selon page
        if (currentPage==0) renderCover(gfx, x, y, w, h);
        else if (currentPage>=1 && currentPage<=5) renderCreature(gfx, x, y, w, h, creatures[currentPage-1]);
        else if (currentPage==6) renderPathologies(gfx, x, y, w, h);
        else if (currentPage==7) renderProtocol(gfx, x, y, w, h);
        else if (currentPage==8) renderProgress(gfx, x, y, w, h);

        // footer pagination dots
        int dotY = y+h-10;
        int dotX0 = this.width/2 - (TOTAL_PAGES*7)/2;
        for(int i=0;i<TOTAL_PAGES;i++) {
            int col = i==currentPage ? 0xFF8B4513 : 0xFFD2B48C;
            gfx.fill(dotX0 + i*7, dotY, dotX0 + i*7 +5, dotY+5, col);
            if(i==currentPage) gfx.fill(dotX0 + i*7 +1, dotY+1, dotX0 + i*7 +4, dotY+4, 0xFFFFFFFF);
        }
        gfx.drawCenteredString(this.font, (currentPage+1)+"/"+TOTAL_PAGES, this.width/2, dotY+7, 0x8B4513);
        gfx.drawString(this.font, Component.literal("§oPour Asfax - Infirmier au bloc"), x+8, y+h-22, 0x8B4513, false);
        gfx.drawString(this.font, Component.literal("§7◀/▶ ou molette"), x+w-68, y+h-22, 0x8B4513, false);

        super.render(gfx, mouseX, mouseY, partialTick);
    }

    private void renderCover(GuiGraphics gfx, int x, int y, int w, int h) {
        Player p = Minecraft.getInstance().player;
        int ty = y + 32;
        // grande croix rouge
        gfx.fill(this.width/2 -22, ty+2, this.width/2+22, ty+14, 0xFFB22222);
        gfx.fill(this.width/2 -8, ty-10, this.width/2+8, ty+26, 0xFFB22222);
        gfx.fill(this.width/2 -18, ty+4, this.width/2+18, ty+12, 0xFFFFFFFF);
        gfx.fill(this.width/2 -6, ty-8, this.width/2+6, ty+24, 0xFFFFFFFF);
        // sous titre
        ty += 36;
        gfx.drawCenteredString(this.font, "§6§lChronicles of the Wounded Beasts", this.width/2, ty, 0x8B4513);
        ty+=10;
        gfx.drawCenteredString(this.font, "§7Tu ne domptes plus, tu soignes.", this.width/2, ty, 0x5A3E2B);
        ty+=16;
        // stats mondiaux rapides
        gfx.drawString(this.font, Component.literal("§8Monde (" + (p!=null? "rayon 128":"global") + "):"), x+12, ty, 0x000000, false); ty+=10;
        gfx.drawString(this.font, Component.literal(" §c☠ Blessés: " + worldWounded + "  §a❤ Soignés: " + worldHealed + "  §c✚ Opérés: " + worldOperated), x+12, ty, 0x000000, false); ty+=10;
        gfx.drawString(this.font, Component.literal(" §7Total pris en charge: " + (worldWounded+worldHealed)), x+12, ty, 0x000000, false); ty+=14;

        if (p != null) {
            int diag = BestiaryProgress.getDiagTotal(p);
            int ops = BestiaryProgress.getOpsTotal(p);
            int sut = BestiaryProgress.getSutureTotal(p);
            int healed = BestiaryProgress.getHealedTotal(p);
            float comp = BestiaryProgress.getCompletionPercent(p);
            gfx.drawString(this.font, Component.literal("§6Ton avancement:"), x+12, ty, 0x000000, false); ty+=10;
            gfx.drawString(this.font, Component.literal(" §b🔬 Diag: " + diag + "  §c✂ Ops: " + ops + "  §d🪡 Sutures: " + sut), x+12, ty, 0x000000, false); ty+=10;
            gfx.drawString(this.font, Component.literal(" §a❤ Guéris: " + healed + "  §eAnalyses: " + BestiaryProgress.getAnalysisTotal(p)), x+12, ty, 0x000000, false); ty+=10;
            // Barre progression
            int barW = w-24;
            int barX = x+12;
            gfx.fill(barX, ty+2, barX+barW, ty+8, 0xFFD2B48C);
            int filled = (int)(barW * (comp/100f));
            gfx.fill(barX, ty+2, barX+filled, ty+8, 0xFF2E8B57);
            gfx.drawString(this.font, Component.literal(String.format(" §7%.0f%% complété", comp)), barX+barW+4, ty+1, 0x000000, false);
            ty+=14;
            gfx.drawString(this.font, Component.literal(" §7Créatures: " + BestiaryProgress.getUnlockedCreatureCount(p)+"/5  §7Pathologies: "+BestiaryProgress.getUnlockedWoundCount(p)+"/5"), x+12, ty, 0x000000, false); ty+=10;
        }
        gfx.drawString(this.font, Component.literal("§8→ Feuillette avec ▶ pour voir le bestiaire"), x+12, y+h-34, 0x000000, false);
    }

    private void renderCreature(GuiGraphics gfx, int x, int y, int w, int h, CreatureEntry c) {
        Player p = Minecraft.getInstance().player;
        boolean seen = p==null || BestiaryProgress.hasSeen(p, c.id);
        int ty = y + 30;
        // icone oeuf
        if (seen) {
            gfx.renderItem(c.egg, x+14, ty-6);
            gfx.renderItemDecorations(this.font, c.egg, x+14, ty-6);
        } else {
            gfx.fill(x+14, ty-6, x+30, ty+10, 0xFFCCCCCC);
            gfx.drawString(this.font, Component.literal("?"), x+20, ty-1, 0x000000, false);
        }
        // nom + sci
        gfx.drawString(this.font, Component.literal((seen? "§l"+c.name : "§7§l??? - Non découvert")), x+38, ty, seen?0x000000:0x888888, false);
        gfx.drawString(this.font, Component.literal("§o" + (seen? c.sci : "Inconnu")), x+38, ty+10, 0x8B4513, false);
        ty+=22;
        // habitat etc
        gfx.drawString(this.font, Component.literal("§6Habitat: §7" + (seen? c.habitat : "???")), x+12, ty, 0x000000, false); ty+=9;
        gfx.drawString(this.font, Component.literal("§6Trait: §7" + (seen? c.trait : "???")), x+12, ty, 0x000000, false); ty+=9;
        // soin
        gfx.drawString(this.font, Component.literal("§6Soin: §7Scalpel→Suture (+33% tame)"), x+12, ty, 0x000000, false); ty+=9;
        if (c.id.equals("wounded_fox")) gfx.drawString(this.font, Component.literal(" §7→ Confiance renard via suture"), x+12, ty, 0x000000, false);
        if (c.id.equals("wounded_villager")) gfx.drawString(this.font, Component.literal(" §7→ Héros du village + émeraude"), x+12, ty, 0x000000, false);
        if (c.id.equals("wounded_horse")) gfx.drawString(this.font, Component.literal(" §7→ Apprivoise avec pomme dorée"), x+12, ty, 0x000000, false);
        if (!c.id.equals("wounded_wolf") && !c.id.equals("wounded_cat")) ty+=0; else ty+=0;
        if (seen) ty+=2; else ty+=2;
        gfx.drawString(this.font, Component.literal("§8Pathologies possibles: toutes (5 types)"), x+12, ty, 0x000000, false); ty+=9;
        gfx.drawString(this.font, Component.literal(" §7Contusion  Hémorragie  Fracture"), x+12, ty, 0x000000, false); ty+=9;
        gfx.drawString(this.font, Component.literal(" §7Infection  Brûlure"), x+12, ty, 0x000000, false); ty+=12;

        // stats perso
        if (p != null) {
            int healed = BestiaryProgress.getHealedFor(p, c.id);
            gfx.drawString(this.font, Component.literal("§6Toi: §a" + healed + " soigné(s)"), x+12, ty, 0x000000, false); ty+=9;
            if (!seen) {
                gfx.drawString(this.font, Component.literal("§c⚠ Utilise Seringue sur cette créature pour débloquer"), x+12, ty, 0xCC0000, false); ty+=9;
            } else if (healed==0) {
                gfx.drawString(this.font, Component.literal("§e→ Soigne-en un pour valider l'entrée !"), x+12, ty, 0x000000, false); ty+=9;
            } else {
                gfx.drawString(this.font, Component.literal("§a✔ Entrée validée !"), x+12, ty, 0x006400, false); ty+=9;
            }
        }
        // world count pour cette creature? scan limité 128
        ty+=2;
        gfx.drawString(this.font, Component.literal("§8Astuce Asfax: infirmerie/hut accélère la récup."), x+12, ty, 0x666666, false);
    }

    private void renderPathologies(GuiGraphics gfx, int x, int y, int w, int h) {
        Player p = Minecraft.getInstance().player;
        int ty = y + 30;
        gfx.drawString(this.font, Component.literal("§8Chaque blessé a 1 pathologie aléatoire (poids)."), x+12, ty, 0x000000, false); ty+=12;
        WoundType[] all = WoundType.values();
        // headers
        gfx.drawString(this.font, Component.literal("§lType            Requis          Risque"), x+12, ty, 0x8B0000, false); ty+=9;
        gfx.fill(x+12, ty, x+w-12, ty+1, 0xFF8B4513); ty+=5;
        for(WoundType wt: all) {
            boolean seen = p==null || BestiaryProgress.hasSeenWound(p, wt.getId());
            String req = "";
            if (!wt.needsAnesthetic() && !wt.needsBandage()) req="Scalpel→Suture";
            else {
                if (wt.needsAnesthetic()) req+="Anesth. ";
                if (wt.needsBandage()) req+="Bandage";
            }
            String risk = switch(wt) {
                case CONTUSION -> "Aucun";
                case HEMORRAGIE -> "Rechute 50%";
                case FRACTURE -> "Douleur 50%";
                case INFECTION -> "Poison";
                case BRULURE -> "Feu continu";
            };
            String line = String.format(" %s  %s  %s", wt.getDisplay(), req, risk);
            // truncate
            int col = seen ? 0x000000 : 0x999999;
            String disp = seen ? line : " §7??? - Non diagnostiquée";
            gfx.drawString(this.font, Component.literal(disp), x+12, ty, col, false);
            // description sous ligne si vu
            if (seen) {
                ty+=9;
                gfx.drawString(this.font, wt.getDescription(), x+18, ty, 0x333333, false);
            }
            ty+=11;
        }
        gfx.drawString(this.font, Component.literal("§6Poids spawn: Contu 35% Hemor 22% Frac 18% Infec 13% Brul 12%"), x+12, ty, 0x000000, false); ty+=10;
        gfx.drawString(this.font, Component.literal("§7Table d'Analyse = mémorise & affiche requis"), x+12, ty, 0x000000, false);
    }

    private void renderProtocol(GuiGraphics gfx, int x, int y, int w, int h) {
        int ty = y + 30;
        gfx.drawString(this.font, Component.literal("§lProtocole Asfax - 4 étapes"), x+12, ty, 0x8B0000, false); ty+=12;
        String[] steps = {
            "§61. Seringue §7- Diagnostic HP + patho",
            "  §7Clic créature → HP% + type + stocke",
            "  §7pour Table d'Analyse. + lenteur 5s",
            "§62. Scalpel §7- Incision (+1❤) tag operated",
            "  §7Fracture/Infect/Brûlure → §dAnesthésiant",
            "  §7sinon 50% échec (cri, -1❤). Table fournit",
            "§63. Kit Suture §7- (+3❤ + Régé II)",
            "  §7Hémorra/Infec/Brûlure → §eBandage",
            "  §733% tame si Tamable. Sinon infection.",
            "§64. Infirmerie/Hut/Brancard §7- Heal zone",
            "  §70.5❤/2s (inf) 1.5-3.5❤/2s hut Lv1-5"
        };
        for(String s: steps) { gfx.drawString(this.font, Component.literal(s), x+12, ty, 0x000000, false); ty+=9; }
        ty+=4;
        gfx.drawString(this.font, Component.literal("§c⚠ Suture sans scalpel = Poison !"), x+12, ty, 0xCC0000, false); ty+=10;
        gfx.drawString(this.font, Component.literal("§aInfirmerie: 8 blocs  §bHut: 16-32 blocs"), x+12, ty, 0x000000, false);
    }

    private void renderProgress(GuiGraphics gfx, int x, int y, int w, int h) {
        Player p = Minecraft.getInstance().player;
        int ty = y + 30;
        if (p==null) { gfx.drawString(this.font, Component.literal("§7Pas de joueur"), x+12, ty, 0x000000,false); return; }
        gfx.drawString(this.font, Component.literal("§6Avancement détaillé"), x+12, ty, 0x000000,false); ty+=12;
        gfx.drawString(this.font, Component.literal(" §7Diagnostiqués: " + BestiaryProgress.getDiagTotal(p)), x+12, ty,0x000000,false); ty+=9;
        gfx.drawString(this.font, Component.literal(" §7Opérations: " + BestiaryProgress.getOpsTotal(p)), x+12, ty,0x000000,false); ty+=9;
        gfx.drawString(this.font, Component.literal(" §7Sutures: " + BestiaryProgress.getSutureTotal(p)), x+12, ty,0x000000,false); ty+=9;
        gfx.drawString(this.font, Component.literal(" §aGuéris totaux: " + BestiaryProgress.getHealedTotal(p) + " /20"), x+12, ty,0x000000,false); ty+=9;
        gfx.drawString(this.font, Component.literal(" §bAnalyses table: " + BestiaryProgress.getAnalysisTotal(p)), x+12, ty,0x000000,false); ty+=12;

        // liste créatures
        gfx.drawString(this.font, Component.literal("§6Créatures:"), x+12, ty,0x000000,false); ty+=10;
        for(CreatureEntry ce: creatures) {
            boolean seen = BestiaryProgress.hasSeen(p, ce.id);
            int healed = BestiaryProgress.getHealedFor(p, ce.id);
            String icon = seen ? (healed>0 ? "§a✔" : "§e○") : "§7?";
            gfx.drawString(this.font, Component.literal(String.format(" %s %s : %d soignés", icon, ce.name, healed)), x+12, ty, 0x000000,false); ty+=9;
        }
        ty+=2;
        gfx.drawString(this.font, Component.literal("§6Pathologies vues: " + BestiaryProgress.getUnlockedWoundCount(p) + "/5"), x+12, ty,0x000000,false); ty+=9;
        for(WoundType wt: WoundType.values()) {
            boolean seen = BestiaryProgress.hasSeenWound(p, wt.getId());
            gfx.drawString(this.font, Component.literal((seen?"§a✔ ":"§7? ") + wt.getDisplay()), x+14, ty, seen?0x000000:0x999999,false); ty+=9;
        }
        ty+=4;
        float comp = BestiaryProgress.getCompletionPercent(p);
        gfx.drawString(this.font, Component.literal(String.format("§lComplétion: %.0f%%", comp)), x+12, ty, 0x8B0000,false); ty+=9;
        if (comp>=100) gfx.drawString(this.font, Component.literal("§6★ Maître Vétérinaire ! Parle au hut Lv5"), x+12, ty, 0xFFD700,false);
        else gfx.drawString(this.font, Component.literal("§7Objectif série Asfax: 100% = 20 soins + 5 créatures + 5 pathos"), x+12, ty, 0x666666,false);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
