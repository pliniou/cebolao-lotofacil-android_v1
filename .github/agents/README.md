# 🤖 Agentes Especializados - Cebolão Lotofácil

**Version**: 1.0  
**Last Updated**: 7 de fevereiro de 2026  

---

## 🎯 Overview

Este projeto utiliziza um **sistema de agentes especializados** para organizar o desenvolvimento de forma eficiente e escalável.

Cada agente é um **especialista** em um aspecto específico do desenvolvimento Android, com escopo rigorosamente definido, breakpoints claros e formato de entrega padronizado.

---

## 📚 Documentação

### 🔍 Onde Começar?

1. **[INDEX.md](./INDEX.md)** ← **COMECE AQUI**
   - Registry de todos os agentes
   - Matriz de seleção rápida (qual agente chamar)
   - Fluxo de escalação entre agentes

2. **[CLI-SETUP.md](./CLI-SETUP.md)**
   - Como invocar agentes via VSCode Chat
   - Exemplos prontos para copy-paste
   - Troubleshooting

3. **[DELIVERABLES.md](./DELIVERABLES.md)**
   - Política de entrega (código, não documentação)
   - Critérios de aceitação simplificados

### 👥 Agentes Disponíveis

#### 1. 🔧 [ANDROID-NATIVE-SPECIALIST](./ANDROID-NATIVE-SPECIALIST.agent.md)
**Framework Android, Lifecycle, Native APIs, Build System**
- Responsabilidade: Tudo específico do Android Framework
- Escopo: Activity, Fragment, Services, Permissions, Manifest, Build config
- Breakpoint: 1000 tokens max, 3 files por sessão

#### 2. 🎯 [KOTLIN-EXPERT](./KOTLIN-EXPERT.agent.md)
**Linguagem Kotlin, Coroutines, Idiomas, Refactoring**
- Responsabilidade: Idiomas Kotlin e best practices de linguagem
- Escopo: Null safety, Collections, Extension functions, Coroutines, Refactorings pequenos
- Breakpoint: 800 tokens max, 2 files por sessão, 1 refactoring

#### 3. 🎨 [JETPACK-COMPOSE-DEVELOPER](./JETPACK-COMPOSE-DEVELOPER.agent.md)
**Jetpack Compose, Material 3, UI Components, Animations**
- Responsabilidade: Implementação visual, layouts, interações
- Escopo: Composables, Material 3, State management UI, Animations, Semantics
- Breakpoint: 1200 tokens max, 3 files, 3 composables por sessão

#### 4. 🗄️ [BACKEND-DATA-ENGINEER](./BACKEND-DATA-ENGINEER.agent.md)
**Room Database, Retrofit, Offline-First, Data Persistence**
- Responsabilidade: Persistência, networking, sincronização
- Escopo: Entities, DAOs, API definitions, Migrations, Offline-first pattern
- Breakpoint: 1000 tokens max, 2 files, 1 migration por sessão

#### 5. 🏗️ [ARCHITECTURE-EXPERT](./ARCHITECTURE-EXPERT.agent.md)
**Clean Architecture, Domain Layer, MVVM, UseCases, Padrões**
- Responsabilidade: Estrutura geral, decisões arquiteturais
- Escopo: Domain models, Repository interfaces, UseCases, ViewModel structure, DI
- Breakpoint: 1500 tokens max, 5 files, 1 refactoring estrutural

#### 6. 🎨 [MOBILE-DESIGN-SPECIALIST](./MOBILE-DESIGN-SPECIALIST.agent.md)
**Design Tokens, Design System, Acessibilidade (WCAG), Material 3**
- Responsabilidade: Padrões visuais, design system, acessibilidade
- Escopo: Design tokens, Colors, Typography, Spacing, WCAG AA/AAA, Dark mode
- Breakpoint: 1000 tokens max, 3 files, 5 tokens por sessão

---

## 🚀 Quick Start

### Para Invocar um Agente

1. **Copie a URL de um agente** (ex: `.github/agents/JETPACK-COMPOSE-DEVELOPER.agent.md`)
2. **Abra VSCode Chat** (Ctrl+Shift+I)
3. **Use a sintaxe**:
   ```
   @JETPACK-COMPOSE-DEVELOPER [SUA TAREFA AQUI]
   
   Context: [contexto adicional]
   Files: [files específicos, se houver]
   Requirements: [requisitos específicos]
   ```

4. **Exemplo completo**:
   ```
   @JETPACK-COMPOSE-DEVELOPER Criar StatisticsCard composable com Material 3
   
   Context: Nova feature mostrando estatísticas de jogos
   Requirements:
   - Dark mode support
   - Acessibilidade (contentDescription)
   - @PreviewLightDark
   - Fade-in animation
   ```

### Para Selecionar o Agente Certo

Consulte [INDEX.md](./INDEX.md) para a **matriz de seleção rápida**:

| Seu Problema | Agente |
|---|---|
| "Como estruturar nova feature?" | ARCHITECTURE-EXPERT |
| "Preciso criar Composable" | JETPACK-COMPOSE-DEVELOPER |
| "Preciso de database" | BACKEND-DATA-ENGINEER |
| "Refine Kotlin idioms" | KOTLIN-EXPERT |
| "Setup permissions" | ANDROID-NATIVE-SPECIALIST |
| "Verificar WCAG/design" | MOBILE-DESIGN-SPECIALIST |

---

## 📊 Fluxo de Trabalho Típico

```
┌─────────────────────────────────────────────┐
│ 1. ARCHITECT-EXPERT                         │
│ Design feature (models, usecases, etc)     │
│ ↓                                           │
│ Entrega: arquitetura pronta                │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ 2. BACKEND-DATA-ENGINEER                    │
│ Implementar data layer (Room, Retrofit)    │
│ ↓                                           │
│ Entrega: dados prontos para usar           │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ 3. JETPACK-COMPOSE-DEVELOPER                │
│ Criar screens com UI                       │
│ ↓                                           │
│ Entrega: UI funcional                      │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ 4. KOTLIN-EXPERT (if needed)                │
│ Refine code idiomaticness                  │
│ ↓                                           │
│ Entrega: código otimizado                  │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ 5. MOBILE-DESIGN-SPECIALIST (if needed)     │
│ Verificar WCAG AA + dark mode              │
│ ↓                                           │
│ Entrega: design polido                     │
└─────────────────────────────────────────────┘
                    ↓
                  ✅ READY
```

---

```

---

## 📋 Política de Entrega

**Foco em Código, Não em Documentação**

Agentes entregam:
- ✅ Código compilando sem erros
- ✅ Padrões do projeto mantidos
- ✅ Testes passando
- ❌ Sem relatórios/sumários .md

Para mais detalhes: [DELIVERABLES.md](./DELIVERABLES.md)

---

## 🛡️ Padrões de Escopo

Cada agente **respeita rigorosamente seu escopo**:

```
ANDROID-NATIVE    ← Framework Android
         ↓
ARCHITECTURE      ← Clean Arch, Padrões
    ↙      ↘
DATA-ENG   KOTLIN/COMPOSE/DESIGN
   ↓          ↓         ↓        ↓
 Room     Kotlin    Material  Design
Retrofit  Idioms     3         System
```

**Nenhum agente ultrapassa suas responsabilidades.**

Se ultrapassar, agente escalata explicitamente:

```
⚠️ ESCALATION: ARCHITECTURE-EXPERT
Reason: This requires domain model changes
Context: Need UseCase pattern here
```

---

## 🔧 Configuração

### Estrutura de Arquivos

```
.github/agents/
├── README.md                              ← Você está aqui
├── INDEX.md                               ← Matriz de seleção
├── CLI-SETUP.md                           ← Como usar CLI
├── DELIVERABLES.md                        ← Formato de entrega
│
├── ANDROID-NATIVE-SPECIALIST.agent.md     ← Agent 1
├── KOTLIN-EXPERT.agent.md                 ← Agent 2
├── JETPACK-COMPOSE-DEVELOPER.agent.md     ← Agent 3
├── BACKEND-DATA-ENGINEER.agent.md         ← Agent 4
├── ARCHITECTURE-EXPERT.agent.md           ← Agent 5
└── MOBILE-DESIGN-SPECIALIST.agent.md      ← Agent 6
```

### Próximos Passos para Setup Completo

Se quiser CI/CD integration (futuro):

```
.github/workflows/
├── agents-quality-check.yml  (lint, build, tests)
├── agents-docs.yml           (atualizar docs)
└── agents-pipeline.yml       (coordenar agentes)
```

---

## 🎓 Princípios de Design

### 1. **Especialização Profunda**
Cada agente é especialista EM TUDO relacionado a seu escopo. Não faz work fora.

### 2. **Escopo Rigoroso**
Fronteiras claras entre agentes evitam conflito e confusão.

### 3. **Breakpoints Claros**
Limites de tokens, files, refactorings por sessão evitam loops infinitos.

### 4. **Escalação Transparente**
Quando sai do escopo, agente escalata explicitamente com contexto.

### 5. **Entrega Padronizada**
Todos seguem DELIVERABLES.md - formato consiste, verificável.

### 6. **Documentation First**
Cada agente é documentado em seu próprio `.agent.md` com exemplos.

---

## 📈 Vantagens do Sistema

✅ **Qualidade**: Cada aspectos é trabalho por especialista  
✅ **Eficiência**: Agentes focados fazem trabalho em menos iterações  
✅ **Escalabilidade**: Fácil de adicionar novos agentes conforme projeto cresce  
✅ **Rastreabilidade**: Código em git é prova de trabalho  
✅ **Paralelização**: Time pode trabalhar em múltiplos agentes em paralelo  
✅ **Conhecimento**: Documentação serve como knowledge base  

---

## 🚨 Troubleshooting

### "Qual agente devo chamar?"
→ Consulte [INDEX.md](./INDEX.md) matriz de seleção (coluna "Quando usar")

### "Como invocar via CLI?"
→ Veja [CLI-SETUP.md](./CLI-SETUP.md) com exemplos prontos

### "O agente saiu do escopo"
→ Revert e invoque agente correto com scope mais claro

### "Build falha após agente trabalhar"
→ É responsabilidade do agente consertar. Execute `./gradlew build` e relate.

### "Agente gerou documentação quando não era necessário"
→ Lembre-o de FOCAR EM CÓDIGO. Revise DELIVERABLES.md política.

---

## 🔗 Links Importantes

- **[INDEX.md](./INDEX.md)** - Decisão rápida de qual agente chamar ⭐
- **[CLI-SETUP.md](./CLI-SETUP.md)** - Como invocar agentes ⭐
- **[DELIVERABLES.md](./DELIVERABLES.md)** - Formato de entrega esperado
- **Project Wiki**: [docs/](../../docs/)
- **Architecture Guide**: [docs/ARCHITECTURE.md](../../docs/ARCHITECTURE.md)
- **Design System**: [docs/DESIGN_SYSTEM.md](../../docs/DESIGN_SYSTEM.md)

---

## 💡 Pro Tips

1. **Sempre consulte INDEX.md primeiro** - Não tenha dúvida de qual agente chamar
2. **Seja específico nas requisições** - "Criar StatisticsCard com Material 3" > "Criar um card"
3. **Valide localmente antes de escalação** - `./gradlew build` deve passar
4. **Leia deliverable completo** - Entenda o que foi feito antes de continuar
5. **Use os ejemplos** - CLI-SETUP.md tem copy-paste pronto
6. **Respeite escopos** - Não peça ao Kotlin-Expert para fazer UI

---

## 📞 Suporte

Se encontrar:

- ❌ **Bug num agente**: Relate qual agente, qual task, qual erro
- 💭 **Dúvida de escopo**: Consulte documentação do agente (arquivo `.agent.md`)
- 🔄 **Problema de escalação**: Verifique contexto passado, talvez faltaram detalhes

---

## 🚀 Ready?

→ **Vá para [INDEX.md](./INDEX.md) e escolha seu agente!**

Boa sorte com desenvolvimento especializado! 🎉

---

**Sistema de Agentes v1.0 | Cebolão Lotofácil Android | Feb 2026**
