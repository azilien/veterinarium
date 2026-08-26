package com.veterinarium.client;

import com.veterinarium.entity.WoundedFoxEntity;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WoundedFoxRenderer extends MobRenderer<WoundedFoxEntity, FoxModel<WoundedFoxEntity>> {
    private static final ResourceLocation WOUNDED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_fox.png");
    private static final ResourceLocation HEALED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_fox_healed.png");
    private static final ResourceLocation WOUNDED_TAME = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_fox_tame.png");
    private static final ResourceLocation HEALED_TAME = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_fox_healed_tame.png");

    public WoundedFoxRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new FoxModel<>(ctx.bakeLayer(ModelLayers.FOX)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(WoundedFoxEntity entity) {
        boolean trusted = entity.getTags().contains("veterinarium_trusted") || entity.getTags().contains("veterinarium_healed");
        boolean healed = entity.isHealed();
        if (trusted && healed) return HEALED_TAME;
        if (trusted) return WOUNDED_TAME;
        if (healed) return HEALED;
        return WOUNDED;
    }
}
