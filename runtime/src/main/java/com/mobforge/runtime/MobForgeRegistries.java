package com.mobforge.runtime;

import com.mobforge.runtime.config.EntityDefinition;
import com.mobforge.runtime.data.MobForgeData;
import com.mobforge.runtime.entity.MobForgeAttributeBuilder;
import com.mobforge.runtime.entity.MobForgeEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registro dinâmico de entidades (lado comum).
 *
 * <p>Fluxo: no {@link RegisterEvent} de {@link Registries#ENTITY_TYPE}, lê as definições
 * de {@link MobForgeData} e registra um {@link EntityType} por definição. Em seguida,
 * {@link EntityAttributeCreationEvent} associa os atributos. Renderers são registrados no
 * lado cliente em {@link MobForgeClientEvents}.</p>
 */
@EventBusSubscriber(modid = MobForgeRuntime.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class MobForgeRegistries {

    private MobForgeRegistries() {
    }

    @SubscribeEvent
    public static void registerEntityTypes(RegisterEvent event) {
        event.register(Registries.ENTITY_TYPE, helper -> {
            for (EntityDefinition def : MobForgeData.getDefinitions()) {
                MobCategory category = def.isBoss() || def.isMonster()
                        ? MobCategory.MONSTER
                        : MobCategory.CREATURE;

                EntityType<MobForgeEntity> type = EntityType.Builder
                        .<MobForgeEntity>of(MobForgeEntity::new, category)
                        .sized(def.dimensions.width, def.dimensions.height)
                        .build(def.id);

                ResourceLocation key = ResourceLocation.fromNamespaceAndPath(
                        MobForgeRuntime.MOD_ID, def.id);

                helper.register(key, type);
                MobForgeData.registerEntityType(def.id, type);
            }
        });
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        for (EntityDefinition def : MobForgeData.getDefinitions()) {
            EntityType<MobForgeEntity> type = MobForgeData.getEntityType(def.id);
            if (type != null) {
                event.put(type, MobForgeAttributeBuilder.build(def));
            }
        }
    }
}
