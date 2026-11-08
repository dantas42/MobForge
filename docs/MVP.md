# MobForge — MVP

## Objetivo do MVP

Provar a tese central do projeto **de ponta a ponta**, no menor escopo possível:

> Um usuário consegue definir uma criatura (modelo + atributos + um ataque) e vê-la
> funcionar no jogo, **sem escrever nenhuma linha de Java**.

O MVP cobre o **fim da Fase 1 + início da Fase 2** e deve responder às perguntas de risco:

1. O registro dinâmico de entidades/renderers funciona? (maior risco)
2. O formato JSON é suficiente para descrever a criatura?
3. O pipeline Blockbench → GeckoLib → entidade funciona?

## Escopo (dentro)

- **Runtime:**
  - Registrar 1 entidade customizada a partir de `entity.schema.json`.
  - Modelo + textura + animação via GeckoLib (importado do Blockbench).
  - Atributos: vida, dano, velocidade, tamanho, categoria.
  - IA básica: hostilidade + ataque corpo a corpo (goals vanilla).
  - 1 skill simples: dano com cooldown e animação de ataque.
  - Spawn manual (comando/summon) — **sem** spawn natural ainda.
- **Editor (mínimo):**
  - Criar projeto.
  - Criar 1 entidade (atributos + modelo importado).
  - Validar contra schema.
  - Exportar o config (+ assets) que o Runtime consome.

## Escopo (fora do MVP)

- Sistema completo de skills (Fase 3).
- Fases de boss, projéteis, área de dano, summon, explosão.
- Spawn natural/biome modifiers e loot (vêm depois; MVP usa summon).
- Exportação como jar autocontido (Fase 4) — MVP usa config em disco.
- Visualizador 3D (Fase 5).
- Qualquer geração de Java por criatura (nunca).

## Critérios de aceite (definition of done)

1. Iniciar o jogo com o Runtime + config em `config/mobforge/`.
2. A criatura aparece via `/summon mobforge:<id>`.
3. Renderiza com o modelo/textura do Blockbench.
4. Tem os atributos configurados (vida/dano/velocidade/tamanho).
5. Persegue e ataca o jogador em corpo a corpo.
6. Executa a skill com cooldown e animação corretos.
7. O editor valida e gera o JSON consumido pelo Runtime sem edição manual.

## Entregas do MVP (ordem sugerida)

1. `schemas/entity.schema.json` + `schemas/skill.schema.json`.
2. Projeto Gradle do Runtime com entidade base e leitor de config.
3. Spike de registro dinâmico (entidade + renderer + atributos) — **fazer primeiro**.
4. Integração GeckoLib (modelo/textura/animação).
5. IA básica + melee.
6. Engine de skill mínima (trigger + 1 condição + 1 ação + cooldown).
7. Editor mínimo (projeto + 1 entidade + validação + export).

## Riscos específicos do MVP

- **Registro dinâmico**: se falhar, fallback para um `EntityType` "coringa" único,
  parametrizado por config (menos elegante, mas funcional).
- **GeckoLib**: validar a versão exata (`4.9.x` para NeoForge 1.21.1) no início do spike.

## Medida de sucesso

Tempo para criar e jogar com uma criatura simples (modelo Blockbench → summon no jogo)
deve ser **minutos, sem Java**. O MVP é bem-sucedido se isso ocorrer de forma
reproduzível.
