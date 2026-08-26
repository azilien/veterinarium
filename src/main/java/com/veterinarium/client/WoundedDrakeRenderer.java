package com.veterinarium.client;

import com.veterinarium.entity.WoundedDrakeEntity;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WoundedDrakeRenderer extends MobRenderer<WoundedDrakeEntity, net.minecraft.client.model.EntityModel<WoundedDrakeEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/phantom.png");
    private static final ResourceLocation HEALED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_drake_healed.png");
    private static final ResourceLocation WOUNDED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_drake.png");

    @SuppressWarnings("unchecked")
    public WoundedDrakeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, (net.minecraft.client.model.EntityModel<WoundedDrakeEntity>) (net.minecraft.client.model.EntityModel<?>) new PhantomModel(ctx.bakeLayer(ModelLayers.PHANTOM)), 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(WoundedDrakeEntity e) {
        if (e.isHealed()) return HEALED;
        return WOUNDED;
    }
}
