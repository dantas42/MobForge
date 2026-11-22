package com.mobforge.runtime.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobforge.runtime.config.EntityDefinition;
import com.mobforge.runtime.entity.MobForgeEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Carrega as definições de entidade (JSON) e mantém o vínculo entre id/config e o
 * {@link EntityType} registrado dinamicamente.
 *
 * <p><b>Fase 1 (MVP):</b> lê de {@code config/mobforge/entities/*.json} (disco).
 * <b>Fase 4:</b> também lerá de {@code mobforge/} dentro do jar do companion.</p>
 */
public final class MobForgeData {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setLenient().create();

    private static final Map<String, EntityDefinition> DEFINITIONS = new LinkedHashMap<>();
    private static final Map<String, EntityType<MobForgeEntity>> ENTITY_TYPES = new HashMap<>();
    private static boolean loaded = false;

    private MobForgeData() {
    }

    /** Definições carregadas (ordem estável de leitura). */
    public static synchronized List<EntityDefinition> getDefinitions() {
        load();
        return new ArrayList<>(DEFINITIONS.values());
    }

    /** Registra o EntityType criado dinamicamente, vinculando ao id da config. */
    public static void registerEntityType(String id, EntityType<MobForgeEntity> type) {
        ENTITY_TYPES.put(id, type);
    }

    public static EntityType<MobForgeEntity> getEntityType(String id) {
        return ENTITY_TYPES.get(id);
    }

    /** Resolve a definição associada a um EntityType (usado pela entidade em runtime). */
    public static EntityDefinition get(EntityType<?> type) {
        for (Map.Entry<String, EntityType<MobForgeEntity>> entry : ENTITY_TYPES.entrySet()) {
            if (entry.getValue() == type) {
                return DEFINITIONS.get(entry.getKey());
            }
        }
        return null;
    }

    private static void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        Path dir = FMLPaths.CONFIGDIR.get().resolve("mobforge").resolve("entities");
        if (!Files.isDirectory(dir)) {
            LOGGER.warn("[MobForge] Diretório de configs não encontrado: {}", dir);
            return;
        }

        try (Stream<Path> files = Files.list(dir)) {
            files.filter(p -> p.getFileName().toString().endsWith(".json"))
                    .sorted()
                    .forEach(MobForgeData::loadFile);
        } catch (Exception e) {
            LOGGER.error("[MobForge] Falha ao listar configs em {}", dir, e);
        }

        LOGGER.info("[MobForge] {} definição(ões) de entidade carregada(s).", DEFINITIONS.size());
    }

    private static void loadFile(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            EntityDefinition def = GSON.fromJson(reader, EntityDefinition.class);
            if (def == null || def.id == null || def.id.isBlank()) {
                LOGGER.error("[MobForge] Config inválida (id ausente): {}", path);
                return;
            }
            DEFINITIONS.put(def.id, def);
            LOGGER.info("[MobForge] Entidade carregada: {}", def.id);
        } catch (Exception e) {
            LOGGER.error("[MobForge] Falha ao carregar config: {}", path, e);
        }
    }
}
