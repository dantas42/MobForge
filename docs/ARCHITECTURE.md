# MobForge — Arquitetura

## 1. Visão geral

O MobForge é dividido em **três componentes**, mais um **contrato de dados** compartilhado
que os une:

```
┌─────────────────────┐         ┌─────────────────────────────┐
│   MobForge Editor   │  export │   MobForge Generated Mods   │
│  (desktop, visual)  ├────────►│  (companion: assets+config) │
└─────────┬───────────┘         └──────────────┬──────────────┘
          │  escreve JSON                      │  depende
          ▼                                    ▼
   ┌──────────────────────────────────────────────────────┐
   │                 MobForge Runtime (mod NeoForge)       │
   │   interpreta JSON, registra entidades, executa skills │
   └──────────────────────────────────────────────────────┘
          ▲
          │
   ┌──────┴───────┐
   │ JSON Schemas  │   contrato de dados único (versionado)
   └───────────────┘
```

- **Editor**: produz arquivos (configurações + assets) e empacota um mod companion.
- **Runtime**: é o "motor". Lê configurações, registra entidades/renderers/atributos,
  implementa IA, combate, skills, condições, fases, spawn e loot de forma genérica.
- **Generated Mods**: jar NeoForge fino que (a) declara dependência do Runtime,
  (b) carrega assets (modelos/texturas/animações), (c) carrega configurações.

A decisão central é: **nenhum código Java específico por criatura**. O companion mod tem
uma única classe Java *genérica* (template), idêntica para qualquer mod gerado, mudando
apenas o `modId`/pacote.

---

## 2. Componente A — MobForge Editor

### Responsabilidades
- Gerenciar projetos de mod (criar, abrir, salvar, versionar).
- Criar/editar criaturas (atributos, combate, skills, fases, spawn, loot).
- Importar assets do Blockbench (modelos `.geo.json`, texturas, animações).
- Validar configurações contra o JSON Schema **antes** de exportar.
- Visualizar informações da criatura (atributos, resumo de skills).
- Exportar como mod companion (jar) e/ou projeto Gradle.

### Subdivisão interna (conceitual)
```
editor/
  core/          # modelo de domínio do projeto (independe de UI)
  schema/        # validação JSON (consumo dos schemas em schemas/)
  assets/        # importação/normalização de arquivos do Blockbench
  export/        # geração do companion (template + empacotamento)
  ui/            # telas (projetos, criaturas, atributos, skills, export)
  viewer/        # visualizador 3D (Fase 5)
  test/          # testes do domínio e da exportação
```

### Decisão de tecnologia
Ver [`TECHNOLOGY_DECISIONS.md`](TECHNOLOGY_DECISIONS.md). Recomendação: **Tauri 2 +
React + TypeScript** (fallback: Electron + React). Three.js para o visualizador 3D futuro.

---

## 3. Componente B — MobForge Runtime (mod NeoForge)

### Papel
Mod base `mobforge` que implementa **toda a lógica genérica** e interpreta as
configurações. É a peça mais complexa e onde a complexidade deve viver.

### Subsistemas

| Subsistema | Responsabilidade |
|------------|------------------|
| `config` | Carregar e validar JSON de entidades/skills/fases/spawn/loot. |
| `registry` | Registro **dinâmico** de entity types, renderers e atributos a partir do config. |
| `entity` | Entidade base (`MobForgeEntity`) e mapeamento de atributos. |
| `ai` | IA básica: goals vanilla mapeados do config (hostilidade, melee, follow). |
| `combat` | Combate corpo a corpo, dano, resistências, knockback. |
| `skill` | Engine de skills: triggers, condições, ações, cooldowns, sequências. |
| `phase` | Máquina de fases de boss e transições por condição. |
| `spawn` | Spawn natural (biome modifiers) e por evento/estrutura. |
| `loot` | Loot tables a partir do config. |
| `animation` | Integração GeckoLib (modelos, animações, triggers de animação). |
| `particle` | Efeitos de partículas ligados a skills/animações. |
| `network` | Sincronização servidor→cliente de cooldowns, fases e efeitos visuais. |

### Registro dinâmico de entidades (ponto crítico)

NeoForge registra entidades via eventos de registro (`RegisterEvent`) no início do load.
Para entidades definidas em JSON, o Runtime:

1. No evento de registro de `ENTITY_TYPE`, lê as configurações e registra um
   `EntityType<MobForgeEntity>` por entidade, com id derivado do config
   (`mobforge:<id>`).
2. Registra os `EntityAttributeSupplier` (vida, dano, velocidade etc.) com valores do
   config.
3. No evento de renderers (`EntityRenderersEvent.RegisterRenderers`), registra um
   `GeoEntityRenderer` GeckoLib apontando para o modelo/textura/animação do config.

Isso é viável e é o maior risco técnico (ver Riscos). Um *spike* valida isso no início
da Fase 1.

### Estratégia de carregamento de config (duas vias)

- **Fase 1 (MVP)**: leitura de disco — `config/mobforge/entities/*.json`. Simples e
  suficiente para iterar rápido.
- **Fase 4 (export)**: leitura do jar do companion — config empacotado em `mobforge/`
  dentro do mod gerado, lido via filesystem do mod. Torna o mod autocontido.

O Runtime suporta **ambas**: disco (override de desenvolvimento) e jar (distribuição).

### Modelo de execução de skills

Cada entidade carrega uma lista de skills. A `SkillEngine` (tick) avalia, por skill:

1. **Trigger** (evento que ativa a avaliação: ataque, dano recebido, intervalo, spawn...).
2. **Condições** (todas devem ser verdadeiras).
3. **Ações** (executadas em ordem).
4. **Parâmetros** (cooldown, animação, partículas, alvo, prioridade).

Componentes (`Action`, `Condition`, `Trigger`) são interfaces registradas num catálogo,
permitindo extensão sem tocar no núcleo. Ver [`DATA_MODEL.md`](DATA_MODEL.md).

### Máquina de fases (bosses)

Uma entidade do tipo boss tem fases. Cada fase define:
- Condições de ativação (ex.: vida < 50%).
- Conjunto de skills ativas.
- Modificadores (velocidade, multiplicador de dano, animação padrão).

A `PhaseMachine` monitora condições e faz a transição, notificando a engine de skills e a
camada de animação.

---

## 4. Componente C — MobForge Generated Mods

### Estratégia de exportação

O mod gerado é um **companion** do Runtime. Conteúdo do jar:

```
meu-mod.jar
├── META-INF/
│   ├── MANIFEST.MF
│   └── neoforge.mods.toml          # modId, nome, versão, dependência do mobforge
├── mobforge/                        # configurações (contrato de dados)
│   ├── manifest.json                # lista de entidades do mod
│   ├── entities/<id>.json
│   ├── skills/ ... (opcional)
│   └── loot/ ... (opcional)
├── assets/<modid>/                  # assets (obrigatório estar aqui p/ GeckoLib)
│   ├── geo/*.geo.json
│   ├── textures/entity/*.png
│   └── animations/*.animation.json
└── com/mobforge/generated/<modid>/
    └── MobForgeCompanion.class      # classe genérica (template)
```

### Dois caminhos de exportação

1. **Projeto Gradle (primário, Fase 4)** — o editor gera um projeto Gradle completo
   (wrapper, `build.gradle`, `neoforge.mods.toml`, assets, config e a classe genérica
   com pacote único por `modId`) e executa o wrapper para produzir o jar. Mais robusto e
   compatível com modpacks; evita colisão de classes entre mods gerados.
2. **Jar precompilado (otimização de UX)** — ship de um jar template e injeção de
   assets/config + reescrita do `neoforge.mods.toml`. Rápido, mas acopla versão e traz
   risco de colisão de classe. Avaliar após o caminho 1 estar sólido.

O usuário final que só quer o mod recebe um `.jar` (via Gradle wrapper com JDK detectado
ou empacotado); o usuário avançado recebe o projeto para customizar.

### Por que não gerar Java por criatura
Gera manutenção, recompilação e dificuldade de composição. Mantém-se a classe Java do
companion **fixa e genérica**; toda a variação está no JSON + assets.

---

## 5. Contrato de dados (schemas)

Os `schemas/` são a **única fonte de verdade** do formato entre Editor e Runtime:

```
schemas/
  common.schema.json        # tipos base (referências de asset, identificadores)
  entity.schema.json        # definição de entidade
  skill.schema.json         # skill (trigger + condições + ações + params)
  action.schema.json        # catálogo de ações
  condition.schema.json     # catálogo de condições
  phase.schema.json         # fases de boss
  spawn.schema.json         # configuração de spawn
  loot.schema.json          # configuração de loot
  manifest.schema.json      # manifest do mod gerado
```

O Editor usa os schemas para validar; o Runtime valida ao carregar (falha cedo e com
mensagem clara). O campo `schemaVersion` garante evolução compatível.

---

## 6. Estrutura de diretórios (monorepo)

```
MobForge/
├── README.md
├── docs/                      # documentação (esta pasta)
├── schemas/                   # JSON Schema — contrato único
├── runtime/                   # MobForge Runtime (mod NeoForge)
│   ├── settings.gradle
│   ├── build.gradle
│   ├── gradle.properties
│   └── src/
│       ├── main/java/com/mobforge/runtime/
│       │   ├── MobForgeRuntime.java
│       │   ├── config/
│       │   ├── registry/
│       │   ├── entity/
│       │   ├── ai/
│       │   ├── combat/
│       │   ├── skill/         # engine, action, condition, trigger
│       │   ├── phase/
│       │   ├── spawn/
│       │   ├── loot/
│       │   ├── particle/
│       │   └── network/
│       ├── main/resources/META-INF/neoforge.mods.toml
│       └── test/java/         # testes de skills/condições/fases
├── editor/                    # MobForge Editor (desktop)
│   └── src/                   # core, schema, assets, export, ui, viewer, test
├── companion-template/        # template do mod companion (classe genérica)
├── examples/                  # exemplos de mods/configs gerados
└── .github/workflows/         # CI (build do runtime, testes do editor, lint schemas)
```

---

## 7. Fluxos principais

### Criar e jogar com uma criatura
1. Editor: criar projeto → criar entidade → importar modelo/textura/animação →
   configurar atributos/skills → validar → exportar.
2. Export gera `meu-mod.jar` (companion) + garante que o Runtime esteja presente.
3. No jogo: carregar `mobforge` (Runtime) + `meu-mod.jar` → Runtime registra as entidades
   do companion → spawn/loot funcionam conforme config.

### Adicionar uma nova ação ao Runtime (extensibilidade)
1. Implementar `SkillAction` com seu `type` e validação de parâmetros.
2. Registrar no catálogo de ações.
3. Atualizar `action.schema.json`.
4. Editor passa a oferecer a nova ação automaticamente (se consumir o schema).

---

## 8. Riscos e mitigação

| Risco | Impacto | Mitigação |
|-------|---------|-----------|
| Registro dinâmico de entidades/renderers | Alto (bloqueia tudo) | Spike no início da Fase 1; fallback: registrar um `EntityType` "coringa" parametrizável. |
| Acoplamento ao formato GeckoLib | Médio | Abstração `ModelProvider`; Blockbench exporta nativamente GeckoLib; mitigar migração. |
| Leitura de config antes do ResourceManager | Médio | Carregar via filesystem do mod/disco no evento de registro, não via resource manager. |
| Escopo das skills (explosão combinatória) | Médio | Catálogo fechado + schema versionado + validação; expandir incrementalmente. |
| Colisão de classes/mods entre mods gerados | Médio | Pacote único por `modId` no caminho Gradle; validar id globalmente único. |
| Visualizador 3D do formato GeckoLib | Médio (Fase 5) | Usar export glTF do Blockbench ou parser próprio do `.geo.json`; adiar. |
| Deriva de versão do Minecraft | Alto (longo prazo) | Runtime versionado por versão de MC; manter formato de config estável. |
| Testabilidade no jogo | Médio | Extrair lógica pura (skills/condições/fases) para testes unitários sem client. |
