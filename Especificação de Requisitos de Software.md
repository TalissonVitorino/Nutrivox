# Especificação de Requisitos de Software
## Aplicativo de Nutrição com Plano Alimentar, Evolução do Paciente e Assistente de IA
### Versão 1.0

---

## 1. Visão Geral do Documento

### 1.1 Objetivo

Este documento especifica, de forma funcional e não funcional, os requisitos de um 
aplicativo de nutrição voltado ao atendimento clínico e ao acompanhamento de pacientes
por nutricionistas.

A especificação foi elaborada para ser suficientemente completa, consistente e acionável para
permitir que um time de engenharia de software realize:
- planejamento técnico
- modelagem da solução
- definição de arquitetura
- refinamento do backlog
- implementação incremental
- testes de aceitação
- validação com stakeholders

O documento foi estruturado com base em boas práticas de engenharia de software, com foco em:
- clareza
- rastreabilidade
- correção
- consistência
- separação entre requisitos funcionais e não funcionais
- definição de regras de negócio
- redução de ambiguidades

---

### 1.2 Escopo do Produto

O produto é um sistema digital de nutrição composto por:

- um aplicativo do paciente
- um ambiente do nutricionista
- um backend centralizado
- um módulo de inteligência artificial restrito ao domínio alimentar
- uma base de alimentos e composição nutricional
- um módulo de registro e acompanhamento de evolução clínica e alimentar

O sistema deve permitir que o nutricionista cadastre e mantenha planos alimentares personalizados,
acompanhe medidas e evolução do paciente, registre histórico clínico e utilize uma IA como assistente
para sugestões alimentares, sem retirar a autoridade técnica do profissional.

---

### 1.3 Premissas do Produto

1. O nutricionista é o responsável final pelo plano alimentar.
2. A IA não cria automaticamente dietas finais sem supervisão humana.
3. A IA atua como assistente de apoio, oferecendo sugestões contextualizadas com embasamento no plano alimentar.
4. O paciente pode visualizar, registrar consumo e consultar orientações, mas não deve poder alterar
5. diretamente a prescrição clínica.
5. O sistema deve priorizar clareza, usabilidade, segurança de dados e rastreabilidade das mudanças.
6. O domínio principal da IA deve ser estritamente nutrição e alimentação.
7. O produto deve ser concebido com foco inicial em Kotlin Multiplatform, scalável.

---

### 1.4 Perfis de Usuário

#### 1.4.1 Nutricionista
Profissional responsável por:
- cadastrar pacientes
- registrar avaliações
- criar e revisar planos alimentares
- acompanhar evolução
- consultar sugestões da IA
- configurar substituições e orientações
- revisar registros alimentares

#### 1.4.2 Paciente
Usuário responsável por:
- visualizar plano alimentar ativo
- alternar entre dietas autorizadas
- registrar refeições consumidas
- acompanhar calorias e distribuição nutricional
- consultar assistente de IA nutricional
- visualizar evolução e orientações

#### 1.4.3 Administrador do Sistema
Responsável por:
- gestão administrativa
- controle de usuários
- manutenção de tabelas de alimentos
- parametrização institucional
- monitoramento técnico e auditoria

---

## 2. Contexto de Negócio

### 2.1 Problema de Negócio

Soluções atuais de nutrição frequentemente apresentam pelo menos um dos seguintes problemas:
- baixa clareza na visualização do plano alimentar ativo
- pouca diferenciação entre dietas alternativas e dieta principal
- detalhamento nutricional insuficiente por refeição
- pouca transparência sobre calorias e composição dos alimentos
- excesso de funcionalidades de baixo valor clínico
- assistentes de IA genéricos e pouco controlados
- baixa consistência de UX e ‘design’ system
- dificuldade de operacionalização pelo nutricionista e usuários finais

---

### 2.2 Objetivo de Negócio

Construir um sistema que ofereça:
- prescrição alimentar estruturada pelo nutricionista
- acompanhamento clínico e evolutivo do paciente
- cálculo detalhado de calorias, macronutrientes e micronutrientes
- substituições alimentares equivalentes
- melhor experiência do paciente no entendimento do plano
- melhor produtividade do nutricionista
- assistente de IA útil, restrito e seguro

---

### 2.3 Proposta de Valor

O sistema deverá se diferenciar por:
- forte organização do plano alimentar
- melhor usabilidade e hierarquia visual
- cálculo nutricional em nível de alimento, refeição e dia
- suporte a múltiplas dietas por paciente com troca intuitiva
- IA como copiloto nutricional, não como substituta do profissional
- rastreabilidade clínica
- arquitetura preparada para evolução

---

## 3. Escopo Funcional

### 3.1 Módulos do Sistema

O sistema deverá conter, no mínimo, os seguintes módulos:

1. Autenticação e gestão de acesso
2. Cadastro de pacientes
3. Prontuário nutricional
4. Avaliação antropométrica e evolução
5. Plano alimentar
6. Gestão de dietas por paciente
7. Detalhamento de refeições
8. Registro de consumo alimentar
9. Cálculo de calorias, macronutrientes e micronutrientes
10. Substituições alimentares
11. Assistente de IA nutricional
12. Tema, aparência e personalização visual
13. Relatórios e exportações
14. Auditoria e histórico
15. Integrações futuras

---

## 4. Requisitos Funcionais

---

## 4.1 Autenticação e Controle de Acesso

### RF-001 — Cadastro de usuário profissional
O sistema deve permitir o cadastro de nutricionistas com dados mínimos de identificação e credenciais seguras.

**Entradas mínimas:**
- nome
- e-mail
- senha
- registro profissional, se aplicável
- telefone, opcional

**Regras:**
- e-mail deve ser único
- senha deve obedecer à política mínima de segurança
- o cadastro deve ser confirmado por fluxo seguro

---

### RF-002 — Login
O sistema deve permitir autenticação de usuários cadastrados.

**Regras:**
- autenticação via e-mail e senha
- sessão persistente com expiração configurável
- revogação de sessão em logout

---

### RF-003 — Perfis de acesso
O sistema deve permitir controle de acesso por perfil.

**Perfis mínimos:**
- nutricionista
- paciente
- administrador

**Regras:**
- cada perfil deve ter permissões específicas
- o paciente não deve acessar funções clínicas de administração do plano
- o nutricionista não deve acessar configurações administrativas de sistema sem permissão

---

## 4.2 Cadastro e Gestão de Pacientes

### RF-004 — Cadastro de paciente
O sistema deve permitir que o nutricionista cadastre pacientes.

**Dados mínimos:**
- nome completo
- sexo
- data de nascimento
- telefone
- e-mail, opcional
- objetivo principal
- restrições alimentares
- observações clínicas iniciais

---

### RF-005 — Histórico do paciente
O sistema deve manter histórico longitudinal do paciente.

**Deve armazenar:**
- avaliações anteriores
- planos alimentares anteriores
- observações por consulta
- evolução de medidas
- dietas ativas e inativas
- registros de consumo

---

### RF-006 — Pesquisa de pacientes
O sistema deve permitir pesquisa e filtragem de pacientes.

**Filtros mínimos:**
- nome
- objetivo
- status do acompanhamento
- presença de plano ativo
- última consulta

---

## 4.3 Prontuário Nutricional

### RF-007 — Ficha clínica do paciente
O sistema deve disponibilizar uma ficha clínica estruturada do paciente.

**Campos desejáveis:**
- queixa principal
- histórico familiar
- patologias
- intolerâncias
- alergias
- medicações
- suplementação
- hábitos intestinais
- padrão de sono
- rotina de atividade física
- ingestão hídrica
- preferências e aversões alimentares

---

### RF-008 — Evolução clínica por consulta
O nutricionista deve poder registrar evolução a cada consulta.

**Dados por consulta:**
- data
- observações gerais
- adesão ao plano
- intercorrências
- sintomas relatados
- ajustes definidos
- condutas e recomendações

---

## 4.4 Avaliação Antropométrica e Evolução

### RF-009 — Registro de medidas
O sistema deve permitir registro de medidas antropométricas.

**Mínimo esperado:**
- peso
- altura
- IMC calculado
- circunferência abdominal
- circunferência de cintura
- quadril
- percentual de gordura, quando houver
- massa magra, quando houver
- água corporal, quando houver

---

### RF-010 — Histórico temporal de medidas
O sistema deve manter histórico das medidas por data.

**Saídas esperadas:**
- lista cronológica
- gráficos de evolução
- comparativo entre datas
- destaque de variação

---

### RF-011 — Comparação entre avaliações
O sistema deve permitir comparar:
- avaliação atual versus anterior
- avaliação atual versus primeira
- qualquer avaliação versus outra

---

## 4.5 Plano Alimentar

### RF-012 — Criação manual do plano alimentar
O sistema deve permitir que o nutricionista crie manualmente um plano alimentar para um paciente.

**Observação crítica:**
A criação do plano deve ser de responsabilidade do nutricionista. A IA não deve substituir esse fluxo.

---

### RF-013 — Estrutura do plano alimentar
Um plano alimentar deve possuir, no mínimo:
- nome do plano
- data de início
- data de término, opcional
- objetivo
- status
- refeições
- alimentos por refeição
- orientações gerais
- dietas relacionadas ou alternativas, quando aplicável

---

### RF-014 — Status do plano alimentar
O sistema deve suportar estados para o plano alimentar.

**Estados mínimos:**
- rascunho
- ativo
- inativo
- arquivado
- substituído

---

### RF-015 — Refeições no plano
O plano alimentar deve permitir o cadastro de múltiplas refeições.

**Exemplos:**
- café da manhã
- lanche da manhã
- almoço
- lanche da tarde
- jantar
- ceia

**Regra:**
A nomenclatura da refeição deve ser configurável pelo nutricionista.

---

### RF-016 — Ordem e horário da refeição
Cada refeição deve conter:
- título
- horário sugerido
- ordem de exibição
- lista de alimentos
- calorias totais
- macros totais
- micros totais, quando calculáveis

---

### RF-017 — Alimentos por refeição
Cada alimento prescrito deve conter, no mínimo:
- nome do alimento
- gramatura
- unidade caseira, quando disponível
- observação descritiva
- calorias
- macronutrientes
- micronutrientes, quando disponíveis
- substituições equivalentes, quando existirem

---

### RF-018 — Exibição detalhada por refeição
Ao abrir uma refeição, o paciente deve visualizar:
- horário
- nome da refeição
- calorias totais
- proteína total
- carboidrato total
- gordura total
- alimentos detalhados
- gramaturas
- equivalentes
- orientações específicas da refeição, se houver

---

### RF-019 — Destaque do plano alimentar ativo
Na tela inicial do paciente, o sistema deve deixar visualmente claro:
- qual plano está ativo
- qual dieta está em uso
- quais dietas alternativas estão disponíveis
- como trocar entre dietas autorizadas

---

### RF-020 — Troca de dieta pelo paciente
O sistema deve permitir que o paciente alterne entre planos ou dietas previamente autorizados pelo nutricionista.

**Regras:**
- o paciente não pode criar ou editar dietas
- apenas dietas associadas e liberadas pelo nutricionista podem ser exibidas
- deve haver indicação visual explícita da dieta selecionada
- a troca deve atualizar toda a tela e os cálculos exibidos

---

### RF-021 — Hierarquia visual do plano alimentar
A tela inicial do plano alimentar deve apresentar, de forma prioritária:
1. título da tela
2. nome do plano ativo
3. seletor de dieta
4. resumo nutricional do dia
5. lista de refeições
6. orientações nutricionais

---

## 4.6 Registro de Consumo Alimentar

### RF-022 — Registro de refeição consumida
O paciente deve poder registrar que consumiu uma refeição.

**Ação principal esperada:**
- botão de registrar refeição

**Regras:**
- o registro deve armazenar data e hora
- o sistema deve permitir marcação de consumo completo ou parcial
- deve ser possível ajustar a quantidade efetivamente consumida

---

### RF-023 — Recalcular consumo com base no registro
Ao registrar o que foi consumido, o sistema deve recalcular:
- calorias ingeridas da refeição
- macronutrientes ingeridos da refeição
- micronutrientes ingeridos da refeição, se aplicável
- totais do dia

---

### RF-024 — Histórico diário de consumo
O paciente e o nutricionista devem poder visualizar o histórico de consumo do dia e de períodos anteriores.

---

### RF-025 — Comparação entre prescrito e consumido
O sistema deve permitir comparar:
- o que foi prescrito
- o que foi efetivamente consumido
- diferença calórica
- diferença em macros
- diferença em micros, quando possível

---

## 4.7 Cálculo Nutricional

### RF-026 — Cálculo por alimento
O sistema deve calcular, para cada alimento:
- calorias
- proteínas
- carboidratos
- gorduras
- fibras, quando disponível
- micronutrientes, quando disponíveis na base

---

### RF-027 — Cálculo por refeição
O sistema deve calcular o somatório nutricional de cada refeição.

---

### RF-028 — Cálculo do total diário
O sistema deve calcular o total diário prescrito e consumido.

---

### RF-029 — Exibição compacta na lista de refeições
Cada card de refeição deve exibir resumidamente:
- horário
- nome da refeição
- calorias totais
- macros principais
- status de consumo

---

### RF-030 — Base de composição nutricional
O sistema deve utilizar uma base estruturada de alimentos com composição nutricional.

**A base deve suportar:**
- alimentos in natura
- industrializados
- unidades caseiras
- gramas
- valores por 100 g e por porção
- micronutrientes, quando disponíveis

---

### RF-031 — Micronutrientes
O sistema deve suportar, quando a base permitir, exibição e cálculo de micronutrientes como:
- cálcio
- ferro
- magnésio
- fósforo
- potássio
- sódio
- zinco
- vitaminas A, C, D, E, K
- vitaminas do complexo B

---

## 4.8 Substituições Alimentares

### RF-032 — Cadastro de substituições
O nutricionista deve poder cadastrar substituições alimentares por alimento.

**Cada substituição pode considerar:**
- equivalência calórica
- equivalência de macronutrientes
- compatibilidade com a refeição
- adequação ao objetivo do paciente

---

### RF-033 — Exibição de substituições
Ao visualizar um alimento, o paciente deve poder ver substituições permitidas.

---

### RF-034 — Sugestão da IA para substituições
O sistema deve exibir, dentro do contexto de um alimento, um botão chamado **Sugestão da IA**.

Ao acionar esse botão, o sistema deve apresentar substituições sugeridas com base em:
- calorias aproximadas
- categoria do alimento
- objetivo do paciente
- contexto da refeição
- restrições alimentares do paciente
- diretrizes definidas pelo nutricionista, quando houver

**Regra central:**
A IA sugere, mas não altera automaticamente a dieta.

---

### RF-035 — Explicação da sugestão
Cada sugestão da IA deve poder apresentar justificativa simples e rastreável, por exemplo:
- equivalência calórica aproximada
- melhor adequação a low carb
- alternativa com perfil semelhante
- opção com menor densidade energética

---

### RF-036 — Confirmação de substituição
Caso o sistema permita que o paciente registre consumo com substituição, a substituição deve:
- ser explicitamente confirmada
- ficar registrada no histórico
- impactar o cálculo do consumo efetivo
- permanecer visível ao nutricionista

---

## 4.9 Assistente de IA Nutricional

### RF-037 — Chat de IA nutricional
O sistema deve possuir um módulo de chat com IA voltado exclusivamente para temas alimentares e nutricionais não diagnósticos.

---

### RF-038 — Escopo restrito da IA
A IA deve ser restringida a responder apenas temas como:
- alimentação
- hábitos alimentares
- chás e alimentos com finalidades gerais de bem-estar
- substituições alimentares
- hidratação
- rotina alimentar
- orientações gerais não médicas

**Não deve responder como escopo primário:**
- diagnóstico médico
- prescrição medicamentosa
- tratamento clínico médico
- interpretação definitiva de doença
- substituição da conduta do nutricionista

---

### RF-039 — Sugestões informativas da IA
A IA deve poder responder perguntas como:
- sugestões de chá para retenção líquida
- sugestões de chá para ansiedade
- sugestões de chá para foco e concentração
- orientações gerais de alimentação conforme o contexto do paciente

**Regra:**
As respostas devem conter linguagem prudente, sem caráter de prescrição médica.

---

### RF-040 — Contextualização pelo perfil do paciente
Quando autorizado, o chat pode considerar:
- dieta ativa
- restrições alimentares
- objetivo do paciente
- histórico alimentar relevante

---

### RF-041 — Limitação de domínio
Perguntas fora do domínio devem ser recusadas ou redirecionadas.

---

### RF-042 — Histórico de chat
O sistema deve manter histórico de conversas do paciente com a IA, conforme política de retenção definida.

---

### RF-043 — Sinalização de assistente virtual
O sistema deve deixar claro que a resposta foi gerada por IA e não substitui acompanhamento profissional.

---

## 4.10 UX, Tema e Aparência

### RF-044 — Tela de personalização visual
O sistema deve oferecer uma área de configuração de aparência.

---

### RF-045 — Temas
O sistema deve suportar, no mínimo:
- tema claro
- tema escuro
- automático conforme sistema operacional

---

### RF-046 — Paletas de cor
O sistema deve permitir seleção entre paletas de cor pré-definidas.

**Requisitos de UX:**
- nomes compreensíveis
- visualização prévia
- aplicação consistente no app inteiro
- manutenção de contraste e acessibilidade

---

### RF-047 — Aplicação global de tema
A mudança de cor deve refletir de forma consistente em:
- botões primários
- ícones de destaque
- estados selecionados
- gráficos
- cabeçalhos
- controles interativos

---

### RF-048 — Preview do tema
O sistema deve oferecer visualização clara do tema antes da confirmação, quando tecnicamente viável.

---

### RF-049 — Consistência visual
A interface deve seguir um design system com:
- cores semânticas
- tipografia padronizada
- espaçamento consistente
- componentes reutilizáveis
- estados de interação previsíveis

---

## 4.11 Relatórios

### RF-050 — Relatório de plano alimentar
O sistema deve permitir exportar o plano alimentar do paciente.

---

### RF-051 — Relatório de evolução
O sistema deve permitir gerar relatório com:
- histórico de medidas
- comparativos
- adesão
- observações

---

### RF-052 — Relatório nutricional
O sistema deve permitir gerar relatório nutricional com:
- calorias prescritas
- calorias consumidas
- macros
- micros, quando disponíveis
- substituições registradas

---

## 4.12 Auditoria e Histórico

### RF-053 — Histórico de alterações
O sistema deve registrar alterações relevantes em:
- plano alimentar
- refeições
- alimentos
- substituições
- medidas
- observações clínicas

---

### RF-054 — Rastreabilidade
Cada alteração relevante deve possuir, no mínimo:
- autor
- data
- hora
- tipo de alteração
- estado anterior, quando pertinente
- novo estado

---

## 5. Regras de Negócio

### RN-001
O plano alimentar somente é considerado válido para o paciente quando estiver com status ativo.

### RN-002
O paciente pode visualizar somente planos vinculados ao seu cadastro e liberados para sua conta.

### RN-003
A IA não pode publicar, ativar ou substituir planos automaticamente.

### RN-004
Sugestões da IA são assistivas e não devem ser interpretadas como prescrição final.

### RN-005
A troca de dieta pelo paciente deve ocorrer apenas entre dietas explicitamente autorizadas pelo nutricionista.

### RN-006
Ao registrar consumo parcial de um alimento, o sistema deve recalcular proporcionalmente os valores nutricionais.

### RN-007
Se a base nutricional de um alimento estiver incompleta, o sistema deve deixar claro quais nutrientes possuem valores indisponíveis.

### RN-008
A nomenclatura de refeições deve ser configurável, mas a ordenação e o comportamento interno devem continuar estruturados.

### RN-009
Refeições sem alimentos válidos não devem ser publicadas como parte de um plano ativo sem aviso ao nutricionista.

### RN-010
O sistema deve priorizar clareza na indicação de qual dieta está ativa.

### RN-011
Funcionalidades de baixo valor clínico, como registro de humor da refeição, não entram no escopo inicial obrigatório.

### RN-012
O diário alimentar, caso exista no futuro, não deve substituir o registro objetivo de refeição consumida como mecanismo principal.

---

## 6. Requisitos Não Funcionais

---

## 6.1 Arquitetura e Tecnologia

### RNF-001 — Stack tecnológica principal
O sistema deve ser projetado para uso com:
- Kotlin Multiplatform, quando aplicável
- Jetpack Compose para UI
- backend escalável, preferencialmente em Kotlin
- APIs desacopladas para serviços de IA

---

### RNF-002 — Separação de responsabilidades
A solução deve separar claramente:
- camada de apresentação
- camada de domínio
- camada de dados
- camada de integração externa

---

### RNF-003 — IA desacoplada do cliente
A integração com IA não deve depender exclusivamente de chamadas diretas do aplicativo cliente para provedores externos; preferencialmente deve existir mediação por backend próprio para:
- controle de uso
- segurança
- observabilidade
- troca futura de provedor

---

## 6.2 Usabilidade

### RNF-004 — Clareza de navegação
O sistema deve possuir navegação intuitiva e previsível, com baixa carga cognitiva.

---

### RNF-005 — Hierarquia visual
Os elementos mais relevantes da tela devem ser claramente identificáveis:
- título da tela
- plano ativo
- ação principal
- resumo nutricional
- lista de refeições

---

### RNF-006 — Consistência de interface
Componentes visuais semelhantes devem manter o mesmo comportamento ao longo do sistema.

---

### RNF-007 — Acessibilidade
O sistema deve atender boas práticas mínimas de acessibilidade:
- contraste adequado
- tamanhos de toque adequados
- ícones acompanhados de texto quando necessário
- leitura compreensível
- estados visuais claros

---

### RNF-008 — Redução de ambiguidade
A interface não deve apresentar nomenclaturas ambíguas quando houver alternativa mais clara. Exemplo:
- preferir “Lanche da manhã” a “Colação”, quando assim configurado pelo profissional

---

## 6.3 Desempenho

### RNF-009 — Tempo de resposta local
A navegação entre telas locais deve ocorrer com percepção de fluidez para o usuário.

---

### RNF-010 — Carregamento de dados
A listagem de refeições e abertura do detalhe de refeição devem ocorrer com tempo de resposta adequado mesmo em dispositivos intermediários.

---

### RNF-011 — Degradação controlada
Na indisponibilidade temporária da IA, o restante do sistema deve continuar funcional.

---

## 6.4 Segurança

### RNF-012 — Proteção de dados sensíveis
Os dados pessoais e clínicos devem ser tratados com criptografia em trânsito e controles apropriados de proteção em repouso.

---

### RNF-013 — Autorização
Toda operação sensível deve validar o perfil do usuário e a permissão correspondente.

---

### RNF-014 — Auditoria
Ações críticas devem ser registradas para fins de rastreabilidade.

---

### RNF-015 — Sessão segura
Sessões de usuário devem expirar adequadamente e permitir revogação.

---

## 6.5 Confiabilidade e Integridade

### RNF-016 — Consistência nutricional
Os cálculos nutricionais devem ser determinísticos e reprodutíveis com base na base de dados utilizada.

---

### RNF-017 — Integridade transacional
Alterações de plano alimentar e registro de consumo devem evitar estados parcialmente persistidos.

---

### RNF-018 — Versionamento lógico do plano
O sistema deve permitir preservar histórico de versões ou alterações relevantes do plano alimentar.

---

## 6.6 Escalabilidade

### RNF-019 — Crescimento do sistema
A arquitetura deve suportar aumento progressivo de:
- usuários
- pacientes
- alimentos
- registros
- mensagens de IA

---

### RNF-020 — Evolução modular
Novos módulos devem poder ser incorporados sem reescrita estrutural do núcleo do sistema.

---

## 6.7 Manutenibilidade

### RNF-021 — Código modular
O código deve ser organizado em módulos com responsabilidades claras.

---

### RNF-022 — Testabilidade
As regras de cálculo, permissões, recomendações e troca de dietas devem ser testáveis automaticamente.

---

### RNF-023 — Observabilidade
O backend deve possuir logs, métricas e rastreamento suficientes para diagnosticar falhas relevantes.

---

## 7. Requisitos de Dados

### RD-001 — Entidades mínimas
A solução deve contemplar, no mínimo, as seguintes entidades lógicas:
- Usuário
- Paciente
- Nutricionista
- PlanoAlimentar
- Dieta
- Refeição
- Alimento
- Porção
- Substituição
- RegistroConsumo
- Avaliação
- MedidaAntropométrica
- EvoluçãoClínica
- MensagemIA
- Tema/Aparência
- Auditoria

---

### RD-002 — Histórico
Entidades clínicas e nutricionais relevantes devem suportar histórico temporal.

---

### RD-003 — Normalização lógica
A modelagem de dados deve evitar redundâncias indevidas e inconsistências de atualização.

---

## 8. Casos de Uso Principais

### UC-001 — Nutricionista cria plano alimentar
**Ator principal:** Nutricionista  
**Fluxo resumido:**
1. Abre paciente
2. Cria novo plano
3. Define nome e objetivo
4. Adiciona refeições
5. Adiciona alimentos e quantidades
6. Revisa totais nutricionais
7. Publica plano

---

### UC-002 — Paciente visualiza plano alimentar
**Ator principal:** Paciente  
**Fluxo resumido:**
1. Acessa tela inicial
2. Visualiza plano ativo
3. Seleciona refeição
4. Consulta alimentos, gramaturas, calorias e macros
5. Registra consumo

---

### UC-003 — Paciente troca dieta autorizada
**Ator principal:** Paciente  
**Fluxo resumido:**
1. Abre seletor de dieta
2. Escolhe uma dieta autorizada
3. Sistema troca o plano exibido
4. Sistema atualiza todos os cards e totais

---

### UC-004 — Paciente solicita sugestão da IA
**Ator principal:** Paciente  
**Fluxo resumido:**
1. Abre detalhe do alimento
2. Clica em “Sugestão da IA”
3. Sistema consulta IA
4. Exibe substituições com justificativa
5. Paciente registra consumo com ou sem substituição

---

### UC-005 — Paciente conversa com assistente nutricional
**Ator principal:** Paciente  
**Fluxo resumido:**
1. Abre chat
2. Faz pergunta sobre alimentação
3. IA responde dentro do domínio permitido
4. Histórico fica salvo

---

### UC-006 — Nutricionista acompanha evolução
**Ator principal:** Nutricionista  
**Fluxo resumido:**
1. Abre perfil do paciente
2. Consulta medidas históricas
3. Compara avaliações
4. Revisa adesão ao plano
5. Ajusta estratégia alimentar

---

## 9. Requisitos de UX Específicos do Produto

### UX-001
A tela principal do paciente deve comunicar imediatamente o nome do plano alimentar ativo.

### UX-002
O mecanismo de troca de dieta deve ser simples, visível e semanticamente claro.

### UX-003
A lista de refeições deve ser escaneável em poucos segundos.

### UX-004
O detalhe da refeição deve enfatizar:
- o que comer
- quanto comer
- valor energético
- composição nutricional

### UX-005
A ação principal da refeição deve ser “Registrar refeição”.

### UX-006
Ações de menor valor clínico não devem ocupar posição de destaque no fluxo inicial do paciente.

### UX-007
O sistema de cores deve ser consistente, previsível e semanticamente útil, não apenas decorativo.

### UX-008
O design deve reduzir confusão entre:
- plano
- dieta
- refeição
- substituição
- consumo

---

## 10. Itens Fora do Escopo Inicial

Os itens abaixo podem existir em roadmap futuro, mas não são obrigatórios para a primeira versão:

- marketplace de profissionais
- integração com balanças inteligentes
- integração com wearables
- rede social interna
- gamificação avançada
- teleconsulta embutida
- humor da refeição
- diário alimentar fotográfico como módulo central
- prescrição automática completa por IA sem supervisão

---

## 11. Restrições e Diretrizes

### RST-001
A IA deve operar com escopo nutricional restrito e controlado por política de prompts e filtros.

### RST-002
A plataforma deve permitir futura troca de provedor de IA sem refatoração profunda do cliente.

### RST-003
A primeira versão deve priorizar corretude do domínio, usabilidade e cálculo nutricional antes de features periféricas.

### RST-004
A autoridade clínica final sobre o plano alimentar é sempre do nutricionista.

---

## 12. Critérios de Aceitação de Alto Nível

### CA-001
Será considerado atendido o requisito de plano alimentar se o nutricionista puder criar manualmente um plano completo com refeições, alimentos, quantidades e publicação.

### CA-002
Será considerado atendido o requisito de visualização do plano se o paciente conseguir identificar imediatamente qual dieta está ativa e visualizar claramente suas refeições.

### CA-003
Será considerado atendido o requisito de consumo se o paciente puder registrar o que consumiu e o sistema recalcular os totais do dia.

### CA-004
Será considerado atendido o requisito de IA de substituição se o sistema oferecer sugestões coerentes, explicáveis e não automáticas por alimento.

### CA-005
Será considerado atendido o requisito de chat de IA se a IA responder questões alimentares dentro do domínio permitido e recusar temas externos.

### CA-006
Será considerado atendido o requisito de UX de aparência se a troca de tema for intuitiva, consistente e aplicada globalmente.

---

## 13. Correção de Corretude e Consistência dos Requisitos

### 13.1 Correções de ambiguidade aplicadas
Foram removidas ou reduzidas ambiguidades relacionadas a:
- quem cria a dieta
- quem pode trocar a dieta
- o papel da IA
- diferença entre plano, dieta e refeição
- papel do registro de consumo
- escopo do chat de IA

---

### 13.2 Correções de conflito funcional
Os seguintes conflitos foram resolvidos nesta especificação:
1. **IA criando dieta** versus **nutricionista criando dieta**
    - decisão final: o nutricionista cria manualmente; a IA apenas sugere.
2. **diário alimentar como ação central** versus **registro objetivo da refeição**
    - decisão final: o registro da refeição é ação principal.
3. **várias dietas sem clareza** versus **um plano ativo visível**
    - decisão final: deve haver indicação clara do plano/dieta ativa.
4. **botões de baixo valor clínico em destaque**
    - decisão final: não compõem o fluxo principal obrigatório.

---

### 13.3 Pontos com necessidade de refinamento futuro
Embora esta especificação esteja suficientemente completa para planejamento inicial, ainda deverão ser refinados na fase de discovery técnico:
- modelo detalhado da base nutricional
- política de retenção do histórico de IA
- nível exato de cálculo de micronutrientes por base utilizada
- estratégia de sincronização offline, se aplicável
- detalhamento de permissões administrativas
- estratégia regulatória e jurídica para o assistente de IA

---

## 14. Recomendações para Planejamento de Engenharia

### 14.1 Recomendações de priorização
Recomenda-se implementar por fases:

#### Fase 1 — Núcleo clínico
- autenticação
- cadastro de paciente
- prontuário
- plano alimentar manual
- refeições e alimentos
- cálculo nutricional
- visualização do paciente
- registro de consumo

#### Fase 2 — Evolução e substituições
- histórico de medidas
- gráficos
- substituições
- sugestão da IA
- auditoria

#### Fase 3 — Assistente nutricional e refinamentos
- chat de IA
- temas e personalização
- relatórios avançados
- melhorias de UX
- otimizações de domínio

---

### 14.2 Recomendações de testes
O time deverá prever, no mínimo:
- testes unitários de cálculo nutricional
- testes de regras de permissão
- testes de troca de dieta
- testes de persistência de histórico
- testes de interface para fluxo principal do paciente
- testes de robustez da integração com IA
- testes de regressão em design system

---

## 15. Conclusão

Este documento define uma especificação funcional e não funcional completa para um aplicativo de nutrição centrado no nutricionista, com foco em:
- prescrição alimentar manual e controlada
- melhor usabilidade para o paciente
- cálculo nutricional detalhado
- clareza de dietas e refeições
- rastreabilidade clínica
- uso de IA como suporte e não como substituição do profissional

A especificação está estruturada para permitir que uma equipe de engenharia:
- estime esforço
- modele arquitetura
- derive histórias de usuário
- produza backlog técnico
- defina critérios de aceite
- inicie implementação com menor risco de ambiguidades críticas

---
```