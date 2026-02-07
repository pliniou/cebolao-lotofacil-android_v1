# 📑 Agents Registry & Selection Guide

**Version**: 1.0  
**Last Updated**: 7 de fevereiro de 2026  

---

## 🎯 Agents Disponíveis

Este é o **registry centralizado** de todos os agentes especializados disponíveis para o projeto **Cebolão Lotofácil**.

Cada agente tem escopo rigorosamente definido para evitar sobreposição de trabalho e garantir qualidade de entrega.

---

## 📋 Lista de Agentes

### 1. 🔧 [ANDROID-NATIVE-SPECIALIST](./ANDROID-NATIVE-SPECIALIST.agent.md)

**Responsabilidade**: Framework Android, Lifecycle, Integração nativa

**Quando usar**:
- ✅ Problema com Activity/Fragment lifecycle
- ✅ Integração com APIs nativas (Bluetooth, NFC, Camera)
- ✅ Configuração de Manifest
- ✅ Build system (Gradle, AGP)
- ✅ Permissions & Security
- ✅ Android Testing (Espresso)

**Quando NÃO usar**:
- ❌ UI/Composables → Use JETPACK-COMPOSE-DEVELOPER
- ❌ Banco de dados → Use BACKEND-DATA-ENGINEER
- ❌ Lógica de negócio → Use ARCHITECTURE-EXPERT
- ❌ Kotlin idioms → Use KOTLIN-EXPERT
- ❌ Design system → Use MOBILE-DESIGN-SPECIALIST

**Exemplo de tarefa**:
```
"Setup runtime permissions for camera access in MainActivity"
"Configure NetworkSecurityConfig for API certificate pinning"
"Add WorkManager for background sync"
```

---

### 2. 🎯 [KOTLIN-EXPERT](./KOTLIN-EXPERT.agent.md)

**Responsabilidade**: Linguagem Kotlin, Coroutines, Idiomas, Refactoring puro

**Quando usar**:
- ✅ Refinar idiomas Kotlin (scope functions, sealed classes)
- ✅ Otimizar Coroutines (dispatcher, context switching)
- ✅ Collections optimization (Sequence vs List)
- ✅ Extension functions & DSLs
- ✅ Null safety improvements
- ✅ Small refactorings (1-2 funções)

**Quando NÃO usar**:
- ❌ Refactoring arquitetural grande → Use ARCHITECTURE-EXPERT
- ❌ UI components → Use JETPACK-COMPOSE-DEVELOPER
- ❌ Database → Use BACKEND-DATA-ENGINEER
- ❌ Framework integration → Use ANDROID-NATIVE-SPECIALIST
- ❌ Design → Use MOBILE-DESIGN-SPECIALIST

**Exemplo de tarefa**:
```
"Refactor GenerateGamesUseCase to use better Kotlin idioms"
"Optimize Flow collection to use sequences where appropriate"
"Add proper null safety to repository implementations"
```

---

### 3. 🎨 [JETPACK-COMPOSE-DEVELOPER](./JETPACK-COMPOSE-DEVELOPER.agent.md)

**Responsabilidade**: Jetpack Compose, Material 3, UI Components, Animations

**Quando usar**:
- ✅ Criar/refatorer Composables
- ✅ Material 3 components & colors
- ✅ State management em UI (remember, StateFlow)
- ✅ Layouts (Column, Row, LazyColumn)
- ✅ Animations & transitions
- ✅ Preview & @PreviewLightDark
- ✅ Acessibilidade UI (semantics, contentDescription)

**Quando NÃO usar**:
- ❌ ViewModel logic → Use ARCHITECTURE-EXPERT
- ❌ Data fetching → Use BACKEND-DATA-ENGINEER
- ❌ Design tokens → Use MOBILE-DESIGN-SPECIALIST
- ❌ Kotlin refactoring → Use KOTLIN-EXPERT
- ❌ Framework setup → Use ANDROID-NATIVE-SPECIALIST

**Exemplo de tarefa**:
```
"Create StatisticsCard composable with animations"
"Refactor HomeScreen to use ViewModel state correctly"
"Add dark mode support to all screens"
"Implement accessible form inputs with screen reader labels"
```

---

### 4. 🗄️ [BACKEND-DATA-ENGINEER](./BACKEND-DATA-ENGINEER.agent.md)

**Responsabilidade**: Room Database, Retrofit, Offline-First, Data Persistence

**Quando usar**:
- ✅ Room entities, DAOs, queries
- ✅ Retrofit API definitions
- ✅ Network interceptors & error handling
- ✅ Database migrations
- ✅ Offline-first implementation
- ✅ DataStore preferences
- ✅ Data source implementations

**Quando NÃO usar**:
- ❌ UI implementation → Use JETPACK-COMPOSE-DEVELOPER
- ❌ Business logic → Use ARCHITECTURE-EXPERT
- ❌ Framework setup → Use ANDROID-NATIVE-SPECIALIST
- ❌ Kotlin refactoring → Use KOTLIN-EXPERT
- ❌ Design → Use MOBILE-DESIGN-SPECIALIST

**Exemplo de tarefa**:
```
"Create Room migration for adding new column to draws table"
"Implement offline-first sync for draw history"
"Setup Retrofit interceptor for auth token handling"
"Add database query performance optimization"
```

---

### 5. 🏗️ [ARCHITECTURE-EXPERT](./ARCHITECTURE-EXPERT.agent.md)

**Responsabilidade**: Clean Architecture, Domain Layer, MVVM, UseCases, Padrões

**Quando usar**:
- ✅ Design de nova feature (estrutura)
- ✅ Criar UseCases
- ✅ Definir Repository interfaces
- ✅ ViewModel setup com StateFlow
- ✅ Error handling strategy
- ✅ Refactoring estrutural grande
- ✅ DI configuration (Hilt)
- ✅ Domain models

**Quando NÃO usar**:
- ❌ UI implementation → Use JETPACK-COMPOSE-DEVELOPER
- ❌ Database implementation → Use BACKEND-DATA-ENGINEER
- ❌ Kotlin idioms → Use KOTLIN-EXPERT
- ❌ Framework integration → Use ANDROID-NATIVE-SPECIALIST
- ❌ Design tokens → Use MOBILE-DESIGN-SPECIALIST

**Exemplo de tarefa**:
```
"Design UserStats feature with proper architecture"
"Create GetUserStatsUseCase with validation"
"Refactor Authentication flow using proper patterns"
"Setup error handling hierarchy for app"
```

---

### 6. 🎨 [MOBILE-DESIGN-SPECIALIST](./MOBILE-DESIGN-SPECIALIST.agent.md)

**Responsabilidade**: Design System, Design Tokens, Acessibilidade, Material 3

**Quando usar**:
- ✅ Definir design tokens (colors, spacing, typography)
- ✅ Material 3 color scheme
- ✅ Dark mode strategy
- ✅ Acessibilidade audit (WCAG AA/AAA)
- ✅ Responsive design strategy
- ✅ Animation/motion design
- ✅ Icon system
- ✅ Design system documentation

**Quando NÃO usar**:
- ❌ Composable implementation → Use JETPACK-COMPOSE-DEVELOPER
- ❌ Business logic → Use ARCHITECTURE-EXPERT
- ❌ Kotlin code → Use KOTLIN-EXPERT
- ❌ Database → Use BACKEND-DATA-ENGINEER
- ❌ Framework → Use ANDROID-NATIVE-SPECIALIST

**Exemplo de tarefa**:
```
"Define spacing tokens for consistent padding/margins"
"Audit WCAG compliance for new designs"
"Create dark mode color palette matching Material 3"
"Design responsive layout for tablet screens"
```

---

## 🔀 Fluxo de Escalação Entre Agentes

```
USER REQUEST
    ↓
┌─────────────────────────────────────┐
│ ARCHITECTURE-EXPERT:                │
│ Qual camada está envolvida?         │
│ - Presentation → JETPACK-COMPOSE    │
│ - Domain → ARCHITECTURE-EXPERT      │
│ - Data → BACKEND-DATA-ENGINEER      │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ JETPACK-COMPOSE-DEVELOPER:          │
│ UI Composables + State Management    │
│ ↓ Precisa dados? → BACKEND-DATA-ENG │
│ ↓ Precisa negócio? → ARCHITECTURE   │
│ ↓ Precisa design? → MOBILE-DESIGN   │
└─────────────────────────────────────┘
```

---

## 📊 Matriz de Seleção Rápida

| Problema | Agente | Por quê |
|----------|--------|--------|
| "ActivityResult callback falha" | ANDROID-NATIVE | Lifecycle Android |
| "Refine GenerateGamesUseCase com Kotlin idioms" | KOTLIN-EXPERT | Language idioms |
| "Criar novo Composable para stats" | JETPACK-COMPOSE | UI implementation |
| "Setup Room migration para novo campo" | BACKEND-DATA-ENG | Schema change |
| "Design feature UserStats" | ARCHITECTURE | Camada domain + structure |
| "Definir colors para dark mode" | MOBILE-DESIGN | Design system |

---

## 🎬 Seu Fluxo de Trabalho

### Passo 1: Identifique o Problema
```
"Preciso criar uma feature que mostra estatísticas de jogos..."
```

### Passo 2: Consulte a Matriz
```
→ Feature design = ARCHITECTURE-EXPERT
```

### Passo 3: Invoque o Agente
```
"ARCHITECTURE-EXPERT: Design UserStats feature com 
    Domain model, UseCase, Repository interface"
```

### Passo 4: Arquiteto Entrega Estrutura
```
✅ Domain models criados
✅ UserStatsRepository interface
✅ GetUserStatsUseCase blueprint
✅ Hilt DI configuration
→ Próximo: BACKEND-DATA-ENGINEER para implementação
```

### Passo 5: Escalade para Especialista de Dados
```
"BACKEND-DATA-ENGINEER: Implementar UserStatsRepositoryImpl
    com Room entities e Retrofit API integration"
```

### Passo 6: Escalade para UI Developer
```
"JETPACK-COMPOSE-DEVELOPER: Criar UserStatsScreen 
    com Composables e ViewModel integration"
```

### Passo 7: Design Refinement
```
"MOBILE-DESIGN-SPECIALIST: Garantir WCAG AA e 
    dark mode compliance"
```

---

## 💡 Dicas de Uso

### ✅ Use Como CERTO
- Invoque um agente por vez
- Deixe agentes escalarem quando necessário
- Use o INDEX para decidir qual agente chamar
- Espere código compilando, não documentação

### ❌ Evite
- Chamar JETPACK-COMPOSE para criar UseCase (errado!)
- Pedir KOTLIN-EXPERT para implementar UI (escopo incorreto)
- Misturar múltiplas responsabilidades numa chamada
- Ignorar quando agente der escalation path

---

## 📞 Quando Pedir Escalação

Cada agente está configurado para **reconhecer quando seu escopo foi excedido** e automaticamente sugerir escalação:

```
⚠️ ESCALATION: BACKEND-DATA-ENGINEER
Reason: This requires database schema changes
File: data/repository/GameRepositoryImpl.kt
Context: Created ViewModel, need data layer setup
```

### Você deve:
1. ✅ Ler recomendação de escalação
2. ✅ Chamar agente sugerido
3. ✅ Passar contexto/detalhes do passo anterior

---

## 🔧 Integração com CLI

Ver [CLI-SETUP.md](./CLI-SETUP.md) para instruções completas de:
- Como invocar agentes via command line
- Aliases sugeridos
- Exemplos prontos para copy-paste
- Troubleshooting

---

## 📚 Referências

- [ANDROID-NATIVE-SPECIALIST.agent.md](./ANDROID-NATIVE-SPECIALIST.agent.md)
- [KOTLIN-EXPERT.agent.md](./KOTLIN-EXPERT.agent.md)
- [JETPACK-COMPOSE-DEVELOPER.agent.md](./JETPACK-COMPOSE-DEVELOPER.agent.md)
- [BACKEND-DATA-ENGINEER.agent.md](./BACKEND-DATA-ENGINEER.agent.md)
- [ARCHITECTURE-EXPERT.agent.md](./ARCHITECTURE-EXPERT.agent.md)
- [MOBILE-DESIGN-SPECIALIST.agent.md](./MOBILE-DESIGN-SPECIALIST.agent.md)
- [DELIVERABLES.md](./DELIVERABLES.md) - Política de entrega
- [CLI-SETUP.md](./CLI-SETUP.md) - Guia de integração

---

## ✨ Summary

| Agent | Specialty | Max Tokens | Max Files |
|-------|-----------|-----------|-----------|
| 🔧 Android Native | Framework, Lifecycle, Native APIs | 1000 | 3 |
| 🎯 Kotlin Expert | Language, Coroutines, Idioms | 800 | 2 |
| 🎨 Compose Dev | UI, Material 3, Animations | 1200 | 3 |
| 🗄️ Data Engineer | Database, Network, Offline | 1000 | 2 |
| 🏗️ Architect | Clean Arch, Domain, MVVM | 1500 | 5 |
| 🎨 Design Specialist | Design Tokens, A11y, Responsive | 1000 | 3 |

---

**🚀 Pronto para parceria especializada!**

Use este document para navegar entre agentes de forma eficiente.
