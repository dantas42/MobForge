# MobForge — CONTINUAR (guia de retomada)

Use este arquivo no início de cada sessão para saber onde estamos e o que fazer em
seguida.

---

## Estado atual

- **Fase 0 — Planejamento e arquitetura:** ✅ **concluída.**
- **Fase 1 — Runtime mínimo:** 🔄 **em andamento** (spike de registro dinâmico criado).
- **Fases 2–6:** ⬜ não iniciadas.

O spike de registro dinâmico (maior risco técnico) foi **implementado**: projeto Gradle do
Runtime, leitor de config, e registro de entity types/atributos/renderers via GeckoLib.
**Ainda não foi compilado/testado** — a máquina atual não tem JDK/Gradle instalados.

## O que já foi decidido (resumo)

- Minecraft **1.21.1**, NeoForge **21.1.x**, Java **21**.
- Modelos/animações: **GeckoLib 4.9.x** (NeoForge), importados do Blockbench.
- Editor: **Tauri 2 + React + TypeScript** (fallback Electron + React).
- Dados: **JSON + JSON Schema** versionado, compartilhado em `schemas/`.
- Exportação: **mod companion** (assets + config + classe Java genérica), **sem Java por
  criatura**.
- Skills: **triggers + condições + ações** como componentes registrados.
- Detalhes e motivações: ver `TECHNOLOGY_DECISIONS.md` e `ARCHITECTURE.md`.

## Tarefas concluídas (Fase 0)

- [x] Análise da proposta e problemas arquiteturais.
- [x] Avaliação de viabilidade técnica.
- [x] Definição da arquitetura (3 componentes + contrato de dados).
- [x] Definição da estrutura de diretórios (monorepo).
- [x] Definição do modelo de dados inicial.
- [x] Identificação das dependências e versões.
- [x] Definição da estratégia de geração dos mods.
- [x] Definição do MVP realista.
- [x] Roadmap incremental (Fases 0–6).
- [x] Documentação criada em `docs/`.

## Tarefas concluídas (Fase 1 — spike de registro dinâmico)

- [x] Projeto Gradle do Runtime (`runtime/`) com NeoForge 21.1.264 + GeckoLib 4.9.3.
- [x] `schemas/entity.schema.json` e `schemas/skill.schema.json`.
- [x] Leitor de config (`MobForgeData`) — lê `config/mobforge/entities/*.json`.
- [x] DTO `EntityDefinition` (mapeia o JSON de entidade).
- [x] Registro dinâmico de `EntityType` (`MobForgeRegistries`).
- [x] Registro de atributos via `EntityAttributeCreationEvent`.
- [x] Registro de renderer GeckoLib (`MobForgeClientEvents` + `MobForgeRenderer`).
- [x] Entidade base `MobForgeEntity` (melee + animação idle/walk).
- [x] Modelo `MobForgeGeoModel` data-driven.
- [x] Assets de exemplo (geo + animation + textura) e config de exemplo.

## Pendências da Fase 1 (não iniciadas)

- [ ] Compilar e validar o spike (requer JDK 21 + Gradle — não disponíveis nesta máquina).
- [ ] Skill mínima (trigger + cooldown + ação de dano) com animação de ataque.
- [ ] Spawn natural (biome modifiers) e loot.

## Pendências da Fase 0 (opcionais, sem bloqueio)

- [ ] Definir licença do Runtime (sugestão MIT) e do Editor.
- [ ] Confirmar versão exata do GeckoLib para NeoForge 1.21.1 no momento do spike.
- [ ] (Se quiser) registrar os ADRs num formato/ferramenta dedicada (hoje: markdown).

## Próximo passo concreto

**Compilar e validar o spike** — o código foi escrito, mas ainda não foi compilado/testado.

1. Instalar **JDK 21** (ou deixar o `foojay-resolver` baixá-lo via Gradle).
2. Em `runtime/`: `gradle wrapper --gradle-version 8.13` e depois `gradlew build`.
3. Corrigir eventuais erros de API (GeckoLib 4.9.3 / NeoForge 21.1.264).
4. `gradlew runClient` + copiar `examples/config/mobforge/entities/test_dummy.json` para
   `runtime/run/config/mobforge/entities/`.
5. `/summon mobforge:test_dummy` → validar registro + render + melee.

Se o registro dinâmico for validado, evoluir para o MVP da Fase 1: skill mínima (trigger +
cooldown + ação de dano) com animação de ataque (ver `MVP.md`).

## Onde ler

| Quero saber… | Arquivo |
|--------------|---------|
| O que é o projeto e seus princípios | `VISION.md` |
| Como os componentes se encaixam | `ARCHITECTURE.md` |
| Por que escolhemos essas tecnologias | `TECHNOLOGY_DECISIONS.md` |
| O formato dos JSONs | `DATA_MODEL.md` |
| A ordem das fases | `ROADMAP.md` |
| O escopo mínimo | `MVP.md` |

## Regras para as próximas sessões

- Retomar **sempre** por este arquivo.
- Não pular para implementação sem concluir a tarefa em andamento.
- Atualizar este arquivo (status, pendências, próximo passo) **ao final de cada sessão**.
- Documentar decisões novas como ADRs em `TECHNOLOGY_DECISIONS.md`.
- Manter `schemas/` como fonte de verdade do contrato de dados.

---

## Checklist de atualização (preencher ao encerrar)

- [ ] Estado atual atualizado.
- [ ] Tarefas concluídas atualizadas.
- [ ] Pendências atualizadas.
- [ ] Próximo passo concreto definido.
