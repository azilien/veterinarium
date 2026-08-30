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
    private static final int TOTAL_PAGES = 11;
    private int worldWounded = 0, worldHealed = 0, worldOperated = 0;
    private int tickCounter = 0;
    private int lastMouseX = 0, lastMouseY = 0;

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
        new CreatureEntry("wounded_villager","Villageois Blessé","Homo sapiens - Civil","Villages","Donne émeraude si soigné", new ItemStack(ModItems.WOUNDED_VILLAGER_SPAWN_EGG.get()), 0xFF8ACB8A),
        new CreatureEntry("wounded_drake","Drake Blessé","Draco nocturnus - Boss","End / Montagnes","Vol +60❤ Boss, souffle", new ItemStack(ModItems.WOUNDED_DRAKE_SPAWN_EGG.get()), 0xFF1A1A2E)
    };

    public MedicalFileScreen() {
        super(Component.literal("Dossier Médical"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int by = this.height / 2 + 90;
        this.addRenderableWidget(Button.builder(Component.literal("◀"), b -> { if(currentPage>0) currentPage--; })
                .bounds(cx - 55, by, 20, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("▶"), b -> { if(currentPage<TOTAL_PAGES-1) currentPage++; })
                .bounds(cx + 35, by, 20, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Fermer"), b -> this.onClose())
                .bounds(cx - 20, by, 40, 20).build());
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
    public void renderBackground(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        gfx.fill(0, 0, this.width, this.height, 0xFF1E1E1E);
    }
    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gfx, mouseX, mouseY, partialTick);
        lastMouseX = mouseX; lastMouseY = mouseY;
        int x = this.width / 2 - 145;
        int y = this.height / 2 - 100;
        int w = Math.min(380, Math.max(290, this.width * 3 / 4));
        int h = Math.min(240, Math.max(200, this.height * 3 / 4));
        x = this.width / 2 - w / 2;
        y = this.height / 2 - h / 2;

        // ombre
        gfx.fill(x+4, y+4, x+w+4, y+h+4, 0x44000000);
        // bord cuir
        gfx.fill(x, y, x+w, y+h, 0xFF2B1B0F);
        // page papier
        gfx.fill(x+2, y+2, x+w-2, y+h-2, 0xFFF0E6D2);
        // header marron
        gfx.fill(x+4, y+4, x+w-4, y+18, 0xFF8B4513);
        // header text
        String header = switch(currentPage) {
            case 0 -> Component.translatable("gui.veterinarium.medical_file.header.cover").getString();
            case 1 -> Component.translatable("gui.veterinarium.medical_file.header.recipes").getString();
            case 2,3,4,5,6,7 -> Component.translatable("gui.veterinarium.medical_file.header.bestiary", Component.translatable("entity.veterinarium."+creatures[currentPage-2].id).getString().toUpperCase()).getString();
            case 8 -> Component.translatable("gui.veterinarium.medical_file.header.pathologies").getString();
            case 9 -> Component.translatable("gui.veterinarium.medical_file.header.protocol").getString();
            case 10 -> Component.translatable("gui.veterinarium.medical_file.header.progress").getString();
            default -> Component.translatable("gui.veterinarium.medical_file.title").getString();
        };
        gfx.drawCenteredString(this.font, "§l"+header, this.width/2, y+8, 0xFFFFFF);
        gfx.drawCenteredString(this.font, Component.translatable("gui.veterinarium.medical_file.subtitle").getString(), this.width/2, y+20, 0x6B3A1F);

        // contenu selon page
        if (currentPage==0) renderCover(gfx, x, y, w, h);
        else if (currentPage==1) renderRecipes(gfx, x, y, w, h);
        else if (currentPage>=2 && currentPage<=7) renderCreature(gfx, x, y, w, h, creatures[currentPage-2]);
        else if (currentPage==8) renderPathologies(gfx, x, y, w, h);
        else if (currentPage==9) renderProtocol(gfx, x, y, w, h);
        else if (currentPage==10) renderProgress(gfx, x, y, w, h);

        // footer pagination dots
        int dotY = y+h-10;
        int dotX0 = this.width/2 - (TOTAL_PAGES*7)/2;
        for(int i=0;i<TOTAL_PAGES;i++) {
            int col = i==currentPage ? 0xFF8B4513 : 0xFFD2B48C;
            gfx.fill(dotX0 + i*7, dotY, dotX0 + i*7 +5, dotY+5, col);
            if(i==currentPage) gfx.fill(dotX0 + i*7 +1, dotY+1, dotX0 + i*7 +4, dotY+4, 0xFFFFFFFF);
        }
        gfx.drawCenteredString(this.font, (currentPage+1)+"/"+TOTAL_PAGES, this.width/2, dotY+7, 0xFFFFFF);
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.footer.nav").getString()), x+w-68, y+h-22, 0x8B4513, true);

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
        gfx.drawCenteredString(this.font, Component.translatable("gui.veterinarium.medical_file.cover.chronicles").getString(), this.width/2, ty, 0x8B4513);
        ty+=10;
        gfx.drawCenteredString(this.font, Component.translatable("gui.veterinarium.medical_file.cover.tagline").getString(), this.width/2, ty, 0x5A3E2B);
        ty+=16;
        // stats mondiaux rapides
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.cover.world").getString()), x+12, ty, 0x000000, true); ty+=10;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.cover.wounded").getString() + " " + worldWounded + "  " + Component.translatable("gui.veterinarium.medical_file.cover.healed").getString() + " " + worldHealed + "  " + Component.translatable("gui.veterinarium.medical_file.cover.operated").getString() + " " + worldOperated), x+12, ty, 0x000000, true); ty+=10;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.cover.total").getString() + " " + (worldWounded+worldHealed)), x+12, ty, 0x000000, true); ty+=14;

        if (p != null) {
            int diag = BestiaryProgress.getDiagTotal(p);
            int ops = BestiaryProgress.getOpsTotal(p);
            int sut = BestiaryProgress.getSutureTotal(p);
            int healed = BestiaryProgress.getHealedTotal(p);
            float comp = BestiaryProgress.getCompletionPercent(p);
            gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.cover.progress").getString()), x+12, ty, 0x000000, true); ty+=10;
            gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.cover.diag").getString() + " " + diag + "  " + Component.translatable("gui.veterinarium.medical_file.cover.ops").getString() + " " + ops + "  " + Component.translatable("gui.veterinarium.medical_file.cover.sutures").getString() + " " + sut), x+12, ty, 0x000000, true); ty+=10;
            gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.cover.healed_total").getString() + " " + healed + "  " + Component.translatable("gui.veterinarium.medical_file.cover.analyses").getString() + " " + BestiaryProgress.getAnalysisTotal(p)), x+12, ty, 0x000000, true); ty+=10;
            // Barre progression
            int barW = w-24;
            int barX = x+12;
            gfx.fill(barX, ty+2, barX+barW, ty+8, 0xFFD2B48C);
            int filled = (int)(barW * (comp/100f));
            gfx.fill(barX, ty+2, barX+filled, ty+8, 0xFF2E8B57);
            gfx.drawString(this.font, Component.literal(String.format(Component.translatable("gui.veterinarium.medical_file.cover.bar").getString(), comp)), barX+barW+4, ty+1, 0x000000, true);
            ty+=14;
            gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.cover.creatures").getString() + " " + BestiaryProgress.getUnlockedCreatureCount(p)+"/6  " + Component.translatable("gui.veterinarium.medical_file.cover.pathologies").getString() + " " + BestiaryProgress.getUnlockedWoundCount(p)+"/5"), x+12, ty, 0x000000, true); ty+=10;
        }
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.cover.next").getString()), x+12, y+h-34, 0x000000, true);
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
            gfx.drawString(this.font, Component.literal("?"), x+20, ty-1, 0x000000, true);
        }
        // nom + sci
        gfx.drawString(this.font, Component.literal((seen? "§l"+Component.translatable("entity.veterinarium."+c.id).getString() : Component.translatable("gui.veterinarium.medical_file.bestiary.not_discovered").getString())), x+38, ty, seen?0x000000:0x888888, true);
        String sciKey = "gui.veterinarium.medical_file.creature.sci." + c.id.replace("wounded_","");
        String sciVal = seen ? Component.translatable(sciKey).getString() : Component.translatable("gui.veterinarium.medical_file.bestiary.unknown").getString();
        gfx.drawString(this.font, Component.literal("§o" + sciVal), x+38, ty+10, 0x8B4513, true);
        ty+=22;
        // habitat etc
        String habitatKey = switch(c.id) {
            case "wounded_wolf" -> "forest";
            case "wounded_cat" -> "village";
            case "wounded_horse" -> "plains";
            case "wounded_fox" -> "taiga";
            case "wounded_villager" -> "civil";
            case "wounded_drake" -> "end";
            default -> "forest";
        };
        String traitKey = "gui.veterinarium.medical_file.creature.trait." + c.id.replace("wounded_","");
        String habitatVal = seen ? Component.translatable("gui.veterinarium.medical_file.creature.habitat." + habitatKey).getString() : "???";
        String traitVal = seen ? Component.translatable(traitKey).getString() : "???";
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.habitat", habitatVal).getString()), x+12, ty, 0x000000, true); ty+=9;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.trait", traitVal).getString()), x+12, ty, 0x000000, true); ty+=9;
        // soin
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.care").getString()), x+12, ty, 0x000000, true); ty+=9;
        if (c.id.equals("wounded_fox")) gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.fox_bonus").getString()), x+12, ty, 0x000000, true);
        if (c.id.equals("wounded_villager")) gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.villager_bonus").getString()), x+12, ty, 0x000000, true);
        if (c.id.equals("wounded_horse")) gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.horse_bonus").getString()), x+12, ty, 0x000000, true);
        if (!c.id.equals("wounded_wolf") && !c.id.equals("wounded_cat")) ty+=0; else ty+=0;
        if (seen) ty+=2; else ty+=2;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.pathologies_all").getString()), x+12, ty, 0x000000, true); ty+=9;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.list1").getString()), x+12, ty, 0x000000, true); ty+=9;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.list2").getString()) + "  §cSaignement", x+12, ty, 0x000000, true); ty+=12;

        // stats perso
        if (p != null) {
            int healed = BestiaryProgress.getHealedFor(p, c.id);
            gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.healed", healed).getString()), x+12, ty, 0x000000, true); ty+=9;
            if (!seen) {
                gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.use_syringe").getString()), x+12, ty, 0xCC0000, true); ty+=9;
            } else if (healed==0) {
                gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.validate").getString()), x+12, ty, 0x000000, true); ty+=9;
            } else {
                gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.validated").getString()), x+12, ty, 0x006400, true); ty+=9;
            }
        }
        // world count pour cette creature? scan limité 128
        ty+=2;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.bestiary.tip").getString()), x+12, ty, 0x666666, true);
    }

    private void renderPathologies(GuiGraphics gfx, int x, int y, int w, int h) {
        Player p = Minecraft.getInstance().player;
        int ty = y + 30;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.pathologies.desc").getString()), x+12, ty, 0x000000, true); ty+=12;
        WoundType[] all = WoundType.values();
        // headers
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.pathologies.header").getString()), x+12, ty, 0x8B0000, true); ty+=9;
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
                case CONTUSION -> Component.translatable("wound.veterinarium.risk.none").getString();
                case HEMORRAGIE -> Component.translatable("wound.veterinarium.risk.relapse").getString();
                case FRACTURE -> Component.translatable("wound.veterinarium.risk.pain").getString();
                case INFECTION -> Component.translatable("wound.veterinarium.risk.poison").getString();
                case BRULURE -> Component.translatable("wound.veterinarium.risk.fire").getString();
                case SAIGNEMENT -> Component.translatable("wound.veterinarium.risk.bleed").getString();
            };
            String line = String.format(" %s  %s  %s", wt.getDisplay(), req, risk);
            int col = seen ? 0x000000 : 0x2B2B2B;
            String disp = seen ? line : " §8" + Component.translatable("gui.veterinarium.medical_file.pathologies.not_diag").getString();
            gfx.drawString(this.font, Component.literal(disp), x+12, ty, col, true);
            if (seen) {
                ty+=9;
                gfx.drawString(this.font, wt.getDescription(), x+18, ty, 0x2B2B2B, true);
            } else {
                ty+=9;
                gfx.drawString(this.font, Component.literal("§8→ " + Component.translatable("gui.veterinarium.medical_file.pathologies.not_diag_desc").getString()), x+18, ty, 0x555555, true);
            }
            ty+=11;
        }
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.pathologies.weights").getString()), x+12, ty, 0x000000, true); ty+=10;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.pathologies.table").getString()), x+12, ty, 0x000000, true);
    }

    private void renderProtocol(GuiGraphics gfx, int x, int y, int w, int h) {
        int ty = y + 30;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.protocol.title").getString()), x+12, ty, 0x8B0000, true); ty+=12;
        String[] steps = {
            Component.translatable("gui.veterinarium.medical_file.protocol.step1").getString(),
            Component.translatable("gui.veterinarium.medical_file.protocol.step1a").getString(),
            Component.translatable("gui.veterinarium.medical_file.protocol.step1b").getString(),
            Component.translatable("gui.veterinarium.medical_file.protocol.step2").getString(),
            Component.translatable("gui.veterinarium.medical_file.protocol.step2a").getString(),
            Component.translatable("gui.veterinarium.medical_file.protocol.step2b").getString(),
            Component.translatable("gui.veterinarium.medical_file.protocol.step3").getString(),
            Component.translatable("gui.veterinarium.medical_file.protocol.step3a").getString(),
            Component.translatable("gui.veterinarium.medical_file.protocol.step3b").getString(),
            Component.translatable("gui.veterinarium.medical_file.protocol.step4").getString(),
            Component.translatable("gui.veterinarium.medical_file.protocol.step4a").getString()
        };
        for(String s: steps) { gfx.drawString(this.font, Component.literal(s), x+12, ty, 0x000000, true); ty+=9; }
        ty+=4;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.protocol.warning").getString()), x+12, ty, 0xCC0000, true); ty+=10;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.protocol.range").getString()), x+12, ty, 0x000000, true);
    }

    private void renderRecipes(GuiGraphics gfx, int x, int y, int w, int h) {
        int ty = y + 30;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.recipes.title").getString()), x+12, ty, 0xFFD700, true); ty+=10;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.recipes.desc").getString()), x+12, ty, 0xAAAAAA, true); ty+=12;
        // Grilles compactes 2x3 - espacées 68px horiz, 58px vert
        drawMiniRecipe(gfx, x+10, ty, "Seringue", new ItemStack(ModItems.SYRINGE.get()),
                new ItemStack(net.minecraft.world.item.Items.GLASS), null, null,
                new ItemStack(net.minecraft.world.item.Items.IRON_NUGGET), null, null,
                new ItemStack(net.minecraft.world.item.Items.IRON_NUGGET), null, null);
        drawMiniRecipe(gfx, x+85, ty, "Scalpel", new ItemStack(ModItems.SCALPEL.get()),
                null, new ItemStack(net.minecraft.world.item.Items.IRON_INGOT), null,
                new ItemStack(net.minecraft.world.item.Items.IRON_INGOT), null, null,
                null, null, null);
        drawMiniRecipe(gfx, x+160, ty, "Bandage x4", new ItemStack(ModItems.BANDAGE.get(), 4),
                new ItemStack(net.minecraft.world.item.Items.WHITE_WOOL), new ItemStack(net.minecraft.world.item.Items.STRING), null,
                null, null, null,
                null, null, null);
        ty+=56;
        drawMiniRecipe(gfx, x+10, ty, "Suture", new ItemStack(ModItems.SUTURE_KIT.get()),
                new ItemStack(net.minecraft.world.item.Items.STRING), null, null,
                new ItemStack(net.minecraft.world.item.Items.PAPER), null, null,
                new ItemStack(net.minecraft.world.item.Items.STRING), null, null);
        drawMiniRecipe(gfx, x+85, ty, "Sphère", new ItemStack(ModItems.VET_SPHERE.get()),
                null, new ItemStack(net.minecraft.world.item.Items.IRON_NUGGET), null,
                new ItemStack(net.minecraft.world.item.Items.IRON_NUGGET), new ItemStack(net.minecraft.world.item.Items.GLASS), new ItemStack(net.minecraft.world.item.Items.IRON_NUGGET),
                null, new ItemStack(net.minecraft.world.item.Items.IRON_NUGGET), null);
        drawMiniRecipe(gfx, x+160, ty, "Hut", new ItemStack(ModItems.HOSPITAL_HUT.get()),
                new ItemStack(net.minecraft.world.item.Items.WHITE_WOOL), new ItemStack(net.minecraft.world.item.Items.WHITE_WOOL), new ItemStack(net.minecraft.world.item.Items.WHITE_WOOL),
                new ItemStack(net.minecraft.world.item.Items.BRICK), new ItemStack(ModItems.INFIRMARY.get()), new ItemStack(net.minecraft.world.item.Items.BRICK),
                new ItemStack(net.minecraft.world.item.Items.WHITE_WOOL), new ItemStack(net.minecraft.world.item.Items.WHITE_WOOL), new ItemStack(net.minecraft.world.item.Items.WHITE_WOOL));
        ty+=60;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.recipes.tip").getString()), x+12, ty, 0xAAAAAA, true);
    }
    private void drawMiniRecipe(GuiGraphics gfx, int rx, int ry, String name, ItemStack result, ItemStack... grid) {
        // grid 9 slots (3x3) + result
        gfx.drawString(this.font, Component.literal("§6" + name), rx, ry, 0x000000, true);
        int gx = rx; int gy = ry + 10;
        // fond grille 3x3
        gfx.fill(gx-1, gy-1, gx+50, gy+50, 0xFF8B4513);
        gfx.fill(gx, gy, gx+49, gy+49, 0xFFD2B48C);
        for(int row=0;row<3;row++) for(int col=0;col<3;col++) {
            int idx = row*3+col;
            int ix = gx + col*16 +1; int iy = gy + row*16 +1;
            gfx.fill(ix, iy, ix+15, iy+15, 0xFFF0E6D2);
            gfx.fill(ix, iy, ix+15, iy+1, 0xFF8B4513);
            gfx.fill(ix, iy, ix+1, iy+15, 0xFF8B4513);
            if (idx < grid.length && grid[idx] != null && !grid[idx].isEmpty()) {
                gfx.renderItem(grid[idx], ix+0, iy+0);
            }
        }
        // flèche
        gfx.drawString(this.font, Component.literal("→"), gx+52, gy+20, 0x000000, true);
        // résultat
        gfx.fill(gx+60, gy+12, gx+84, gy+36, 0xFF8B4513);
        gfx.fill(gx+61, gy+13, gx+83, gy+35, 0xFFF0E6D2);
        gfx.renderItem(result, gx+64, gy+16);
        gfx.renderItemDecorations(this.font, result, gx+64, gy+16);
        // tooltip on hover for result
        if (lastMouseX >= gx+60 && lastMouseX <= gx+84 && lastMouseY >= gy+12 && lastMouseY <= gy+36) {
            gfx.renderTooltip(this.font, result, lastMouseX, lastMouseY);
        }
        // tooltip on hover for grid slots
        for(int row=0;row<3;row++) for(int col=0;col<3;col++) {
            int idx = row*3+col;
            int ix = gx + col*16 +1; int iy = gy + row*16 +1;
            if (idx < grid.length && grid[idx] != null && !grid[idx].isEmpty()) {
                if (lastMouseX >= ix && lastMouseX <= ix+15 && lastMouseY >= iy && lastMouseY <= iy+15) {
                    gfx.renderTooltip(this.font, grid[idx], lastMouseX, lastMouseY);
                }
            }
        }
    }

    private void renderProgress(GuiGraphics gfx, int x, int y, int w, int h) {
        Player p = Minecraft.getInstance().player;
        int ty = y + 30;
        if (p==null) { gfx.drawString(this.font, Component.literal("§7Pas de joueur"), x+12, ty, 0x000000,true); return; }
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.progress.detailed").getString()), x+12, ty, 0x000000,true); ty+=12;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.progress.diagnosed", BestiaryProgress.getDiagTotal(p)).getString()), x+12, ty,0x000000,true); ty+=9;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.progress.operations", BestiaryProgress.getOpsTotal(p)).getString()), x+12, ty,0x000000,true); ty+=9;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.progress.sutures", BestiaryProgress.getSutureTotal(p)).getString()), x+12, ty,0x000000,true); ty+=9;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.progress.healed_total", BestiaryProgress.getHealedTotal(p)).getString() + " /20"), x+12, ty,0x000000,true); ty+=9;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.progress.analyses", BestiaryProgress.getAnalysisTotal(p)).getString()), x+12, ty,0x000000,true); ty+=12;

        // liste créatures
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.progress.creatures").getString()), x+12, ty,0x000000,true); ty+=10;
        for(CreatureEntry ce: creatures) {
            boolean seen = BestiaryProgress.hasSeen(p, ce.id);
            int healed = BestiaryProgress.getHealedFor(p, ce.id);
            String icon = seen ? (healed>0 ? "§a✔" : "§e○") : "§7?";
            String entName = Component.translatable("entity.veterinarium."+ce.id).getString();
            gfx.drawString(this.font, Component.literal(String.format(" %s %s : %d soignés", icon, entName, healed)), x+12, ty, 0x000000,true); ty+=9;
        }
        ty+=2;
        gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.progress.pathologies", BestiaryProgress.getUnlockedWoundCount(p)).getString()), x+12, ty,0x000000,true); ty+=9;
        for(WoundType wt: WoundType.values()) {
            boolean seen = BestiaryProgress.hasSeenWound(p, wt.getId());
            gfx.drawString(this.font, Component.literal((seen?"§a✔ ":"§7? ") + wt.getDisplay()), x+14, ty, seen?0x000000:0x2B2B2B,true); ty+=9;
        }
        ty+=4;
        float comp = BestiaryProgress.getCompletionPercent(p);
        gfx.drawString(this.font, Component.literal(String.format(Component.translatable("gui.veterinarium.medical_file.progress.completion_line").getString(), comp)), x+12, ty, 0x8B0000,true); ty+=9;
        if (comp>=100) gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.progress.master").getString()), x+12, ty, 0xFFD700,true);
        else gfx.drawString(this.font, Component.literal(Component.translatable("gui.veterinarium.medical_file.progress.objective").getString()), x+12, ty, 0x666666,true);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
