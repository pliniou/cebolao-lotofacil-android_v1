# 🚀 Integration Guide - Unified Agents System

**Version**: 2.0  
**Last Updated**: 7 de fevereiro de 2026

---

## Quick Start

### Invoke an Agent (VSCode Chat)

```bash
@android-native-specialist Setup runtime permissions for camera
@kotlin-expert Refactor GenerateGamesUseCase with idiomatic Kotlin
@jetpack-compose-developer Create UserStatsCard composable
@backend-data-engineer Implement Room migration for new column
@architecture-expert Design UserStats feature architecture
@mobile-design-specialist Define spacing tokens system
```

---

## Multi-CLI Configuration

Each agent is configured for multiple CLIs with consistent settings:

### Supported CLIs
- **OpenAI/ChatGPT** → Uses `openai.yaml` config
- **Anthropic/Claude** → Uses `claude.yaml` config
- **Google Gemini** → Uses `gemini.yaml` config
- **GitHub Codex** → Uses `codex.yaml` config

### How to Switch CLIs

Each agent folder contains 4 YAML files with identical scope but different model configurations:

```
agents/android-native-specialist/
├── MANIFEST.md          ← Agent definition (model-agnostic)
├── openai.yaml          ← OpenAI/ChatGPT config
├── claude.yaml          ← Claude config
├── gemini.yaml          ← Google Gemini config
└── codex.yaml           ← GitHub Codex config
```

**All configs maintain the same:**
- Expertise scope
- System prompt principles
- Output policies
- Escalation rules

**Different models may have different:**
- Model name (gpt-4, claude-opus, gemini-pro)
- Token limits
- Temperature settings

---

## Request Format

### Basic Format
```
@[AGENT_NAME] [TASK_DESCRIPTION]
```

### Full Format (Recommended)
```
@[AGENT_NAME] [TASK_DESCRIPTION]

Context: [Additional context if needed]
Files: [Specific files if any]
Requirements: [Specific requirements]
```

---

## Agent Reference Matrix

| Agent | Scope | Don't Handle |
|-------|-------|--------------|
| **android-native-specialist** | Framework, lifecycle, native APIs, build system | UI, DB, business logic, Kotlin, design |
| **kotlin-expert** | Idioms, null safety, coroutines, refactoring | Architecture, UI, DB, framework, design |
| **jetpack-compose-developer** | Composables, Material 3, UI state, animations | ViewModel logic, data fetching, design tokens, framework |
| **backend-data-engineer** | Room, Retrofit, migrations, offline-first | UI, business logic, framework, Kotlin, design |
| **architecture-expert** | Clean Architecture, domain models, UseCase, DI | UI details, DB details, Kotlin idioms, framework, design |
| **mobile-design-specialist** | Design tokens, Material 3, accessibility, WCAG | Composable implementation, business logic, code, DB, framework |

---

## Escalation Flow

```text
REQUEST
  │
  ├─ Framework/Native Android? → ANDROID-NATIVE-SPECIALIST
  ├─ Kotlin idioms/refactoring? → KOTLIN-EXPERT
  ├─ UI/Composables? → JETPACK-COMPOSE-DEVELOPER
  ├─ Database/Retrofit? → BACKEND-DATA-ENGINEER
  ├─ Architecture/Domain? → ARCHITECTURE-EXPERT
  ├─ Design tokens/WCAG? → MOBILE-DESIGN-SPECIALIST
  └─ Outside all scopes? → Request clarification
```

---

## Output Standards

Every agent delivery must include:
- ✅ Code that compiles without errors
- ✅ Zero new lint warnings
- ✅ Follows project patterns
- ✅ Tests passing (if applicable)
- ❌ NO automatic .md documentation
- ❌ NO execution summaries
- ❌ NO checklist reports

**Code is the deliverable. Documentation is optional.**

---

## Examples

### Example 1: Android Native Setup
```
@android-native-specialist Setup WorkManager for background sync

Context: Need to sync game results every hour
Files: MainActivity.kt, AndroidManifest.xml
Requirements:
- Use ExistingPeriodicWorkPolicy.KEEP
- Handle network failures gracefully
- Add permissions to manifest
```

### Example 2: UI Development
```
@jetpack-compose-developer Create GameResultsScreen

Context: New feature to display lottery results
Requirements:
- Material 3 Card components
- Loading, content, empty, error states
- Dark mode support
- @PreviewLightDark preview
```

### Example 3: Data Layer
```
@backend-data-engineer Add caching strategy for API results

Context: API calls are slow, need local-first approach
Files: LotofacilRepository.kt, GameDao.kt
Requirements:
- Cache results for 1 hour
- Refresh in background
- Return cached data during network error
```

### Example 4: Architecture
```
@architecture-expert Design error handling strategy

Requirements:
- Create AppError sealed class hierarchy
- Map API errors to domain errors
- Propagate errors to UI via ViewModel state
- Document all error types
```

### Example 5: Design System
```
@mobile-design-specialist Define Material 3 color palette

Requirements:
- Dark mode colors based on Material 3
- WCAG AA contrast ratios verified
- Create Color.kt with all tokens
- Document color usage guidelines
```

---

## Configuration File Structure

Each agent folder contains 5 files:

### 1. MANIFEST.md
Agent's mission, scope, tech stack, patterns, constraints.

### 2-5. YAML Config Files
```yaml
interface:
  display_name: "Agent Name"
  short_description: "Brief description"
  version: "2.0"
  expertise_level: "Expert"

system_prompt: |
  [Detailed system prompt]

cli_specific_config:
  model: "[Model name]"
  temperature: 0.2
  max_tokens: [Number]

default_prompt: |
  [Example invocation format]
```

---

## Directory Structure

```
agents/
├── AGENTS-INDEX.md
├── INTEGRATION-GUIDE.md (this file)
├── android-native-specialist/
│   ├── MANIFEST.md
│   ├── openai.yaml
│   ├── claude.yaml
│   ├── gemini.yaml
│   └── codex.yaml
├── kotlin-expert/
│   ├── MANIFEST.md
│   ├── openai.yaml
│   ├── claude.yaml
│   ├── gemini.yaml
│   └── codex.yaml
├── jetpack-compose-developer/
│   ├── MANIFEST.md
│   ├── openai.yaml
│   ├── claude.yaml
│   ├── gemini.yaml
│   └── codex.yaml
├── backend-data-engineer/
│   ├── MANIFEST.md
│   ├── openai.yaml
│   ├── claude.yaml
│   ├── gemini.yaml
│   └── codex.yaml
├── architecture-expert/
│   ├── MANIFEST.md
│   ├── openai.yaml
│   ├── claude.yaml
│   ├── gemini.yaml
│   └── codex.yaml
└── mobile-design-specialist/
    ├── MANIFEST.md
    ├── openai.yaml
    ├── claude.yaml
    ├── gemini.yaml
    └── codex.yaml
```

---

## Key Principles

1. **Single Agent State of Truth** - Each agent has one MANIFEST.md
2. **Multi-CLI Support** - Same scope, multiple model configs
3. **Clear Escalation** - Every agent knows when to escalate
4. **Code-Driven Delivery** - Output is code, not documentation
5. **Interchangeable Agents** - Swap models without reconfiguration
6. **No Redundancy** - One definition per concept

---

## Troubleshooting

### Agent Won't Fix My Issue
Check the "Don't Handle" column in the matrix above. If outside scope, you're talking to the wrong agent.

### Agent is Generating Documentation
Remind it: "Entrega é código. Sem .md automático."

### Switching Between CLIs
All agents support all CLIs. Just use the agent name - the CLI you're using will decide which config file to load.

### Need Custom Configuration
Edit the appropriate YAML file in the agent folder. All 4 CLI versions must stay consistent.

---

## Maintenance

**DO**:
- Keep MANIFEST.md and all YAML files in sync
- Update version numbers when changing scope
- Keep escalation rules consistent

**DON'T**:
- Create new agent files for edge cases
- Mix responsibilities between agents
- Create redundant agent definitions
