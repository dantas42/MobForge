package com.mobforge.runtime.entity;

import com.mobforge.runtime.config.EntityDefinition;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;

/**
 * Constrói o {@link AttributeSupplier} de uma entidade a partir da definição JSON.
 */
public final class MobForgeAttributeBuilder {

    private MobForgeAttributeBuilder() {
    }

    public static AttributeSupplier.Builder build(EntityDefinition def) {
        EntityDefinition.AttributesDefinition a = def.attributes;
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, a.maxHealth)
                .add(Attributes.ARMOR, a.armor)
                .add(Attributes.ARMOR_TOUGHNESS, a.armorToughness)
                .add(Attributes.ATTACK_DAMAGE, a.attackDamage)
                .add(Attributes.MOVEMENT_SPEED, a.movementSpeed)
                .add(Attributes.FOLLOW_RANGE, a.followRange)
                .add(Attributes.KNOCKBACK_RESISTANCE, a.knockbackResistance);
    }
}
