package com.veterinarium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class MedicalFileScreen extends Screen {
    public MedicalFileScreen() {
        super(Component.literal("Dossier Médical"));
    }

    @Override
    protected void init() {
        // bouton fermer central
        this.addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal("Fermer"), b -> this.onClose())
                .bounds(this.width / 2 - 50, this.height - 30, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gfx, mouseX, mouseY, partialTick);
        // fond livre
        int x = this.width / 2 - 110;
        int y = this.height / 2 - 80;
        int w = 220;
        int h = 160;
        gfx.fill(x, y, x + w, y + h, 0xFF2B1B0F);
        gfx.fill(x+2, y+2, x+w-2, y+h-2, 0xFFF5E6C8);
        gfx.fill(x+4, y+4, x+w-4, y+14, 0xFF8B4513);
        gfx.drawCenteredString(this.font, "§lDOSSIER MEDICAL", this.width/2, y+6, 0xFFFFFF);
        gfx.drawCenteredString(this.font, "Veterinarium - Hopital des Monstres", this.width/2, y+16, 0xFFD700);

        // stats
        Level level = Minecraft.getInstance().level;
        int wounded = 0, healed = 0, operated = 0, total = 0;
        if (level != null) {
            List<LivingEntity> all = level.getEntitiesOfClass(LivingEntity.class, 
                new net.minecraft.world.phys.AABB(-30000000, -64, -30000000, 30000000, 320, 30000000));
            for (LivingEntity e : all) {
                if (e.getTags().contains("veterinarium_wounded")) wounded++;
                if (e.getTags().contains("veterinarium_healed")) healed++;
                if (e.getTags().contains("veterinarium_operated")) operated++;
                if (e.getTags().contains("veterinarium_wounded") || e.getTags().contains("veterinarium_healed")) total++;
            }
        }

        int ty = y + 30;
        gfx.drawString(this.font, Component.literal("§6Patients dans ce monde:"), x+10, ty, 0x000000, false);
        ty += 14;
        gfx.drawString(this.font, Component.literal(" §c☠ Blessés: " + wounded), x+10, ty, 0x000000, false); ty+=10;
        gfx.drawString(this.font, Component.literal(" §a❤ Soignés: " + healed), x+10, ty, 0x000000, false); ty+=10;
        gfx.drawString(this.font, Component.literal(" §c✚ Opérés: " + operated), x+10, ty, 0x000000, false); ty+=10;
        gfx.drawString(this.font, Component.literal(" §7Total pris en charge: " + total), x+10, ty, 0x000000, false); ty+=14;

        gfx.drawString(this.font, Component.literal("§8Protocole Asfax:"), x+10, ty, 0x000000, false); ty+=10;
        gfx.drawString(this.font, Component.literal(" §71. Seringue → Diagnostic HP + Anesthésie"), x+10, ty, 0x000000, false); ty+=9;
        gfx.drawString(this.font, Component.literal(" §72. Scalpel → Opère (enlève 'needs_scalpel')"), x+10, ty, 0x000000, false); ty+=9;
        gfx.drawString(this.font, Component.literal(" §73. Kit Suture → Soigne + 33% tame"), x+10, ty, 0x000000, false); ty+=9;
        gfx.drawString(this.font, Component.literal(" §c⚠ Suture sans scalpel = Infection!"), x+10, ty, 0xFF0000, false); ty+=12;

        gfx.drawString(this.font, Component.literal("§7Infirmerie: pose-la près des enclos"), x+10, ty, 0x000000, false); ty+=9;
        gfx.drawString(this.font, Component.literal("§7→ heal 0.5❤/2s dans 8 blocs"), x+10, ty, 0x000000, false); ty+=12;

        gfx.drawString(this.font, Component.literal("§oPour Asfax - Infirmier au bloc"), x+10, y + h - 12, 0x666666, false);

        super.render(gfx, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
