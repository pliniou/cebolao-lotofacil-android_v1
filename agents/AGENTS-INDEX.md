# 🤖 Cebolão Lotofácil - Unified Agents Registry

**Version**: 2.0  
**Last Updated**: 7 de fevereiro de 2026  
**Status**: Consolidated - Single source of truth for all agents

---

## 📌 Overview

Estrutura **unificada e sem redundâncias** de agentes especializados. Cada agente é responsável por um domínio específico com escopo rigorosamente definido.

**Princípio**: *Código é entrega. Documentação é secundária.*

---

## 🎯 Agentes Disponíveis

### 1️⃣ `android-native-specialist/` 
**Framework Android, Lifecycle, Native APIs, Build System**
- **Escopo**: Activity, Fragment, Services, Permissions, Manifest, Build config, Android Testing
- **NÃO faz**: UI, Database, Business logic, Kotlin idioms, Design

### 2️⃣ `kotlin-expert/`
**Linguagem Kotlin, Coroutines, Idiomas, Refactoring**
- **Escopo**: Null safety, Collections, Extension functions, Coroutines, Refatoração idiomática
- **NÃO faz**: Refactoring arquitetural, UI, Database, Framework, Design

### 3️⃣ `jetpack-compose-developer/`
**Jetpack Compose, Material 3, UI Components, Animations**
- **Escopo**: Composables, Material 3, State UI, Animations, Accessibility (UI), Preview
- **NÃO faz**: ViewModel logic, Data fetching, Business rules, Design tokens, Framework

### 4️⃣ `backend-data-engineer/`
**Room Database, Retrofit, Offline-First, Data Persistence**
- **Escopo**: Entities, DAOs, API definitions, Migrations, Offline-first, DataStore
- **NÃO faz**: UI, Business logic, Framework setup, Kotlin refactoring, Design

### 5️⃣ `architecture-expert/`
**Clean Architecture, Domain Layer, MVVM, UseCases, Padrões**
- **Escopo**: Domain models, Repository interfaces, UseCases, ViewModel structure, DI, Error handling
- **NÃO faz**: UI implementation, Database details, Kotlin refactoring, Framework, Design

### 6️⃣ `mobile-design-specialist/`
**Design System, Design Tokens, Acessibilidade, Material 3**
- **Escopo**: Design tokens, Colors, Typography, Spacing, Dark mode, WCAG compliance
- **NÃO faz**: Composable implementation, Business logic, Kotlin code, Database, Framework

---

## 🚀 Como Usar

### Invocação Básica (VSCode Chat)
```
@android-native-specialist [SUA TAREFA]
```

### Formato Recomendado
```
@[AGENT_NAME] [TASK_DESCRIPTION]

Context: [Contexto adicional]
Files: [Arquivos específicos se houver]
Requirements: [Requisitos específicos]
```

### Exemplos

#### ANDROID-NATIVE-SPECIALIST
```
@android-native-specialist Setup runtime permissions for camera

Context: Implementing photo upload feature
Requirements:
- ActivityResultContracts
- Graceful permission denial
- AndroidManifest.xml update
```

#### KOTLIN-EXPERT
```
@kotlin-expert Refactor GenerateGamesUseCase with idiomatic Kotlin

Requirements:
- Use scope functions
- Optimize loops with sequences
- Improve null safety
```

#### JETPACK-COMPOSE-DEVELOPER
```
@jetpack-compose-developer Create UserStatsCard composable

Requirements:
- Material 3 styling
- Dark mode support
- Fade-in animation
```

#### BACKEND-DATA-ENGINEER
```
@backend-data-engineer Implement Room migration for new column

Context: Adding PIN feature
Requirements:
- Safe data migration
- Preserve existing data
- Update entity and DAO
```

#### ARCHITECTURE-EXPERT
```
@architecture-expert Design UserStats feature architecture

Requirements:
- Domain models
- Repository interface
- UseCases
- ViewModel skeleton
- Hilt DI config
```

#### MOBILE-DESIGN-SPECIALIST
```
@mobile-design-specialist Define spacing tokens system

Requirements:
- Create xs, sm, md, lg, xl tokens
- Audit WCAG AA compliance
- Document dark mode colors
```

---

## 🔧 Configurações Multi-CLI

Cada agente suporta múltiplos CLIs:
- **OpenAI/ChatGPT** → `openai.yaml`
- **Anthropic/Claude** → `claude.yaml`
- **Google Gemini** → `gemini.yaml`
- **GitHub Codex** → `codex.yaml`

Intercambie agentes mantendo configurações consistentes.

---

## ✅ Política de Entrega

- **Código func funcional compilando** ✅
- **Sem lint warnings**  ✅
- **Seguindo padrões do projeto** ✅
- **Testes passando** ✅
- **Sem documentação .md espontânea** ❌

Código é a entrega. Ponto final.

---

## 📂 Estrutura

```
agents/
├── AGENTS-INDEX.md (este arquivo)
├── android-native-specialist/
│   ├── MANIFEST.md
│   ├── openai.yaml
│   ├── claude.yaml
│   ├── gemini.yaml
│   └── codex.yaml
├── kotlin-expert/
├── jetpack-compose-developer/
├── backend-data-engineer/
├── architecture-expert/
└── mobile-design-specialist/
```

Cada agente é **independente, completo e auto-suficiente**.

---

## 🔄 Fluxo de Escalação

```
REQUISIÇÃO DO USUÁRIO
    ↓
AGENT → Análise do escopo
    ├─ Se dentro de escopo → Execute
    ├─ Se design/tokens → Escalate para MOBILE-DESIGN-SPECIALIST
    ├─ Se UI/Compose → Escalate para JETPACK-COMPOSE-DEVELOPER
    ├─ Se data/persistence → Escalate para BACKEND-DATA-ENGINEER
    ├─ Se kotlin/idioms → Escalate para KOTLIN-EXPERT
    ├─ Se framework/native → Escalate para ANDROID-NATIVE-SPECIALIST
    └─ Se arquitetura/domínio → Escalate para ARCHITECTURE-EXPERT
    ↓
CÓDIGO COMPILANDO
```

---

## ✨ Princípios

1. **Single Responsibility** - Cada agente tem escopo claro e bem-definido
2. **No Redundancy** - Uma verdade única para cada conceito
3. **Code-First** - Entrega é código, não documentação
4. **Multi-CLI** - Funciona com VSCode, ChatGPT, Gemini, Codex
5. **Interchangeable** - Trocar de agente sem perder configuração
