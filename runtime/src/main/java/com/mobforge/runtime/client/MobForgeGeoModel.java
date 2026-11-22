package com.mobforge.runtime.client;

import com.mobforge.runtime.entity.MobForgeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * Modelo GeckoLib data-driven: resolve os caminhos de modelo/textura/animação a partir
 * da {@link com.mobforge.runtime.config.EntityDefinition} da entidade (e do mod dono dos
 * assets), permitindo que um único modelo atenda a todas as criaturas.
 */
public class MobForgeGeoModel extends GeoModel<MobForgeEntity> {

    @Override
    public ResourceLocation getModelResource(MobForgeEntity entity) {
        return resolve(entity, entity.definition().model.geo);
    }

    @Override
    public ResourceLocation getTextureResource(MobForgeEntity entity) {
        String texture = entity.definition().model.textures.getOrDefault("default", null);
        return resolve(entity, texture);
    }

    @Override
    public ResourceLocation getAnimationResource(MobForgeEntity entity) {
        return resolve(entity, entity.definition().model.animations);
    }

    private ResourceLocation resolve(MobForgeEntity entity, String path) {
        if (path == null || path.isBlank()) {
            path = "textures/entity/default.png";
        }
        String namespace = entity.definition().sourceModId;
        if (namespace == null || namespace.isBlank()) {
            namespace = "mobforge";
        }
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
