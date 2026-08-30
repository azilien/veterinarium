package com.veterinarium.client;

import com.veterinarium.entity.WoundedCowEntity;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WoundedCowRenderer extends MobRenderer<WoundedCowEntity, CowModel<WoundedCowEntity>> {
    private static final ResourceLocation WOUNDED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_cow.png");
    private static final ResourceLocation HEALED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_cow_healed.png");

    public WoundedCowRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CowModel<>(ctx.bakeLayer(ModelLayers.COW)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(WoundedCowEntity entity) {
        return entity.isHealed() ? HEALED : WOUNDED;
    }
}
