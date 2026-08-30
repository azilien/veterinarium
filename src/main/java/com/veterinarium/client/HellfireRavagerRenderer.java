package com.veterinarium.client;

import com.veterinarium.entity.HellfireRavagerEntity;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HellfireRavagerRenderer extends MobRenderer<HellfireRavagerEntity, WolfModel<HellfireRavagerEntity>> {
    private static final ResourceLocation TEXTURE_FALLBACK = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/wolf/wolf.png");
    private static final ResourceLocation HELLFIRE_FIRE = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/hellfire_ravager.png");
    private static final ResourceLocation HELLFIRE_ACID = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/hellfire_ravager_acid.png");
    private static final ResourceLocation HELLFIRE_SHADOW = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/hellfire_ravager_shadow.png");

    public HellfireRavagerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WolfModel<>(ctx.bakeLayer(ModelLayers.WOLF)), 0.6f);
    }

    @Override
    public ResourceLocation getTextureLocation(HellfireRavagerEntity entity) {
        return switch (entity.getMutationType()) {
            case "acid" -> HELLFIRE_ACID;
            case "shadow" -> HELLFIRE_SHADOW;
            default -> HELLFIRE_FIRE;
        };
    }

    @Override
    protected float getBob(HellfireRavagerEntity livingBase, float partialTicks) {
        return super.getBob(livingBase, partialTicks) * 1.0f;
    }
}
