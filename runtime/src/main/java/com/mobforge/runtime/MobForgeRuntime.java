package com.mobforge.runtime;

import net.neoforged.fml.common.Mod;

/**
 * Ponto de entrada do MobForge Runtime.
 *
 * <p>Este mod NÃO registra entidades diretamente: o registro é feito de forma dinâmica
 * em {@link MobForgeRegistries} (entidades/atributos) e {@link MobForgeClientEvents}
 * (renderers), a partir das definições JSON carregadas por
 * {@link com.mobforge.runtime.data.MobForgeData}.</p>
 */
@Mod(MobForgeRuntime.MOD_ID)
public final class MobForgeRuntime {

    public static final String MOD_ID = "mobforge";
    public static final String MOD_NAME = "MobForge Runtime";

    public MobForgeRuntime() {
        // O registro é feito via @EventBusSubscriber. Nada a fazer aqui.
    }
}
