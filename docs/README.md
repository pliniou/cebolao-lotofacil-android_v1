# 📚 DOCUMENTAÇÃO - ÍNDICE

**Projeto**: Cebolão Lotofácil  
**Versão**: v1.0.0-phase3  
**Última Atualização**: 6 de fevereiro de 2026

---

## 📂 ESTRUTURA DE DOCUMENTAÇÃO

### ⭐ **ACTIVE_TASKS.md** - TAREFAS ATUAIS (COMECE AQUI!)
🚀 **Tarefas prontas para fazer - sem tarefas concluídas**
- ✅ Fases 1-3 removidas (completas)
- 🟡 Fase 4: 5 tarefas de Acessibilidade (próximas)
- 📋 Fase 5: 4 tarefas de Performance
- Checklist com estimativas de tempo
- **LEIA ISTO PRIMEIRO**

### 1. **STATUS.md** - ESTADO ATUAL
Estado completo do projeto
- ✅ Fase 1: Foundation (5 fixes)
- ✅ Fase 2: Design System (2 files)
- ✅ Fase 3: Statistics UI (7 tasks) - COMPLETA
- 🟡 Fase 4: Acessibilidade - EM PROGRESSO
- Métricas e verificações

### 2. **QUICK_START_PHASE4.md** - INÍCIO RÁPIDO TAREFA 4.1
Passo-a-passo para começar imediatamente
- Primeira tarefa: Screen Reader Labels
- Componentes: 7 arquivos
- Tempo: ~2.5 horas
- Código exemplo pronto

### 3. **PHASES_4_5_ROADMAP.md** - PLANEJAMENTO
Planejamento detalhado para Fases 4-5
- **Fase 4**: 5 tarefas (Screen Readers, Color Contrast, Keyboard Nav, Dynamic Text, Haptics)
- **Fase 5**: 4 tarefas (Lazy Loading, Remember, Image Opt, Metrics)
- Cronograma de 2 semanas
- Prioridades e critérios de aceitação

### 4. **DESIGN_SYSTEM.md** - DESIGN TOKENS
Referência técnica dos design tokens (arquivo em /docs/DESIGN_SYSTEM.md)
- AppSize (15+ tokens de dimensão)
- AppAlpha (20+ tokens de opacidade)
- AppSpacing, AppElevation
- Material3 Theme colors

### 5. **ARCHITECTURE.md** - ARQUITETURA
Visão geral da estrutura de código (arquivo em /docs/architecture/ARCHITECTURE.md)
- Clean Architecture
- MVVM + Hilt
- Domain/Data/UI layers

### 6. **SCREENS.md** - TELAS
Documentação de cada tela (arquivo em /docs/screens/SCREENS.md)
- HomeScreen
- FrequencyAnalysisScreen
- UserStatsScreen
- Componentes relacionados

### 7. **SKILLS.md** - PADRÕES & TÉCNICAS
Padrões de código utilizados (arquivo em /docs/skills/SKILLS.md)
- Jetpack Compose patterns
- Kotlin best practices
- Animation patterns

---

## 🎯 COMO USAR ESTA DOCUMENTAÇÃO

### Para Desenvolvedores Novos
1. Leia **STATUS.md** para entender o estado atual
2. Leia **DESIGN_SYSTEM.md** para conhecer tokens
3. Leia **SCREENS.md** para ver a estrutura de componentes
4. Leia **ARCHITECTURE.md** para entender o projeto

### Para Continuar o Desenvolvimento
1. Leia **PHASES_4_5_ROADMAP.md** para ver o que vem próximo
2. Para cada tarefa, leia o arquivo correspondente em /docs/
3. Implemente seguindo os padrões em **SKILLS.md**

### Para Debugar
1. Consulte **DESIGN_SYSTEM.md** para valores esperados
2. Consulte **SCREENS.md** para estrutura esperada
3. Use tokens em **AppSize.kt** e **AppAlpha.kt**

---

## 📊 FASE 3 - RESUMO TÉCNICO

### Componentes Criados
1. **PrizeTierCard** - Card com badges de tier (🥇🥈🥉)
2. **RankingBadge** - Badge de posição ordinal
3. **RankedNumberBall** - NumberBall com overlay de ranking
4. **TrendIndicator** - Indicador de tendência (↑↓→)
5. **ExpandableSection** - Seção expansível com animação

### Arquivos Modificados
- `LastDrawSection.kt` - Refatoração com PrizeTierCard
- `FrequencyComponents.kt` - TopNumbersSection com ranking
- `RecencyComponents.kt` - OverdueItem com ranges
- `BarChart.kt` - Remoção de rotação 45°
- `FrequencyAnalysisScreen.kt` - Accordion layout
- `UserStatsSection.kt` - Trend indicators
- `strings.xml` + `strings-en.xml` - 6 strings novas

### Métricas
- **Arquivos Modificados**: 7 principais + 2 recursos
- **Componentes Novos**: 5
- **Linhas de Código**: ~600 adicionadas
- **Erros de Compilação**: 0 ✅
- **Dark Mode**: 100% suportado
- **Responsive**: Sim, testado

---

## 🚀 PRÓXIMAS PRIORIDADES

### Semana 1: Fase 4 - Accessibility
**Tarefas**: 4.1 (Screen Readers) + 4.2 (Color Contrast)
**Duração**: 3 dias
**Deliverables**: WCAG AA compliance + TalkBack funcional

### Semana 2: Fase 4 - Navigation & Sizing
**Tarefas**: 4.3 (Keyboard) + 4.4 (Dynamic Text) + 4.5 (Haptics)
**Duração**: 4 dias
**Deliverables**: Keyboard nav completa + 200% text support

### Semana 3: Fase 5 - Performance
**Tarefas**: 5.1 (Lazy Loading) + 5.2 (Remember) + 5.3 (Images)
**Duração**: 4 dias
**Deliverables**: 60fps + 200MB memory + <40MB APK

### Semana 4: Validação
**Tarefas**: 5.4 (Metrics) + Testes E2E
**Duração**: 3 dias
**Deliverables**: Metrics dashboard + teste completo

---

## 🔗 REFERÊNCIAS RÁPIDAS

### Design Tokens
```kotlin
// Dimensões
AppSize.numberBallSmall      // 40.dp
AppSize.numberBallMedium     // 48.dp
AppSize.numberBallLarge      // 64.dp
AppSize.chartHeightDefault   // 240.dp

// Opacidade (WCAG AA)
AppAlpha.textPrimary         // 1.0f (100%)
AppAlpha.textSecondary       // 0.74f (74%)
AppAlpha.textTertiary        // 0.60f (60%)
AppAlpha.textDisabled        // 0.38f (38%)

// Espaçamento
AppSpacing.xs                // 4.dp
AppSpacing.sm                // 8.dp
AppSpacing.md                // 16.dp
AppSpacing.lg                // 24.dp

// Elevação
AppElevation.xs              // 1.dp
AppElevation.sm              // 4.dp
AppElevation.md              // 8.dp
AppElevation.lg              // 12.dp
```

### Componentes Core
```kotlin
// NumberBall
NumberBall(
    number = 15,
    size = AppSize.numberBallMedium,
    isHighlighted = true
)

// BarChart com gaussiana
BarChart(
    data = frequencies,
    maxValue = maxFrequency,
    chartHeight = 240.dp,
    showGaussCurve = true
)

// ExpandableSection
ExpandableSection(
    title = "Seção Expansível",
    isExpandedByDefault = true
) {
    // Conteúdo
}

// TrendIndicator
TrendIndicator(
    value = 8.5,
    baselineValue = 7.5
)
```

### Arquitetura
```
app/src/main/java/com/cebolao/lotofacil/
├── core/
│   ├── constants/
│   ├── coroutine/
│   ├── error/
│   ├── result/
│   ├── security/
│   └── utils/
├── data/
│   ├── datasource/
│   ├── network/
│   ├── parser/
│   └── repository/
├── di/
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── DataSourceModule.kt
│   ├── NetworkModule.kt
│   ├── RepositoryModule.kt
│   └── UtilityModule.kt
├── domain/
│   ├── model/
│   ├── repository/
│   ├── service/
│   └── usecase/
├── ui/
│   ├── components/
│   ├── model/
│   ├── screens/
│   │   ├── home/
│   │   ├── insights/
│   │   ├── stats/
│   │   ├── checker/
│   │   └── about/
│   ├── theme/
│   └── ...
└── viewmodels/
```

---

## 📝 CONVENÇÕES DE CÓDIGO

### Composables
```kotlin
@Composable
fun MyComponent(
    data: Data,
    modifier: Modifier = Modifier,
    onAction: (String) -> Unit = {}
) {
    // Implementação
}
```

### Remember Cache
```kotlin
val derivedData = remember(data) {
    expensiveCalculation(data)
}
```

### Animations
```kotlin
val animatedValue by animateFloatAsState(
    targetValue = if (isExpanded) 1f else 0f,
    animationSpec = spring(...)
)
```

### Semantics
```kotlin
modifier = Modifier.semantics {
    contentDescription = "Descrição em português"
    role = Role.Button
}
```

---

## 🧪 TESTING

### Unit Tests
Localização: `app/src/test/java/com/cebolao/lotofacil/`

### UI Tests
Localização: `app/src/androidTest/java/com/cebolao/lotofacil/`

---

## 📞 SUPORTE

Para dúvidas sobre documentação:
1. Consulte o arquivo específico em `/docs`
2. Verifique `STATUS.md` para contexto geral
3. Veja `PHASES_4_5_ROADMAP.md` para próximos passos
