package com.mobforge.runtime;

import com.mobforge.runtime.client.MobForgeRenderer;
import com.mobforge.runtime.config.EntityDefinition;
import com.mobforge.runtime.data.MobForgeData;
import com.mobforge.runtime.entity.MobForgeEntity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Registro de renderers (somente cliente).
 */
@EventBusSubscriber(modid = MobForgeRuntime.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class MobForgeClientEvents {

    private MobForgeClientEvents() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (EntityDefinition def : MobForgeData.getDefinitions()) {
            EntityType<MobForgeEntity> type = MobForgeData.getEntityType(def.id);
            if (type != null) {
                event.registerEntityRenderer(type, MobForgeRenderer::new);
            }
        }
    }
}
