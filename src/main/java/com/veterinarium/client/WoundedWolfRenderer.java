package com.veterinarium.client;

import com.veterinarium.entity.WoundedWolfEntity;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WoundedWolfRenderer extends MobRenderer<WoundedWolfEntity, WolfModel<WoundedWolfEntity>> {
    private static final ResourceLocation WOUNDED_TEXTURE = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_wolf.png");
    private static final ResourceLocation HEALED_TEXTURE = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_wolf_healed.png");
    private static final ResourceLocation WOUNDED_TAME_TEXTURE = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_wolf_tame.png");
    private static final ResourceLocation HEALED_TAME_TEXTURE = ResourceLocation.fromNamespaceAndPath("veterinarium", "textures/entity/wounded_wolf_healed_tame.png");

    public WoundedWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new WolfModel<>(context.bakeLayer(ModelLayers.WOLF)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(WoundedWolfEntity entity) {
        boolean tame = entity.isTame();
        boolean healed = entity.isHealed();
        if (tame && healed) return HEALED_TAME_TEXTURE;
        if (tame) return WOUNDED_TAME_TEXTURE;
        if (healed) return HEALED_TEXTURE;
        return WOUNDED_TEXTURE;
    }
}
