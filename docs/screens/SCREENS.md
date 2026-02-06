# Screens Documentation - Cebolão Lotofácil

Descrição de cada tela da aplicação, seus componentes, estados e fluxos.

## 1. Home Screen

**Path:** `ui/screens/home/HomeScreen.kt`

### Responsabilidade
Tela inicial exibindo último sorteio, estatísticas gerais e atalhos para principais funções.

### Componentes
- **LastDrawSection**: Exibe número e data do último sorteio
- **StatisticsSection**: Cards com estatísticas gerais (números quentes/frios)
- **WelcomeBanner**: Banner informativo
- **Quick Action Buttons**: Atalhos para Filters, Checker, Insights

### ViewModel
`HomeViewModel` com estado `HomeScreenState`

```kotlin
data class HomeScreenState(
    val isLoading: Boolean = false,
    val homeScreenData: HomeScreenData? = null,
    val error: AppError? = null,
    val shouldRefresh: Boolean = false
)
```

### Fluxo de Carregamento
```
onScreenEnter()
  ↓
homeViewModel.getHomeScreenData()
  ↓
GetHomeScreenDataUseCase.invoke()
  ├─ GetHistoryUseCase (último sorteio)
  ├─ GetFrequencyAnalysisUseCase (números quentes/frios)
  ├─ GetSavedGamesUseCase (jogos salvos)
  └─ GetUserGameStatisticsUseCase (stats)
  ↓
Atualizar UI com dados
```

### Estados
- **Loading**: CircularProgressIndicator enquanto busca dados
- **Content**: Dados exibidos com seções
- **Error**: ErrorCard com botão de retry
- **Empty**: EmptyState se nenhum dado disponível

### Otimizações
- ✅ State hoisting com composables stateless
- ✅ LazyColumn para scrolling eficiente
- ✅ Timeout de 5000ms na busca

---

## 2. Filters Screen

**Path:** `ui/screens/filters/FiltersScreen.kt`

### Responsabilidade
Configurar filtros estatísticos para geração de jogos (até 9 filtros).

### Componentes
- **FilterCard**: Card para cada filtro (repetível)
- **FilterRangeSlider**: Seletor de range
- **Checkbox/Toggle**: Para ativar/desativar filtros
- **GenerationActionsPanel**: Botões "Gerar" e "Limpar"

### ViewModel
`FiltersViewModel` com estado `FiltersScreenState`

```kotlin
data class FiltersScreenState(
    val filters: Map<FilterType, FilterValue> = emptyMap(),
    val isGenerating: Boolean = false,
    val generatedGames: List<Game>? = null,
    val error: AppError? = null
)
```

### Filtros Disponíveis (até 9)
1. Range de números
2. Mínimo de números consecutivos
3. Máximo de números pares
4. Máximo de números ímpares
5. Números quentes (últimas N semanas)
6. Números frios (últimas N semanas)
7. Padrão de repetição
8. Análise de tendência
9. Personalizado (livre)

### Fluxo de Geração
```
Usuário configura filtros
  ↓
Clica em "Gerar Jogos"
  ↓
FiltersViewModel.onGenerateGames()
  ↓
GenerateGamesUseCase.invoke(filters)
  ↓
GetHistoryUseCase (buscar histórico)
  ↓
Aplicar algoritmo de filtração
  ↓
GameRepository.saveGame() × N
  ↓
Navigate to GeneratedGamesScreen
```

### Estados
- **Configuring**: Usuário editando filtros
- **Generating**: CircularProgressIndicator enquanto processa
- **Success**: Navigate com games gerados
- **Error**: ErrorCard descrevendo problema

### Otimizações
- ✅ derivedStateOf para filtros ativos
- ✅ Timeout de 5000ms na geração
- ✅ Validação de restrições em tempo real

---

## 3. Generated Games Screen

**Path:** `ui/screens/games/GeneratedGamesScreen.kt`

### Responsabilidade
Exibir lista de jogos gerados, com opções de conferência, salvamento e exclusão.

### Componentes
- **GameCard**: Card exibindo números, data, ações
  - Numbers display (grid ou lista compacta)
  - Botão de conferência
  - Botão de salvar/fixar
  - Botão de compartilhar

### ViewModel
`GameViewModel` com estado `GeneratedGamesScreenState`

```kotlin
data class GeneratedGamesScreenState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val selectedGame: Game? = null,
    val error: AppError? = null
)
```

### Ações
- **Check Game**: Navigate para CheckerScreen com jogo pré-selecionado
- **Save Game**: Salva em banco local
- **Pin/Unpin**: Fixa/desafixa jogo favorito
- **Delete**: Remove jogo da tela

### Estados
- **Loading**: Lista vazia com spinner
- **Content**: LazyColumn com GameCards
- **Empty**: EmptyState se nenhum jogo
- **Error**: ErrorCard com retry

### Performance
- ✅ Chaves estáveis em LazyColumn
- ✅ Composables GameCard stateless
- ✅ Paginação se lista > 100 itens

---

## 4. Checker Screen

**Path:** `ui/screens/checker/CheckerScreen.kt`

### Responsabilidade
Conferir um jogo (ou lista de números) contra histórico de sorteios.

### Componentes
- **NumberInput**: Campo para entrada de números
- **CheckResultCard**: Resultado da conferência
  - Hits por sorteio
  - Estatísticas agregadas
  - Ganhos potenciais

### ViewModel
`CheckerViewModel` com estado `CheckerScreenState`

```kotlin
data class CheckerScreenState(
    val inputNumbers: List<Int> = emptyList(),
    val isChecking: Boolean = false,
    val checkResult: CheckResult? = null,
    val error: AppError? = null
)
```

### Fluxo de Conferência
```
Usuário entra com números (1-25)
  ↓
Valida 15-20 números
  ↓
Clica "Conferir"
  ↓
CheckerViewModel.onCheckGame()
  ↓
CheckGameUseCase.invoke(numbers)
  ↓
GetHistoryUseCase (buscar histórico)
  ↓
Comparar com cada sorteio
  ↓
CheckResultCard exibe:
    - Quantas vezes bateu 11, 12, 13, 14, 15
    - Probabilidade estatística
    - Ganhos potenciais
```

### Estados
- **Idle**: Tela vazia aguardando input
- **Checking**: CircularProgressIndicator
- **Result**: CheckResultCard com estatísticas
- **Error**: ErrorCard se problema na conferência

### Validações
- Mínimo 15 números, máximo 20
- Números de 01 a 25
- Sem duplicatas

---

## 5. Insights / Frequency Analysis Screen

**Path:** `ui/screens/insights/FrequencyAnalysisScreen.kt`

### Responsabilidade
Exibir análise estatística de números (frequência, padrões, tendências).

### Componentes
- **FrequencyChart**: Gráfico de frequência de números
- **PatternListSection**: Lista de padrões identificados
- **TrendSection**: Indicadores de tendência
- **RecencyComponents**: Números recentes vs antigos

### ViewModel
`InsightsViewModel` com estado `InsightsScreenState`

```kotlin
data class InsightsScreenState(
    val frequencyAnalysis: FrequencyAnalysis? = null,
    val patternAnalysis: PatternAnalysis? = null,
    val trendAnalysis: TrendAnalysis? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null
)
```

### Dados Exibidos
- Números mais frequentes (últimas N semanas)
- Números menos frequentes (números "frios")
- Padrões identificados (pares/ímpares, consecutivos, etc.)
- Tendências (números em alta/baixa)

### Fluxo de Carregamento
```
onScreenEnter()
  ↓
insightsViewModel.loadAnalytics()
  ↓
GetFrequencyAnalysisUseCase.invoke()
GetPatternAnalysisUseCase.invoke()
GetTrendAnalysisUseCase.invoke()
  ↓
Compilar dados para exibição
  ↓
Renderizar gráficos e listas
```

### Estados
- **Loading**: Skeleton loaders para gráficos
- **Content**: Análises exibidas
- **Error**: ErrorCard com retry

---

## 6. About Screen

**Path:** `ui/screens/about/AboutScreen.kt`

### Responsabilidade
Informações sobre o aplicativo, regras da Lotofácil, créditos, etc.

### Componentes
- **InfoItem**: Seção com título, descrição e ícone
  - Rules: Regras da Lotofácil
  - Prizes: Tabela de prêmios
  - Contact: Links de contato
  - Licenses: Licenças abertas

### ViewModel
Nenhum (tela estática com dados hardcoded em strings.xml)

### Conteúdo Estático
- Versão do app (obtida de BuildConfig)
- Regras da Lotofácil (traduzido PT-BR/EN)
- Tabela de prêmios (exemplo: ganhar com 11-15 acertos)
- Links úteis (Caixa Econômica Federal, etc.)
- Licenças de bibliotecas

### Navegação
- Link para tela de UserStats via botão/ícone
- Links externos em SafeLinks

---

## 7. User Stats Screen

**Path:** `ui/screens/stats/UserStatsScreen.kt`

### Responsabilidade
Exibir estatísticas de desempenho do usuário (jogos salvos, melhor performance, histórico, etc.).

### Componentes
- **StatCard**: Card com métrica individual
  - Total de jogos salvos
  - Taxa de acerto média
  - Melhor resultado
  - Última conferência

### ViewModel
`UserStatsViewModel` com estado `UserStatsScreenState`

```kotlin
data class UserStatsScreenState(
    val userStats: UserGameStatistics? = null,
    val recentGames: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val error: AppError? = null
)
```

### Fluxo de Carregamento
```
onScreenEnter()
  ↓
userStatsViewModel.loadStatistics()
  ↓
GetUserGameStatisticsUseCase.invoke()
GetSavedGamesUseCase.invoke()
  ↓
Compilar métricas do usuário
  ↓
Renderizar cards e histórico
```

### Estados
- **Loading**: Skeleton cards
- **Content**: Stats exibidos
- **Empty**: EmptyState se nenhum jogo salvo
- **Error**: ErrorCard com retry

### Ações
- **Limpar Não Fixados**: Botão para limpar jogos não fixados (confirmar primeiro)
- **Exportar**: Opção de exportar estatísticas como CSV/PDF

---

## Navigation Graph

```
HomeScreen
  ├─→ FiltersScreen
  │     └─→ GeneratedGamesScreen
  ├─→ CheckerScreen
  ├─→ InsightsScreen
  ├─→ AboutScreen
  │     └─→ UserStatsScreen
  └─→ UserStatsScreen (via botão home)
```

## State Management Pattern

Todas as telas seguem o mesmo padrão:

```kotlin
// ViewModel
@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val useCase: UseCase
) : BaseViewModel() {
    private val _state = MutableStateFlow<FeatureState>(FeatureState())
    val state = _state.asStateFlow()
    
    fun onAction(action: Action) {
        viewModelScope.launch {
            try {
                jobTracker.track(this)
                // ... use case invocation
                _state.value = _state.value.copy(data = result)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = mapError(e))
            }
        }
    }
    
    override fun onCleared() {
        jobTracker.cancelAll()
        super.onCleared()
    }
}

// Screen Composable
@Composable
fun FeatureScreen(viewModel: FeatureViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    when {
        state.isLoading -> LoadingState()
        state.error != null -> ErrorState(state.error) { viewModel.onRetry() }
        state.data != null -> ContentState(state.data)
        else -> EmptyState()
    }
}
```

## Accessibility Checklist por Screen

- [ ] Todas as imagens têm `contentDescription` apropriado
- [ ] Botões e elementos interativos ≥ 48dp
- [ ] Contraste de texto adequado (WCAG AA)
- [ ] Labels de input acessíveis
- [ ] Ordem de foco lógica (tab order)
- [ ] Testado com TalkBack screen reader
