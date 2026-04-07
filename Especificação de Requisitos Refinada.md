# Especificação de Requisitos de Software — Refinada
## Aplicativo Nutrivox — Nutrição Clínica com Plano Alimentar, Evolução e Assistente de IA
### Versão 2.0

---

## 1. Visão Geral do Documento

### 1.1 Objetivo

Este documento especifica os requisitos funcionais e não funcionais do aplicativo Nutrivox, um sistema de nutrição clínica voltado ao atendimento e acompanhamento de pacientes por nutricionistas.

Esta versão incorpora todas as correções, refinamentos e novos requisitos identificados na revisão crítica da versão 1.0, incluindo:
- definição formal da hierarquia de entidades do domínio
- normalização de nomenclatura
- resolução de ambiguidades e conflitos
- adição de requisitos funcionais ausentes
- refinamento de escopo da IA
- correções de domínio clínico-nutricional

---

### 1.2 Escopo do Produto

O produto é um sistema digital de nutrição composto por:

- um aplicativo do paciente (mobile)
- um ambiente do nutricionista (mobile/web)
- um backend centralizado
- um módulo de inteligência artificial restrito ao domínio alimentar
- uma base de alimentos e composição nutricional
- um módulo de registro e acompanhamento de evolução clínica e alimentar

O sistema permite que o nutricionista cadastre e mantenha planos alimentares personalizados, acompanhe medidas e evolução do paciente, registre histórico clínico e utilize uma IA como assistente para sugestões alimentares, sem retirar a autoridade técnica do profissional.

---

### 1.3 Premissas do Produto

1. O nutricionista é o responsável final pelo plano alimentar.
2. A IA não cria, ativa ou publica dietas sem aprovação do nutricionista.
3. A IA atua como assistente de apoio, oferecendo sugestões contextualizadas.
4. O paciente pode visualizar plano, registrar consumo e consultar orientações, mas não pode alterar a prescrição clínica.
5. O sistema deve priorizar clareza, usabilidade, segurança de dados e rastreabilidade.
6. O domínio da IA é estritamente nutrição e alimentação, sem caráter diagnóstico ou prescritivo.
7. O produto será desenvolvido com Kotlin Multiplatform e Jetpack Compose.
8. Dados de saúde do paciente devem ser tratados em conformidade com a LGPD.

---

### 1.4 Glossário do Domínio

| Termo | Definição |
|-------|-----------|
| **Plano Alimentar** | Container principal de prescrição, criado pelo nutricionista para um paciente. Tem objetivo, datas, status e metas nutricionais. Contém uma ou mais variações de dieta. Apenas um plano pode estar ativo por paciente. |
| **Variação de Dieta** | Versão alternativa da distribuição de refeições dentro de um plano. Ex.: "Dia de treino", "Dia de descanso", "Low carb", "Opção 1". O paciente alterna apenas entre variações autorizadas. |
| **Refeição** | Momento alimentar dentro de uma variação (café da manhã, almoço, etc.). Tem horário sugerido e ordem de exibição. Contém itens. |
| **Item da Refeição** | Alimento prescrito dentro de uma refeição, com quantidade, medida caseira, dados nutricionais e possíveis substituições autorizadas. |
| **Substituição Autorizada** | Alternativa alimentar pré-aprovada pelo nutricionista para um item específico. O paciente pode usá-la livremente. |
| **Sugestão da IA** | Alternativa alimentar sugerida algoritmicamente, sem pré-aprovação do nutricionista. Sempre sinalizada como sugestão. Rastreada separadamente. |
| **Registro de Consumo** | Declaração do paciente sobre o que efetivamente consumiu numa refeição, com data, hora e detalhamento por item. |
| **Meta Nutricional** | Valores-alvo diários (calorias, macronutrientes) definidos pelo nutricionista para o plano. Servem como referência para acompanhamento de progresso. |
| **Medida Caseira** | Unidade de uso cotidiano (colher de sopa, fatia, concha, etc.) vinculada a um alimento com fator de conversão para gramas. |

---

### 1.5 Hierarquia Formal de Entidades

```
Paciente
 └── Plano Alimentar (1 ativo por vez)
      ├── Metas Nutricionais Diárias
      ├── Orientações Gerais
      └── Variação de Dieta (1..N, uma marcada como padrão)
           └── Refeição (1..N, ordenadas)
                └── Item da Refeição (1..N)
                     ├── Alimento (da base nutricional)
                     ├── Quantidade + Medida Caseira
                     ├── Dados Nutricionais (calculados)
                     └── Substituições Autorizadas (0..N)
```

---

### 1.6 Perfis de Usuário

#### 1.6.1 Nutricionista
Profissional responsável por:
- cadastrar pacientes e gerar convites de acesso
- registrar avaliações e evolução clínica
- criar, editar, publicar e desativar planos alimentares
- definir metas nutricionais
- configurar variações de dieta, refeições, itens e substituições
- acompanhar registros de consumo do paciente
- revisar sugestões da IA utilizadas pelo paciente
- configurar diretrizes e permissões da IA por paciente

#### 1.6.2 Paciente
Usuário responsável por:
- visualizar plano alimentar ativo
- alternar entre variações de dieta autorizadas
- registrar consumo de refeições (total, parcial, com substituição, fora do plano)
- acompanhar progresso diário (metas vs. consumido)
- consultar assistente de IA nutricional
- visualizar evolução de medidas e orientações

#### 1.6.3 Administrador do Sistema (V1+)
Responsável por:
- gestão de usuários (ativar, desativar, resetar)
- manutenção da base de alimentos
- parametrização institucional
- monitoramento técnico e auditoria

> **Nota:** O perfil Administrador não é escopo do MVP. No MVP, funções administrativas básicas ficam sob o nutricionista.

---

## 2. Contexto de Negócio

### 2.1 Problema de Negócio

Soluções atuais de nutrição frequentemente apresentam:
- baixa clareza na visualização do plano alimentar ativo
- pouca diferenciação entre variações de dieta
- detalhamento nutricional insuficiente por refeição
- pouca transparência sobre calorias e composição dos alimentos
- excesso de funcionalidades de baixo valor clínico
- assistentes de IA genéricos, descontrolados e sem escopo definido
- baixa consistência de UX e design system
- dificuldade de operacionalização pelo nutricionista

---

### 2.2 Objetivo de Negócio

Construir um sistema que ofereça:
- prescrição alimentar estruturada pelo nutricionista
- acompanhamento clínico e evolutivo do paciente
- cálculo detalhado de calorias e macronutrientes
- substituições alimentares equivalentes e rastreáveis
- melhor experiência do paciente no entendimento do plano
- melhor produtividade do nutricionista
- assistente de IA útil, restrito, transparente e seguro

---

### 2.3 Proposta de Valor

O sistema se diferencia por:
- forte organização hierárquica do plano alimentar (plano > variação > refeição > item)
- melhor usabilidade e hierarquia visual
- cálculo nutricional em nível de alimento, refeição e dia com metas de referência
- suporte a múltiplas variações de dieta com troca intuitiva
- IA como copiloto nutricional, nunca como substituta do profissional
- rastreabilidade clínica completa (incluindo origem de substituições)
- registro de consumo com baixo atrito

---

## 3. Escopo Funcional — Módulos do Sistema

1. Autenticação e gestão de acesso
2. Onboarding e vínculo nutricionista-paciente
3. Cadastro e gestão de pacientes
4. Prontuário nutricional
5. Avaliação antropométrica e evolução
6. Plano alimentar (com variações de dieta)
7. Metas nutricionais
8. Refeições e itens
9. Base de alimentos e medidas caseiras
10. Registro de consumo alimentar
11. Cálculo nutricional hierárquico
12. Substituições alimentares (autorizadas e sugeridas)
13. Assistente de IA nutricional (sugestões + chat)
14. Notificações e lembretes
15. Relatórios e exportações
16. Auditoria e histórico
17. Tema, aparência e personalização visual
18. Consentimento e privacidade (LGPD)

---

## 4. Requisitos Funcionais

---

### 4.1 Autenticação e Controle de Acesso

#### RF-001 — Cadastro de nutricionista
O sistema deve permitir o cadastro de nutricionistas com dados mínimos de identificação e credenciais seguras.

**Entradas mínimas:**
- nome completo
- e-mail (único)
- senha (política mínima de segurança)
- registro profissional (CRN), se aplicável
- telefone (opcional)

**Regras:**
- e-mail deve ser único no sistema
- senha deve ter mínimo de 8 caracteres, com letras e números
- cadastro deve ser confirmado por fluxo seguro (e-mail de verificação)

---

#### RF-002 — Login
O sistema deve permitir autenticação de usuários cadastrados via e-mail e senha.

**Regras:**
- sessão persistente com expiração configurável
- revogação de sessão em logout
- bloqueio temporário após N tentativas inválidas

---

#### RF-003 — Recuperação de senha
O sistema deve permitir recuperação de senha via e-mail, com link de redefinição temporário e seguro.

---

#### RF-004 — Perfis de acesso
O sistema deve controlar acesso por perfil.

**Perfis no MVP:**
- nutricionista
- paciente

**Regras:**
- cada perfil tem permissões específicas
- o paciente não acessa funções de criação/edição de plano
- o nutricionista não acessa dados de pacientes de outros nutricionistas (salvo autorização)

---

### 4.2 Onboarding e Vínculo

#### RF-005 — Convite ao paciente
O nutricionista deve poder gerar um convite (link ou código único) para que o paciente crie sua conta no aplicativo.

**Regras:**
- o convite tem validade configurável
- ao aceitar, o paciente se vincula automaticamente ao nutricionista que convidou
- o paciente pode estar vinculado a mais de um nutricionista

---

#### RF-006 — Cadastro do paciente (onboarding)
Ao aceitar o convite, o paciente completa seu cadastro.

**Dados preenchidos pelo paciente:**
- e-mail
- senha
- aceitação de termos de uso e política de privacidade
- consentimento para uso de dados pela IA (obrigatório para usar funcionalidades de IA)

**Dados pré-preenchidos pelo nutricionista (no momento do convite):**
- nome completo
- sexo
- data de nascimento
- telefone
- objetivo principal
- restrições alimentares
- observações clínicas iniciais

---

#### RF-007 — Consentimento e termos
O sistema deve apresentar termos de uso e política de privacidade no cadastro, exigindo consentimento explícito.

**Regras:**
- consentimento deve ser registrado com data e versão do documento aceito
- para módulo de IA: consentimento adicional informando natureza assistiva e não-prescritiva
- consentimento revogável a qualquer momento nas configurações
- revogação do consentimento de IA desabilita funcionalidades de IA para o paciente

---

### 4.3 Gestão de Pacientes

#### RF-008 — Lista de pacientes
O nutricionista deve poder visualizar seus pacientes cadastrados com busca e filtros.

**Filtros mínimos:**
- nome
- objetivo
- status do acompanhamento (ativo, inativo)
- presença de plano ativo
- última consulta

---

#### RF-009 — Histórico do paciente
O sistema deve manter histórico longitudinal do paciente.

**Deve armazenar:**
- avaliações anteriores
- planos alimentares anteriores (com status)
- observações por consulta
- evolução de medidas
- variações de dieta ativas e inativas
- registros de consumo
- sugestões da IA utilizadas

---

### 4.4 Prontuário Nutricional

#### RF-010 — Ficha clínica do paciente
O sistema deve disponibilizar uma ficha clínica estruturada.

**Campos:**
- queixa principal
- histórico familiar
- patologias
- intolerâncias alimentares
- alergias alimentares
- medicações em uso
- suplementação
- hábitos intestinais
- padrão de sono
- rotina de atividade física
- ingestão hídrica
- preferências alimentares
- aversões alimentares

---

#### RF-011 — Evolução clínica por consulta
O nutricionista deve poder registrar evolução a cada consulta.

**Dados por registro:**
- data
- observações gerais
- adesão ao plano
- intercorrências
- sintomas relatados
- ajustes definidos
- condutas e recomendações

---

### 4.5 Avaliação Antropométrica e Evolução

#### RF-012 — Registro de medidas antropométricas
O sistema deve permitir registro de medidas.

**Medidas mínimas:**
- peso (kg)
- altura (cm)
- IMC (calculado automaticamente)
- circunferência abdominal
- circunferência de cintura
- circunferência de quadril
- percentual de gordura (quando disponível)
- massa magra (quando disponível)
- água corporal (quando disponível)

---

#### RF-013 — Histórico temporal de medidas
O sistema deve manter histórico de medidas por data.

**Saídas:**
- lista cronológica
- gráficos de evolução
- comparativo entre datas
- destaque de variação (positiva ou negativa, conforme objetivo)

---

#### RF-014 — Comparação entre avaliações
O sistema deve permitir comparar:
- avaliação atual vs. anterior
- avaliação atual vs. primeira
- qualquer avaliação vs. outra (seleção livre)

---

### 4.6 Plano Alimentar

#### RF-015 — Criação manual do plano alimentar
O sistema deve permitir que o nutricionista crie manualmente um plano alimentar para um paciente.

**Observação crítica:**
A criação do plano é responsabilidade exclusiva do nutricionista. A IA não substitui esse fluxo.

---

#### RF-016 — Estrutura do plano alimentar
Um plano alimentar deve possuir:

- nome do plano
- data de início
- data de término (opcional)
- objetivo clínico
- status (rascunho | ativo | inativo | arquivado | substituído)
- metas nutricionais diárias (RF-020)
- orientações gerais (texto livre)
- uma ou mais variações de dieta

**Regras:**
- deve haver ao menos uma variação para publicar o plano
- uma variação deve ser marcada como "padrão"
- o nutricionista define quais variações o paciente pode alternar

---

#### RF-017 — Status do plano alimentar

**Estados:**
- **Rascunho**: em construção, não visível para o paciente
- **Ativo**: publicado e visível para o paciente (apenas 1 por paciente)
- **Inativo**: pausado, não visível para o paciente
- **Arquivado**: histórico, apenas para consulta
- **Substituído**: foi automaticamente movido para este status quando um novo plano foi ativado

**Regras:**
- ao ativar um novo plano, o anterior é automaticamente movido para "substituído"
- apenas um plano pode estar ativo por paciente por vez
- planos em rascunho não são visíveis para o paciente

---

#### RF-018 — Variações de dieta
Cada plano pode conter múltiplas variações de dieta.

**Cada variação contém:**
- nome (ex.: "Dia de treino", "Dia de descanso", "Opção low carb")
- lista de refeições própria
- flag: liberada para o paciente (sim/não)
- flag: padrão (exibida por default ao paciente)

**Regras:**
- cada variação tem seu próprio conjunto de refeições e itens
- o nutricionista pode liberar ou bloquear variações individualmente
- ao menos uma variação deve ser marcada como padrão

---

#### RF-019 — Refeições
Cada variação contém múltiplas refeições.

**Dados por refeição:**
- nome (configurável pelo nutricionista — ex.: "Café da manhã", "Lanche da manhã")
- horário sugerido
- ordem de exibição
- lista de itens
- orientações específicas da refeição (texto livre, opcional)
- totais nutricionais (calculados — RF-028)

**Exemplos padrão:**
- café da manhã, lanche da manhã, almoço, lanche da tarde, jantar, ceia

---

#### RF-020 — Metas nutricionais diárias
Ao criar um plano, o nutricionista deve definir metas diárias.

**Metas obrigatórias:**
- calorias (kcal)
- proteínas (g)
- carboidratos (g)
- gorduras (g)

**Metas opcionais:**
- fibras (g)
- proteínas por kg de peso (g/kg)

**Regras:**
- o sistema deve exibir progresso do paciente em relação a essas metas
- ao visualizar o plano, o paciente vê metas vs. consumido
- o sistema deve alertar quando o total prescrito do plano diverge significativamente das metas (para o nutricionista, na criação)

---

#### RF-021 — Itens da refeição
Cada item prescrito contém:

- alimento (referência à base nutricional)
- quantidade (gramas)
- medida caseira (ex.: "2 fatias", "1 colher de sopa") com conversão automática
- observação descritiva (opcional — ex.: "com azeite", "sem açúcar")
- calorias (calculadas)
- macronutrientes (calculados)
- micronutrientes (quando disponíveis na base)
- substituições autorizadas (0..N, cadastradas pelo nutricionista — RF-032)

**Regras:**
- o paciente vê medida caseira como informação primária e gramatura como secundária
- valores nutricionais são calculados automaticamente com base na gramatura e na base de dados

---

#### RF-022 — Itens livres (ad libitum)
O sistema deve suportar itens sem gramatura fixa para casos como "salada à vontade", "temperos livres".

**Regras:**
- itens livres não entram no cálculo nutricional automático
- devem ser visualmente identificados como "livre" ou "à vontade"
- o nutricionista pode adicionar observação descritiva

---

#### RF-023 — Visualização do plano pelo paciente
Na tela inicial do paciente, o sistema deve exibir com clareza:

1. nome do plano ativo
2. seletor de variação de dieta (com variação atual destacada)
3. resumo nutricional do dia (meta vs. consumido)
4. lista de refeições com card compacto
5. orientações do nutricionista

**Cada card de refeição deve exibir:**
- nome da refeição
- horário sugerido
- calorias totais
- macros principais (P/C/G)
- status de consumo (pendente / registrado)

---

#### RF-024 — Detalhe da refeição
Ao abrir uma refeição, o paciente visualiza:

- nome da refeição + horário
- calorias totais + macros totais
- lista de itens detalhados:
  - nome do alimento
  - medida caseira (primário)
  - gramatura (secundário)
  - calorias
  - macros (expandível)
  - indicador de substituições disponíveis
  - botão "Sugestão da IA"
- orientações específicas da refeição
- botão primário: "Registrar Consumo"

---

#### RF-025 — Alternância de variação pelo paciente
O paciente pode alternar entre variações de dieta autorizadas dentro do plano ativo.

**Regras:**
- o paciente não pode criar, editar ou excluir variações
- apenas variações marcadas como "liberada" são exibidas no seletor
- a troca atualiza toda a tela: refeições, cálculos, resumo nutricional
- a variação padrão é exibida automaticamente ao abrir o app
- o sistema registra qual variação o paciente usou em cada dia (para rastreabilidade)

---

#### RF-026 — Duplicação de plano
O nutricionista deve poder:
- duplicar um plano alimentar existente como base para um novo
- duplicar uma variação de dieta dentro do mesmo plano
- duplicar uma refeição de uma variação para outra

**Regras:**
- a duplicação cria cópia editável sem vínculo com o original
- o plano duplicado inicia com status "rascunho"

---

#### RF-027 — Preview do plano
O nutricionista deve poder visualizar o plano como o paciente verá, antes de publicar.

**Inclui:**
- tela de refeições completa
- totais nutricionais
- variações de dieta
- comparação entre totais do plano e metas definidas

---

### 4.7 Base de Alimentos

#### RF-028 — Base de composição nutricional
O sistema deve utilizar uma base estruturada de alimentos com composição nutricional.

**A base deve suportar:**
- alimentos in natura
- alimentos industrializados
- unidades caseiras (com fator de conversão)
- gramas
- valores por 100g e por porção
- macronutrientes (proteínas, carboidratos, gorduras, fibras)
- micronutrientes (quando disponíveis)

---

#### RF-029 — Busca de alimentos
O sistema deve oferecer busca na base de alimentos.

**Funcionalidades:**
- busca por nome (com suporte a variações: "batata doce", "batata-doce")
- filtragem por categoria (frutas, carnes, laticínios, cereais, etc.)
- exibição de informação nutricional resumida nos resultados
- seleção rápida para adicionar ao plano (nutricionista) ou ao registro de consumo (paciente)

---

#### RF-030 — Medidas caseiras
O sistema deve manter tabela de medidas caseiras vinculadas a alimentos.

**Cada medida caseira contém:**
- nome (ex.: "colher de sopa", "fatia", "concha média")
- fator de conversão para gramas (ex.: "1 colher de sopa de azeite" = 13g)

**Regras:**
- cada alimento pode ter múltiplas medidas caseiras
- ao prescrever ou registrar, o sistema converte automaticamente para gramas para cálculo
- o paciente vê a medida caseira como informação primária

---

#### RF-031 — Indicador de completude nutricional
Quando a base de um alimento tiver dados incompletos de micronutrientes, o sistema deve:
- indicar visualmente quais nutrientes têm dados indisponíveis
- não exibir totais de micronutrientes quando a cobertura for inferior a um limiar definido
- não gerar falsa sensação de adequação nutricional

---

### 4.8 Cálculo Nutricional

#### RF-032 — Cálculo nutricional hierárquico
O sistema deve calcular valores nutricionais em três níveis:

1. **Por item**: com base na gramatura e nos dados da base
2. **Por refeição**: somatório dos itens
3. **Por dia**: somatório das refeições

**Nutrientes calculados:**
- calorias (kcal)
- proteínas (g)
- carboidratos (g)
- gorduras (g)
- fibras (g), quando disponível
- micronutrientes, quando disponíveis na base

**Regras:**
- cálculos devem ser determinísticos e reprodutíveis
- itens livres (ad libitum) não entram no cálculo automático
- o sistema deve exibir totais tanto do prescrito quanto do consumido

---

#### RF-033 — Comparação prescrito vs. consumido vs. meta
O sistema deve permitir comparar, para cada dia:

- meta nutricional definida pelo nutricionista
- total prescrito no plano (para a variação ativa)
- total efetivamente consumido (com base nos registros)
- diferença (kcal, macros)

---

### 4.9 Registro de Consumo Alimentar

#### RF-034 — Registro de consumo
O paciente deve poder registrar o consumo de cada refeição.

**Modos de registro:**

1. **Consumo total**: marca a refeição inteira como consumida (1 toque — modo rápido)
2. **Consumo parcial por item**: marca individualmente quais itens consumiu e ajusta quantidade se necessário
3. **Consumo com substituição autorizada**: seleciona uma substituição pré-aprovada no lugar do item original
4. **Consumo com alimento fora do plano**: registra um alimento que não está no plano (buscando na base). Fica sinalizado como "fora do plano"

**Regras:**
- registros são vinculados a data e hora
- registro retroativo é permitido (até N dias configuráveis)
- cada registro recalcula automaticamente os totais nutricionais do dia
- o paciente pode editar ou desfazer um registro no mesmo dia

---

#### RF-035 — Recálculo de consumo
Ao registrar consumo (total, parcial ou com substituição), o sistema deve recalcular:

- calorias ingeridas da refeição
- macronutrientes ingeridos da refeição
- totais do dia
- progresso em relação às metas

**Regra:** ao registrar consumo parcial (ex.: metade da porção), o sistema recalcula proporcionalmente.

---

#### RF-036 — Histórico de consumo
O paciente e o nutricionista devem poder visualizar o histórico de consumo por dia e por período.

**Informações exibidas:**
- refeições registradas vs. pendentes
- itens consumidos com quantidades
- substituições utilizadas (com indicação de origem: autorizada ou sugestão IA)
- alimentos fora do plano
- totais nutricionais do dia

---

### 4.10 Substituições Alimentares

#### RF-037 — Substituições autorizadas
O nutricionista deve poder cadastrar substituições para cada item da refeição.

**Cada substituição contém:**
- alimento substituto (da base)
- quantidade/medida caseira equivalente
- equivalência calórica aproximada
- equivalência de macros aproximada
- observação (opcional)

**Regras:**
- substituições autorizadas podem ser usadas livremente pelo paciente
- ficam visíveis no detalhe do item da refeição

---

#### RF-038 — Exibição de substituições
Ao visualizar um item da refeição, o paciente deve ver as substituições autorizadas disponíveis, com nome, porção e dados nutricionais resumidos.

---

#### RF-039 — Sugestão da IA para substituições
O sistema deve exibir, no detalhe de cada item, um botão **"Sugestão da IA"**.

**Ao acionar:**
1. O sistema consulta a IA com contexto: alimento original, refeição, objetivo do paciente, restrições alimentares, metas do plano.
2. A IA retorna 2 a 4 alternativas com justificativa.
3. Cada sugestão exibe: nome, porção, calorias, macros e motivo da sugestão.

**Se o paciente escolher uma sugestão da IA:**
- o consumo é registrado com a tag "sugestão IA" (distinto de "substituição autorizada")
- a sugestão fica visível no histórico do nutricionista

**Regras:**
- a IA deve respeitar restrições alimentares do paciente (alergias, intolerâncias)
- a sugestão nunca altera o plano prescrito — apenas afeta o registro de consumo
- o nutricionista pode visualizar todas as sugestões da IA usadas pelo paciente
- o nutricionista pode desabilitar sugestões de IA para um paciente específico
- cada sugestão exibe disclaimer: "Sugestão gerada por IA. Consulte seu nutricionista."

---

#### RF-040 — Justificativa da sugestão
Cada sugestão da IA deve apresentar justificativa simples e rastreável, como:
- "equivalência calórica aproximada"
- "perfil de macros similar"
- "mesmo grupo alimentar"
- "opção com mais fibras dentro da faixa calórica"

---

#### RF-041 — Rastreabilidade de substituições no registro
Ao registrar consumo com substituição, o sistema deve:
- registrar a **origem**: autorizada pelo nutricionista ou sugerida pela IA
- gravar no histórico com data, hora e contexto
- recalcular o consumo com os valores do alimento substituto
- garantir visibilidade ao nutricionista, com distinção visual entre tipos

---

### 4.11 Assistente de IA Nutricional

#### RF-042 — Chat de IA nutricional
O sistema deve possuir um módulo de chat com IA voltado exclusivamente para temas alimentares e nutricionais, de caráter informativo e não diagnóstico.

---

#### RF-043 — Escopo restrito da IA

**A IA pode responder sobre:**
- propriedades nutricionais de alimentos
- informações sobre grupos alimentares
- dicas gerais de hidratação
- orientações gerais de rotina alimentar
- esclarecimentos sobre o plano alimentar ativo do paciente
- informações sobre preparo e conservação de alimentos

**A IA NÃO deve:**
- associar alimentos ou infusões a condições clínicas específicas (ex.: "chá para ansiedade")
- sugerir suplementação
- contradizer ou reinterpretar o plano do nutricionista
- responder sobre diagnóstico, medicação ou tratamento médico
- usar linguagem imperativa/prescritiva

**Todas as respostas devem conter linguagem prudente e disclaimer.**

---

#### RF-044 — Contextualização pelo perfil
Quando o paciente tiver dado consentimento, o chat pode considerar:
- variação de dieta ativa
- restrições alimentares
- objetivo do paciente
- metas nutricionais

---

#### RF-045 — Limitação de domínio
Perguntas fora do domínio nutricional devem ser recusadas com redirecionamento educado.

**Exemplo:** "Não posso ajudar com essa questão. Posso ajudar com dúvidas sobre alimentação e seu plano nutricional!"

---

#### RF-046 — Histórico de chat
O sistema deve manter histórico de conversas, conforme política de retenção definida.

**Regras:**
- o paciente pode visualizar seu histórico
- a política de retenção deve ser transparente para o paciente
- se o nutricionista tiver acesso ao chat, o paciente deve ser informado

---

#### RF-047 — Sinalização de assistente virtual
Toda resposta da IA deve:
- ser visualmente identificada como gerada por IA (badge, ícone)
- conter disclaimer de que não substitui acompanhamento profissional
- usar linguagem não-imperativa ("uma opção seria...", "considerando seu plano...")

---

### 4.12 Notificações

#### RF-048 — Notificações ao paciente
O sistema deve suportar notificações push.

**Tipos:**
- lembrete de refeição (baseado no horário sugerido)
- lembrete de registro de consumo (se passou X tempo e não registrou)
- novo plano publicado pelo nutricionista
- mensagem/recado do nutricionista
- atualização de variação de dieta

**Regras:**
- o paciente pode configurar quais notificações receber
- horários são baseados nos horários das refeições do plano ativo

---

#### RF-049 — Recados do nutricionista
O nutricionista deve poder enviar recados/orientações pontuais ao paciente.

**Exemplos:** "Nesta semana, aumente a hidratação." / "Próxima consulta dia 15/04."

---

### 4.13 Relatórios

#### RF-050 — Relatório de plano alimentar
O sistema deve permitir exportar o plano alimentar completo do paciente (com variações, refeições, itens, substituições e metas).

---

#### RF-051 — Relatório de evolução
O sistema deve gerar relatório com:
- histórico de medidas
- comparativos antropométricos
- adesão ao plano (% de refeições registradas)
- observações clínicas por consulta

---

#### RF-052 — Relatório nutricional
O sistema deve gerar relatório com:
- metas nutricionais definidas
- calorias prescritas vs. consumidas (por período)
- macronutrientes prescritos vs. consumidos
- substituições registradas (com tipo: autorizada / sugestão IA)
- alimentos fora do plano registrados

---

### 4.14 UX, Tema e Aparência

#### RF-053 — Configuração de aparência
O sistema deve oferecer área de configuração de aparência.

---

#### RF-054 — Temas
O sistema deve suportar:
- tema claro
- tema escuro
- automático (conforme SO)

---

#### RF-055 — Paletas de cor
Seleção entre paletas de cor pré-definidas.

**Regras:**
- nomes compreensíveis
- visualização prévia
- aplicação consistente em todo o app
- manutenção de contraste e acessibilidade WCAG AA

---

#### RF-056 — Aplicação global de tema
Mudança de tema deve refletir em: botões primários, ícones de destaque, estados selecionados, gráficos, cabeçalhos, controles interativos.

---

### 4.15 Auditoria e Histórico

#### RF-057 — Histórico de alterações
O sistema deve registrar alterações relevantes em:
- plano alimentar (criação, edição, ativação, desativação)
- variações de dieta
- refeições e itens
- substituições
- medidas antropométricas
- observações clínicas

---

#### RF-058 — Rastreabilidade
Cada alteração deve possuir:
- autor
- data e hora
- tipo de alteração
- estado anterior (quando pertinente)
- novo estado

---

## 5. Regras de Negócio

### RN-001
Apenas um plano alimentar pode estar ativo por paciente por vez. Ao ativar um novo, o anterior é automaticamente movido para "substituído".

### RN-002
O paciente pode visualizar somente planos vinculados ao seu cadastro e liberados pelo nutricionista.

### RN-003
A IA não pode publicar, ativar ou substituir planos automaticamente.

### RN-004
Sugestões da IA são assistivas e devem ser sempre sinalizadas como tal. Não são prescrição.

### RN-005
A alternância de variação pelo paciente deve ocorrer apenas entre variações explicitamente autorizadas pelo nutricionista, dentro do plano ativo.

### RN-006
Ao registrar consumo parcial de um item, o sistema deve recalcular proporcionalmente os valores nutricionais.

### RN-007
Se a base nutricional de um alimento estiver incompleta, o sistema deve sinalizar quais nutrientes têm valores indisponíveis.

### RN-008
A nomenclatura de refeições é configurável pelo nutricionista, mas a ordenação e o comportamento interno permanecem estruturados.

### RN-009
Refeições sem itens válidos não podem ser publicadas como parte de um plano ativo. O sistema deve alertar o nutricionista.

### RN-010
A interface deve indicar de forma inequívoca qual variação de dieta está ativa.

### RN-011
O registro de consumo com substituição deve manter a informação de origem (autorizada vs. sugestão IA), visível para o nutricionista.

### RN-012
O sistema só utiliza dados clínicos do paciente como contexto para a IA se houver consentimento explícito. Consentimento revogável a qualquer momento.

### RN-013
Planos em status "rascunho" não são visíveis para o paciente.

### RN-014
Funcionalidades de baixo valor clínico (humor da refeição, gamificação) não entram no escopo obrigatório.

---

## 6. Requisitos Não Funcionais

### 6.1 Arquitetura e Tecnologia

#### RNF-001 — Stack tecnológica
- Kotlin Multiplatform
- Jetpack Compose para UI
- Backend escalável (preferencialmente Kotlin/JVM)
- APIs desacopladas para IA

#### RNF-002 — Separação de responsabilidades
Camadas: apresentação, domínio, dados, integração externa.

#### RNF-003 — IA desacoplada
Integração com IA mediada por backend próprio (controle, segurança, observabilidade, troca futura de provedor).

---

### 6.2 Usabilidade

#### RNF-004 — Clareza de navegação
Navegação intuitiva e previsível com baixa carga cognitiva.

#### RNF-005 — Hierarquia visual
Elementos mais relevantes claramente identificáveis: plano ativo, próxima refeição, progresso do dia, ação principal.

#### RNF-006 — Consistência de interface
Componentes visuais semelhantes mantêm o mesmo comportamento em todo o sistema.

#### RNF-007 — Acessibilidade
- Contraste WCAG AA
- Touch targets mínimos 48dp
- Ícones com texto quando necessário
- Leitura compreensível
- Estados visuais claros
- Suporte a leitores de tela
- Respeito à preferência de tamanho de fonte do sistema

#### RNF-008 — Redução de ambiguidade
A interface deve usar nomenclatura consistente conforme glossário (seção 1.4). Termos técnicos devem ter explicação contextual quando exibidos pela primeira vez.

---

### 6.3 Desempenho

#### RNF-009 — Fluidez de navegação
Navegação entre telas deve ser percebida como fluida.

#### RNF-010 — Carregamento de dados
Listagens e detalhes devem carregar em tempo adequado mesmo em dispositivos intermediários.

#### RNF-011 — Degradação controlada
Na indisponibilidade temporária da IA, o restante do sistema deve continuar funcional. O usuário deve ver mensagem clara de indisponibilidade.

---

### 6.4 Segurança

#### RNF-012 — Proteção de dados sensíveis
Dados pessoais e clínicos com criptografia em trânsito (TLS) e controles de proteção em repouso.

#### RNF-013 — Autorização
Toda operação sensível deve validar perfil e permissão.

#### RNF-014 — Auditoria
Ações críticas registradas para rastreabilidade.

#### RNF-015 — Sessão segura
Sessões com expiração adequada e suporte a revogação.

---

### 6.5 Confiabilidade e Integridade

#### RNF-016 — Consistência nutricional
Cálculos nutricionais determinísticos e reprodutíveis.

#### RNF-017 — Integridade transacional
Alterações de plano e registro de consumo não devem resultar em estados parciais.

#### RNF-018 — Versionamento do plano
O sistema deve preservar histórico de versões/alterações do plano.

---

### 6.6 Escalabilidade e Manutenibilidade

#### RNF-019 — Crescimento progressivo
Arquitetura deve suportar aumento de: usuários, pacientes, alimentos, registros, mensagens de IA.

#### RNF-020 — Evolução modular
Novos módulos sem reescrita estrutural do núcleo.

#### RNF-021 — Código modular
Módulos com responsabilidades claras.

#### RNF-022 — Testabilidade
Regras de cálculo, permissões, troca de variação e registro de consumo devem ser testáveis automaticamente.

#### RNF-023 — Observabilidade
Backend com logs, métricas e tracing.

---

### 6.7 Privacidade (LGPD)

#### RNF-024 — Consentimento
Coleta e tratamento de dados pessoais e de saúde exigem consentimento explícito, registrado e revogável.

#### RNF-025 — Portabilidade
O paciente deve poder solicitar exportação dos seus dados.

#### RNF-026 — Exclusão
O paciente deve poder solicitar exclusão da conta e de seus dados, respeitado prazo legal de retenção quando aplicável.

#### RNF-027 — Transparência
Política de privacidade acessível, clara e em linguagem simples.

---

## 7. Requisitos de Dados

### RD-001 — Entidades mínimas

- Usuário
- Nutricionista
- Paciente
- VínculoNutricionistaPaciente
- PlanoAlimentar
- VariaçãoDeDieta
- Refeição
- ItemDaRefeição
- Alimento
- MedidaCaseira
- SubstituiçãoAutorizada
- RegistroDeConsumo
- ItemDoConsumo
- MetaNutricional
- Avaliação
- MedidaAntropométrica
- EvoluçãoClínica
- MensagemIA
- Consentimento
- Notificação
- Tema/Aparência
- Auditoria

### RD-002 — Histórico temporal
Entidades clínicas e nutricionais devem suportar histórico temporal.

### RD-003 — Normalização lógica
Modelagem sem redundâncias indevidas e sem inconsistências de atualização.

---

## 8. Casos de Uso Principais

### UC-001 — Nutricionista cria plano alimentar
**Ator:** Nutricionista
**Fluxo:**
1. Abre perfil do paciente
2. Clica "Novo Plano" (ou "Duplicar plano anterior")
3. Define nome, objetivo, datas
4. Define metas nutricionais (kcal, P, C, G)
5. Cria variação padrão
6. Adiciona refeições (nome, horário, ordem)
7. Adiciona itens por refeição (busca de alimentos, quantidade, medida caseira)
8. Cadastra substituições autorizadas por item
9. (Opcional) Cria variações adicionais
10. Revisa totais vs. metas
11. Usa "Preview como paciente"
12. Publica plano

### UC-002 — Paciente visualiza plano alimentar
**Ator:** Paciente
**Fluxo:**
1. Abre app → tela "Meu Plano"
2. Vê plano ativo + variação atual + progresso do dia
3. Toca em card de refeição
4. Visualiza itens com medidas caseiras, calorias, macros
5. Vê substituições disponíveis por item

### UC-003 — Paciente alterna variação de dieta
**Ator:** Paciente
**Fluxo:**
1. Na home, toca no seletor de variação
2. Escolhe uma variação autorizada
3. Sistema atualiza refeições, cálculos e totais
4. Sistema registra a variação do dia

### UC-004 — Paciente registra consumo
**Ator:** Paciente
**Fluxo:**
1. Abre detalhe da refeição
2. Toca "Registrar Consumo"
3. Escolhe modo: tudo, parcial, com substituição ou fora do plano
4. Confirma
5. Sistema recalcula totais do dia
6. Progresso na home atualiza

### UC-005 — Paciente solicita sugestão da IA
**Ator:** Paciente
**Fluxo:**
1. Abre detalhe da refeição
2. Em um item, toca "Sugestão da IA"
3. Sistema consulta IA com contexto
4. Exibe 2-4 alternativas com justificativa
5. Paciente seleciona uma (ou cancela)
6. Se selecionou: registra consumo com tag "sugestão IA"

### UC-006 — Paciente conversa com assistente
**Ator:** Paciente
**Fluxo:**
1. Abre tab "Assistente"
2. Faz pergunta sobre alimentação
3. IA responde dentro do domínio, com disclaimer
4. Histórico salvo

### UC-007 — Nutricionista acompanha evolução
**Ator:** Nutricionista
**Fluxo:**
1. Abre perfil do paciente
2. Consulta medidas históricas
3. Compara avaliações
4. Revisa aderência (% refeições registradas)
5. Revisa sugestões da IA usadas
6. Registra nova consulta com observações
7. Ajusta plano se necessário

### UC-008 — Paciente faz onboarding
**Ator:** Paciente
**Fluxo:**
1. Recebe convite (link/código) do nutricionista
2. Abre link → tela de cadastro
3. Preenche e-mail, senha
4. Aceita termos e política de privacidade
5. Aceita consentimento de IA (ou recusa)
6. Cria conta → vinculado ao nutricionista
7. Se há plano ativo: vê tela "Meu Plano"
8. Se não há plano: vê estado vazio orientador

---

## 9. Requisitos de UX

### UX-001
A tela principal do paciente deve comunicar imediatamente o plano ativo, a variação selecionada e o progresso do dia.

### UX-002
O seletor de variação de dieta deve ser simples, visível e semanticamente claro.

### UX-003
A lista de refeições deve ser escaneável em poucos segundos.

### UX-004
O detalhe da refeição deve enfatizar: o que comer, quanto (medida caseira), valor energético e composição.

### UX-005
A ação principal de cada refeição deve ser "Registrar Consumo". Deve ser a ação de maior destaque visual.

### UX-006
Ações de menor valor clínico não devem ocupar posição de destaque no fluxo principal.

### UX-007
O sistema de cores deve ser consistente, semanticamente útil e acessível. Elementos da IA devem ter cor distinta do tema.

### UX-008
A interface deve manter distinção visual clara entre: plano, variação, refeição, substituição autorizada, sugestão IA e registro de consumo.

### UX-009
Estados vazios (sem plano, sem registro, sem histórico) devem exibir mensagem orientadora com sugestão de ação.

### UX-010
Informação nutricional deve usar progressive disclosure: resumo visível, detalhes sob demanda.

---

## 10. Itens Fora do Escopo Inicial

- Marketplace de profissionais
- Integração com balanças/wearables
- Rede social interna
- Gamificação avançada
- Teleconsulta
- Humor da refeição
- Diário alimentar fotográfico como módulo central
- Prescrição automática por IA
- Sugestões de chás/fitoterápicos com finalidade terapêutica
- Cálculo de TMB/GET automatizado (pode entrar em V1)
- Perfil Administrador completo (simplificado no nutricionista para MVP)
- Modo offline (desejável para roadmap)

---

## 11. Restrições e Diretrizes

### RST-001
A IA opera com escopo nutricional restrito, controlado por política de prompts e filtros. Não associa alimentos a condições clínicas.

### RST-002
A plataforma deve permitir futura troca de provedor de IA sem refatoração profunda.

### RST-003
A primeira versão prioriza corretude do domínio, usabilidade e cálculo nutricional antes de features periféricas.

### RST-004
A autoridade clínica final sobre o plano alimentar é sempre do nutricionista.

### RST-005
O sistema deve estar em conformidade com LGPD para tratamento de dados de saúde.

---

## 12. Critérios de Aceitação de Alto Nível

### CA-001
Plano alimentar: nutricionista cria plano completo com variações, refeições, itens, metas e substituições, e publica.

### CA-002
Visualização: paciente identifica imediatamente o plano ativo, a variação selecionada, suas refeições e o progresso do dia.

### CA-003
Consumo: paciente registra consumo (total, parcial, com substituição) e o sistema recalcula totais.

### CA-004
Metas: paciente visualiza progresso diário (consumido vs. meta) de forma clara.

### CA-005
Substituição IA: sistema oferece sugestões coerentes, explicáveis e não automáticas, com rastreabilidade de origem.

### CA-006
Chat IA: IA responde questões alimentares dentro do domínio e recusa temas externos.

### CA-007
Onboarding: paciente recebe convite, cria conta, aceita termos e acessa o app.

### CA-008
Aparência: troca de tema é intuitiva, consistente e aplicada globalmente.

---

## 13. Priorização de Implementação

### Fase 1 — MVP (Núcleo Clínico)
- Autenticação (RF-001 a RF-004)
- Onboarding e vínculo (RF-005 a RF-007)
- Gestão de pacientes (RF-008)
- Ficha clínica simplificada (RF-010)
- Plano alimentar completo (RF-015 a RF-023)
- Metas nutricionais (RF-020)
- Base de alimentos e busca (RF-028 a RF-030)
- Cálculo nutricional (RF-032, RF-033)
- Visualização do paciente (RF-023 a RF-025)
- Registro de consumo (RF-034 a RF-036)

### Fase 2 — V1 (Evolução e IA)
- Histórico do paciente (RF-009)
- Evolução clínica (RF-011)
- Avaliação antropométrica (RF-012 a RF-014)
- Substituições autorizadas (RF-037, RF-038)
- Sugestão da IA (RF-039 a RF-041)
- Duplicação de plano (RF-026)
- Notificações (RF-048, RF-049)
- Relatórios (RF-050 a RF-052)
- Auditoria (RF-057, RF-058)

### Fase 3 — Expansão
- Chat de IA (RF-042 a RF-047)
- Temas e personalização (RF-053 a RF-056)
- Indicador de completude nutricional (RF-031)
- Comparação avançada entre avaliações (RF-014)
- Micronutrientes detalhados

---

## 14. Recomendações de Testes

- Testes unitários de cálculo nutricional hierárquico
- Testes de regras de permissão por perfil
- Testes de alternância de variação de dieta
- Testes de registro de consumo (todos os 4 modos)
- Testes de persistência de histórico e auditoria
- Testes de rastreabilidade de origem de substituição
- Testes de interface para fluxo principal do paciente
- Testes de robustez da integração com IA (escopo, degradação)
- Testes de consentimento e revogação
- Testes de regressão em design system
