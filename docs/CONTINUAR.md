# MobForge — CONTINUAR (guia de retomada)

Use este arquivo no início de cada sessão para saber onde estamos e o que fazer em
seguida.

---

## Estado atual

- **Fase 0 — Planejamento e arquitetura:** ✅ **concluída.**
- **Fase 1 — Runtime mínimo:** ⬜ não iniciada.
- **Fases 2–6:** ⬜ não iniciadas.

Nenhum código foi implementado ainda (por decisão: não implementar antes de definir a
arquitetura). Este repositório contém apenas documentação.

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

## Pendências da Fase 0 (opcionais, sem bloqueio)

- [ ] Definir licença do Runtime (sugestão MIT) e do Editor.
- [ ] Confirmar versão exata do GeckoLib para NeoForge 1.21.1 no momento do spike.
- [ ] (Se quiser) registrar os ADRs num formato/ferramenta dedicada (hoje: markdown).

## Próximo passo concreto (início da Fase 1)

**Primeira tarefa:** criar o **spike de registro dinâmico de entidades** — é o maior risco
técnico e deve ser validado antes de tudo.

Sequência recomendada:

1. Criar o projeto Gradle do Runtime (`runtime/`) com NeoForge 21.1.x + GeckoLib 4.9.x.
2. Escrever `schemas/entity.schema.json` e `schemas/skill.schema.json`.
3. **Spike**: registrar dinamicamente um `EntityType`, seus atributos e um renderer
   GeckoLib a partir de um JSON mínimo.
4. Se o spike passar, evoluir para o MVP da Fase 1 (ver `MVP.md`).

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
