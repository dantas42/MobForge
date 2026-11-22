package com.mobforge.runtime.config;

import java.util.List;
import java.util.Map;

/**
 * Definição de entidade (mapeia 1:1 para o JSON descrito em {@code docs/DATA_MODEL.md}).
 *
 * <p>Campos ausentes no JSON mantêm os defaults declarados aqui. Esta classe será
 * evoluída para acompanhar {@code schemas/entity.schema.json}.</p>
 */
public class EntityDefinition {

    public int schemaVersion = 1;
    public String id;
    public String displayName;
    public String category = "monster";
    /** Mod dono dos assets (modelo/textura/animação). Default: o próprio runtime. */
    public String sourceModId = "mobforge";

    public ModelDefinition model = new ModelDefinition();
    public DimensionsDefinition dimensions = new DimensionsDefinition();
    public AttributesDefinition attributes = new AttributesDefinition();
    public CombatDefinition combat = new CombatDefinition();

    /** Skills — a engine completa entra na Fase 3. Presente apenas como contrato. */
    public List<SkillDefinition> skills = List.of();
    /** Fases de boss — máquina de fases entra na Fase 3. */
    public List<PhaseDefinition> phases = List.of();

    public boolean isBoss() {
        return "boss".equalsIgnoreCase(category);
    }

    public boolean isMonster() {
        return "monster".equalsIgnoreCase(category);
    }

    public static class ModelDefinition {
        public String geo;
        public Map<String, String> textures = Map.of();
        public String animations;
    }

    public static class DimensionsDefinition {
        public float width = 0.6f;
        public float height = 1.8f;
    }

    public static class AttributesDefinition {
        public double maxHealth = 20.0;
        public double armor = 0.0;
        public double armorToughness = 0.0;
        public double attackDamage = 2.0;
        public double movementSpeed = 0.25;
        public double followRange = 32.0;
        public double knockbackResistance = 0.0;
    }

    public static class CombatDefinition {
        public boolean hostile = true;
        public MeleeDefinition melee = new MeleeDefinition();
    }

    public static class MeleeDefinition {
        public double range = 3.0;
        public double damage = 2.0;
        public int attackCooldownTicks = 20;
    }

    /** Esqueleto do contrato de skill (completo na Fase 3). */
    public static class SkillDefinition {
        public String id;
        public double cooldownSeconds = 0.0;
    }

    /** Esqueleto do contrato de fase de boss (completo na Fase 3). */
    public static class PhaseDefinition {
        public String id;
        public List<ConditionDefinition> activation = List.of();
        public List<String> skills = List.of();
    }

    public static class ConditionDefinition {
        public String type;
        public Map<String, Object> params = Map.of();
    }
}
