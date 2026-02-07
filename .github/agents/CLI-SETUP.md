# 🔧 CLI Setup & Integration Guide

**Version**: 1.0  
**Last Updated**: 7 de fevereiro de 2026  

---

## 📋 Overview

Este guide explica como integrar os **agentes especializados** com sua CLI e como invocá-los de forma eficiente.

---

## 🚀 Quick Start

### Invocação Básica via GitHub Copilot Chat

#### Option 1: Via VSCode Chat Panel (Recomendado)

```
Abra: Ctrl+Shift+I (ou Cmd+Shift+I no Mac)
Type: @JETPACK-COMPOSE-DEVELOPER Create UserStats screen with Material 3...
```

#### Option 2: Via Chat Box Input

```
@KOTLIN-EXPERT Refactor GenerateGamesUseCase using better Kotlin idioms
```

#### Option 3: Mencionar Agent via Workspace

```
/agents/JETPACK-COMPOSE-DEVELOPER.agent.md
Implement StatisticsCard composable...
```

---

## 📝 Formato de Requisição Padrão

### Estrutura

```
@[AGENT_NAME] [TASK_DESCRIPTION]

Context: [Contexto adicional se necessário]
Files: [Arquivos específicos to modify, if any]
Requirements: [Reqs específicos]
```

### Exemplos Prontos para Copy-Paste

#### 1️⃣ ANDROID-NATIVE-SPECIALIST

```
@ANDROID-NATIVE-SPECIALIST Setup runtime permissions for camera in MainActivity

Context: Implementing camera feature for game photo upload
Files: app/src/main/java/com/cebolao/lotofacil/MainActivity.kt
Requirements:
- Use ActivityResultContracts
- Handle permission denial gracefully
- Add to AndroidManifest.xml
```

#### 2️⃣ KOTLIN-EXPERT

```
@KOTLIN-EXPERT Refactor GenerateGamesUseCase with better Kotlin idioms

Context: Current implementation is functional but not idiomatic
Files: app/src/main/java/com/cebolao/lotofacil/domain/usecase/GenerateGamesUseCase.kt
Requirements:
- Use scope functions (let, apply)
- Optimize loops with sequences where appropriate
- Improve null safety
```

#### 3️⃣ JETPACK-COMPOSE-DEVELOPER

```
@JETPACK-COMPOSE-DEVELOPER Create StatisticsCard composable with animations

Context: New feature showing game statistics
Files: app/src/main/java/com/cebolao/lotofacil/ui/components/StatisticsCard.kt
Requirements:
- Material 3 Card component
- Dark mode support (@PreviewLightDark)
- Accessibility labels (contentDescription)
- Fade-in animation on load
```

#### 4️⃣ BACKEND-DATA-ENGINEER

```
@BACKEND-DATA-ENGINEER Create Room migration for isPinned column

Context: Adding ability to pin important games
Files: app/src/main/java/com/cebolao/lotofacil/data/database/
Requirements:
- Safe migration (preserves data)
- Update SavedGameEntity.kt
- Add migration_1_2.sql
- Update AppDatabase version
```

#### 5️⃣ ARCHITECTURE-EXPERT

```
@ARCHITECTURE-EXPERT Design UserStats feature with Clean Architecture

Context: New feature to show user game statistics
Requirements:
- Domain models (UserStats.kt)
- Repository interface (UserStatsRepository.kt)
- UseCases (GetUserStatsUseCase.kt, UpdateUserStatsUseCase.kt)
- ViewModel skeleton (UserStatsViewModel.kt)
- Hilt DI configuration
```

#### 6️⃣ MOBILE-DESIGN-SPECIALIST

```
@MOBILE-DESIGN-SPECIALIST Define spacing tokens and verify WCAG compliance

Context: Establishing consistent spacing system
Requirements:
- Create AppSpacing tokens (xs, sm, md, lg, xl)
- Audit WCAG AA contrast for all colors
- Validate dark mode colors
- Document in DESIGN_SYSTEM.md
```

---

## 🔄 Advanced: Chaining Agents

### Sequential Execution (Recomendado)

Quando múltiplos agentes precisam trabalhar em sequência:

```
Step 1: ARCHITECTURE-EXPERT designs feature
@ARCHITECTURE-EXPERT Design UserStats feature... [ver exemplo acima]

[Aguarde deliverable completo]

Step 2: BACKEND-DATA-ENGINEER implements data layer
@BACKEND-DATA-ENGINEER Implement UserStatsRepositoryImpl with Room

Context: Architecture-Expert designed UserStats feature
- Domain models created (in domain/model/UserStats.kt)
- Repository interface at domain/repository/UserStatsRepository.kt
Files: 
  - app/src/main/java/com/cebolao/lotofacil/data/repository/UserStatsRepositoryImpl.kt
  - app/src/main/java/com/cebolao/lotofacil/data/database/entity/UserStatsEntity.kt
  - app/src/main/java/com/cebolao/lotofacil/data/database/dao/UserStatsDao.kt

[Aguarde data layer completo]

Step 3: JETPACK-COMPOSE-DEVELOPER implements UI
@JETPACK-COMPOSE-DEVELOPER Create UserStatsScreen with ViewModel integration

Context: Architecture and data layers completed
- Repository implemented and available for injection
- ViewModel skeleton ready
Files:
  - app/src/main/java/com/cebolao/lotofacil/ui/screens/UserStatsScreen.kt
  - app/src/main/java/com/cebolao/lotofacil/viewmodels/UserStatsViewModel.kt (complete)
```

---

## 🛡️ Safety Guards & Best Practices

### 1. Sempre Provide Context
```
❌ BAD: @JETPACK-COMPOSE-DEVELOPER Create a button
✅ GOOD: @JETPACK-COMPOSE-DEVELOPER Create confirmation dialog with Material 3 style for game deletion
```

### 2. Specify Files Explicitly
```
❌ BAD: Fix the data layer
✅ GOOD: Update data layer - Files: HistoricalDrawRepositoryImpl.kt, HistoricalDrawDao.kt
```

### 3. Include Requirements
```
❌ BAD: @KOTLIN-EXPERT Optimize the code
✅ GOOD: @KOTLIN-EXPERT Optimize coroutine handling using withContext pattern
         Requirements: Ensure Main dispatcher for UI updates
```

### 4. Validate Before Escalating
```
After agent delivers:
1. Check if build succeeds: ./gradlew build
2. Check lint: ./gradlew lint
3. Check tests: ./gradlew test
4. Check changes compile locally
5. THEN escalate to next agent
```

---

## 🎯 Using Agent Selection Matrix

From [INDEX.md](./INDEX.md), use this decision tree:

```
My task is about:
├─ UI Composables → JETPACK-COMPOSE-DEVELOPER
├─ Database/Network → BACKEND-DATA-ENGINEER
├─ Feature Structure → ARCHITECTURE-EXPERT
├─ Kotlin Idioms → KOTLIN-EXPERT
├─ Framework/Lifecycle → ANDROID-NATIVE-SPECIALIST
└─ Design/Colors/A11y → MOBILE-DESIGN-SPECIALIST
```

---

## 📊 Expected Workflow

### Daily Workflow Example

```
[ Day 1: Feature Design ]
@ARCHITECTURE-EXPERT Design NewFeature
Status: ✅ Architecture documented
→ Next: BACKEND-DATA-ENGINEER

[ Day 2: Data Implementation ]
@BACKEND-DATA-ENGINEER Implement data layer for NewFeature
Status: ✅ Repository + DAOs + Migrations
→ Next: JETPACK-COMPOSE-DEVELOPER

[ Day 3: UI Implementation ]
@JETPACK-COMPOSE-DEVELOPER Create screens for NewFeature
Status: ✅ Composables with previews
→ Next: KOTLIN-EXPERT (if refactoring needed)

[ Day 4: Polish ]
@KOTLIN-EXPERT Optimize ViewModel coroutines
@MOBILE-DESIGN-SPECIALIST Verify WCAG AA + dark mode
Status: ✅ Feature complete
```

---

## 🔍 Monitoring Agent Output

### What to Look For

```markdown
✅ GOOD Agent Output:
- Clearspecific deliverables
- Checklist all checked
- No escalation needed (or clear escalation path)
- Code compiles
- Tests pass
- Follows project patterns

⚠️ QUESTIONABLE Output:
- Vague deliverables
- Incomplete checklist
- Multiple escalations (should be max 1-2)
- Build warnings
- Tests failing
- Deviates from project patterns

❌ BAD Output:
- No deliverables section
- Agent exceeded scope
- Contradicts existing code
- No testing
- Breaks compilation
```

---

## 🚨 Troubleshooting

### "Agent went out of scope"

**Problem**: Agent made changes outside their specialty.

**Solution**:
1. Revert changes (`git checkout -- modified-files`)
2. Invoke correct agent with clear scope
3. Report in issue for feedback

### "Build fails after agent work"

**Problem**: Compilation errors after agent delivered.

**Solution**:
1. Run `./gradlew clean build`
2. Check error messages
3. Ask same agent to fix (it's their responsibility)
4. If out of scope, ask appropriate agent

### "Incomplete delivery"

**Problem**: Agent didn't complete task fully.

**Solution**:
1. Check deliverable checklist - is it really incomplete?
2. Ask agent to complete remaining items
3. Be specific: "Item X in checklist incomplete. Please finish."

### "Agent escalated but unclear path"

**Problem**: Escalation reason confusing.

**Solution**:
1. Re-read agent's escalation context
2. Consult [INDEX.md](./INDEX.md) for correct agent
3. Invoke next agent with context from escalation

---

## 🔗 Integration with Existing Tools

### With VSCode

Each agent's document is VSCode-searchable:

```
Ctrl+P → agents/JETPACK-COMPOSE
→ shows all agent references in Explorer
```

### With Git

Tag commits with agent name:

```bash
git commit -m "feat: UserStats screen [via JETPACK-COMPOSE-DEVELOPER]"
git commit -m "refactor: GenerateGamesUseCase idioms [via KOTLIN-EXPERT]"
```

### With Issues/PRs

Reference agent in issue discussions:

```markdown
This task requires:
1. @ARCHITECTURE-EXPERT to design feature
2. @BACKEND-DATA-ENGINEER for data layer
3. @JETPACK-COMPOSE-DEVELOPER for UI

See .github/agents/INDEX.md for full context.
```

---

## 📈 Scaling with Multiple Team Members

If working as a team:

```
Team Member A: 🏗️ ARCHITECTURE-EXPERT
┌─ Designs features
│  └─ Pass to Team B

Team Member B: 🗄️ BACKEND-DATA-ENGINEER  
┌─ Implements data
│  └─ Pass to Team C

Team Member C: 🎨 JETPACK-COMPOSE-DEVELOPER
┌─ Implements UI
│  └─ Pass to Team D

Team Member D: 🎯 KOTLIN-EXPERT + 🎨 DESIGN
└─ Optimize + Polish
```

Each member invokes their agents consistently.

---

## ✅ Pre-Invocation Checklist

Before asking an agent, verify:

- [ ] This task matches their specialty (check INDEX.md)
- [ ] I provided clear context
- [ ] I specified which files are involved
- [ ] I listed specific requirements
- [ ] Code compiles before (baseline)
- [ ] I'm not mixing multiple responsibilities
- [ ] Previous agent's work (if escalation) is complete

---

## 🚀 Pro Tips

1. **Be Specific** - "Create StatisticsCard composable" > "Create a card"
2. **Provide Files** - Exact paths help agents orient quickly
3. **Chain Systematically** - Don't jump between agents
4. **Verify Locally** - `./gradlew build` before escalating
5. **Check Results** - Verify code compiles and follows patterns
6. **Focus Code** - Expect code, not documentation
7. **Version Control** - Commit after each agent's work (git checkpoints)

---

## 🎯 Command Examples

### (Copy-Paste Ready)

```bash
# Start new feature design
# Paste into VSCode Chat:
@ARCHITECTURE-EXPERT Design UserStats feature with:
- Domain model (UserStats.kt)
- Repository interface
- 2 UseCases (Get + Update)
- ViewModel structure
- Hilt DI configuration

Requirements:
- Follow existing patterns (see GenerateGamesUseCase)
- Use sealed class for states
- Include error handling
```

```bash
# Implement data layer
@BACKEND-DATA-ENGINEER Implement UserStatsRepositoryImpl with:
- Room entity + Dao
- Retrofit API endpoint
- LocalDataSource implementation
- Offline-first sync pattern

Files:
- data/repository/UserStatsRepositoryImpl.kt
- data/database/entity/UserStatsEntity.kt
- data/database/dao/UserStatsDao.kt
- data/network/api/UserStatsDto.kt
```

```bash
# Implement UI
@JETPACK-COMPOSE-DEVELOPER Create UserStatsScreen with:
- Main composable (stateful)
- Content composable (stateless)
- ViewModel integration
- Material 3 styling
- 2+ @Preview (light + dark)

Requirements:
- Animations on load
- WCAG AA semantics
- Responsive design
```

---

## 📚 Reference

- [INDEX.md](./INDEX.md) - Agent decision matrix
- [DELIVERABLES.md](./DELIVERABLES.md) - Output format expected
- [ANDROID-NATIVE-SPECIALIST.agent.md](./ANDROID-NATIVE-SPECIALIST.agent.md)
- [KOTLIN-EXPERT.agent.md](./KOTLIN-EXPERT.agent.md)
- [JETPACK-COMPOSE-DEVELOPER.agent.md](./JETPACK-COMPOSE-DEVELOPER.agent.md)
- [BACKEND-DATA-ENGINEER.agent.md](./BACKEND-DATA-ENGINEER.agent.md)
- [ARCHITECTURE-EXPERT.agent.md](./ARCHITECTURE-EXPERT.agent.md)
- [MOBILE-DESIGN-SPECIALIST.agent.md](./MOBILE-DESIGN-SPECIALIST.agent.md)

---

**🎯 Happy Agent Collaboration! Use specific, structured requests for best results.** 🚀
