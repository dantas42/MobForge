# MobForge — Visão

## 1. O problema

Criar uma criatura, monstro ou boss para Minecraft exige hoje:

1. Saber Java e conhecer a API do NeoForge (registro de entidade, renderer, atributos, IA).
2. Escrever código específico por entidade (classe de entidade, renderer, modelo, goals).
3. Implementar manualmente lógica de combate, skills, fases e loot.
4. Recompilar o mod a cada alteração de comportamento.

Isso coloca a criação de conteúdo (design de criaturas) atrás de uma barreira de
programação. Designers, servidores e criadores de conteúdo que sabem modelar no
Blockbench e equilibrar atributos não conseguem transformar essas ideias em mods.

## 2. A proposta

O MobForge é uma **ferramenta visual de criação de conteúdo** que separa o *design de
criaturas* (o que o usuário quer) da *implementação técnica* (como o jogo executa).

O usuário combina elementos existentes — modelos, atributos, ataques, skills, condições,
fases e loot — através de um editor visual, e o MobForge gera um mod pronto para uso.

A complexidade técnica (registro de entidades, IA, sistema de skills, animações, spawn)
fica **concentrada no Runtime**, um mod base reutilizável que interpreta configurações
em JSON. Cada mod gerado é apenas um *companion* que carrega assets e configurações e
declara dependência do Runtime.

## 3. O que o MobForge É

- **Um editor visual** para criar e configurar criaturas.
- **Um sistema de templates e componentes** reutilizáveis.
- **Um configurador de entidades** (atributos, combate, spawn, loot).
- **Um sistema modular de skills** (ações + condições + parâmetros).
- **Uma ferramenta de exportação** que gera mods compatíveis.
- **Uma plataforma extensível** para novos componentes (ações, condições, efeitos).

## 4. O que o MobForge NÃO É

- **Não é um gerador de código Java livre.** Não gera classes Java por criatura; a lógica
  genérica vive no Runtime.
- **Não é uma ferramenta obrigatoriamente baseada em IA.** IA pode auxiliar em sugestões
  futuras, mas não é o mecanismo central.
- **Não cria mecânicas inteiramente novas para cada criatura.** As mecânicas disponíveis
  vêm do catálogo de componentes do Runtime.
- **Não é excessivamente complexo para o usuário final.** O usuário trabalha com
  conceitos familiares (vida, dano, cooldown, área, efeito), não com código.

## 5. Princípios de design

1. **Configuração sobre código.** Tudo que puder ser configurado deve ser configurado em
   JSON; Java só quando estritamente necessário.
2. **Complexidade no Runtime, simplicidade no Editor.** O Runtime concentra a lógica; o
   editor apenas descreve intenções.
3. **Contrato de dados único.** Editor e Runtime compartilham um JSON Schema versionado
   como única fonte de verdade.
4. **Simplicidade e modularidade.** Sem abstrações desnecessárias; cada subsistema com
   uma responsabilidade clara.
5. **Extensibilidade por catálogo.** Ações, condições e efeitos são componentes
   registrados que podem crescer sem reescrever o núcleo.
6. **Testabilidade.** Lógica de skills, condições e fases deve ser testável de forma
   isolada, fora do jogo quando possível.
7. **Evolução incremental.** Cada fase entrega valor utilizável, nunca um "big bang".

## 6. Público-alvo

- **Criadores de conteúdo e designers** que modelam no Blockbench.
- **Servidores e packs de mods** que querem criaturas e bosses customizados.
- **Modders iniciantes** que querem prototipar sem escrever Java.
- **Modders experientes** que querem acelerar a produção de entidades repetitivas.

## 7. Métricas de sucesso (visão de longo prazo)

- Criar uma criatura com modelo, atributos e um ataque em **menos de 10 minutos**, sem
  tocar em Java.
- Exportar um mod jogável (jar) com um clique.
- Adicionar uma nova ação/condição ao Runtime **sem modificar o núcleo** (só registro).
- Mods gerados funcionam em servidores e modpacks com o Runtime instalado.

## 8. Escopo inicial (1.21.1)

A primeira versão tem como alvo **Minecraft 1.21.1 + NeoForge + Java 21**. Versões
futuras de Minecraft serão suportadas por novas versões do Runtime, mantendo o formato
de configuração estável sempre que possível.
