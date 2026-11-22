package com.mobforge.runtime.client;

import com.mobforge.runtime.entity.MobForgeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderer GeckoLib para {@link MobForgeEntity}. O modelo é o {@link MobForgeGeoModel}
 * (data-driven), portanto este renderer serve para todas as criaturas.
 */
public class MobForgeRenderer extends GeoEntityRenderer<MobForgeEntity> {

    public MobForgeRenderer(EntityRendererProvider.Context context) {
        super(context, new MobForgeGeoModel());
    }
}
