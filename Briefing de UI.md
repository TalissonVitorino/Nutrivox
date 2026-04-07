# Briefing de UI — Nutrivox
## Documento para geração de interface em ferramenta de IA/Design
### Versão 1.0

---

## 1. Sobre o Produto

**Nutrivox** é um app de nutrição clínica onde:
- O **nutricionista** cria planos alimentares personalizados para seus pacientes
- O **paciente** visualiza seu plano, registra o que comeu e acompanha seu progresso
- Uma **IA assistiva** sugere substituições alimentares e responde dúvidas sobre nutrição (nunca prescreve)

O app é **mobile-first** (Android + iOS) com estilo **profissional e clínico** — não é app de fitness gamificado.

---

## 2. Diretrizes Visuais

### Estilo Geral
- **Design limpo, moderno, com espaço em branco generoso**
- Material Design 3 / Material You
- Mood: profissional, confiável, sereno — não "divertido" ou "gamificado"
- Referências de inspiração: Apple Health (clareza), MyFitnessPal (organização de refeições), Headspace (serenidade), Noom (motivação)

### Cores

**Cor de destaque (default):** Verde-esmeralda `#00897B`
- Aplicada em: botões primários, headers, ícones ativos, gráficos, seletor de variação ativa

**Cor da IA (fixa):** Roxo `#7C4DFF`
- Aplicada em: TODO elemento gerado por IA — badges, cards de sugestão, bolhas de chat, botão "Sugestão IA"
- NUNCA misturar com conteúdo do nutricionista

**Cores semânticas (fixas):**
- Verde `#4CAF50`: sucesso, meta atingida, consumo registrado
- Amarelo `#FFC107`: atenção, próximo do limite
- Vermelho `#F44336`: alerta, acima da meta, erro
- Cinza `#9E9E9E`: pendente, inativo, desabilitado

**Paletas alternativas de tema:**
- Cerúleo `#1976D2`
- Água-marinha `#00838F`
- Tangerina `#EF6C00`

### Tipografia
- Fonte: Roboto (Android) / San Francisco (iOS) — system default
- Hierarquia: 4 níveis de peso visual
  - Título: Bold 22-24sp
  - Seção/card: Semi-bold 16-18sp
  - Corpo: Regular 14-16sp
  - Metadado/disclaimer: Regular 12sp
- Números nutricionais: **tabular figures** (monospaced) para alinhamento

### Grid e Espaçamento
- Base unit: 8dp
- Margins laterais: 16dp
- Padding interno de cards: 16dp
- Gap entre cards: 12dp
- Gap entre seções: 24dp
- Border-radius de cards: 12-16dp
- Elevação: sutil, prefira surface tonals

### Dark Mode
- Previsto desde o início
- Backgrounds escuros, surfaces com tonalidade
- Cores semânticas e cor da IA mantidas
- Contraste WCAG AA obrigatório

---

## 3. Navegação

### App do Paciente — Bottom Navigation (4 tabs)

| # | Ícone | Label | Descrição |
|---|-------|-------|-----------|
| 1 | Clipboard/utensílios | **Meu Plano** | Home — plano ativo, refeições do dia, progresso calórico |
| 2 | Gráfico/trending up | **Progresso** | Histórico de consumo, aderência, evolução de medidas |
| 3 | Sparkle/chat bubble | **Assistente** | Chat com IA nutricional |
| 4 | Pessoa/engrenagem | **Perfil** | Dados pessoais, notificações, tema, privacidade |

### App do Nutricionista — Navegação lateral ou top

| Seção | Descrição |
|-------|-----------|
| Dashboard | Visão geral, alertas, atividade recente |
| Pacientes | Lista com busca e filtros |
| [Paciente selecionado] | Sub-tabs: Prontuário, Planos, Avaliações, Consumo, Evolução |
| Configurações | Perfil profissional, IA, tema |

---

## 4. Telas a Gerar

### TELA 1 — Home do Paciente ("Meu Plano")

**Objetivo:** O paciente abre o app e vê imediatamente seu plano alimentar e progresso do dia.

**Layout (de cima para baixo):**

1. **Header fixo**
   - Nome do plano: "Plano Reeducação Alimentar" (bold, 20sp)
   - Seletor de variação: chips horizontais → "Padrão" (selecionado) | "Dia de treino"

2. **Progresso do dia**
   - Barra linear de calorias: `████████░░░░ 1350 / 1800 kcal` (cor semântica)
   - 3 mini-indicadores circulares lado a lado:
     - Proteínas: 85/120g
     - Carboidratos: 180/220g
     - Gorduras: 45/60g

3. **Lista de refeições** (cards verticais)
   - Cada card:
     ```
     🍽 Café da manhã                    07:00
        350 kcal  P:15g  C:40g  G:12g
                                  ○ Pendente
     ```
   - Card registrado: borda verde, ícone ✓, texto "Registrado"
   - Card pendente: borda neutra, ícone ○, texto "Pendente"

4. **Card de orientações** (colapsável)
   - Texto do nutricionista: "Priorize hidratação nesta semana. Mínimo 2L de água."

**Estados:**
- **Vazio (sem plano):** Ilustração suave + "Seu nutricionista está preparando seu plano. Você será notificado quando estiver pronto."
- **Loading:** Skeleton shimmer nos cards
- **Erro:** "Não foi possível carregar seu plano." + botão "Tentar novamente"

---

### TELA 2 — Detalhe da Refeição

**Objetivo:** Mostrar exatamente o que comer, quanto, e permitir registro.

**Layout:**

1. **Header**
   - Nome: "Café da manhã" (bold)
   - Horário: "07:00"
   - Totais: "350 kcal | P:15g C:40g G:12g"

2. **Lista de itens**
   - Cada item:
     ```
     Pão integral
     2 fatias (50g)                      130 kcal
     [Substituições ▾]  [✨ Sugestão IA]
     ```
   - "2 fatias" em semi-bold (medida caseira = destaque)
   - "(50g)" em cor secundária
   - Chips de ação: "Substituições" (verde sutil) + "✨ Sugestão IA" (roxo sutil)
   - Expandir item → mostra P/C/G/Fibras

3. **Orientação da refeição** (se houver)
   - "Consumir o café ao menos 30 min antes do treino."

4. **Bottom bar fixa**
   - Botão primário grande: **"Registrar Consumo"** (cor de destaque, full width)

---

### TELA 3 — Registro de Consumo (Bottom Sheet)

**Objetivo:** Registro rápido com opção de ajuste.

**Layout do bottom sheet:**

1. **Peek state (meia tela)**
   - Título: "Registrar consumo — Café da manhã"
   - Botão destaque: **"Comi tudo"** (ação rápida, 1 toque)
   - Link: "Ajustar individualmente ▾" (expande para full)

2. **Full state (tela cheia)**
   - Lista de itens com checkbox + controle de porção:
     ```
     ☑ Pão integral        2 fatias    [- ████████ +]   130 kcal
     ☑ Azeite              1 col. sopa [- ████████ +]   117 kcal
     ☐ Ovo mexido           2 unid.    (desmarcado)       0 kcal
     ☑ Café s/ açúcar      1 xícara   [- ████████ +]     5 kcal
     ```
   - Botão: "Trocar por substituição" (em cada item)
   - Botão: "+ Adicionar alimento fora do plano"
   - Resumo atualizado em tempo real: "Total desta refeição: 252 kcal"
   - Botão: **"Confirmar registro"**

---

### TELA 4 — Sugestão da IA (Bottom Sheet)

**Objetivo:** Mostrar alternativas sugeridas pela IA com transparência.

**Layout do bottom sheet (cor de fundo com tint roxo sutil):**

1. **Disclaimer fixo no topo**
   - "✨ Sugestões geradas por IA. Consulte seu nutricionista."
   - Fundo: roxo 8% opacity

2. **Contexto**
   - "Substituindo: Pão integral (2 fatias, 130 kcal)"

3. **Cards de sugestão** (2-4)
   ```
   ┌─ ✨ ──────────────────────────────────┐
   │ Aveia em flocos                        │
   │ 40g (3 col. sopa)           150 kcal  │
   │ P:5g  C:27g  G:3g  Fibras:4g         │
   │ "Perfil energético similar, rica em    │
   │  fibras"                               │
   │                          [Selecionar]  │
   └────────────────────────────────────────┘
   ```

4. **Botão "Cancelar"** (texto, sem destaque)

**Estado loading:** Skeleton shimmer com tint roxo
**Estado erro:** "Não foi possível gerar sugestões. Tente novamente."

---

### TELA 5 — Progresso do Paciente

**Objetivo:** Motivar o paciente mostrando evolução e aderência.

**Layout:**

1. **Seletor de período** (tabs: "Semana" | "Mês" | "3 meses")

2. **Gráfico de aderência**
   - Barras diárias: % de refeições registradas
   - Cores: verde (>80%), amarelo (50-80%), vermelho (<50%)
   - Label: "Aderência média: 78%"

3. **Gráfico de calorias**
   - Linha: consumo diário
   - Linha pontilhada: meta
   - Área entre as linhas com cor sutil

4. **Cards de macros acumulados** (semana/mês)
   - Média diária de P, C, G vs. meta

5. **Seção "Medidas"** (quando disponível)
   - Mini-gráfico de peso
   - Últimas 2 medidas com comparativo

**Estado vazio:** "Comece registrando suas refeições para acompanhar seu progresso!" + botão "Ir para Meu Plano"

---

### TELA 6 — Chat Assistente IA

**Objetivo:** Suporte nutricional informativo em formato de conversa.

**Layout:**

1. **Header**
   - "✨ Assistente Nutricional"
   - Fundo com tint roxo sutil

2. **Disclaimer (primeiro acesso ou banner fixo)**
   - "Sou um assistente de IA. Minhas respostas são informativas e não substituem seu nutricionista."

3. **Área de mensagens**
   - Bolhas do paciente: alinhadas à direita, cor neutra
   - Bolhas da IA: alinhadas à esquerda, fundo com tint roxo, badge "✨ Assistente"
   
   Exemplo de conversa:
   ```
   [Paciente]: Posso trocar arroz branco por integral?
   
   [✨ Assistente]: O arroz integral tem perfil similar ao
   branco, com mais fibras. Para 150g do seu plano:
   • Integral: ~170 kcal | P:4g C:35g G:1.3g | Fibras: 2.7g
   • Branco: ~193 kcal | P:4g C:42g G:0.3g | Fibras: 0.6g
   Uma opção seria experimentar. Consulte seu nutricionista 
   para ajustar o plano.
   ```

4. **Chips de sugestão** (quando não há conversa ativa)
   - "O que posso comer no lanche?"
   - "Alternativas ao arroz?"
   - "Dicas de hidratação"

5. **Input fixo no bottom**
   - Campo de texto + botão enviar

**IA indisponível:** "O assistente está temporariamente indisponível. Tente em instantes." O campo de input fica desabilitado.

---

### TELA 7 — Lista de Pacientes (Nutricionista)

**Objetivo:** Encontrar e acessar pacientes.

**Layout:**

1. **Search bar** (topo, com ícone de filtro)
2. **Filtros** (colapsáveis): objetivo, status, plano ativo
3. **Lista de cards**
   ```
   ┌──────────────────────────────────────────┐
   │ 👤 Maria Silva                           │
   │    Emagrecimento | Plano: Reeducação     │
   │    Última consulta: 28/03/2026           │
   │    Aderência: ██████░░ 78%               │
   └──────────────────────────────────────────┘
   ```
4. **FAB** "+" para novo paciente

---

### TELA 8 — Editor de Plano Alimentar (Nutricionista)

**Objetivo:** Criar plano completo de forma eficiente.

**Layout em seções colapsáveis (ou stepper):**

1. **Seção: Dados gerais**
   - Campos: nome, objetivo, data início, data término (opcional)

2. **Seção: Metas nutricionais**
   - Campos numéricos: kcal, proteínas (g), carboidratos (g), gorduras (g), fibras (g, opcional)

3. **Seção: Variações de dieta**
   - Lista: "Padrão" (obrigatória) + botão "+ Variação"
   - Cada variação: nome, toggle "Liberar para paciente", toggle "Padrão"

4. **Seção: Refeições** (por variação selecionada via tab)
   - Cards de refeição com nome, horário, drag handle para reordenar
   - Dentro de cada refeição: lista de itens
   - Cada item: busca de alimento (inline), quantidade, medida caseira, observação
   - Botão "+ Substituição autorizada" por item
   - Botão "+ Item" por refeição
   - Botão "+ Refeição"

5. **Barra de totais** (fixa no bottom)
   - "Prescrito: 1780 kcal | Meta: 1800 kcal"
   - "P: 118g/120g | C: 215g/220g | G: 58g/60g"
   - Indicador visual: verde se próximo da meta, amarelo se divergente

6. **Ações**
   - "Salvar rascunho" (secundário)
   - "Preview" (terciário)
   - "Publicar" (primário)

---

### TELA 9 — Perfil do Paciente (Nutricionista)

**Objetivo:** Visão completa para decisão clínica.

**Layout:**

1. **Card de resumo** (header)
   - Nome, idade, objetivo, plano ativo, aderência, última consulta

2. **Sub-tabs** (scrollable)
   - Prontuário | Planos | Avaliações | Consumo | Evolução

3. **Tab "Consumo"** (exemplo)
   - Calendário com indicadores por dia (verde/amarelo/cinza)
   - Detalhe do dia selecionado: refeições, itens, substituições (com badges de origem)
   - Totais nutricionais do dia

---

### TELA 10 — Onboarding do Paciente

**Objetivo:** Cadastro rápido vindo de convite.

**Layout (2 passos):**

1. **Passo 1: Credenciais**
   - Mensagem: "Seu nutricionista [Nome] convidou você!"
   - Campos: e-mail, senha, confirmar senha
   - Botão: "Continuar"

2. **Passo 2: Termos e Consentimento**
   - ☐ "Li e aceito os Termos de Uso" (link)
   - ☐ "Li e aceito a Política de Privacidade" (link)
   - ☐ "Autorizo o uso dos meus dados pelo assistente de IA para sugestões nutricionais personalizadas" (com texto explicativo)
   - Botão: "Criar conta"

3. **Sucesso**
   - Animação breve de boas-vindas
   - → Redireciona para Home

---

## 5. Regras de Consistência

1. **Todo card de refeição** tem a mesma estrutura: ícone + nome + horário | kcal + macros + status
2. **Todo elemento da IA** tem cor roxo `#7C4DFF` + badge ✨ + disclaimer
3. **Todo botão primário** usa a cor de destaque do tema
4. **Todo estado vazio** tem ilustração + texto orientador + ação sugerida
5. **Todo formulário** tem validação inline e feedback em tempo real
6. **Medida caseira** é sempre informação primária, gramatura é secundária
7. **Progresso** (meta vs consumido) é sempre representado com barra/círculo + números
8. **Status de consumo** usa: ○ Pendente (cinza) | ✓ Registrado (verde) | ◐ Parcial (amarelo)

---

## 6. Alertas de UX

- **NÃO poluir a Home com dados demais.** O paciente precisa de scanability. Macros resumidos sim, micronutrientes na home não.
- **NÃO esconder "Registrar Consumo".** Deve ser a ação mais óbvia e acessível de toda a jornada do paciente.
- **NÃO misturar visualmente nutricionista e IA.** Cores e badges distintos sempre. O paciente nunca deve se perguntar "quem disse isso?"
- **NÃO usar termos técnicos sem contexto.** "Macros" pode precisar de tooltip. "IMC" precisa de explicação na primeira vez.
- **CUIDADO com o editor de plano.** É a tela mais complexa. Precisa de progressive disclosure, etapas claras, feedback visual de totais em tempo real. Não tentar mostrar tudo de uma vez.
- **CUIDADO com a troca de variação.** O paciente pode não entender quando usar "Dia de treino" vs "Dia de descanso". Considerar tooltip ou texto auxiliar configurável pelo nutricionista.

---

## 7. Componentes para Figma

### Componentes obrigatórios

| Componente | Variantes |
|------------|-----------|
| **MealCard** | pendente, registrado, parcial |
| **ItemCard** | default, expandido, com-substituicao, com-sugestao-ia, ad-libitum |
| **NutrientProgressBar** | abaixo-meta, na-meta, acima-meta |
| **MacroCircle** | proteina, carboidrato, gordura |
| **AIBadge** | small (inline), medium (card header) |
| **AIDisclaimer** | banner, inline |
| **VariationSelector** | 2-opcoes, 3+-opcoes |
| **StatusChip** | pendente, registrado, parcial, fora-do-plano, sugestao-ia |
| **EmptyState** | sem-plano, sem-registro, sem-historico, ia-indisponivel |
| **ErrorState** | generico, rede, ia |
| **BottomSheet** | peek, half, full |
| **SearchBar** | default, com-filtros, focused |
| **ConsentToggle** | aceito, nao-aceito, com-explicacao |

### Tokens de cor

```
// Tema (personalizável)
theme.primary = #00897B (default esmeralda)
theme.onPrimary = #FFFFFF
theme.primaryContainer = #00897B @ 12%

// IA (fixo)
ai.primary = #7C4DFF
ai.surface = #7C4DFF @ 8%
ai.onSurface = #311B92

// Semânticas (fixas)
semantic.success = #4CAF50
semantic.warning = #FFC107
semantic.error = #F44336
semantic.neutral = #9E9E9E

// Superfícies
surface.background = #FAFAFA (light) / #121212 (dark)
surface.card = #FFFFFF (light) / #1E1E1E (dark)
surface.elevated = #FFFFFF (light) / #2C2C2C (dark)

// Texto
text.primary = #212121 (light) / #E0E0E0 (dark)
text.secondary = #616161 (light) / #9E9E9E (dark)
text.tertiary = #9E9E9E (light) / #757575 (dark)
```

### Tokens de tipografia

```
type.display = 28sp Bold
type.title.large = 24sp Bold
type.title.medium = 20sp Semi-bold
type.body.large = 16sp Regular
type.body.medium = 14sp Regular
type.body.small = 12sp Regular
type.label = 14sp Semi-bold
type.number = 14-16sp Tabular Medium
```

### Tokens de espaçamento

```
space.xs = 4dp
space.sm = 8dp
space.md = 12dp
space.lg = 16dp
space.xl = 24dp
space.xxl = 32dp
```

### Tokens de forma

```
shape.card = 12dp
shape.chip = 8dp
shape.button = 12dp
shape.sheet = 16dp (top corners)
shape.input = 8dp
shape.circle = 50%
```

---

## 8. Checklist de Entrega

- [ ] Todas as 10 telas geradas com dados realistas (não lorem ipsum)
- [ ] Cada tela com estado default (com dados)
- [ ] Cada tela com estado vazio (quando aplicável)
- [ ] Cada tela com estado loading (skeleton)
- [ ] Cada tela com estado erro
- [ ] Dark mode para todas as telas
- [ ] Componentes da IA sempre com cor roxo + badge + disclaimer
- [ ] Botão "Registrar Consumo" como maior destaque visual nas telas de refeição
- [ ] Medida caseira como info primária, gramatura como secundária
- [ ] Progresso (meta vs consumido) visível na Home sem scroll
- [ ] Seletor de variação acessível na Home sem navegação extra
- [ ] Contraste WCAG AA em todas as combinações
- [ ] Dados nutricionais com números alinhados (tabular figures)
- [ ] Nenhum elemento da IA sem disclaimer ou badge
