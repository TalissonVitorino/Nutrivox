# Nutrivox — Plano Mestre de Implementação

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implementar o app Nutrivox completo — desde o design system no Figma até o backend funcional e o app mobile em Kotlin Multiplatform + Compose.

**Architecture:**
- Frontend: Kotlin Multiplatform com Compose Multiplatform (Android + iOS + Desktop)
- Backend: Ktor (Kotlin/JVM) com clean architecture (domain, data, API layers)
- Database: PostgreSQL via Exposed ORM
- IA: API mediada por backend próprio (OpenAI/Anthropic, trocável)
- Auth: JWT com refresh tokens
- Módulos Gradle existentes: `:composeApp`, `:shared`, `:server`

**Tech Stack:**
- Kotlin 2.3.20, Compose Multiplatform 1.10.3, Ktor 3.4.1
- Exposed (ORM), PostgreSQL, Flyway (migrations)
- Kotlinx.serialization, Kotlinx.datetime
- Koin (DI), Ktor Client (networking no app)
- JWT (auth), BCrypt (passwords)

---

## Inventário do Figma (19 telas existentes)

| # | Node ID | Tela | Tipo | Descrição |
|---|---------|------|------|-----------|
| 1 | 1:41 | Prontuário do Paciente (Nutricionista) | Nutri | Perfil paciente com tabs: Prontuário, Avaliações, Planos, Anamnese. Insights de IA. Notas clínicas. |
| 2 | 1:275 | Evolução e Metas (Paciente) | Paciente | Resumo do período, adesão, proteína média, hidratação, gráfico calórico, recomendações, conquistas |
| 3 | 1:585 | Editor de Plano (Nutricionista) | Nutri | Refeições colapsáveis, busca de ingredientes, sugestão IA, botões Visualizar/Publicar |
| 4 | 1:656 | Chat Nutricionista-Paciente | Nutri | Chat direto com paciente, sugestão de resposta IA, editar e inserir, atualização de plano inline |
| 5 | 1:1015 | Configurações (Nutricionista) | Nutri | Perfil, clínica, preferências IA, privacidade, notificações, modo escuro |
| 6 | 1:1123 | Login Nutricionista (v1) | Auth | Login com email/CRN, senha, biometria, sem scroll |
| 7 | 1:1257 | Login Nutricionista (v2) | Auth | Versão completa com CTA "Create account", termos |
| 8 | 1:1350 | Plano Alimentar (Paciente) | Paciente | Seletor de dias, refeições com itens, substituições, botão registrar, análise IA |
| 9 | 1:1585 | Nova Avaliação (Nutricionista) | Nutri | Formulário: medidas, circunferências, composição corporal, notas clínicas |
| 10 | 1:1736 | Home do Paciente | Paciente | Plano ativo, macros circulares, sugestão IA, lista de refeições, botão registrar |
| 11 | 1:1937 | Login Paciente | Auth | Login simples, biometria, termos |
| 12 | 1:2011 | Preview do Plano (Nutricionista) | Nutri | Visualização como paciente: macros, refeições, notas, botões Editar/Publicar |
| 13 | 1:2179 | Chat Paciente (com Nutricionista + IA) | Paciente | Tabs Nutricionista/Assistente, conversa, chips de ação rápida |
| 14 | 1:2247 | Registro de Consumo | Paciente | Tela completa: macros consumidos vs meta, ajuste por item, busca alimento extra, foto, notas |
| 15 | 1:2608 | Login Nutricionista (v3 - pt-BR) | Auth | Versão em português |
| 16 | 1:2692 | Planos do Paciente (Nutricionista) | Nutri | Tab Planos: adesão, sugestões IA, plano ativo, histórico, criar novo |
| 17 | 1:3000 | Lista de Pacientes (Nutricionista) | Nutri | Cards com avatar, meta, adesão, última antropometria, último contato |
| 18 | 1:3142 | Dashboard (Nutricionista) | Nutri | Consultas hoje, alertas, insights IA, adesão agregada, métricas |
| 19 | 1:3445 | Perfil (Paciente) | Paciente | Dados, objetivos, preferências, restrições, configurações, nutricionista vinculado |

---

## Mapeamento Figma → Requisitos Refinados

| Tela Figma | RFs Cobertos | Lacunas vs Spec Refinada |
|------------|-------------|--------------------------|
| Home Paciente (1:1736) | RF-023, RF-025, RF-020 | Falta seletor de **variação de dieta** (mostra dias, não variações). Falta barra de progresso kcal (tem circles mas não barra). |
| Plano Alimentar (1:1350) | RF-024, RF-025, RF-037, RF-038 | Seletor por dias (Seg/Ter) em vez de variações nomeadas. Botão "Registrar" ok. Substituições ok. |
| Registro de Consumo (1:2247) | RF-034, RF-035, RF-039 | Muito bom. Tem ajuste por item, busca extra, foto, sugestão IA. Falta modo "Comi tudo" rápido. |
| Evolução (1:275) | RF-013, RF-033, RF-020 | Excelente. Conquistas é feature extra não na spec. |
| Chat Paciente (1:2179) | RF-042, RF-043, RF-047 | Tabs Nutricionista/Assistente — bom. Falta disclaimer IA mais explícito. |
| Editor de Plano (1:585) | RF-015, RF-016, RF-019, RF-021, RF-029 | Falta seção de metas nutricionais (RF-020). Falta variações de dieta. |
| Preview Plano (1:2011) | RF-027 | OK, mas falta comparação totais vs metas. |
| Prontuário (1:41) | RF-010, RF-011 | Bom. Insights IA ok. |
| Nova Avaliação (1:1585) | RF-012 | Completo. |
| Planos do Paciente (1:2692) | RF-009, RF-017 | Bom. Adesão e histórico ok. |
| Lista Pacientes (1:3000) | RF-008 | OK. |
| Dashboard (1:3142) | — (novo) | Não está na spec refinada, mas é feature útil para nutricionista. |
| Login Nutri (1:2608) | RF-001, RF-002 | OK. |
| Login Paciente (1:1937) | RF-006 | Falta fluxo de onboarding via convite (RF-005). |
| Configurações Nutri (1:1015) | RF-053-056 | Bom. Preferências IA ok. |
| Perfil Paciente (1:3445) | RF-007 | Bom. Consentimentos ok. |
| Chat Nutri (1:656) | RF-049 | Sugestão de resposta IA é feature avançada. Bom. |

### Lacunas de UI que precisam ser criadas ou ajustadas:

1. **Seletor de variação de dieta** — as telas usam seletor de dias. Precisamos de variações nomeadas ("Dia de treino", "Low carb").
2. **Metas nutricionais no Editor de Plano** — seção para definir kcal/P/C/G target.
3. **Onboarding do paciente via convite** — tela de aceitar convite + criar conta.
4. **Modo "Comi tudo"** no registro — atalho rápido.
5. **Disclaimer IA** mais visível no chat do paciente.
6. **Estado vazio** — tela quando paciente não tem plano ativo.

---

## Estrutura do Plano

O plano está dividido em **4 projetos independentes** que podem ser executados em sequência ou parcialmente em paralelo:

### Projeto 1: Ajustes de UI no Figma
Corrigir lacunas identificadas no Figma antes de implementar.

### Projeto 2: Backend (Ktor + PostgreSQL)
Modelagem de dados, API REST, autenticação, lógica de negócio.

### Projeto 3: Shared Module (Domínio + DTOs)
Modelos compartilhados, DTOs, interfaces de repositório, lógica de domínio.

### Projeto 4: Frontend (Compose Multiplatform)
Telas, navegação, state management, integração com backend.

**Ordem recomendada:** Projeto 1 → Projeto 2 + Projeto 3 (paralelo) → Projeto 4

---

# PROJETO 1 — Ajustes de UI no Figma

> Ajustar as telas existentes e criar telas faltantes antes de implementar.

### Task 1.1: Criar tela de Onboarding do Paciente (via convite)

**Ação:** Criar nova tela no Figma

**Conteúdo:**
- Tela 1: "Seu nutricionista [Nome] convidou você!" + campos: e-mail, senha, confirmar senha
- Tela 2: Termos de uso, política de privacidade, consentimento IA
- Tela 3: Sucesso + redirect para Home

- [ ] **Step 1:** Gerar a tela de onboarding no Figma usando `generate_figma_design`

---

### Task 1.2: Ajustar Home do Paciente — seletor de variação

**Ação:** Modificar a tela 1:1736

**Mudanças:**
- Substituir seletor de dias ("Hoje, Amanhã") por seletor de variações ("Padrão", "Dia de treino")
- Manter dias como filtro secundário ou remover
- Adicionar barra de progresso linear de kcal (meta vs consumido) acima dos circles de macros

- [ ] **Step 1:** Ajustar via Figma MCP

---

### Task 1.3: Ajustar Editor de Plano — metas + variações

**Ação:** Modificar tela 1:585

**Mudanças:**
- Adicionar seção "Metas Nutricionais" (kcal, P, C, G inputs) após dados gerais
- Adicionar conceito de "Variações de dieta" (tabs ou lista)
- Adicionar barra de totais fixa no bottom comparando prescrito vs meta

- [ ] **Step 1:** Ajustar via Figma MCP

---

### Task 1.4: Adicionar modo "Comi tudo" no registro

**Ação:** Modificar tela 1:2247

**Mudanças:**
- Adicionar botão de atalho "Comi tudo" no topo do formulário de registro

- [ ] **Step 1:** Ajustar via Figma MCP

---

### Task 1.5: Criar tela de estado vazio (paciente sem plano)

**Ação:** Criar nova tela

**Conteúdo:**
- Ilustração + "Seu nutricionista está preparando seu plano. Você será notificado."

- [ ] **Step 1:** Gerar via Figma MCP

---

### Task 1.6: Melhorar disclaimer IA no chat do paciente

**Ação:** Modificar tela 1:2179

**Mudanças:**
- Banner fixo no topo da aba "Assistente": "Sou um assistente de IA. Respostas informativas, não substituem seu nutricionista."
- Badge ✨ mais proeminente nas bolhas da IA

- [ ] **Step 1:** Ajustar via Figma MCP

---

# PROJETO 2 — Backend (Ktor + PostgreSQL)

> API REST completa com auth, CRUD de planos, registro de consumo, cálculo nutricional e gateway de IA.

## Arquitetura do Backend

```
server/src/main/kotlin/com/kotlincrossplatform/nutrivox/
├── Application.kt                    # Ktor setup, plugins, routing
├── plugins/
│   ├── Authentication.kt             # JWT config
│   ├── ContentNegotiation.kt         # JSON serialization
│   ├── CORS.kt                       # CORS setup
│   ├── StatusPages.kt                # Error handling
│   └── Database.kt                   # Exposed + PostgreSQL + Flyway
├── auth/
│   ├── AuthRoutes.kt                 # POST /auth/register, /auth/login, /auth/refresh
│   ├── AuthService.kt                # Business logic: register, login, token generation
│   └── PasswordHasher.kt             # BCrypt wrapper
├── users/
│   ├── UserRoutes.kt                 # GET/PUT /users/me
│   ├── UserService.kt                # User profile operations
│   └── UserTable.kt                  # Exposed table definition
├── patients/
│   ├── PatientRoutes.kt              # CRUD /patients, /patients/{id}
│   ├── PatientService.kt             # Business logic
│   ├── PatientTable.kt               # Table
│   └── InviteService.kt              # Invite code generation + validation
├── clinical/
│   ├── ClinicalRecordRoutes.kt       # CRUD /patients/{id}/clinical-records
│   ├── ClinicalRecordService.kt
│   ├── ClinicalRecordTable.kt
│   ├── AssessmentRoutes.kt           # CRUD /patients/{id}/assessments
│   ├── AssessmentService.kt
│   └── AssessmentTable.kt
├── plans/
│   ├── PlanRoutes.kt                 # CRUD /patients/{id}/plans
│   ├── PlanService.kt                # Create, activate, deactivate, duplicate
│   ├── PlanTable.kt
│   ├── DietVariationTable.kt
│   ├── MealTable.kt
│   ├── MealItemTable.kt
│   ├── SubstitutionTable.kt
│   └── NutritionalGoalTable.kt
├── consumption/
│   ├── ConsumptionRoutes.kt          # POST /consumption, GET /consumption/history
│   ├── ConsumptionService.kt         # Register, recalculate
│   ├── ConsumptionRecordTable.kt
│   └── ConsumptionItemTable.kt
├── foods/
│   ├── FoodRoutes.kt                 # GET /foods/search, GET /foods/{id}
│   ├── FoodService.kt
│   ├── FoodTable.kt
│   └── HouseholdMeasureTable.kt
├── nutrition/
│   └── NutritionCalculator.kt        # Pure function: calculate per item, meal, day
├── ai/
│   ├── AIRoutes.kt                   # POST /ai/suggest-substitution, POST /ai/chat
│   ├── AIService.kt                  # Prompt building, context assembly
│   ├── AIGateway.kt                  # Interface to external AI provider
│   └── AIMessageTable.kt
├── notifications/
│   ├── NotificationRoutes.kt
│   └── NotificationService.kt
├── audit/
│   ├── AuditService.kt               # Log changes
│   └── AuditTable.kt
└── common/
    ├── ApiResponse.kt                 # Standardized response wrapper
    ├── Pagination.kt                  # Pagination params + response
    └── Exceptions.kt                  # Domain exceptions
```

## Database Migrations (Flyway)

```
server/src/main/resources/db/migration/
├── V1__create_users.sql
├── V2__create_patients_and_links.sql
├── V3__create_clinical_records.sql
├── V4__create_assessments.sql
├── V5__create_foods_and_measures.sql
├── V6__create_plans_and_structure.sql
├── V7__create_consumption.sql
├── V8__create_ai_messages.sql
├── V9__create_audit.sql
└── V10__seed_foods.sql
```

---

### Task 2.1: Setup do banco de dados e dependências

**Files:**
- Modify: `server/build.gradle.kts`
- Modify: `gradle/libs.versions.toml`
- Create: `server/src/main/kotlin/com/kotlincrossplatform/nutrivox/plugins/Database.kt`
- Create: `server/src/main/kotlin/com/kotlincrossplatform/nutrivox/plugins/ContentNegotiation.kt`
- Create: `server/src/main/kotlin/com/kotlincrossplatform/nutrivox/plugins/StatusPages.kt`
- Create: `server/src/main/resources/application.conf`

- [ ] **Step 1: Adicionar dependências ao version catalog**

```toml
# Adicionar em [versions]
exposed = "0.61.0"
postgresql = "42.7.5"
flyway = "11.8.0"
hikari = "6.3.0"
bcrypt = "0.10.2"
kotlinx-serialization = "1.8.1"
kotlinx-datetime = "0.6.2"
koin = "4.1.0"

# Adicionar em [libraries]
exposed-core = { module = "org.jetbrains.exposed:exposed-core", version.ref = "exposed" }
exposed-dao = { module = "org.jetbrains.exposed:exposed-dao", version.ref = "exposed" }
exposed-jdbc = { module = "org.jetbrains.exposed:exposed-jdbc", version.ref = "exposed" }
exposed-kotlin-datetime = { module = "org.jetbrains.exposed:exposed-kotlin-datetime", version.ref = "exposed" }
postgresql = { module = "org.postgresql:postgresql", version.ref = "postgresql" }
flyway-core = { module = "org.flywaydb:flyway-core", version.ref = "flyway" }
flyway-postgresql = { module = "org.flywaydb:flyway-database-postgresql", version.ref = "flyway" }
hikari = { module = "com.zaxxer:HikariCP", version.ref = "hikari" }
bcrypt = { module = "at.favre.lib:bcrypt", version.ref = "bcrypt" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinx-datetime" }
ktor-server-content-negotiation = { module = "io.ktor:ktor-server-content-negotiation-jvm", version.ref = "ktor" }
ktor-serialization-json = { module = "io.ktor:ktor-serialization-kotlinx-json-jvm", version.ref = "ktor" }
ktor-server-auth = { module = "io.ktor:ktor-server-auth-jvm", version.ref = "ktor" }
ktor-server-auth-jwt = { module = "io.ktor:ktor-server-auth-jwt-jvm", version.ref = "ktor" }
ktor-server-status-pages = { module = "io.ktor:ktor-server-status-pages-jvm", version.ref = "ktor" }
ktor-server-cors = { module = "io.ktor:ktor-server-cors-jvm", version.ref = "ktor" }
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-cio = { module = "io.ktor:ktor-client-cio", version.ref = "ktor" }
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
koin-ktor = { module = "io.insert-koin:koin-ktor", version.ref = "koin" }
koin-logger = { module = "io.insert-koin:koin-logger-slf4j", version.ref = "koin" }

# Adicionar em [plugins]
kotlinx-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

- [ ] **Step 2: Atualizar server/build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
    application
}

group = "com.kotlincrossplatform.nutrivox"
version = "1.0.0"

application {
    mainClass.set("com.kotlincrossplatform.nutrivox.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)

    // Ktor Server
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.cors)

    // Ktor Client (for AI gateway)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)

    // Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.postgresql)
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
    implementation(libs.hikari)

    // Auth
    implementation(libs.bcrypt)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // DI
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)

    // Logging
    implementation(libs.logback)

    // Test
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}
```

- [ ] **Step 3: Criar application.conf**

```hocon
# server/src/main/resources/application.conf
ktor {
    deployment {
        port = 8080
        port = ${?PORT}
    }
    application {
        modules = [com.kotlincrossplatform.nutrivox.ApplicationKt.module]
    }
}

database {
    url = "jdbc:postgresql://localhost:5432/nutrivox"
    url = ${?DATABASE_URL}
    user = "nutrivox"
    user = ${?DATABASE_USER}
    password = "nutrivox"
    password = ${?DATABASE_PASSWORD}
    maxPoolSize = 10
}

jwt {
    secret = "nutrivox-dev-secret-change-in-production"
    secret = ${?JWT_SECRET}
    issuer = "nutrivox"
    audience = "nutrivox-app"
    realm = "Nutrivox"
    accessTokenExpiration = 3600000   # 1 hour
    refreshTokenExpiration = 604800000 # 7 days
}

ai {
    provider = "anthropic"
    provider = ${?AI_PROVIDER}
    apiKey = ${?AI_API_KEY}
    model = "claude-sonnet-4-20250514"
    model = ${?AI_MODEL}
}
```

- [ ] **Step 4: Criar Database.kt plugin**

```kotlin
// server/src/main/kotlin/com/kotlincrossplatform/nutrivox/plugins/Database.kt
package com.kotlincrossplatform.nutrivox.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    val dbUrl = environment.config.property("database.url").getString()
    val dbUser = environment.config.property("database.user").getString()
    val dbPassword = environment.config.property("database.password").getString()
    val maxPoolSize = environment.config.property("database.maxPoolSize").getString().toInt()

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = dbUrl
        username = dbUser
        password = dbPassword
        maximumPoolSize = maxPoolSize
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    val dataSource = HikariDataSource(hikariConfig)

    Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .load()
        .migrate()

    Database.connect(dataSource)
}
```

- [ ] **Step 5: Criar ContentNegotiation.kt**

```kotlin
// server/src/main/kotlin/com/kotlincrossplatform/nutrivox/plugins/ContentNegotiation.kt
package com.kotlincrossplatform.nutrivox.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
}
```

- [ ] **Step 6: Criar StatusPages.kt**

```kotlin
// server/src/main/kotlin/com/kotlincrossplatform/nutrivox/plugins/StatusPages.kt
package com.kotlincrossplatform.nutrivox.plugins

import com.kotlincrossplatform.nutrivox.common.ApiResponse
import com.kotlincrossplatform.nutrivox.common.Exceptions.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ApiResponse.error(cause.message ?: "Not found"))
        }
        exception<UnauthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, ApiResponse.error(cause.message ?: "Unauthorized"))
        }
        exception<ForbiddenException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, ApiResponse.error(cause.message ?: "Forbidden"))
        }
        exception<ValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ApiResponse.error(cause.message ?: "Validation error"))
        }
        exception<ConflictException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, ApiResponse.error(cause.message ?: "Conflict"))
        }
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Internal server error"))
        }
    }
}
```

- [ ] **Step 7: Criar common/Exceptions.kt e common/ApiResponse.kt**

```kotlin
// server/src/main/kotlin/com/kotlincrossplatform/nutrivox/common/Exceptions.kt
package com.kotlincrossplatform.nutrivox.common

object Exceptions {
    class NotFoundException(message: String) : RuntimeException(message)
    class UnauthorizedException(message: String = "Unauthorized") : RuntimeException(message)
    class ForbiddenException(message: String = "Forbidden") : RuntimeException(message)
    class ValidationException(message: String) : RuntimeException(message)
    class ConflictException(message: String) : RuntimeException(message)
}
```

```kotlin
// server/src/main/kotlin/com/kotlincrossplatform/nutrivox/common/ApiResponse.kt
package com.kotlincrossplatform.nutrivox.common

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null
) {
    companion object {
        fun <T> ok(data: T) = ApiResponse(success = true, data = data)
        fun error(message: String) = ApiResponse<Unit>(success = false, error = message)
    }
}
```

- [ ] **Step 8: Criar common/Pagination.kt**

```kotlin
// server/src/main/kotlin/com/kotlincrossplatform/nutrivox/common/Pagination.kt
package com.kotlincrossplatform.nutrivox.common

import io.ktor.server.application.*
import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val total: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)

data class PaginationParams(val page: Int, val pageSize: Int) {
    val offset get() = (page - 1) * pageSize
}

fun ApplicationCall.paginationParams(): PaginationParams {
    val page = request.queryParameters["page"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val pageSize = request.queryParameters["pageSize"]?.toIntOrNull()?.coerceIn(1, 100) ?: 20
    return PaginationParams(page, pageSize)
}
```

- [ ] **Step 9: Compilar e verificar**

Run: `cd server && ../gradlew compileKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 10: Commit**

```bash
git add -A
git commit -m "feat(server): setup database, serialization, error handling and dependencies"
```

---

### Task 2.2: Migration V1 — Users + Auth

**Files:**
- Create: `server/src/main/resources/db/migration/V1__create_users.sql`
- Create: `server/src/main/kotlin/com/kotlincrossplatform/nutrivox/users/UserTable.kt`
- Create: `server/src/main/kotlin/com/kotlincrossplatform/nutrivox/auth/PasswordHasher.kt`
- Create: `server/src/main/kotlin/com/kotlincrossplatform/nutrivox/auth/AuthService.kt`
- Create: `server/src/main/kotlin/com/kotlincrossplatform/nutrivox/plugins/Authentication.kt`
- Create: `server/src/main/kotlin/com/kotlincrossplatform/nutrivox/auth/AuthRoutes.kt`
- Test: `server/src/test/kotlin/com/kotlincrossplatform/nutrivox/auth/AuthServiceTest.kt`

- [ ] **Step 1: Criar migration V1**

```sql
-- V1__create_users.sql
CREATE TYPE user_role AS ENUM ('nutritionist', 'patient', 'admin');

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    professional_registration VARCHAR(100), -- CRN for nutritionists
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
```

- [ ] **Step 2: Criar UserTable.kt (Exposed)**

```kotlin
// server/src/main/kotlin/com/kotlincrossplatform/nutrivox/users/UserTable.kt
package com.kotlincrossplatform.nutrivox.users

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object UserTable : Table("users") {
    val id = uuid("id").autoGenerate()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20) // nutritionist, patient, admin
    val fullName = varchar("full_name", 255)
    val phone = varchar("phone", 50).nullable()
    val professionalRegistration = varchar("professional_registration", 100).nullable()
    val isActive = bool("is_active").default(true)
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object RefreshTokenTable : Table("refresh_tokens") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(UserTable.id)
    val token = varchar("token", 500).uniqueIndex()
    val expiresAt = timestampWithTimeZone("expires_at")
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(id)
}
```

- [ ] **Step 3: Criar PasswordHasher.kt**

```kotlin
// server/src/main/kotlin/com/kotlincrossplatform/nutrivox/auth/PasswordHasher.kt
package com.kotlincrossplatform.nutrivox.auth

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
    fun hash(password: String): String =
        BCrypt.withDefaults().hashToString(12, password.toCharArray())

    fun verify(password: String, hash: String): Boolean =
        BCrypt.verifyer().verify(password.toCharArray(), hash).verified
}
```

- [ ] **Step 4: Criar AuthService.kt**

```kotlin
// server/src/main/kotlin/com/kotlincrossplatform/nutrivox/auth/AuthService.kt
package com.kotlincrossplatform.nutrivox.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kotlincrossplatform.nutrivox.common.Exceptions.*
import com.kotlincrossplatform.nutrivox.users.RefreshTokenTable
import com.kotlincrossplatform.nutrivox.users.UserTable
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long
)

data class TokenPair(val accessToken: String, val refreshToken: String)

data class RegisterRequest(val email: String, val password: String, val fullName: String, val role: String, val phone: String? = null, val professionalRegistration: String? = null)

data class LoginRequest(val email: String, val password: String)

class AuthService(private val jwtConfig: JwtConfig) {

    fun register(request: RegisterRequest): UUID = transaction {
        val existing = UserTable.selectAll().where { UserTable.email eq request.email }.singleOrNull()
        if (existing != null) throw ConflictException("Email already registered")

        if (request.password.length < 8) throw ValidationException("Password must be at least 8 characters")

        val now = OffsetDateTime.now()
        UserTable.insert {
            it[email] = request.email
            it[passwordHash] = PasswordHasher.hash(request.password)
            it[role] = request.role
            it[fullName] = request.fullName
            it[phone] = request.phone
            it[professionalRegistration] = request.professionalRegistration
            it[isActive] = true
            it[createdAt] = now
            it[updatedAt] = now
        }[UserTable.id]
    }

    fun login(request: LoginRequest): TokenPair = transaction {
        val user = UserTable.selectAll().where { UserTable.email eq request.email }.singleOrNull()
            ?: throw UnauthorizedException("Invalid credentials")

        if (!PasswordHasher.verify(request.password, user[UserTable.passwordHash]))
            throw UnauthorizedException("Invalid credentials")

        if (!user[UserTable.isActive])
            throw ForbiddenException("Account is deactivated")

        generateTokenPair(user[UserTable.id], user[UserTable.role])
    }

    fun refresh(refreshToken: String): TokenPair = transaction {
        val tokenRow = RefreshTokenTable.selectAll()
            .where { RefreshTokenTable.token eq refreshToken }
            .singleOrNull() ?: throw UnauthorizedException("Invalid refresh token")

        if (tokenRow[RefreshTokenTable.expiresAt].toInstant().isBefore(java.time.Instant.now()))
            throw UnauthorizedException("Refresh token expired")

        // Delete used token
        RefreshTokenTable.deleteWhere { RefreshTokenTable.token eq refreshToken }

        val user = UserTable.selectAll().where { UserTable.id eq tokenRow[RefreshTokenTable.userId] }.single()
        generateTokenPair(user[UserTable.id], user[UserTable.role])
    }

    fun logout(userId: UUID): Unit = transaction {
        RefreshTokenTable.deleteWhere { RefreshTokenTable.userId eq userId }
    }

    private fun generateTokenPair(userId: UUID, role: String): TokenPair {
        val now = java.time.Instant.now()

        val accessToken = JWT.create()
            .withIssuer(jwtConfig.issuer)
            .withAudience(jwtConfig.audience)
            .withClaim("userId", userId.toString())
            .withClaim("role", role)
            .withIssuedAt(now)
            .withExpiresAt(now.plusMillis(jwtConfig.accessTokenExpiration))
            .sign(Algorithm.HMAC256(jwtConfig.secret))

        val refreshTokenStr = UUID.randomUUID().toString()
        val refreshExpiry = OffsetDateTime.now().plusSeconds(jwtConfig.refreshTokenExpiration / 1000)

        RefreshTokenTable.insert {
            it[RefreshTokenTable.userId] = userId
            it[token] = refreshTokenStr
            it[expiresAt] = refreshExpiry
            it[createdAt] = OffsetDateTime.now()
        }

        return TokenPair(accessToken, refreshTokenStr)
    }
}
```

- [ ] **Step 5: Criar Authentication.kt plugin**

```kotlin
// server/src/main/kotlin/com/kotlincrossplatform/nutrivox/plugins/Authentication.kt
package com.kotlincrossplatform.nutrivox.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuthentication() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val realm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            this.realm = realm
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                val role = credential.payload.getClaim("role").asString()
                if (userId != null && role != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}

fun JWTPrincipal.userId(): java.util.UUID =
    java.util.UUID.fromString(payload.getClaim("userId").asString())

fun JWTPrincipal.role(): String =
    payload.getClaim("role").asString()
```

- [ ] **Step 6: Criar AuthRoutes.kt**

```kotlin
// server/src/main/kotlin/com/kotlincrossplatform/nutrivox/auth/AuthRoutes.kt
package com.kotlincrossplatform.nutrivox.auth

import com.kotlincrossplatform.nutrivox.common.ApiResponse
import com.kotlincrossplatform.nutrivox.plugins.userId
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class RegisterBody(val email: String, val password: String, val fullName: String, val role: String, val phone: String? = null, val professionalRegistration: String? = null)

@Serializable
data class LoginBody(val email: String, val password: String)

@Serializable
data class RefreshBody(val refreshToken: String)

@Serializable
data class TokenResponse(val accessToken: String, val refreshToken: String)

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val body = call.receive<RegisterBody>()
            val userId = authService.register(RegisterRequest(body.email, body.password, body.fullName, body.role, body.phone, body.professionalRegistration))
            call.respond(ApiResponse.ok(mapOf("userId" to userId.toString())))
        }

        post("/login") {
            val body = call.receive<LoginBody>()
            val tokens = authService.login(LoginRequest(body.email, body.password))
            call.respond(ApiResponse.ok(TokenResponse(tokens.accessToken, tokens.refreshToken)))
        }

        post("/refresh") {
            val body = call.receive<RefreshBody>()
            val tokens = authService.refresh(body.refreshToken)
            call.respond(ApiResponse.ok(TokenResponse(tokens.accessToken, tokens.refreshToken)))
        }

        authenticate("auth-jwt") {
            post("/logout") {
                val principal = call.principal<JWTPrincipal>()!!
                authService.logout(principal.userId())
                call.respond(ApiResponse.ok("Logged out"))
            }
        }
    }
}
```

- [ ] **Step 7: Compilar e verificar**

Run: `cd server && ../gradlew compileKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 8: Commit**

```bash
git add -A
git commit -m "feat(server): add user auth with JWT, BCrypt, register/login/refresh/logout"
```

---

### Task 2.3: Migrations V2-V6 — Core domain tables

**Files:**
- Create: `V2__create_patients_and_links.sql`
- Create: `V3__create_clinical_records.sql`
- Create: `V4__create_assessments.sql`
- Create: `V5__create_foods_and_measures.sql`
- Create: `V6__create_plans_and_structure.sql`

- [ ] **Step 1: V2 — Patients**

```sql
-- V2__create_patients_and_links.sql
CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    sex VARCHAR(20) NOT NULL,
    date_of_birth DATE NOT NULL,
    primary_goal VARCHAR(255),
    dietary_restrictions TEXT,
    clinical_notes TEXT,
    ai_consent BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE nutritionist_patient_links (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nutritionist_id UUID NOT NULL REFERENCES users(id),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(nutritionist_id, patient_id)
);

CREATE TABLE patient_invites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nutritionist_id UUID NOT NULL REFERENCES users(id),
    invite_code VARCHAR(100) NOT NULL UNIQUE,
    patient_name VARCHAR(255) NOT NULL,
    patient_email VARCHAR(255),
    patient_phone VARCHAR(50),
    patient_sex VARCHAR(20) NOT NULL,
    patient_date_of_birth DATE NOT NULL,
    patient_goal VARCHAR(255),
    patient_restrictions TEXT,
    patient_notes TEXT,
    is_used BOOLEAN NOT NULL DEFAULT false,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_patients_user ON patients(user_id);
CREATE INDEX idx_np_links_nutritionist ON nutritionist_patient_links(nutritionist_id);
CREATE INDEX idx_np_links_patient ON nutritionist_patient_links(patient_id);
CREATE INDEX idx_invites_code ON patient_invites(invite_code);
```

- [ ] **Step 2: V3 — Clinical Records**

```sql
-- V3__create_clinical_records.sql
CREATE TABLE clinical_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    chief_complaint TEXT,
    family_history TEXT,
    pathologies TEXT,
    intolerances TEXT,
    allergies TEXT,
    medications TEXT,
    supplementation TEXT,
    bowel_habits TEXT,
    sleep_pattern TEXT,
    physical_activity TEXT,
    water_intake TEXT,
    food_preferences TEXT,
    food_aversions TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE clinical_evolutions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    nutritionist_id UUID NOT NULL REFERENCES users(id),
    date DATE NOT NULL,
    general_notes TEXT,
    plan_adherence TEXT,
    complications TEXT,
    reported_symptoms TEXT,
    adjustments TEXT,
    recommendations TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_clinical_records_patient ON clinical_records(patient_id);
CREATE INDEX idx_clinical_evolutions_patient ON clinical_evolutions(patient_id);
```

- [ ] **Step 3: V4 — Assessments**

```sql
-- V4__create_assessments.sql
CREATE TABLE assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    nutritionist_id UUID NOT NULL REFERENCES users(id),
    date DATE NOT NULL,
    assessment_type VARCHAR(50) NOT NULL DEFAULT 'in-person', -- in-person, self-reported
    weight_kg DECIMAL(6,2),
    height_cm DECIMAL(6,2),
    bmi DECIMAL(5,2),
    waist_cm DECIMAL(6,2),
    hip_cm DECIMAL(6,2),
    abdomen_cm DECIMAL(6,2),
    body_fat_pct DECIMAL(5,2),
    muscle_mass_kg DECIMAL(6,2),
    body_water_pct DECIMAL(5,2),
    clinical_notes TEXT,
    is_draft BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_assessments_patient ON assessments(patient_id);
CREATE INDEX idx_assessments_date ON assessments(patient_id, date);
```

- [ ] **Step 4: V5 — Foods**

```sql
-- V5__create_foods_and_measures.sql
CREATE TABLE foods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(500) NOT NULL,
    category VARCHAR(100),
    calories_per_100g DECIMAL(8,2),
    protein_per_100g DECIMAL(8,2),
    carbs_per_100g DECIMAL(8,2),
    fat_per_100g DECIMAL(8,2),
    fiber_per_100g DECIMAL(8,2),
    sodium_per_100g DECIMAL(8,2),
    -- Micronutrients (nullable — incomplete data is expected)
    calcium_mg DECIMAL(8,2),
    iron_mg DECIMAL(8,2),
    magnesium_mg DECIMAL(8,2),
    potassium_mg DECIMAL(8,2),
    zinc_mg DECIMAL(8,2),
    vitamin_a_mcg DECIMAL(8,2),
    vitamin_c_mg DECIMAL(8,2),
    vitamin_d_mcg DECIMAL(8,2),
    source VARCHAR(100), -- TACO, USDA, manual
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE household_measures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    food_id UUID NOT NULL REFERENCES foods(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL, -- "colher de sopa", "fatia", "unidade"
    grams DECIMAL(8,2) NOT NULL -- conversion factor
);

CREATE INDEX idx_foods_name ON foods USING gin(to_tsvector('portuguese', name));
CREATE INDEX idx_foods_category ON foods(category);
CREATE INDEX idx_household_food ON household_measures(food_id);
```

- [ ] **Step 5: V6 — Plans complete structure**

```sql
-- V6__create_plans_and_structure.sql
CREATE TYPE plan_status AS ENUM ('draft', 'active', 'inactive', 'archived', 'replaced');

CREATE TABLE meal_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    nutritionist_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    objective TEXT,
    status plan_status NOT NULL DEFAULT 'draft',
    start_date DATE,
    end_date DATE,
    general_notes TEXT,
    -- Nutritional goals
    goal_calories DECIMAL(8,2),
    goal_protein_g DECIMAL(8,2),
    goal_carbs_g DECIMAL(8,2),
    goal_fat_g DECIMAL(8,2),
    goal_fiber_g DECIMAL(8,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE diet_variations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_id UUID NOT NULL REFERENCES meal_plans(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL, -- "Padrão", "Dia de treino", "Low carb"
    is_default BOOLEAN NOT NULL DEFAULT false,
    is_patient_accessible BOOLEAN NOT NULL DEFAULT true,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE meals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    variation_id UUID NOT NULL REFERENCES diet_variations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL, -- "Café da Manhã", "Almoço"
    suggested_time TIME,
    sort_order INT NOT NULL DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE meal_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meal_id UUID NOT NULL REFERENCES meals(id) ON DELETE CASCADE,
    food_id UUID REFERENCES foods(id),
    food_name VARCHAR(500) NOT NULL, -- denormalized for display
    quantity_grams DECIMAL(8,2),
    household_measure VARCHAR(100), -- "2 fatias", "1 colher de sopa"
    is_ad_libitum BOOLEAN NOT NULL DEFAULT false, -- "à vontade"
    notes TEXT,
    -- Calculated (denormalized for performance)
    calories DECIMAL(8,2),
    protein_g DECIMAL(8,2),
    carbs_g DECIMAL(8,2),
    fat_g DECIMAL(8,2),
    fiber_g DECIMAL(8,2),
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE authorized_substitutions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meal_item_id UUID NOT NULL REFERENCES meal_items(id) ON DELETE CASCADE,
    food_id UUID REFERENCES foods(id),
    food_name VARCHAR(500) NOT NULL,
    quantity_grams DECIMAL(8,2),
    household_measure VARCHAR(100),
    calories DECIMAL(8,2),
    protein_g DECIMAL(8,2),
    carbs_g DECIMAL(8,2),
    fat_g DECIMAL(8,2),
    notes TEXT
);

CREATE INDEX idx_plans_patient ON meal_plans(patient_id);
CREATE INDEX idx_plans_status ON meal_plans(patient_id, status);
CREATE INDEX idx_variations_plan ON diet_variations(plan_id);
CREATE INDEX idx_meals_variation ON meals(variation_id);
CREATE INDEX idx_items_meal ON meal_items(meal_id);
CREATE INDEX idx_subs_item ON authorized_substitutions(meal_item_id);
```

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "feat(server): add core domain migrations — patients, clinical, assessments, foods, plans"
```

---

### Task 2.4: Migrations V7-V9 — Consumption, AI, Audit

**Files:**
- Create: `V7__create_consumption.sql`
- Create: `V8__create_ai_messages.sql`
- Create: `V9__create_audit.sql`

- [ ] **Step 1: V7 — Consumption**

```sql
-- V7__create_consumption.sql
CREATE TYPE consumption_mode AS ENUM ('full', 'partial', 'with_substitution', 'off_plan');
CREATE TYPE substitution_origin AS ENUM ('authorized', 'ai_suggestion');

CREATE TABLE consumption_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    meal_id UUID REFERENCES meals(id),
    variation_id UUID REFERENCES diet_variations(id),
    date DATE NOT NULL,
    time TIME,
    mode consumption_mode NOT NULL,
    notes TEXT,
    photo_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE consumption_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    record_id UUID NOT NULL REFERENCES consumption_records(id) ON DELETE CASCADE,
    meal_item_id UUID REFERENCES meal_items(id), -- null if off-plan
    food_id UUID REFERENCES foods(id),
    food_name VARCHAR(500) NOT NULL,
    quantity_grams DECIMAL(8,2),
    household_measure VARCHAR(100),
    was_consumed BOOLEAN NOT NULL DEFAULT true,
    is_substitution BOOLEAN NOT NULL DEFAULT false,
    substitution_origin substitution_origin,
    original_food_name VARCHAR(500), -- what was replaced
    is_off_plan BOOLEAN NOT NULL DEFAULT false,
    -- Calculated
    calories DECIMAL(8,2),
    protein_g DECIMAL(8,2),
    carbs_g DECIMAL(8,2),
    fat_g DECIMAL(8,2),
    fiber_g DECIMAL(8,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_consumption_patient_date ON consumption_records(patient_id, date);
CREATE INDEX idx_consumption_items_record ON consumption_items(record_id);
```

- [ ] **Step 2: V8 — AI Messages**

```sql
-- V8__create_ai_messages.sql
CREATE TYPE ai_message_role AS ENUM ('user', 'assistant', 'system');
CREATE TYPE ai_context_type AS ENUM ('substitution', 'chat', 'insight');

CREATE TABLE ai_conversations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    context_type ai_context_type NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE ai_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES ai_conversations(id) ON DELETE CASCADE,
    role ai_message_role NOT NULL,
    content TEXT NOT NULL,
    metadata JSONB, -- context used, model, tokens, etc.
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ai_conversations_patient ON ai_conversations(patient_id);
CREATE INDEX idx_ai_messages_conversation ON ai_messages(conversation_id);
```

- [ ] **Step 3: V9 — Audit**

```sql
-- V9__create_audit.sql
CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    entity_type VARCHAR(100) NOT NULL, -- 'meal_plan', 'meal', 'consumption', etc.
    entity_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL, -- 'create', 'update', 'delete', 'activate', etc.
    previous_state JSONB,
    new_state JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_user ON audit_log(user_id);
CREATE INDEX idx_audit_time ON audit_log(created_at);
```

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat(server): add consumption, AI messages and audit migrations"
```

---

### Task 2.5: Exposed table definitions para todo o domínio

> Criar os Table objects do Exposed para todas as tabelas restantes (V2-V9). Task 2.2 já criou UserTable e RefreshTokenTable.

**Files:**
- Create: `patients/PatientTable.kt`
- Create: `clinical/ClinicalRecordTable.kt`
- Create: `clinical/AssessmentTable.kt`
- Create: `foods/FoodTable.kt`
- Create: `plans/PlanTable.kt` (+ DietVariationTable, MealTable, MealItemTable, SubstitutionTable)
- Create: `consumption/ConsumptionRecordTable.kt`
- Create: `ai/AIMessageTable.kt`
- Create: `audit/AuditTable.kt`

> **Nota para o implementador:** Cada table definition deve espelhar exatamente a migration SQL correspondente. Usar `org.jetbrains.exposed.sql.kotlin.datetime` para tipos de data/hora. Não adicionar lógica — apenas definições de schema.

- [ ] **Step 1:** Criar todos os Table objects seguindo o padrão de UserTable.kt
- [ ] **Step 2:** Compilar: `cd server && ../gradlew compileKotlin` → BUILD SUCCESSFUL
- [ ] **Step 3:** Commit

```bash
git add -A
git commit -m "feat(server): add Exposed table definitions for all domain entities"
```

---

### Task 2.6 — 2.12: Services e Routes para cada módulo

> Cada task segue o padrão: Service (business logic) → Routes (HTTP endpoints) → Test → Commit

| Task | Módulo | Endpoints principais |
|------|--------|---------------------|
| 2.6 | Patients | `POST /patients/invite`, `POST /patients/register`, `GET /patients`, `GET /patients/{id}` |
| 2.7 | Clinical | `GET/POST /patients/{id}/clinical-record`, `GET/POST /patients/{id}/evolutions` |
| 2.8 | Assessments | `GET/POST /patients/{id}/assessments`, `GET /patients/{id}/assessments/compare` |
| 2.9 | Foods | `GET /foods/search?q=`, `GET /foods/{id}`, `GET /foods/{id}/measures` |
| 2.10 | Plans | `POST /patients/{id}/plans`, `PUT /plans/{id}`, `POST /plans/{id}/activate`, `POST /plans/{id}/duplicate`, `GET /plans/{id}/preview` |
| 2.11 | Consumption | `POST /consumption`, `GET /consumption/history?patientId=&date=`, `DELETE /consumption/{id}` |
| 2.12 | AI Gateway | `POST /ai/suggest-substitution`, `POST /ai/chat`, `GET /ai/conversations` |

> Cada task deve seguir o padrão TDD: escrever teste → verificar que falha → implementar → verificar que passa → commit.

---

### Task 2.13: Application.kt — montar tudo

**Files:**
- Modify: `server/src/main/kotlin/com/kotlincrossplatform/nutrivox/Application.kt`

- [ ] **Step 1: Reescrever Application.kt**

```kotlin
package com.kotlincrossplatform.nutrivox

import com.kotlincrossplatform.nutrivox.ai.AIGateway
import com.kotlincrossplatform.nutrivox.ai.AIService
import com.kotlincrossplatform.nutrivox.ai.aiRoutes
import com.kotlincrossplatform.nutrivox.auth.AuthService
import com.kotlincrossplatform.nutrivox.auth.JwtConfig
import com.kotlincrossplatform.nutrivox.auth.authRoutes
import com.kotlincrossplatform.nutrivox.clinical.ClinicalRecordService
import com.kotlincrossplatform.nutrivox.clinical.clinicalRoutes
import com.kotlincrossplatform.nutrivox.consumption.ConsumptionService
import com.kotlincrossplatform.nutrivox.consumption.consumptionRoutes
import com.kotlincrossplatform.nutrivox.foods.FoodService
import com.kotlincrossplatform.nutrivox.foods.foodRoutes
import com.kotlincrossplatform.nutrivox.patients.InviteService
import com.kotlincrossplatform.nutrivox.patients.PatientService
import com.kotlincrossplatform.nutrivox.patients.patientRoutes
import com.kotlincrossplatform.nutrivox.plans.PlanService
import com.kotlincrossplatform.nutrivox.plans.planRoutes
import com.kotlincrossplatform.nutrivox.plugins.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureDatabase()
    configureContentNegotiation()
    configureStatusPages()
    configureAuthentication()

    val jwtConfig = JwtConfig(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        accessTokenExpiration = environment.config.property("jwt.accessTokenExpiration").getString().toLong(),
        refreshTokenExpiration = environment.config.property("jwt.refreshTokenExpiration").getString().toLong()
    )

    val authService = AuthService(jwtConfig)
    val patientService = PatientService()
    val inviteService = InviteService()
    val clinicalService = ClinicalRecordService()
    val foodService = FoodService()
    val planService = PlanService()
    val consumptionService = ConsumptionService()
    val aiGateway = AIGateway(environment.config)
    val aiService = AIService(aiGateway)

    routing {
        authRoutes(authService)

        authenticate("auth-jwt") {
            patientRoutes(patientService, inviteService)
            clinicalRoutes(clinicalService)
            foodRoutes(foodService)
            planRoutes(planService)
            consumptionRoutes(consumptionService)
            aiRoutes(aiService)
        }
    }
}
```

- [ ] **Step 2:** Compilar
- [ ] **Step 3:** Commit

---

# PROJETO 3 — Shared Module (DTOs + Domínio)

> Modelos compartilhados entre frontend e backend.

```
shared/src/commonMain/kotlin/com/kotlincrossplatform/nutrivox/
├── model/
│   ├── User.kt              # UserRole, UserProfile
│   ├── Patient.kt            # PatientSummary, PatientDetail
│   ├── Plan.kt               # PlanSummary, PlanDetail, PlanStatus
│   ├── DietVariation.kt      # DietVariationSummary, DietVariationDetail
│   ├── Meal.kt               # MealSummary, MealDetail
│   ├── MealItem.kt           # MealItemDetail, with substitutions
│   ├── Food.kt               # FoodSummary, FoodDetail, HouseholdMeasure
│   ├── Consumption.kt        # ConsumptionRecord, ConsumptionItem
│   ├── NutritionalGoal.kt    # NutritionalGoal
│   ├── NutrientValues.kt     # Reusable: calories, protein, carbs, fat, fiber
│   ├── Assessment.kt         # AssessmentSummary, AssessmentDetail
│   ├── ClinicalRecord.kt     # ClinicalRecordDetail
│   └── AIMessage.kt          # AIConversation, AIMessage
├── api/
│   ├── AuthApi.kt            # Request/Response DTOs for auth
│   ├── PatientApi.kt         # Request/Response DTOs
│   ├── PlanApi.kt            # Request/Response DTOs
│   ├── ConsumptionApi.kt     # Request/Response DTOs
│   └── AIApi.kt              # Request/Response DTOs
└── util/
    └── NutritionCalculator.kt # Pure calculation functions (shared between client/server)
```

> **Nota:** Usar `kotlinx.serialization` com `@Serializable`. Usar `kotlinx.datetime` para datas. Nenhuma dependência de plataforma.

---

# PROJETO 4 — Frontend (Compose Multiplatform)

> Implementar telas, navegação e integração com backend. Baseado nas telas do Figma.

```
composeApp/src/commonMain/kotlin/com/kotlincrossplatform/nutrivox/
├── App.kt                          # Root composable, theme, navigation host
├── theme/
│   ├── NutrivoxTheme.kt            # Material3 theme with dynamic colors
│   ├── Color.kt                    # Color tokens (theme + AI + semantic)
│   ├── Type.kt                     # Typography scale
│   └── Shape.kt                    # Shape tokens
├── navigation/
│   ├── NavGraph.kt                 # Navigation graph definition
│   ├── PatientNavGraph.kt          # Patient bottom nav tabs
│   └── NutritionistNavGraph.kt     # Nutritionist navigation
├── data/
│   ├── remote/
│   │   ├── ApiClient.kt            # Ktor HttpClient setup
│   │   ├── AuthInterceptor.kt      # JWT token injection
│   │   └── TokenStorage.kt         # Secure token persistence
│   └── repository/
│       ├── AuthRepository.kt
│       ├── PatientRepository.kt
│       ├── PlanRepository.kt
│       ├── ConsumptionRepository.kt
│       ├── FoodRepository.kt
│       └── AIRepository.kt
├── ui/
│   ├── auth/
│   │   ├── LoginScreen.kt          # Figma: 1:1937 (patient) / 1:2608 (nutri)
│   │   ├── OnboardingScreen.kt     # NEW — patient invite onboarding
│   │   └── AuthViewModel.kt
│   ├── patient/
│   │   ├── home/
│   │   │   ├── PatientHomeScreen.kt    # Figma: 1:1736
│   │   │   └── PatientHomeViewModel.kt
│   │   ├── plan/
│   │   │   ├── PlanDetailScreen.kt     # Figma: 1:1350
│   │   │   └── PlanDetailViewModel.kt
│   │   ├── consumption/
│   │   │   ├── ConsumptionScreen.kt    # Figma: 1:2247
│   │   │   └── ConsumptionViewModel.kt
│   │   ├── progress/
│   │   │   ├── ProgressScreen.kt       # Figma: 1:275
│   │   │   └── ProgressViewModel.kt
│   │   ├── chat/
│   │   │   ├── PatientChatScreen.kt    # Figma: 1:2179
│   │   │   └── ChatViewModel.kt
│   │   └── profile/
│   │       ├── PatientProfileScreen.kt # Figma: 1:3445
│   │       └── ProfileViewModel.kt
│   ├── nutritionist/
│   │   ├── dashboard/
│   │   │   ├── DashboardScreen.kt      # Figma: 1:3142
│   │   │   └── DashboardViewModel.kt
│   │   ├── patients/
│   │   │   ├── PatientListScreen.kt    # Figma: 1:3000
│   │   │   └── PatientListViewModel.kt
│   │   ├── patient_detail/
│   │   │   ├── PatientDetailScreen.kt  # Figma: 1:41
│   │   │   ├── PatientPlansTab.kt      # Figma: 1:2692
│   │   │   ├── PatientAssessmentTab.kt # Figma: 1:1585
│   │   │   └── PatientDetailViewModel.kt
│   │   ├── plan_editor/
│   │   │   ├── PlanEditorScreen.kt     # Figma: 1:585
│   │   │   ├── PlanPreviewScreen.kt    # Figma: 1:2011
│   │   │   └── PlanEditorViewModel.kt
│   │   ├── chat/
│   │   │   ├── NutriChatScreen.kt      # Figma: 1:656
│   │   │   └── NutriChatViewModel.kt
│   │   └── settings/
│   │       ├── SettingsScreen.kt       # Figma: 1:1015
│   │       └── SettingsViewModel.kt
│   └── components/
│       ├── MealCard.kt                 # Reusable meal card
│       ├── NutrientProgressBar.kt      # Linear progress for kcal
│       ├── MacroCircle.kt              # Circular progress for P/C/G
│       ├── AIBadge.kt                  # ✨ AI badge
│       ├── AIDisclaimer.kt             # Disclaimer banner
│       ├── StatusChip.kt              # Pendente/Registrado/Parcial
│       ├── VariationSelector.kt        # Chip group for diet variations
│       ├── FoodSearchBar.kt            # Search with results
│       ├── EmptyState.kt               # Illustration + text + CTA
│       └── ErrorState.kt               # Error + retry
```

---

## Ordem de Execução Recomendada

```
SEMANA 1-2: Projeto 1 (Figma) + Projeto 2 Tasks 2.1-2.5 (DB setup + migrations)
SEMANA 3-4: Projeto 2 Tasks 2.6-2.13 (Services + Routes) + Projeto 3 (Shared DTOs)
SEMANA 5-8: Projeto 4 (Frontend — tela por tela, começando por auth → home → plano → consumo → chat)
SEMANA 9-10: Integração end-to-end, polish, testes
```

---

## Decisões de Arquitetura

| Decisão | Escolha | Justificativa |
|---------|---------|---------------|
| ORM | Exposed (DSL mode) | Kotlin-native, type-safe, leve |
| Migrations | Flyway | Standard, confiável, SQL puro |
| Auth | JWT + refresh token | Stateless, escala bem, suporte nativo no Ktor |
| DI | Koin | Leve, Kotlin-first, bom suporte KMP |
| Serialization | kotlinx.serialization | KMP nativo, performante |
| AI Gateway | Backend mediado | Segurança, controle de custo, troca de provider |
| Cálculo nutricional | Função pura no shared | Reutilizado no client (preview) e server (validação) |
| Valores nutricionais nos items | Denormalizados | Performance de leitura; recalculados em write |
| Busca de alimentos | PostgreSQL full-text (tsvector) | Performante, sem dependência externa |
