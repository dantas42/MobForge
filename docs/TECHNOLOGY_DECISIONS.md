# MobForge — Decisões Técnicas (ADRs)

Este documento registra as decisões de arquitetura importantes, os motivos e as
alternativas consideradas. Formato inspirado em *Architecture Decision Records* (ADR).

---

## ADR-001 — Loader: NeoForge (sobre Fabric/Forge)

**Decisão:** NeoForge para Minecraft 1.21.1.

**Motivos:**
- Alinhado à especificação inicial do projeto.
- API de registro moderna (`DeferredRegister` + `RegisterEvent`), que facilita o registro
  dinâmico de entidades a partir de config.
- Suporte robusto a `biome modifiers` (spawn) e datapacks (loot).
- Comunidade ativa e ecossistema de bibliotecas (GeckoLib com build NeoForge oficial).

**Alternativas:**
- *Fabric*: mais leve, mas ecossistema de mods de conteúdo e suporte a GeckoLib menos
  maduro para o caso de uso de entidades animadas complexas.
- *Forge (legado)*: em migração; NeoForge é o sucessor direto.

---

## ADR-002 — Versões: Minecraft 1.21.1 / NeoForge 21.1.x / Java 21

**Decisão:** alvo inicial fixo em MC **1.21.1**, NeoForge **21.1.x**, Java **21**.

**Motivos:**
- Java 21 é o runtime oficial do MC 1.21.1.
- NeoForge 21.1.x é a linha estável correspondente.
- Congelar a versão reduz variáveis durante as fases iniciais; o Runtime será versionado
  por versão de MC no futuro.

**Alternativas:** adotar a versão mais recente do jogo. Descartada por ora para reduzir
risco e porque a base de mods 1.21.1 é ampla.

---

## ADR-003 — Modelos/animações: GeckoLib 4.9.x

**Decisão:** usar GeckoLib como camada de modelo/animação das entidades.

**Motivos:**
- Consome diretamente o formato exportado pelo Blockbench (`.geo.json`, `.animation.json`,
  texturas), removendo a necessidade de escrever model loaders próprios.
- Suporta NeoForge oficialmente (build `geckolib-neoforge-1.21.1-4.9.x`), licença MIT.
- Renderer `GeoEntityRenderer` integra-se ao registro dinâmico de renderers.
- Sistema de animação por triggers combina com o sistema de skills (animação de ataque).

**Alternativas:**
- *Model loaders vanilla (Java)*: exige código por entidade — contrário à filosofia.
- *Model loader custom*: custo alto, sem ganho sobre GeckoLib.
- *ObJ/Bedrock raw*: GeckoLib já normaliza o pipeline Blockbench→entidade.

**Risco aceito:** acoplamento ao formato GeckoLib. Mitigado por uma camada `ModelProvider`
e pela exportação nativa do Blockbench.

---

## ADR-004 — Editor desktop: Tauri 2 + React + TypeScript

**Decisão:** **Tauri 2** (backend Rust) + **React + TypeScript** (frontend). Three.js para
o visualizador 3D futuro. *Fallback pragmático:* Electron + React.

**Critérios avaliados:** custo de desenvolvimento, qualidade visual da UI, visualizador
3D futuro, manipulação de arquivos/zip/jar, tamanho do binário, manutenção.

| Critério | JavaFX | Electron | Tauri |
|----------|:------:|:--------:|:-----:|
| UI moderna e produtiva | Baixo | Alto | Alto |
| Visualizador 3D (Three.js/WebGL) | Fraco | Excelente | Excelente |
| Manipular arquivos/zip/jar | Ok (Java) | Ok (Node) | Ok (Rust) |
| Tamanho/memória do binário | Médio | Alto | Baixo |
| Um só ecossistema (com Runtime Java) | Sim | Não | Não |
| Curva de aprendizado | Média | Baixa | Média (Rust) |

**Motivos da escolha Tauri:**
- A UI é o coração do produto (editor visual rico) → stack web é superior a JavaFX.
- O visualizador 3D (Fase 5) se beneficia enormemente de Three.js/WebGL, inviável com a
  mesma qualidade em JavaFX.
- Binário pequeno e baixo consumo são relevantes para o público de modding.
- Rust cobre bem as operações de filesystem, zip/jar e spawn de processos (Gradle/JDK).

**Alternativas:**
- *Electron + React*: mais rápido de desenvolver (sem Rust), ecossistema maior, mas
  binário pesado. **Fallback** caso a equipe não tenha afinidade com Rust.
- *JavaFX*: compartilha a linguagem do Runtime e permite um único toolchain, mas o custo
  de se construir um editor visual rico + visualizador 3D é alto demais. Descartada.

**Nota sobre integração com o Runtime:** não há acoplamento direto — o editor apenas
produz arquivos e invoca o Gradle. Por isso a linguagem do editor é independente da do
Runtime.

---

## ADR-005 — Contrato de dados: JSON + JSON Schema (versionado)

**Decisão:** configurações em JSON, validadas contra **JSON Schema** versionado,
compartilhado em `schemas/`.

**Motivos:**
- JSON é o formato pedido e legível/edito por humanos e ferramentas.
- JSON Schema fornece validação automática no Editor (antes de exportar) e no Runtime
  (falha cedo com mensagem clara).
- Campo `schemaVersion` permite evolução compatível do formato.

**Alternativas:**
- *TOML/outros*: sem ecossistema de validação equivalente.
- *Datapack nativo (registries JSON)*: a IA/combate/skills não são representáveis no
  sistema de datapacks; só loot/spawn aproveitariam. Descartado como formato principal.

---

## ADR-006 — Exportação: mod companion (sem código Java por criatura)

**Decisão:** o mod gerado é um **companion** fino (assets + config + uma classe Java
genérica) que declara dependência do Runtime. **Nenhuma geração de Java por criatura.**

**Motivos:**
- Mantém toda a lógica no Runtime (reuso, correção centralizada, atualizações).
- Mod gerado é leve, fácil de distribuir e de manter.
- Evita recompilação e geração de código frágil.

**Alternativas:**
- *Gerar classes Java por entidade*: contraria a filosofia; difícil de compor e manter.
- *Runtime único sem companion (só config em disco)*: não distribui como mod autocontido.

**Caminhos de empacotamento (definidos em ARCHITECTURE):**
1. Projeto Gradle gerado (primário).
2. Jar template precompilado (otimização de UX posterior).

---

## ADR-007 — Carregamento de config: disco (dev) e jar (distribuição)

**Decisão:** o Runtime lê config de duas fontes: `config/mobforge/` (disco, para
desenvolvimento) e `mobforge/` dentro do jar do companion (para distribuição). Assets
(modelos/texturas/animações) sempre no jar, em `assets/<modid>/`.

**Motivos:**
- Config precisa estar disponível no momento do registro de entidades (antes do
  ResourceManager completo) → leitura por filesystem do mod/disco.
- Assets precisam estar em `assets/<modid>/` para o GeckoLib resolver por mod id.

**Alternativas:** config via ResourceManager/datapack — descartada pelo timing do
registro.

---

## ADR-008 — Skills: componentes registrados (ação/condição/trigger)

**Decisão:** skills são compostas por `trigger + condições + ações + parâmetros`, onde
cada ação/condição/trigger é um **componente registrado** (interface + `type`), com
parâmetros validados por schema.

**Motivos:**
- Extensibilidade sem tocar no núcleo (novos componentes = novo registro + schema).
- Testabilidade: condições e ações são funções puras testáveis fora do jogo.
- Simplicidade para o usuário: combina peças pré-definidas, sem scripting livre.

**Alternativas:**
- *Scripting livre (Lua/JS)*: mais flexível, porém complexo e inseguro; contraria o
  princípio de simplicidade. Descartado (a proposta pede condições/ações predefinidas).
- *Habilidades hardcoded por entidade*: contrário à filosofia.

---

## ADR-009 — Não implementar visualizador 3D na Fase 0/1 (mas projetar para ele)

**Decisão:** o visualizador 3D é **projetado** (espaço `viewer/`, escolha de Three.js) mas
**não implementado** nas primeiras fases.

**Motivos:**
- Alto custo e risco; não bloqueia o valor central (criar/exportar criaturas).
- A escolha de stack web (ADR-004) já prepara o terreno.

**Alternativas:** começar pelo visualizador — descartada por foco em valor mínimo.

---

## Resumo das decisões

| Tema | Decisão |
|------|---------|
| Loader | NeoForge |
| Versões | MC 1.21.1, NeoForge 21.1.x, Java 21 |
| Modelos/animações | GeckoLib 4.9.x (NeoForge) |
| Editor | Tauri 2 + React + TS (fallback Electron) |
| Dados | JSON + JSON Schema versionado |
| Exportação | Mod companion, sem Java por criatura |
| Config | Disco (dev) + jar (distribuição); assets no jar |
| Skills | Componentes registrados (ação/condição/trigger) |
| Visualizador 3D | Projetado agora, implementado na Fase 5 |
