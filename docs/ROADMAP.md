# MobForge — Roadmap

Roadmap incremental, com entregas utilizáveis ao final de cada fase. Cada fase tem
**entregáveis**, **critérios de saída** e **dependências**.

---

## Fase 0 — Planejamento e arquitetura ✅

**Objetivo:** consolidar viabilidade, arquitetura, tecnologias, modelo de dados e
estratégia de exportação.

Entregáveis:
- Documentação (`VISION`, `ARCHITECTURE`, `TECHNOLOGY_DECISIONS`, `DATA_MODEL`, `MVP`,
  `ROADMAP`, `CONTINUAR`).
- Estrutura de monorepo definida.
- Riscos identificados e mitigados.

**Saída:** base documental para iniciar a Fase 1.

---

## Fase 1 — Runtime mínimo funcional

**Objetivo:** provar que o núcleo funciona — registrar e renderizar uma entidade a
partir de JSON.

Entregáveis:
1. Projeto Gradle do Runtime (NeoForge 21.1.x, Java 21).
2. Leitor de config (disco) + validação contra `entity.schema.json`.
3. **Spike**: registro dinâmico de `EntityType`, atributos e renderer GeckoLib.
4. Entidade base `MobForgeEntity` (vida, dano, velocidade, tamanho, categoria).
5. IA básica (hostilidade + melee via goals vanilla).
6. Uma skill simples (dano com cooldown) via engine mínima.
7. Animação de ataque via GeckoLib (trigger).
8. Importar modelo/textura/animação de exemplo (Blockbench).
9. Testes unitários da lógica pura (condições/ações simples).

**Critérios de saída:** no jogo, `mobforge` registra e renderiza uma criatura definida em
JSON, com atributos, melee e uma skill com cooldown/animación funcionando.

---

## Fase 2 — Editor de entidades

**Objetivo:** primeira versão utilizável do editor.

Entregáveis:
1. App desktop (Tauri/React) com estrutura de projeto.
2. Sistema de projetos (criar/abrir/salvar).
3. Editor de atributos e combate.
4. Importação de assets (normalização Blockbench → `assets/<modid>/`).
5. Edição de configurações de entidade.
6. Validação JSON (consumo dos schemas) com feedback visual.

**Critérios de saída:** criar uma entidade no editor, salvar, validar e exportar o config
que o Runtime consome (sem empacotar jar ainda).

---

## Fase 3 — Sistema de skills completo

**Objetivo:** catálogo de componentes e fases de boss.

Entregáveis:
1. Catálogo de triggers, condições e ações (interfaces + registro).
2. Engine completa: cooldowns, prioridade, sequência, alvos.
3. Fases de boss (máquina de estados + modificadores).
4. Partículas/efeitos vinculados a skills.
5. Componentes de combate: projéteis, área de dano, summon, knockback, teleport,
   explosão, cura, efeitos de status.
6. Testes unitários das novas condições/ações/fases.

**Critérios de saída:** um boss com 3 fases e skills compostas definido 100% em JSON.

---

## Fase 4 — Exportação

**Objetivo:** gerar um mod companion jogável e distribuível.

Entregáveis:
1. Template do companion (classe genérica + `neoforge.mods.toml`).
2. Geração de projeto Gradle por exportação (pacote único por `modId`).
3. Empacotamento de assets + config no jar (`mobforge/` + `assets/`).
4. Runtime lendo config do jar do companion.
5. Validação de dependências (declaração do `mobforge`).
6. (Otimização posterior) jar precompilado para export em um clique.

**Critérios de saída:** exportar `meu-mod.jar`; jogar com ele + Runtime sem config em
disco.

---

## Fase 5 — Visualizador 3D

**Objetivo:** preview visual dentro do editor.

Entregáveis:
1. Viewport 3D (Three.js) integrado ao editor.
2. Importar e visualizar modelo + texturas.
3. Reproduzir animações e ataques.
4. Visualizar efeitos de partículas.
5. Preview de skills.

**Critérios de saída:** inspecionar modelo, rotação/zoom, reproduzir animação de ataque
no editor.

---

## Fase 6 — Expansão

**Objetivo:** ecossistema e extensibilidade.

Entregáveis:
1. Novos componentes (ações/condições/efeitos).
2. Novos tipos de comportamento.
3. Templates compartilháveis (exportar/importar criaturas e skills).
4. Suporte a extensões da comunidade.
5. (Avaliar) suporte a novas versões de Minecraft via Runtime versionado.

---

## Visão temporal (orientativa, não compromisso)

| Fase | Esforço relativo | Prioridade |
|------|------------------|------------|
| 1 | Alto (risco técnico) | Bloqueante |
| 2 | Alto | Alta |
| 3 | Médio-alto | Alta |
| 4 | Médio | Alta |
| 5 | Médio (adiável) | Média |
| 6 | Contínuo | Baixa |

> A ordem 1→2→3→4 é intencional: valida o risco técnico (Fase 1) antes de investir em
> UI; o editor (Fase 2) só faz sentido quando o Runtime já lê config (Fase 1).
