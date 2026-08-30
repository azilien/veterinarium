package com.veterinarium.client;

import com.veterinarium.entity.WoundedChickenEntity;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WoundedChickenRenderer extends MobRenderer<WoundedChickenEntity, ChickenModel<WoundedChickenEntity>> {
    private static final ResourceLocation WOUNDED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_chicken.png");
    private static final ResourceLocation HEALED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_chicken_healed.png");

    public WoundedChickenRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ChickenModel<>(ctx.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(WoundedChickenEntity entity) {
        return entity.isHealed() ? HEALED : WOUNDED;
    }
}
