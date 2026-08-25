package com.veterinarium.client;

import com.veterinarium.entity.WoundedHorseEntity;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WoundedHorseRenderer extends MobRenderer<WoundedHorseEntity, HorseModel<WoundedHorseEntity>> {
    private static final ResourceLocation WOUNDED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_horse.png");
    private static final ResourceLocation HEALED = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_horse_healed.png");
    private static final ResourceLocation WOUNDED_TAME = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_horse_tame.png");
    private static final ResourceLocation HEALED_TAME = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_horse_healed_tame.png");

    public WoundedHorseRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HorseModel<>(ctx.bakeLayer(ModelLayers.HORSE)), 1.1F);
    }

    @Override
    public ResourceLocation getTextureLocation(WoundedHorseEntity entity) {
        boolean tame = entity.isTamed();
        boolean healed = entity.isHealed();
        if (tame && healed) return HEALED_TAME;
        if (tame) return WOUNDED_TAME;
        if (healed) return HEALED;
        return WOUNDED;
    }
}
