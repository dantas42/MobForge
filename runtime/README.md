# MobForge Runtime

Mod base NeoForge que registra e executa criaturas definidas em JSON.

## Pré-requisitos

- **JDK 21** (o plugin `foojay-resolver` no `settings.gradle` pode baixá-lo automaticamente
  se o Gradle for executado com acesso à internet).
- Acesso à internet no primeiro build (baixa NeoForge, mappings e GeckoLib).

## Build

Na primeira vez, gere o wrapper (se ainda não houver `gradlew`):

```bash
gradle wrapper --gradle-version 8.13
```

Depois:

```bash
./gradlew build              # Windows: .\gradlew.bat build
```

O jar sai em `build/libs/mobforge-0.1.0.jar`.

## Rodar em ambiente de desenvolvimento

```bash
./gradlew runClient
```

Para testar a entidade de exemplo:

1. Copie `examples/config/mobforge/entities/test_dummy.json` para
   `<pasta-do-jogo>/config/mobforge/entities/test_dummy.json`.
   - Em dev, a pasta do jogo é `runtime/run/`.
2. Inicie o client.
3. No chat, execute:

   ```
   /summon mobforge:test_dummy
   ```

A criatura deve aparecer com o modelo/textura de exemplo, perseguir e atacar o jogador.

## Estrutura

```
src/main/java/com/mobforge/runtime/
├── MobForgeRuntime.java        # @Mod (ponto de entrada)
├── MobForgeRegistries.java     # registro dinâmico: entity types + atributos
├── MobForgeClientEvents.java   # registro de renderers (cliente)
├── config/EntityDefinition.java# DTO do JSON de entidade
├── data/MobForgeData.java      # carrega JSONs e vincula id <-> EntityType
├── entity/MobForgeEntity.java  # entidade base (uma classe p/ todas as criaturas)
├── entity/MobForgeAttributeBuilder.java
└── client/                     # GeoModel + GeoEntityRenderer (GeckoLib)
```

## Como funciona o registro dinâmico (o spike)

1. `MobForgeData` lê `config/mobforge/entities/*.json` (disco) no primeiro acesso.
2. `RegisterEvent` (entity types) registra um `EntityType` por definição.
3. `EntityAttributeCreationEvent` associa os atributos de cada definição.
4. `EntityRenderersEvent.RegisterRenderers` (cliente) registra o renderer GeckoLib.

Tudo é data-driven: nenhuma classe Java por criatura.

## Observações importantes

- **Assets** (`.geo.json`, texturas, `.animation.json`) devem ficar em
  `assets/<sourceModId>/` — para o exemplo, `assets/mobforge/`. Substitua os placeholders
  por exports reais do Blockbench (formato GeckoLib).
- **GeckoLib em dev run (MC 1.21.1):** dependências externas podem exigir tratamento
  especial para carregar no run. Se o GeckoLib não for carregado em `runClient`, copie o
  `geckolib-neoforge-1.21.1-4.9.3.jar` para a pasta `run/mods/`. (A partir do MC 1.21.9 o
  ModDevGradle resolve isso automaticamente.)
- **Verificação de API:** se a versão exata do GeckoLib/NeoForge mudar, confirme as
  assinaturas (`GeoEntity`, `GeoModel`, `GeoEntityRenderer`) contra a versão usada.

## Próximos passos (Fase 1 → Fase 3)

- [ ] Wire da skill mínima (trigger + cooldown + ação de dano).
- [ ] Spawn natural (biome modifiers).
- [ ] Loot.
- [ ] Engine completa de skills + fases de boss.
