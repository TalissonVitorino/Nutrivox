# Proposta de Design do Produto — Nutrivox
## Versão 1.0

---

## 1. Princípios de Produto

| # | Princípio | Descrição |
|---|-----------|-----------|
| P1 | **Nutricionista no controle** | Toda decisão clínica (criar, ativar, editar plano) é exclusiva do nutricionista. O sistema não toma decisões autônomas sobre prescrição. |
| P2 | **IA como copiloto, nunca como piloto** | A IA sugere, informa e contextualiza. Nunca prescreve, ativa ou substitui automaticamente. A distinção deve ser visualmente inconfundível. |
| P3 | **Clareza sobre complexidade** | Quando houver trade-off entre mostrar mais dados e manter a tela compreensível, priorizar clareza. Informação secundária vai para camadas de detalhe (progressive disclosure). |
| P4 | **Registro sem atrito** | Registrar consumo deve ser tão fácil quanto dar um check. A barreira deve ser mínima para garantir adesão do paciente. |
| P5 | **Rastreabilidade silenciosa** | O sistema registra tudo nos bastidores (alterações, origens, versões) sem poluir a interface do paciente com metadados. O nutricionista acessa quando precisa. |
| P6 | **Progressão visível** | O paciente deve sentir progresso — metas diárias, evolução de medidas, aderência. Dados que motivam, não que sobrecarregam. |
| P7 | **Consistência semântica** | Plano, variação, refeição, item, substituição, sugestão — cada termo tem significado único e aparência visual distinta em toda a interface. |

---

## 2. Arquitetura de Informação

### 2.1 Estrutura Geral

```
NUTRIVOX
│
├── APP DO PACIENTE (mobile)
│   │
│   ├── [Tab 1] MEU PLANO (Home)
│   │   ├── Header: nome do plano + seletor de variação
│   │   ├── Progresso do dia: barra kcal (meta vs consumido)
│   │   ├── Cards de macros: P / C / G (meta vs consumido)
│   │   ├── Lista de refeições (cards compactos)
│   │   │   └── Cada card: nome + horário + kcal + macros + status
│   │   └── Orientações do nutricionista
│   │
│   ├── [Tab 2] PROGRESSO
│   │   ├── Calendário / seletor de período
│   │   ├── Aderência (% refeições registradas)
│   │   ├── Gráfico kcal diárias (meta vs consumido)
│   │   ├── Macros acumulados
│   │   └── Evolução de medidas (quando disponível)
│   │
│   ├── [Tab 3] ASSISTENTE IA
│   │   ├── Disclaimer de boas-vindas
│   │   ├── Histórico de mensagens
│   │   ├── Chips de sugestão rápida
│   │   └── Input de texto
│   │
│   └── [Tab 4] PERFIL
│       ├── Dados pessoais
│       ├── Notificações
│       ├── Tema / aparência
│       └── Privacidade e consentimentos
│
├── APP/PAINEL DO NUTRICIONISTA
│   │
│   ├── DASHBOARD
│   │   ├── Pacientes com alertas
│   │   ├── Atividade recente
│   │   └── Consultas do dia (futuro)
│   │
│   ├── PACIENTES
│   │   ├── Busca e filtros
│   │   └── Cards resumo (nome, objetivo, plano ativo, última consulta)
│   │
│   ├── PERFIL DO PACIENTE (ao selecionar)
│   │   ├── [Sub-tab] Prontuário / Ficha clínica
│   │   ├── [Sub-tab] Planos alimentares
│   │   │   ├── Lista de planos (ativos, anteriores)
│   │   │   └── Editor de plano
│   │   ├── [Sub-tab] Avaliações / Medidas
│   │   ├── [Sub-tab] Consumo
│   │   │   ├── Histórico de registros
│   │   │   ├── Aderência
│   │   │   └── Sugestões IA usadas
│   │   └── [Sub-tab] Evolução clínica
│   │
│   └── CONFIGURAÇÕES
│       ├── Perfil profissional
│       ├── Configurações de IA por paciente
│       └── Tema / aparência
│
└── BACKEND (invisível ao usuário)
    ├── Auth + Permissões
    ├── APIs (Planos, Consumo, Avaliações)
    ├── Motor de Cálculo Nutricional
    ├── Base de Alimentos + Medidas Caseiras
    ├── Gateway de IA (mediado)
    ├── Notificações push
    ├── Auditoria
    └── Relatórios
```

### 2.2 Navegação Principal

**Paciente — Bottom Navigation (4 tabs):**

| Tab | Ícone | Label | Função |
|-----|-------|-------|--------|
| 1 | Clipboard/utensílios | Meu Plano | Home — plano ativo, refeições do dia, progresso |
| 2 | Gráfico/trending | Progresso | Histórico, aderência, evolução |
| 3 | Sparkle/chat | Assistente | Chat com IA nutricional |
| 4 | Pessoa/engrenagem | Perfil | Configurações, dados, tema, privacidade |

**Nutricionista — Side Navigation ou Top Tabs:**

| Seção | Função |
|-------|--------|
| Dashboard | Visão geral, alertas, atividade |
| Pacientes | Lista com busca/filtros |
| [Paciente] | Sub-navegação: prontuário, planos, avaliações, consumo, evolução |
| Configurações | Perfil, IA, tema |

---

## 3. Fluxos Principais

### 3.1 Fluxo: Criação de Plano Alimentar

```
NUTRICIONISTA
│
├── Abre perfil do paciente
├── Toca "Novo Plano" (ou "Duplicar plano anterior")
│
├── ETAPA 1 — Dados gerais
│   ├── Nome do plano
│   ├── Objetivo clínico
│   ├── Data de início / término
│   └── Metas nutricionais (kcal, P, C, G, fibras)
│
├── ETAPA 2 — Variações de dieta
│   ├── Cria variação "Padrão" (obrigatória)
│   ├── (Opcional) Cria variações adicionais
│   └── Define quais são liberadas para o paciente
│
├── ETAPA 3 — Refeições (por variação)
│   ├── Adiciona refeição (nome, horário, ordem)
│   └── Para cada refeição:
│       ├── Busca alimento na base
│       ├── Define quantidade (medida caseira → gramas)
│       ├── Adiciona observação (opcional)
│       ├── Cadastra substituições autorizadas
│       └── Repete para outros itens
│
├── BARRA DE TOTAIS (fixa, atualiza em tempo real)
│   └── Mostra: total prescrito vs. metas definidas
│
├── ETAPA 4 — Revisão
│   ├── Preview "como o paciente vê"
│   ├── Verifica totais vs. metas
│   └── Adiciona orientações gerais
│
└── PUBLICAR
    ├── Confirma publicação
    ├── Plano anterior → status "substituído"
    └── Paciente recebe notificação
```

### 3.2 Fluxo: Visualização do Plano pelo Paciente

```
PACIENTE
│
├── Abre app → Tab "Meu Plano"
│
├── HEADER
│   ├── Nome do plano ativo
│   └── Seletor de variação (chip/dropdown)
│
├── PROGRESSO DO DIA
│   ├── Barra linear: kcal consumidas / kcal meta
│   └── 3 indicadores circulares: P / C / G (consumido / meta)
│
├── LISTA DE REFEIÇÕES
│   ├── Card: [Café da manhã - 07:00]
│   │         [350 kcal | P:15g C:40g G:12g]
│   │         [○ Pendente]
│   ├── Card: [Lanche da manhã - 10:00]
│   │         [180 kcal | P:8g C:25g G:5g]
│   │         [✓ Registrado]
│   └── ...
│
└── ORIENTAÇÕES
    └── Card com texto do nutricionista
```

### 3.3 Fluxo: Registro de Consumo

```
PACIENTE
│
├── Toca no card de refeição → Detalhe
│
├── VÊ ITENS PRESCRITOS
│   ├── Pão integral — 2 fatias (50g) — 130 kcal
│   ├── Azeite — 1 col. sopa (13g) — 117 kcal
│   ├── Ovo mexido — 2 unid. (100g) — 155 kcal
│   └── Café s/ açúcar — 1 xícara (200ml) — 5 kcal
│
├── TOCA "REGISTRAR CONSUMO"
│
├── BOTTOM SHEET abre com opções:
│   │
│   ├── [Atalho] "Comi tudo" → confirma → feito
│   │
│   └── [Editar] Ajuste individual:
│       ├── ☑ Pão integral — 2 fatias ────── [slider/input]
│       ├── ☑ Azeite — 1 col. sopa ────────── [slider/input]
│       ├── ☐ Ovo mexido (desmarcado = não comeu)
│       ├── ☑ Café — 1 xícara ────────────── [slider/input]
│       ├── [Trocar] Ovo → Substituição: "Queijo branco 30g" (autorizada)
│       ├── [+ Alimento fora do plano] → busca na base
│       └── Totais recalculados em tempo real
│
├── CONFIRMAR
│   ├── Registro salvo com data/hora
│   ├── Totais do dia recalculados
│   └── Card na home muda para "✓ Registrado"
│
└── (Pode desfazer no mesmo dia)
```

### 3.4 Fluxo: Sugestão da IA

```
PACIENTE (dentro do detalhe da refeição)
│
├── Em um item, toca botão "✨ Sugestão IA"
│
├── LOADING (breve)
│
├── BOTTOM SHEET com sugestões:
│   │
│   ├── DISCLAIMER (topo):
│   │   "Sugestões geradas por IA. Consulte seu nutricionista."
│   │
│   ├── Sugestão 1:
│   │   [Aveia em flocos — 40g (3 col. sopa)]
│   │   [150 kcal | P:5g C:27g G:3g]
│   │   [Motivo: "Perfil energético similar, rica em fibras"]
│   │
│   ├── Sugestão 2:
│   │   [Tapioca — 2 unid. pequenas (60g)]
│   │   [140 kcal | P:0.5g C:34g G:0.2g]
│   │   [Motivo: "Equivalência calórica, sem glúten"]
│   │
│   └── Sugestão 3: ...
│
├── PACIENTE SELECIONA (ou cancela)
│
└── SE SELECIONOU:
    ├── Item registrado como consumo com tag "sugestão IA"
    ├── Visível no histórico do nutricionista
    └── Totais recalculados
```

### 3.5 Fluxo: Chat da IA

```
PACIENTE → Tab "Assistente"
│
├── PRIMEIRO ACESSO:
│   ├── Mensagem de boas-vindas:
│   │   "Olá! Sou seu assistente nutricional. Posso ajudar com
│   │    dúvidas sobre alimentação e seu plano. Lembre-se: minhas
│   │    respostas não substituem seu nutricionista."
│   └── Chips de sugestão:
│       [O que posso comer no lanche?]
│       [Alternativas ao arroz?]
│       [Dicas de hidratação]
│
├── CONVERSA:
│   ├── Paciente: "Posso trocar arroz branco por integral?"
│   │
│   └── IA [badge ✨ Assistente]:
│       "O arroz integral tem perfil similar ao branco, com mais
│        fibras. Para a porção de 150g do seu plano:
│        • Arroz integral: ~170 kcal | P:4g C:35g G:1.3g | Fibras: 2.7g
│        • Arroz branco: ~193 kcal | P:4g C:42g G:0.3g | Fibras: 0.6g
│        Uma opção seria experimentar. Consulte seu nutricionista
│        para ajustar o plano se preferir."
│
├── FORA DO DOMÍNIO:
│   ├── Paciente: "Que remédio tomar para dor de cabeça?"
│   └── IA: "Não posso ajudar com questões médicas. Para isso,
│            consulte seu médico. Posso ajudar com dúvidas sobre
│            alimentação e nutrição!"
│
└── HISTÓRICO: conversas salvas e acessíveis
```

### 3.6 Fluxo: Onboarding do Paciente

```
NUTRICIONISTA
├── Cadastra paciente (dados básicos)
└── Gera convite (link/código)

PACIENTE
├── Recebe link/código
├── Abre → Tela de cadastro
│   ├── E-mail
│   ├── Senha
│   ├── Aceita Termos de Uso ☑
│   ├── Aceita Política de Privacidade ☑
│   └── Consente uso de dados pela IA ☑ (opcional mas necessário para IA)
├── Cria conta → vinculado ao nutricionista
│
├── SE tem plano ativo:
│   └── → Tela "Meu Plano" (experiência completa)
│
└── SE não tem plano:
    └── → Estado vazio:
        "Bem-vindo! Seu nutricionista está preparando seu
         plano alimentar. Você será notificado quando estiver pronto."
```

### 3.7 Fluxo: Acompanhamento pelo Nutricionista

```
NUTRICIONISTA
│
├── Abre perfil do paciente
│
├── TAB "CONSUMO"
│   ├── Calendário com indicadores visuais por dia
│   │   (verde = registrou tudo, amarelo = parcial, cinza = nada)
│   ├── Detalhe do dia selecionado:
│   │   ├── Refeições registradas vs. pendentes
│   │   ├── Itens consumidos vs. prescritos
│   │   ├── Substituições usadas (com badge de origem)
│   │   └── Alimentos fora do plano
│   └── Totais nutricionais (meta vs. consumido)
│
├── TAB "EVOLUÇÃO"
│   ├── Gráfico de peso ao longo do tempo
│   ├── Tabela comparativa de avaliações
│   ├── Aderência (% refeições registradas por semana)
│   └── Alertas: "Paciente usou 8 sugestões da IA esta semana"
│
└── AÇÃO: registrar nova consulta / ajustar plano
```

---

## 4. Estrutura de Telas

### 4.1 Home do Paciente — "Meu Plano"

| Aspecto | Descrição |
|---------|-----------|
| **Objetivo** | Mostrar o plano alimentar ativo com clareza imediata e progresso do dia |
| **Layout** | Scroll vertical. Header fixo com nome do plano + seletor de variação. |
| **Conteúdo** | (1) Nome do plano + seletor de variação, (2) Progress bar kcal, (3) 3 mini-circles P/C/G, (4) Lista de cards de refeição, (5) Card de orientações |
| **Ação primária** | Tocar em card de refeição → detalhe + registro |
| **Ação secundária** | Trocar variação de dieta via seletor |
| **Prioridade visual** | 1° Progresso calórico, 2° Próxima refeição pendente, 3° Demais refeições |
| **Estado vazio (sem plano)** | Ilustração + "Seu nutricionista está preparando seu plano. Você será notificado quando estiver pronto." |
| **Estado de erro** | "Não foi possível carregar seu plano. Tente novamente." + botão retry |
| **Responsivo** | Em telas maiores, progress e macros podem ficar lado a lado ao invés de empilhados |

### 4.2 Detalhe da Refeição

| Aspecto | Descrição |
|---------|-----------|
| **Objetivo** | Mostrar exatamente o que comer, quanto, e permitir registro |
| **Layout** | Tela full com header, lista de itens e bottom bar fixa |
| **Conteúdo** | Header (nome + horário + totais), lista de itens (nome, medida caseira, gramatura, kcal, chips de ações), orientações da refeição |
| **Ação primária** | "Registrar Consumo" (botão fixo no bottom bar — maior destaque) |
| **Ação secundária** | Ver substituições por item, Sugestão IA por item |
| **Prioridade visual** | 1° Lista de alimentos com medidas caseiras, 2° Botão registrar, 3° Totais nutricionais |
| **Itens expandíveis** | Cada item expande para mostrar macros detalhados e substituições |
| **Comportamento** | Se já registrado, mostra o que foi consumido com opção de editar |

### 4.3 Registro de Consumo (Bottom Sheet)

| Aspecto | Descrição |
|---------|-----------|
| **Objetivo** | Registro rápido com opção de ajuste granular |
| **Layout** | Bottom sheet com peek (atalho "Comi tudo") e full expansion (ajuste individual) |
| **Conteúdo** | Atalho "Comi tudo", lista de itens com checkboxes + ajuste de porção, botão "Adicionar fora do plano", resumo recalculado em tempo real |
| **Ação primária** | "Confirmar" |
| **Ação secundária** | "Comi tudo" (atalho), "Cancelar" |
| **Prioridade visual** | 1° Atalho rápido, 2° Checkboxes de itens, 3° Totais recalculados |

### 4.4 Sugestão da IA (Bottom Sheet)

| Aspecto | Descrição |
|---------|-----------|
| **Objetivo** | Apresentar alternativas sugeridas pela IA com transparência |
| **Layout** | Bottom sheet com disclaimer fixo no topo, lista de cards de sugestão |
| **Conteúdo** | Disclaimer, 2-4 cards de sugestão (nome, porção, kcal, macros, motivo), botão cancelar |
| **Ação primária** | Selecionar uma sugestão |
| **Ação secundária** | Cancelar |
| **Identidade visual** | Fundo levemente tinted com cor da IA (azul/roxo). Badge ✨ em cada card. |
| **Estado de loading** | Skeleton cards com shimmer |
| **Estado de erro** | "Não foi possível gerar sugestões agora. Tente novamente." |

### 4.5 Progresso do Paciente

| Aspecto | Descrição |
|---------|-----------|
| **Objetivo** | Motivar o paciente mostrando evolução e aderência |
| **Layout** | Scroll vertical com seções colapsáveis |
| **Conteúdo** | Seletor de período, gráfico de aderência (% refeições por dia), gráfico de kcal diárias (meta vs consumido), cards de macro acumulados, seção de medidas (mini-gráfico de peso) |
| **Ação primária** | Navegar entre períodos |
| **Ação secundária** | Ver detalhe de um dia específico |
| **Estado vazio** | "Comece registrando suas refeições para acompanhar seu progresso!" + botão "Ir para Meu Plano" |

### 4.6 Chat Assistente IA

| Aspecto | Descrição |
|---------|-----------|
| **Objetivo** | Suporte nutricional informativo contextualizado |
| **Layout** | Chat padrão: lista de mensagens + input fixo no bottom |
| **Conteúdo** | Mensagens (bolhas), badge "✨ Assistente" nas respostas, disclaimer no primeiro acesso, chips de sugestão |
| **Identidade visual** | Header ou fundo levemente diferenciado com cor da IA. Bolhas da IA com cor da IA. |
| **Estado vazio** | Mensagem de boas-vindas + 3 chips de sugestão |
| **IA indisponível** | "O assistente está temporariamente indisponível. Tente em instantes." + estado do restante do app inalterado |

### 4.7 Editor de Plano (Nutricionista)

| Aspecto | Descrição |
|---------|-----------|
| **Objetivo** | Criação e edição eficiente de plano alimentar |
| **Layout** | Formulário em etapas (stepper) ou scroll longo com seções colapsáveis. Barra de totais fixa no bottom. |
| **Seção 1** | Dados gerais: nome, objetivo, datas |
| **Seção 2** | Metas nutricionais: kcal, P, C, G (inputs numéricos com unidade) |
| **Seção 3** | Variações de dieta: tabs ou lista com toggle de liberação |
| **Seção 4** | Refeições por variação: cards expansíveis com drag-to-reorder |
| **Seção 5** | Itens por refeição: busca inline de alimentos, seleção rápida, definição de porção |
| **Barra de totais** | Fixa no bottom: soma prescrita vs. meta, com indicador visual de conformidade |
| **Ações** | Salvar rascunho / Preview / Publicar |
| **Validações** | Inline — alertar se: refeição sem itens, total muito distante da meta, variação sem refeições |

### 4.8 Perfil do Paciente (Nutricionista)

| Aspecto | Descrição |
|---------|-----------|
| **Objetivo** | Visão completa do paciente para decisão clínica |
| **Layout** | Header com card resumo + sub-tabs |
| **Header** | Nome, objetivo, plano ativo, última consulta, aderência geral |
| **Sub-tabs** | Prontuário, Planos, Avaliações, Consumo, Evolução |
| **Cada sub-tab** | Lista cronológica, gráficos quando aplicável, ações contextuais |

### 4.9 Onboarding do Paciente

| Aspecto | Descrição |
|---------|-----------|
| **Objetivo** | Cadastro rápido e claro do paciente vindo de convite |
| **Layout** | Formulário simples em 1-2 telas |
| **Tela 1** | E-mail, senha, confirmação de senha |
| **Tela 2** | Aceitar termos (link para texto completo), aceitar privacidade, consentimento de IA (com explicação clara do que significa) |
| **Após cadastro** | Animação de boas-vindas breve → redirect para Home |

### 4.10 Lista de Pacientes (Nutricionista)

| Aspecto | Descrição |
|---------|-----------|
| **Objetivo** | Encontrar e acessar pacientes rapidamente |
| **Layout** | Search bar no topo + filtros + lista vertical de cards |
| **Card do paciente** | Nome, objetivo, plano ativo (ou "sem plano"), última consulta, badge de aderência |
| **Ação primária** | Tocar no card → perfil do paciente |
| **Ação secundária** | FAB "Novo paciente" |

---

## 5. Design System Conceitual

### 5.1 Hierarquia Visual

| Nível | Elementos | Tratamento |
|-------|-----------|------------|
| **Nível 1** (máximo destaque) | Progresso calórico, nome do plano, ação primária (Registrar Consumo) | Bold, tamanho grande, cor de destaque |
| **Nível 2** (destaque médio) | Cards de refeição, macros, nome da variação | Semi-bold, tamanho médio, cor primária |
| **Nível 3** (suporte) | Horários, gramaturas, detalhes nutricionais, substituições | Regular, tamanho padrão, cor secundária |
| **Nível 4** (terciário) | Disclaimers, metadados, links de configuração | Regular, tamanho menor, cor terciária |

### 5.2 Padrões de Cards

**Card de Refeição (lista na Home):**
```
┌──────────────────────────────────────────┐
│ 🍽  Café da manhã           07:00        │
│    350 kcal  P:15g  C:40g  G:12g        │
│                              ○ Pendente  │
└──────────────────────────────────────────┘
```
- Layout horizontal. Ícone + nome + horário na linha 1. Kcal + macros na linha 2. Status no canto inferior direito.
- Cor de borda: neutra (pendente) ou cor de destaque (registrado).
- Toque → navega para detalhe.

**Card de Item (detalhe da refeição):**
```
┌──────────────────────────────────────────┐
│ Pão integral                             │
│ 2 fatias (50g)              130 kcal     │
│ [Substituições ▾]  [✨ Sugestão IA]     │
└──────────────────────────────────────────┘
```
- Nome em destaque. Medida caseira (primário) + gramatura (secundário, cor mais suave). Kcal alinhado à direita.
- Chips de ação na linha inferior.
- Expandível para macros detalhados (P/C/G/Fibras).

**Card de Sugestão IA:**
```
┌──────────────────────────────────────────┐
│ ✨ Aveia em flocos                       │
│ 40g (3 col. sopa)           150 kcal    │
│ P:5g  C:27g  G:3g  Fibras:4g           │
│ "Perfil energético similar, rica em      │
│  fibras"                                 │
└──────────────────────────────────────────┘
```
- Fundo levemente tinted com cor da IA. Badge ✨. Justificativa em itálico.

**Card de Progresso:**
```
┌──────────────────────────────────────────┐
│ Calorias do dia                          │
│ ████████████░░░░░░░░  1350 / 1800 kcal  │
│                              75%         │
└──────────────────────────────────────────┘
```
- Barra linear com cor semântica (verde até 100%, amarelo 100-110%, vermelho >110%).

### 5.3 Cores

#### Cores Semânticas (fixas em todos os temas)

| Cor | Uso | Hex sugerido (tema claro) |
|-----|-----|--------------------------|
| Verde | Meta atingida, consumo registrado, dentro do alvo | #4CAF50 / #2E7D32 |
| Amarelo/Âmbar | Próximo do limite, atenção | #FFC107 / #F57F17 |
| Vermelho | Acima da meta, alerta, erro | #F44336 / #C62828 |
| Azul/Roxo (IA) | Todo elemento de IA — sugestões, chat, badges | #7C4DFF / #5E35B1 |
| Cinza | Pendente, inativo, desabilitado | #9E9E9E / #616161 |

#### Cor de Destaque (personalizável pelo tema)

| Paleta default | Hex | Nome |
|----------------|-----|------|
| Verde-esmeralda | #00897B | Esmeralda (default) |
| Azul-cerúleo | #1976D2 | Cerúleo |
| Teal | #00838F | Água-marinha |
| Laranja | #EF6C00 | Tangerina |

Aplicada a: botões primários, headers, ícones de destaque, gráficos, seletor ativo, links.

**Regra:** A cor de destaque do tema NUNCA deve ser a mesma cor usada para elementos de IA. Se o usuário escolher tema azul, o tom da IA deve ser roxo (ou vice-versa), mantendo distinção.

### 5.4 Tipografia

| Elemento | Peso | Tamanho | Cor |
|----------|------|---------|-----|
| Título de tela | Bold | 22-24sp | Primária |
| Nome de card/seção | Semi-bold | 16-18sp | Primária |
| Informação nutricional | Regular | 14-16sp | Secundária |
| Medida caseira (destaque) | Semi-bold | 14sp | Primária |
| Gramatura (complemento) | Regular | 14sp | Terciária |
| Valores numéricos (kcal, g) | Tabular/monospace | 14-16sp | Primária ou semântica |
| Disclaimer/metadado | Regular | 12sp | Terciária |
| Badge IA | Medium | 11sp | Cor IA sobre fundo IA |

**Fonte:** System default (Roboto Android, San Francisco iOS).
**Números nutricionais:** Usar tabular figures (monospaced) para alinhamento vertical em listas.

### 5.5 Espaçamento e Grid

- Base unit: **8dp**
- Margins laterais: **16dp**
- Padding interno de cards: **16dp**
- Gap entre cards: **12dp**
- Gap entre seções: **24dp**
- Bottom navigation height: **80dp** (com safe area)
- Bottom bar (ação primária): **64dp** + padding

### 5.6 Componentes Reutilizáveis

| Componente | Variantes |
|------------|-----------|
| MealCard | pendente, registrado, parcial |
| ItemCard | normal, expandido, com-substituicao, com-sugestao-ia |
| NutrientBar | linear (kcal), circular (macros) |
| MacroCircle | P (azul), C (amarelo), G (laranja) — ou cores do tema |
| AIBadge | small (inline), medium (card header) |
| AIDisclaimer | banner (topo de seção), inline (dentro de resposta) |
| StatusChip | pendente, registrado, parcial, fora-do-plano, sugestao-ia |
| VariationSelector | chip group ou dropdown |
| SearchBar | com filtros colapsáveis |
| EmptyState | ilustração + texto + ação |
| ErrorState | ícone + texto + botão retry |
| LoadingSkeleton | shimmer cards |

### 5.7 Acessibilidade

- Contraste WCAG AA (mínimo 4.5:1 para texto, 3:1 para elementos gráficos)
- Touch targets mínimos: **48dp x 48dp**
- Cores semânticas com indicador redundante (ícone ou texto) para daltônicos
- Content descriptions em todos os elementos interativos (TalkBack/VoiceOver)
- Textos escaláveis (respeitar sp e preferência do sistema)
- Focus indicators visíveis para navegação por teclado/switch
- Não depender exclusivamente de cor para comunicar informação

### 5.8 Distinção Visual entre Conceitos

| Conceito | Cor de borda/fundo | Ícone | Label padrão |
|----------|-------------------|-------|--------------|
| Plano alimentar | Cor de destaque (tema) | Clipboard | "Plano: [nome]" |
| Variação de dieta | Chip com cor de destaque | Swap | "Variação: [nome]" |
| Refeição | Card com borda neutra | Utensílios | Nome configurável |
| Item da refeição | Dentro do card de refeição | Nenhum (lista) | Nome do alimento |
| Substituição autorizada | Badge verde | Check + Swap | "Substituição" |
| Sugestão da IA | Badge azul/roxo + fundo tinted | ✨ Sparkle | "Sugestão IA" |
| Registro de consumo | Verde (feito), cinza (pendente) | ✓ Check | "Registrado" / "Pendente" |
| Fora do plano | Badge laranja/âmbar | ⚠ Alert | "Fora do plano" |

### 5.9 Estados de Interface

Cada componente interativo deve ter estados claramente definidos:

| Estado | Tratamento visual |
|--------|-------------------|
| Default | Aparência normal |
| Hover/Pressed | Ripple ou overlay sutil |
| Focused | Borda de foco visível |
| Disabled | Opacidade reduzida (38%), não-interativo |
| Loading | Skeleton shimmer ou spinner |
| Empty | Ilustração + texto orientador + CTA |
| Error | Ícone de erro + mensagem + ação de retry |
| Success | Feedback visual breve (check, cor verde, animação sutil) |

---

## 6. Diretrizes para IA dentro do Produto

### 6.1 Princípio Central
A IA é visualmente e funcionalmente separada do conteúdo clínico do nutricionista. O usuário nunca deve confundir "o que o nutricionista prescreveu" com "o que a IA sugeriu".

### 6.2 Onde a IA Aparece

| Local | Forma | Comportamento |
|-------|-------|---------------|
| Detalhe de item da refeição | Chip "✨ Sugestão IA" | Abre bottom sheet com alternativas |
| Tab "Assistente" | Chat completo | Conversação com histórico |
| (Futuro) Dashboard do nutricionista | Alertas/insights | Padrões detectados no consumo |

### 6.3 Onde a IA NÃO Aparece

- Criação/edição de plano
- Cálculos nutricionais (determinísticos)
- Registro de consumo (ação do paciente)
- Definição de metas
- Avaliações antropométricas
- Prontuário

### 6.4 Linguagem da IA

**Deve usar:**
- "Uma opção seria..."
- "Considerando seu plano..."
- "Você poderia experimentar..."
- "Consulte seu nutricionista para ajustar..."

**Nunca usar:**
- "Você deve comer..."
- "Substitua por..."
- "Não coma..."
- "Eu recomendo o tratamento..."
- Qualquer linguagem imperativa ou prescritiva

### 6.5 Identidade Visual da IA

- **Cor:** Azul/roxo (#7C4DFF ou similar) — sempre distinta da cor de destaque do tema
- **Ícone:** ✨ (sparkle) — usado consistentemente em todo o app
- **Badge:** "✨ IA" ou "✨ Assistente" — presente em todo output da IA
- **Fundo:** Cards e bolhas da IA com tint sutil da cor IA
- **Disclaimer:** Sempre presente, nunca ocultável pelo usuário

### 6.6 Cenário de Indisponibilidade

Quando a IA estiver indisponível:
- Botão "Sugestão IA" fica desabilitado com tooltip "Indisponível no momento"
- Tab "Assistente" mostra mensagem: "O assistente está temporariamente indisponível"
- Todo o restante do app funciona normalmente
- Nenhum dado é perdido

---

## 7. Recomendações para a Próxima Etapa (Geração de UI)

### 7.1 Telas a Gerar (por prioridade)

**Prioridade 1 — Core do paciente:**
1. Home "Meu Plano" (com refeições, progresso, variação)
2. Detalhe da Refeição (com itens, substituições, sugestão IA)
3. Registro de Consumo (bottom sheet)
4. Sugestão da IA (bottom sheet)

**Prioridade 2 — Complemento do paciente:**
5. Progresso / Evolução
6. Chat Assistente IA
7. Perfil / Configurações
8. Onboarding (cadastro via convite)
9. Estado vazio (sem plano)

**Prioridade 3 — Nutricionista:**
10. Lista de Pacientes
11. Perfil do Paciente (com sub-tabs)
12. Editor de Plano Alimentar
13. Dashboard

### 7.2 Diretrizes para a Ferramenta de UI

```
BRIEFING VISUAL — APP NUTRIVOX

PLATAFORMA: Mobile-first (Android + iOS via KMP)
DESIGN SYSTEM: Material Design 3 / Material You
ESTILO: Profissional, limpo, confiável. Não gamificado, não infantil.

COR DEFAULT: Verde-esmeralda (#00897B) como destaque
COR DA IA: Roxo (#7C4DFF) — nunca conflita com tema
SEMÂNTICAS: Verde=ok, Amarelo=atenção, Vermelho=alerta, Cinza=pendente

TIPOGRAFIA: System font (Roboto/SF). 4 níveis de hierarquia.
NÚMEROS: Tabular figures para dados nutricionais.

GRID: Base 8dp. Margins 16dp. Card padding 16dp. Gap 12dp.
CANTOS: Cards com border-radius 12-16dp.
ELEVAÇÃO: Sutil — surface tonals ao invés de sombras pesadas.

BOTTOM NAVIGATION: 4 tabs (Meu Plano, Progresso, Assistente, Perfil)
BOTTOM SHEETS: Para registro de consumo e sugestões IA
CARDS: Compactos, escaneáveis, com layout horizontal

DARK MODE: Previsto desde o início. Backgrounds escuros, surfaces 
com tonalidade, semânticas mantidas, cor IA mantida.

ACESSIBILIDADE: Contraste WCAG AA. Touch 48dp. Content descriptions.

ELEMENTOS DA IA: Sempre com fundo tinted roxo, badge ✨, disclaimer.
NUNCA misturar visualmente conteúdo do nutricionista com conteúdo da IA.

ESTADOS OBRIGATÓRIOS POR TELA:
- Default (com dados)
- Vazio (sem dados, com orientação)
- Loading (skeleton/shimmer)
- Erro (mensagem + retry)

MOOD BOARD: 
- Apple Health (clareza de dados de saúde)
- MyFitnessPal (organização de refeições)  
- Headspace (serenidade e confiança)
- Noom (progressão motivacional)
— Mas com identidade própria, mais profissional e clínica.
```

### 7.3 Componentes a Criar no Figma

| Componente | Variantes necessárias |
|------------|----------------------|
| MealCard | pendente, registrado, registrado-parcial |
| ItemCard | default, expandido, com-substituicao, com-sugestao-ia, ad-libitum |
| NutrientProgressBar | abaixo-meta, na-meta, acima-meta |
| MacroCircle | proteina, carbo, gordura (com cores distintas) |
| AIBadge | small, medium |
| AIDisclaimer | banner, inline |
| VariationSelector | 2-variações, 3+-variações |
| StatusChip | pendente, registrado, parcial, fora-do-plano, sugestao-ia |
| EmptyState | sem-plano, sem-registro, sem-historico, ia-indisponivel |
| ErrorState | generico, rede, ia |
| BottomSheet | peek, half, full |
| SearchBar | default, com-filtros, focused |

### 7.4 Tokens de Design

```
Tokens de Cor:
- color.theme.primary → cor de destaque (personalizável)
- color.theme.onPrimary → texto sobre primary
- color.ai.primary → #7C4DFF (fixo)
- color.ai.surface → #7C4DFF com 8% opacity
- color.ai.onSurface → texto sobre surface IA
- color.semantic.success → verde
- color.semantic.warning → amarelo
- color.semantic.error → vermelho
- color.semantic.neutral → cinza
- color.surface.* → backgrounds (claro/escuro)
- color.text.primary, secondary, tertiary

Tokens de Tipografia:
- type.title.large → 24sp Bold
- type.title.medium → 20sp Bold
- type.body.large → 16sp Regular
- type.body.medium → 14sp Regular
- type.body.small → 12sp Regular
- type.label.medium → 14sp Semi-bold
- type.number.tabular → 14-16sp Tabular

Tokens de Espaçamento:
- space.xs → 4dp
- space.sm → 8dp
- space.md → 12dp
- space.lg → 16dp
- space.xl → 24dp
- space.xxl → 32dp

Tokens de Forma:
- shape.card → 12dp
- shape.chip → 8dp
- shape.button → 12dp
- shape.sheet → 16dp (top corners)
- shape.circle → 50%
```

### 7.5 Checklist Final para UI

- [ ] Toda tela tem estado vazio, loading e erro
- [ ] Todo elemento da IA tem cor IA + badge + disclaimer
- [ ] Botão "Registrar Consumo" é a ação de maior destaque em toda refeição
- [ ] Medida caseira é informação primária, gramatura é secundária
- [ ] Progresso (meta vs consumido) visível na home sem scroll
- [ ] Seletor de variação acessível sem navegação adicional
- [ ] Dark mode funcional com semânticas preservadas
- [ ] Contraste WCAG AA em todas as combinações tema × modo
- [ ] Nenhum texto técnico sem contexto na primeira aparição
- [ ] Cards de refeição escaneáveis (informação essencial em 1 olhada)
