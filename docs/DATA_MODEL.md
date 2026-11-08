# MobForge — Modelo de Dados

## 1. Princípios

- **JSON é o formato canônico.** Editor e Runtime consomem o mesmo formato.
- **Schemas são a fonte de verdade** (`schemas/*.schema.json`), versionados por
  `schemaVersion`.
- **Identificadores**: entidades usam `id` curto (ex.: `abyssal`); o Runtime deriva o id
  registrado (`mobforge:<id>`); assets usam caminho relativo a `assets/<modid>/`.
- **Tudo opcional tem default**: campos ausentes usam valores padrão documentados.
- **Referências de asset** apontam para caminhos dentro do mod (não caminhos absolutos).

---

## 2. Entidades de domínio

```
Project ──1:N──► Entity
Entity  ──1:N──► Skill
Entity  ──1:N──► Phase          (só categoria "boss")
Entity  ──1:1──► Spawn
Entity  ──1:1──► Loot
Entity  ──1:1──► Model (referências de asset)
Skill   ──1:1──► Trigger
Skill   ──0:N──► Condition
Skill   ──1:N──► Action
```

---

## 3. Manifest do mod gerado

Arquivo `mobforge/manifest.json` dentro do jar do companion. Lista as entidades que o
Runtime deve registrar para aquele mod.

```json
{
  "schemaVersion": 1,
  "modId": "meu_mod",
  "entities": ["abyssal", "goblin"]
}
```

---

## 4. Entity

```jsonc
{
  "schemaVersion": 1,
  "id": "abyssal",
  "displayName": "Abissal",
  "category": "boss",                 // animal | creature | monster | boss
  "model": {
    "geo": "geo/abyssal.geo.json",
    "textures": { "default": "textures/entity/abyssal.png" },
    "animations": "animations/abyssal.animation.json"
  },
  "dimensions": { "width": 1.4, "height": 2.8 },
  "attributes": {
    "maxHealth": 500.0,
    "armor": 10.0,
    "armorToughness": 4.0,
    "attackDamage": 12.0,
    "movementSpeed": 0.30,
    "followRange": 40.0,
    "knockbackResistance": 0.6
  },
  "resistances": [
    { "damageType": "magic", "multiplier": 0.5 }
  ],
  "combat": {
    "hostile": true,
    "melee": { "range": 3.0, "damage": 12.0, "attackCooldownTicks": 20 }
  },
  "skills": [ /* ver §5 */ ],
  "phases": [ /* ver §6 — apenas boss */ ],
  "spawn": { /* ver §7 */ },
  "loot": { /* ver §8 */ }
}
```

### Categorias (`category`)
- `animal` — pacífico, não ataca, foge/ignora.
- `creature` — neutro ou hostil simples.
- `monster` — hostil, spawn natural noturno.
- `boss` — hostil com fases; determina presença de `phases`.

---

## 5. Skill

```jsonc
{
  "id": "venom_bite",
  "displayName": "Mordida Venenosa",
  "trigger": { "type": "on_attack", "params": {} },
  "conditions": [
    { "type": "cooldown_ready", "params": { "skill": "venom_bite" } },
    { "type": "distance", "params": { "max": 3.0 } },
    { "type": "random_chance", "params": { "chance": 0.35 } }
  ],
  "actions": [
    { "type": "damage", "params": { "amount": 15.0, "target": "nearest_player" } },
    { "type": "apply_effect", "params": { "effect": "poison", "durationSeconds": 8, "amplifier": 1 } }
  ],
  "params": {
    "cooldownSeconds": 5,
    "animation": "venom_bite",
    "particles": ["poison_cloud"],
    "priority": 10,
    "target": "nearest_player"
  }
}
```

### Triggers
| `type` | Descrição |
|--------|-----------|
| `on_attack` | Ao atacar (golpe) |
| `on_damaged` | Ao receber dano |
| `on_spawn` | Ao surgir |
| `on_interval` | Em intervalos fixos (`params.intervalSeconds`) |
| `on_phase_enter` | Ao entrar numa fase |
| `on_target_acquire` | Ao adquirir alvo |

### Catálogo de ações (inicial)
`damage`, `apply_effect`, `heal`, `spawn_projectile`, `area_damage`, `summon`,
`apply_knockback`, `teleport`, `explosion`, `melee_attack`.

### Catálogo de condições (inicial)
`health_below_pct`, `target_health_below_pct`, `distance`, `cooldown_ready`,
`players_nearby`, `boss_phase`, `random_chance`, `valid_target`.

### Parâmetros comuns de skill
`cooldownSeconds`, `animation`, `particles`, `priority`, `target`
(`nearest_player` | `self` | `random_player` | `last_attacker`).

---

## 6. Phase (boss)

```jsonc
{
  "id": "phase_2",
  "activation": [
    { "type": "health_below_pct", "params": { "pct": 0.50 } }
  ],
  "modifiers": {
    "movementSpeedMultiplier": 1.4,
    "damageMultiplier": 1.2,
    "defaultAnimation": "phase_2_idle"
  },
  "skills": ["area_blast", "summon_minions"]
}
```

Regras:
- Fases avaliadas em ordem; a primeira cuja `activation` for verdadeira é a ativa.
- Se nenhuma fase casa, a entidade usa o conjunto padrão de `skills` da entidade.
- `modifiers` alteram atributos em tempo de execução (multiplicadores).

---

## 7. Spawn

```jsonc
{
  "enabled": true,
  "biomes": ["minecraft:deep_dark", "#minecraft:is_overworld"],   // tags suportadas
  "weight": 40,
  "minGroupSize": 1,
  "maxGroupSize": 3,
  "spawnType": "monster",           // monster | creature | ambient | misc
  "conditions": [
    { "type": "dimension", "params": { "dimension": "minecraft:overworld" } }
  ]
}
```

Mapeado para **biome modifiers** do NeoForge na Fase 1/4. Spawn por **estrutura/evento**
fica para fases posteriores.

---

## 8. Loot

```jsonc
{
  "rolls": { "min": 1, "max": 3 },
  "entries": [
    { "item": "minecraft:rotten_flesh", "weight": 80, "count": { "min": 1, "max": 3 } },
    { "item": "minecraft:diamond", "weight": 5, "count": 1 }
  ],
  "conditions": []
}
```

Na Fase 1, o Runtime gera uma loot table vanilla a partir deste config e a associa à
entidade. Alternativa futura: gerar datapack de loot tables no export.

---

## 9. Referências de asset

| Campo | Formato | Origem |
|-------|---------|--------|
| `model.geo` | `geo/<nome>.geo.json` | Blockbench (GeckoLib format) |
| `model.textures` | `textures/entity/<nome>.png` | Blockbench (pintura) |
| `model.animations` | `animations/<nome>.animation.json` | Blockbench (GeckoLib) |

O editor normaliza os arquivos do projeto Blockbench para essa estrutura dentro de
`assets/<modid>/`.

---

## 10. Evolução e compatibilidade

- Todo documento tem `schemaVersion` (inteiro). Alterações incompatíveis incrementam a
  versão; o Runtime suporta um intervalo `[min, max]` de versões.
- Campos novos são opcionais com default — nunca removem significado de arquivos antigos.
- O JSON Schema em `schemas/` é a implementação executável deste documento.

> Próximo passo concreto do modelo de dados: materializar `schemas/entity.schema.json` e
> `schemas/skill.schema.json` como os dois primeiros artefatos da Fase 1.
