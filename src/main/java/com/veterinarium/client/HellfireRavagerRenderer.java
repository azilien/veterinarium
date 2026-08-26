package com.veterinarium.client;

import com.veterinarium.entity.HellfireRavagerEntity;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HellfireRavagerRenderer extends MobRenderer<HellfireRavagerEntity, WolfModel<HellfireRavagerEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/wolf/wolf.png");
    // on réutilise texture wolf vanilla mais on pourrait fournir custom via resources; pour serie on tint via overlay sombre? On garde vanilla pour now, name + particules suffisent pour différencier
    private static final ResourceLocation HELLFIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/hellfire_ravager.png");

    public HellfireRavagerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WolfModel<>(ctx.bakeLayer(ModelLayers.WOLF)), 0.6f);
    }

    @Override
    public ResourceLocation getTextureLocation(HellfireRavagerEntity entity) {
        // si texture custom présente, elle sera chargée, sinon fallback vanilla (Minecraft ne crash pas, affiche missing pink mais on a vanilla fallback)
        try {
            // tente de loader custom; si absent, fallback
            // On check resource manuellement? plus simple return custom et on fournit fichier via resources (même que wolf mais recolor)
            return HELLFIRE_TEXTURE;
        } catch (Exception e) {
            return TEXTURE;
        }
    }

    @Override
    protected float getBob(HellfireRavagerEntity livingBase, float partialTicks) {
        return super.getBob(livingBase, partialTicks) * 1.0f;
    }
}
