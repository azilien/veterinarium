package com.veterinarium.client;

import com.veterinarium.entity.WoundedCatEntity;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WoundedCatRenderer extends MobRenderer<WoundedCatEntity, CatModel<WoundedCatEntity>> {
    private static final ResourceLocation WOUNDED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_cat.png");
    private static final ResourceLocation HEALED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_cat_healed.png");
    private static final ResourceLocation WOUNDED_TAME = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_cat_tame.png");
    private static final ResourceLocation HEALED_TAME = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_cat_healed_tame.png");

    public WoundedCatRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CatModel<>(ctx.bakeLayer(ModelLayers.CAT)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(WoundedCatEntity entity) {
        boolean tame = entity.isTame();
        boolean healed = entity.isHealed();
        if (tame && healed) return HEALED_TAME;
        if (tame) return WOUNDED_TAME;
        if (healed) return HEALED;
        return WOUNDED;
    }
}
