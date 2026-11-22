package com.mobforge.runtime.entity;

import com.mobforge.runtime.config.EntityDefinition;
import com.mobforge.runtime.data.MobForgeData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.cache.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Entidade base do MobForge. Uma única classe serve para todas as criaturas: o
 * comportamento vem da {@link EntityDefinition} carregada em {@link MobForgeData}.
 *
 * <p>IA/combate/skills serão refinados nas próximas fases; aqui há o mínimo para validar
 * o registro dinâmico + renderização + melee.</p>
 */
public class MobForgeEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MobForgeEntity(EntityType<? extends MobForgeEntity> type, Level level) {
        super(type, level);
    }

    /** Definição JSON desta entidade (nunca nula após o registro). */
    public EntityDefinition definition() {
        return MobForgeData.get(getType());
    }

    @Override
    protected void registerGoals() {
        EntityDefinition def = definition();
        if (def == null || def.combat == null || !def.combat.hostile) {
            return;
        }
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Spike: idle/walk. Animações de ataque/skill (disparadas por trigger) entram na Fase 3.
        controllers.add(new AnimationController<>(this, "main", 5, this::animPredicate));
    }

    private PlayState animPredicate(AnimationState<MobForgeEntity> state) {
        if (state.isMoving()) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object entity) {
        return this.tickCount;
    }
}
