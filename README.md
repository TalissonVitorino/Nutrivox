#  Nutrivox

O **Nutrivox** é uma plataforma completa de nutrição clínica desenvolvida com **Kotlin Multiplatform (KMP)**. O projeto integra o acompanhamento entre nutricionistas e pacientes com o suporte de uma **Inteligência Artificial assistiva** para sugestões alimentares e dúvidas nutricionais.

## Funcionalidades Principais

- **Para Pacientes:** Visualização de planos alimentares, registro de consumo, acompanhamento de metas (calorias/macros) e chat com IA.
- **Para Nutricionistas:** Gestão de pacientes, criação de planos personalizados e acompanhamento da evolução clínica.
- **IA Nutricional:** Sugestões inteligentes de substituições baseadas no perfil do paciente e respostas a dúvidas comuns de nutrição.
- **Multiplataforma:** Suporte para Android, iOS, Web e Desktop utilizando Compose Multiplatform.

## Estrutura do Projeto

O repositório é organizado em uma arquitetura monorepo:

*   [`/composeApp`](./composeApp): Código compartilhado da interface (UI) e lógica de apresentação para todas as plataformas clientes.
*   [`/server`](./server): Backend desenvolvido em **Ktor** (Kotlin) com suporte a banco de dados PostgreSQL.
*   [`/shared`](./shared): Lógica de negócio e modelos de dados compartilhados entre o App e o Servidor.
*   [`/iosApp`](./iosApp): Aplicativo nativo iOS (entrada para o Compose Multiplatform).
*   [`/docs`](./docs): Documentação detalhada, briefings de UI e especificações de requisitos.

## Configuração do Ambiente

### 1. Requisitos
- Android Studio Koala ou superior.
- Xcode (para rodar iOS).
- JDK 17 ou superior.
- Docker (opcional, para o banco de dados).

### 2. Variáveis de Ambiente
O projeto utiliza um arquivo `.env` para gerenciar chaves sensíveis. Existe um modelo chamado `.env.example` na raiz do projeto.

1.  Copie o exemplo:
    ```bash
    cp .env.example .env
    ```
2.  Preencha a sua `OPENAI_API_KEY` e outras configurações no arquivo `.env` recém-criado.

## Como Executar

### Backend (Server)
```bash
./gradlew :server:run
```

### Android
```bash
./gradlew :composeApp:assembleDebug
```

### Desktop (JVM)
```bash
./gradlew :composeApp:run
```

### Web (Wasm)
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

### iOS
Abra a pasta `/iosApp` no Xcode ou utilize a configuração de execução `iosApp` no Android Studio.

## Segurança e Boas Práticas
- **Chaves de API:** Nunca comite o arquivo `.env`. Ele já está incluído no `.gitignore`.
- **Kotlin Multiplatform:** Compartilhamos o máximo de código possível, mantendo a performance nativa.

---
Desenvolvido com ❤️ utilizando Kotlin e Compose Multiplatform.
