# MobForge

Ferramenta desktop para criação e geração de mods de criaturas, monstros e bosses para
Minecraft, sem a necessidade de programar cada entidade em Java.

O MobForge permite criar criaturas personalizadas combinando **templates, componentes e
configurações**, e exportá-las como mods compatíveis com o jogo.

> **Status atual:** Fase 0 — Planejamento e Arquitetura (concluída).
> Veja [`docs/CONTINUAR.md`](docs/CONTINUAR.md) para retomar o desenvolvimento.

## Documentação

| Documento | Descrição |
|-----------|-----------|
| [`docs/VISION.md`](docs/VISION.md) | Visão, objetivos, princípios e o que o projeto é/não é |
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Arquitetura conceitual, componentes e estrutura de diretórios |
| [`docs/TECHNOLOGY_DECISIONS.md`](docs/TECHNOLOGY_DECISIONS.md) | Decisões técnicas e alternativas consideradas (ADRs) |
| [`docs/DATA_MODEL.md`](docs/DATA_MODEL.md) | Modelo de dados e esquema JSON das configurações |
| [`docs/ROADMAP.md`](docs/ROADMAP.md) | Fases de desenvolvimento e roadmap incremental |
| [`docs/MVP.md`](docs/MVP.md) | Escopo do MVP realista e critérios de aceite |
| [`docs/CONTINUAR.md`](docs/CONTINUAR.md) | Guia para retomar o desenvolvimento em sessões futuras |

## Componentes

- **MobForge Runtime** — mod base NeoForge que executa as configurações (lógica genérica).
- **MobForge Editor** — aplicação desktop para criar/editar/exportar mods.
- **MobForge Generated Mods** — mods gerados pelo editor (companion do Runtime).

## Stack

- Minecraft **1.21.1**, NeoForge **21.1.x**, Java **21**.
- GeckoLib **4.9.x** (animações/modelos).
- Editor desktop: **Tauri 2 + React + TypeScript** (ver decisões).
- JSON + JSON Schema como contrato de dados.

## Licença

A definir (sugestão: Runtime em MIT/LGPL, Editor conforme decisão de distribuição).
