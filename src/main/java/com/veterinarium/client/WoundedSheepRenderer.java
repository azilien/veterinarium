package com.veterinarium.client;

import com.veterinarium.entity.WoundedSheepEntity;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WoundedSheepRenderer extends MobRenderer<WoundedSheepEntity, SheepModel<WoundedSheepEntity>> {
    private static final ResourceLocation WOUNDED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_sheep.png");
    private static final ResourceLocation HEALED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_sheep_healed.png");

    public WoundedSheepRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SheepModel<>(ctx.bakeLayer(ModelLayers.SHEEP)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(WoundedSheepEntity entity) {
        return entity.isHealed() ? HEALED : WOUNDED;
    }
}
