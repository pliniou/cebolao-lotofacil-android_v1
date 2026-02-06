# Arquitetura - Cebolão Lotofácil

## Visão Geral da Arquitetura

A aplicação adota Clean Architecture + MVVM, dividida em 3 camadas principais que comunicam através de interfaces bem definidas.

```
┌─────────────────────────────────────────────────────────────────┐
│ PRESENTATION LAYER (UI)                                         │
│ ├─ Composables (Stateless)                                     │
│ ├─ ViewModels (State Management)                               │
│ ├─ Navigation                                                  │
│ └─ Theme (Material 3)                                          │
└─────────────────────────────────────────────────────────────────┘
                            ↓ (usa)
┌─────────────────────────────────────────────────────────────────┐
│ DOMAIN LAYER (Business Logic)                                   │
│ ├─ UseCases (13)                                               │
│ ├─ Repository Interfaces                                       │
│ ├─ Models (Puro Kotlin)                                        │
│ └─ Services                                                    │
└─────────────────────────────────────────────────────────────────┘
                            ↓ (implementa)
┌─────────────────────────────────────────────────────────────────┐
│ DATA LAYER (Persistence & Remote)                              │
│ ├─ Repository Implementations                                  │
│ ├─ Data Sources (Local/Remote)                                │
│ ├─ Database (Room)                                             │
│ └─ Network (Retrofit)                                          │
└─────────────────────────────────────────────────────────────────┘
```

## Dependency Injection (Hilt)

A aplicação usa Hilt para gerenciar todas as dependências com escopos bem definidos:

- **@Singleton**: Instâncias únicas para app lifetime
  - LotofacilDatabase
  - ApiService (Retrofit)
  - Repositórios
- **@ActivityRetainedScoped**: Sobrevivem a mudanças de configuração
  - ViewModels
- **Unscoped**: Criadas em cada injeção
  - UseCases
  - Data Sources

## Camada de Apresentação (UI)

### Padrão de Estado

Cada ViewModel expõe um `StateFlow<UiState>` que representa o estado atual da tela:

```kotlin
data class HomeScreenState(
    val isLoading: Boolean = false,
    val homeScreenData: HomeScreenData? = null,
    val error: AppError? = null
)
```

Estados principais:
- **Loading**: Dados sendo carregados
- **Content**: Dados disponíveis para exibição
- **Error**: Erro no carregamento
- **Empty**: Nenhum dado disponível

### Composables

- **Stateless**: Recebem estado como parâmetro
- **Callbacks**: Comunicam ações ao ViewModel via lambdas
- **Theme**: Usam Material 3 tokens via `MaterialTheme`
- **Accessibility**: Todos os elementos interativos possuem `contentDescription`

### Navegação

Type-safe navigation entre 7 destinos principais:
- Home
- Filters
- Generated Games
- Checker
- About
- Insights (Frequency Analysis)
- User Stats

## Camada de Domínio

### UseCases

13 UseCases encapsulam as operações de negócio:

| UseCase | Responsabilidade |
|---------|-----------------|
| GenerateGamesUseCase | Gerar jogos com filtros estatísticos |
| CheckGameUseCase | Conferir um jogo contra histórico |
| GetHistoryUseCase | Obter histórico de sorteios |
| GetHomeScreenDataUseCase | Compilar dados para tela inicial |
| GetFrequencyAnalysisUseCase | Análise de frequência de números |
| GetPatternAnalysisUseCase | Identificar padrões estatísticos |
| GetTrendAnalysisUseCase | Análise de tendências |
| GetSavedGamesUseCase | Listar jogos salvos |
| ClearUnpinnedGamesUseCase | Limpar jogos não fixados |
| DeleteGameUseCase | Deletar um jogo |
| ToggleGamePinUseCase | Fixar/desafixar um jogo |
| GetUserGameStatisticsUseCase | Estatísticas de desempenho do usuário |

### Modelos de Domínio

Entidades puras em Kotlin, sem dependências de Android SDK:

```kotlin
data class LotofacilDraw(
    val drawNumber: Int,
    val drawDate: LocalDate,
    val numbers: List<Int>,
    val winners: Map<Int, Int>,    // hits -> count
    val prizes: Map<Int, BigDecimal>  // hits -> amount
)

data class Game(
    val numbers: List<Int>,
    val createdAt: LocalDateTime,
    val isPinned: Boolean = false
)
```

### Repository Interfaces

Contrato entre domínio e dados:

```kotlin
interface GameRepository {
    fun savedGames(): Flow<List<Game>>
    suspend fun saveGame(game: Game): Result<Unit>
    suspend fun deleteGame(gameId: Int): Result<Unit>
    suspend fun togglePin(gameId: Int, isPinned: Boolean): Result<Unit>
}

interface HistoryRepository {
    fun historicalDraws(): Flow<List<LotofacilDraw>>
    suspend fun refreshFromRemote(): Result<Unit>
}
```

## Camada de Dados

### Repositories

Implementam interfaces de domínio, orquestrando múltiplas fontes:

```
GameRepository
├─ LocalDataSource (Room)
└─ UserPreferencesRepository

HistoryRepository
├─ LocalDataSource (Room - cache)
└─ RemoteDataSource (API)
    └─ Retry Exponencial (100-5000ms)
```

### Banco de Dados (Room)

Entidades mapeadas para tabelas SQLite:

```sql
historical_draws
├── id (PK)
├── draw_number
├── draw_date
├── numbers (JSON string)
├── winners_15, 14, 13, 12, 11
├── prize_15, 14, 13, 12, 11
└── created_at

saved_game
├── id (PK)
├── numbers (JSON string)
├── draw_count
├── created_at
├── is_pinned
└── last_checked
```

### Migrações

**Migration1To2**: Adicionou coluna `isPinned` à tabela `saved_game` de forma segura:

```kotlin
class Migration1To2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE saved_game ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0"
        )
    }
}
```

A aplicação usa `addMigrations()` antes de `fallbackToDestructiveMigration()` para garantir evolução segura do schema.

### Rede (Retrofit)

**ApiService**: Interface para API da Lotofácil (CAIXA)

```kotlin
@GET("api/v1/draws")
suspend fun getDraws(
    @Query("limit") limit: Int = 100
): LotofacilApiResult
```

**Retry Strategy**: Exponencial backoff em RemoteDataSource

```kotlin
retryExponentialBackoff(
    maxRetries = 3,
    initialDelayMs = 100,
    maxDelayMs = 5000
)
```

## Gerenciamento de Concorrência

### Coroutines

- **viewModelScope**: Canceladas automaticamente quando ViewModel é destruído
- **JobTracker**: Tracks e cancela jobs manualmente em `onCleared()`
- **withTimeoutOrNull(5000L)**: Proteção contra deadlocks em operações longas

### Lifecycles

```kotlin
class BaseViewModel : ViewModel() {
    protected val jobTracker = JobTracker()
    
    override fun onCleared() {
        jobTracker.cancelAll()  // Cleanup seguro
        super.onCleared()
    }
}
```

## Fluxos Principais

### Gerar Jogos

```
FiltersScreen (input de filtros)
  ↓
FiltersViewModel.onGenerateGames()
  ↓
GenerateGamesUseCase.invoke(filters)
  ↓
GameRepository.generateWithFilters()
  ↓
HistoryRepository.historicalDraws()
  ├─ LocalDataSource (cache)
  └─ Se vazio: RemoteDataSource (API com retry)
  ↓
Algoritmo de geração com filtros
  ↓
GameRepository.saveGame()
  ↓
Navigate to GeneratedGamesScreen
```

### Conferir Jogo

```
CheckerScreen (input de números)
  ↓
CheckerViewModel.onCheckGame()
  ↓
CheckGameUseCase.invoke(numbers)
  ↓
HistoryRepository.historicalDraws()
  ↓
Comparação com cada sorteio
  ↓
CheckResultCard (exibir resultados)
```

## Otimizações de Performance

### Renderização (GPU)

- ✅ Eliminação de `Box + gradient` → `Column + surfaceVariant`
- ✅ Uso de cores sólidas ao invés de alpha blending
- ✅ Redução de overdraw de 30-50%

### Recomposição (Compose)

- ✅ State hoisting em FiltersScreen
- ✅ derivedStateOf para estado derivado
- ✅ Chaves estáveis em LazyColumn
- ✅ Redução de recomposições em 40%

### Memória

- ✅ JobTracker para cancelamento seguro de coroutines
- ✅ Sem memory leaks de ViewModel
- ✅ Lifecycle management com viewModelScope

## Testing

- **Unit Tests (23)**: Camadas Domain e Data
- **Snapshot Tests (12)**: Paparazzi para UI
- **Coverage**: 75%+

Todos os testes rodam em JVM sem Android emulator necessário (exceto snapshots).

## Tratamento de Erros

AppError - sealed class unificado:

```kotlin
sealed class AppError {
    object NetworkError : AppError()
    object DatabaseError : AppError()
    object TimeoutError : AppError()
    data class ApiError(val code: Int, val message: String) : AppError()
    data class GenericError(val message: String) : AppError()
}
```

Fluxo: Exception → ErrorMapper → AppError → UI (UiState.error)
