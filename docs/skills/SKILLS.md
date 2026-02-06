# Skills - Cebolão Lotofácil

Definição centralizada de todos os skills do projeto. Cada skill é um conjunto de práticas, padrões e instruções para uma área específica.

## 1. Skill Compose

**Nome:** skill-compose  
**Descrição:** Desenvolvimento da camada de apresentação em Jetpack Compose + Material Design 3  
**Quando usar:** Criar telas, componentes de UI, navegação, estado em MVVM, acessibilidade

### Objetivo

Implementar interfaces reativas, acessíveis e performáticas seguindo a arquitetura MVVM e padrões Material Design 3.

### Fluxo de Trabalho

1. **Mapear estado de tela** (`UiState`) e eventos (`UiEvent`)
2. **Implementar ViewModel** orquestrando use cases
3. **Criar composables stateless** e fazer state hoisting
4. **Conectar navegação** (NavHost) e argumentos de rota
5. **Validar estados** (loading/empty/error/content)
6. **Revisar acessibilidade** e performance de recomposição

### Regras de Implementação

- ✅ Usar `MaterialTheme` e tokens visuais; evitar hardcode de cores/tipografia
- ✅ Coletar estado com `collectAsStateWithLifecycle()` quando aplicável
- ✅ Delegar ações para ViewModel via callbacks
- ✅ Usar `LazyColumn`/`LazyGrid` para listas com chaves estáveis
- ✅ Centralizar strings em `strings.xml` (i18n)
- ✅ Garantir touch targets ≥ 48dp e `contentDescription` apropriado

### Checklist de Revisão

- [ ] Composable contém regra de negócio? (não deve)
- [ ] Tela cobre todos os estados (loading/empty/error/content)?
- [ ] Navegação evita acoplamento indevido entre telas?
- [ ] Tema e componentes seguem padrão visual?
- [ ] Recomposição excessiva foi mitigada?
- [ ] Acessibilidade validada com TalkBack?

### ✅ Não Fazer

- ❌ Buscar dados direto da UI (DAO, API)
- ❌ Guardar estado de negócio em composable
- ❌ Misturar lógica de domínio com formatação visual
- ❌ Hardcode de cores/tipografia
- ❌ Touch targets < 48dp

### Exemplo

```kotlin
// ✅ Composable stateless
@Composable
fun GameCard(
    game: Game,
    onCheck: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(game.numbers.joinToString(), style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onCheck) { Text("Conferir") }
                OutlinedButton(onClick = onDelete) { Text("Deletar") }
            }
        }
    }
}

// ✅ ViewModel com state hoisting
@HiltViewModel
class GameViewModel @Inject constructor(
    private val checkGameUseCase: CheckGameUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<GameState>(GameState())
    val state = _state.asStateFlow()
    
    fun onCheckGame(game: Game) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            checkGameUseCase(game)
                .onSuccess { result ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        result = result
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error
                    )
                }
        }
    }
}
```

### CLI Trigger

```
Use @skill-compose para implementar/revisar screens, componentes e navegação em Jetpack Compose.
```

---

## 2. Skill Offline-First

**Nome:** skill-offline-first  
**Descrição:** Leitura local prioritária com sincronização resiliente em background  
**Quando usar:** Implementar persistence, cache, offline capability, sync com API

### Objetivo

Garantir que o app funcione offline mostrando dados em cache, sincronizando com servidor quando conexão disponível.

### Padrão Stale-While-Revalidate

```
┌─ Usuário abre tela
├─ 1️⃣ Buscar Local (cache) → Mostrar IMEDIATAMENTE
├─ 2️⃣ Background: Buscar Remoto (API)
├─ 3️⃣ Atualizar Local com novos dados
└─ 4️⃣ Notificar UI (novo valor flui via Flow)
```

### Fluxo de Implementação

1. **Local First**: Query Room database
   ```kotlin
   val cached: List<LotofacilDraw> = localDataSource.getDraws()
   emit(cached)  // Show immediately
   ```

2. **Background Refresh**: Fetch remoto em coroutine
   ```kotlin
   val fresh = retryExponentialBackoff {
       remoteDataSource.fetchDraws()
   }
   ```

3. **Save & Emit**: Atualizar cache e notificar UI
   ```kotlin
   localDataSource.saveDraws(fresh)
   emit(fresh)  // UI atualiza com dados novos
   ```

### Retry Strategy

Exponential backoff com jitter:
- Tentativa 1: 100ms
- Tentativa 2: 200ms
- Tentativa 3: 400ms
- Máximo: 5000ms

```kotlin
suspend fun <T> retryExponentialBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 100,
    maxDelayMs: Long = 5000,
    block: suspend () -> T
): T = retry(maxRetries = maxRetries) {
    delay((initialDelayMs * 2.pow(attemptNumber - 1)).toLong().coerceAtMost(maxDelayMs))
    block()
}
```

### Offline Scenarios

- ✅ App offline: Mostrar cache
- ✅ Rede lenta: Timeout após 5s, usar cache
- ✅ Erro de API: Fallback para cache com erro discreto
- ✅ Dados obsoletos: Indicador visual de "Última atualização: 2h atrás"

### Checklist

- [ ] Local cache pronto quando tela abre?
- [ ] Background sync não bloqueia UI?
- [ ] Retry exponencial implementado?
- [ ] Offline: app mostra dados em cache?
- [ ] Indicador de sync status (opcional)?
- [ ] Migrações de banco seguras?

### CLI Trigger

```
Use @skill-offline-first para implementar caching, sincronização e resilência de rede.
```

---

## 3. Skill Engine Developer

**Nome:** skill-engine-developer  
**Descrição:** Desenvolvimento de lógica de geração e análise estatística de números  
**Quando usar:** Implementar algoritmos, análises, geradores, lógica complexa

### Objetivo

Implementar UseCases que executam lógica estatística complexa (geração de jogos, análise de frequência, detecção de padrões, etc.).

### Responsabilidades

1. **Generate Games**: Algoritmo que aplica N filtros
   - Input: Histórico + Filtros
   - Output: Lista de Games gerados
   - Validação: Verificar que todos filtros são satisfeitos

2. **Frequency Analysis**: Contar ocorrências de números
   - Input: Histórico de sorteios
   - Output: Map<Number, Frequency>
   - Período: Últimas N semanas

3. **Pattern Analysis**: Identificar padrões
   - Pares vs Ímpares
   - Números consecutivos
   - Repetição de sequências

4. **Trend Analysis**: Identificar tendências
   - Números em alta (aumentando frequência)
   - Números em baixa (diminuindo frequência)

### Algoritmo de Geração (Pseudocódigo)

```kotlin
fun generateGames(
    history: List<LotofacilDraw>,
    filters: GameFilters,
    count: Int = 10
): List<Game> {
    val candidates = mutableListOf<Game>()
    
    // Gerar múltiplas combinações
    repeat(count * 10) {  // Oversample
        val game = generateRandomGame()  // 15-20 números
        
        // Validar contra todos os filtros
        if (filters.allSatisfy(game, history)) {
            candidates.add(game)
        }
    }
    
    // Ordenar por score (compatibilidade com histórico)
    return candidates
        .distinctBy { it.numbers }
        .take(count)
}
```

### Testas Necessários

- ✅ Gerar jogos com cada filtro individual
- ✅ Gerar jogos com múltiplos filtros (combine)
- ✅ Validar que games gerados satisfazem todos filtros
- ✅ Edge cases: Sem dados, filtros impossíveis, etc.

### Determinismo

- ✅ Usar `Random(seed)` em testes para reproduzibilidade
- ✅ Mesma entrada = mesma saída (importante para debug)
- ✅ Injetar Random e Clock para testabilidade

### CLI Trigger

```
Use @skill-engine-developer para implementar algoritmos estatísticos e análises.
```

---

## 4. Skill MVVM Pattern

**Descrição Implícita:** Aplicado em todos os ViewModels

### Padrão

```
┌─ UI (Composable)
│  └─ Coleta: collectAsStateWithLifecycle()
│
├─ ViewModel (State Management)
│  ├─ Input: Callbacks (onAction)
│  ├─ State: StateFlow<UiState>
│  └─ Logic: Chama use cases
│
└─ Domain (Business Logic)
   ├─ UseCases
   └─ Repositories (interfaces)
```

### Estado

```kotlin
data class UiState(
    val isLoading: Boolean = false,
    val data: Data? = null,
    val error: AppError? = null,
    val isEmpty: Boolean = false
)
```

### ViewModel Base

```kotlin
open class BaseViewModel : ViewModel() {
    protected val jobTracker = JobTracker()
    
    protected fun launch(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                jobTracker.track(this)
                block()
            } catch (e: Exception) {
                handleError(e)
            }
        }.also { jobTracker.track(it) }
    }
}
```

---

## 5. Skill Clean Architecture

**Descrição Implícita:** Aplicado em toda a estrutura

### Camadas

1. **UI**: Composables, ViewModels, Navigation
2. **Domain**: UseCases, Repositories (interfaces), Models
3. **Data**: Repository implementations, DataSources, DB, API

### Dependências

```
UI → Domain ← Data
(UI não conhece Data)
(Domain não conhece UI ou Data)
```

### Pricípios

- ✅ Domain é pure Kotlin (sem Android SDK)
- ✅ Data layer é a única que fala com Android/API
- ✅ UI é stateless (estado no ViewModel)
- ✅ Testes: Domain roda no JVM

---

## Matriz de Skills × Funcionalidades

| Funcionalidade | skill-compose | skill-offline-first | skill-engine-developer |
|----------------|---------------|-------------------|----------------------|
| Gerar Jogos | ✅ (tela) | ✅ (cache histórico) | ✅ (algoritmo) |
| Conferir Jogo | ✅ (tela) | ✅ (cache histórico) | ✅ (comparação) |
| Análises | ✅ (tela) | ✅ (cache dados) | ✅ (cálculos) |
| Salvar Jogos | ✅ (tela) | ✅ (DB local) | |
| Navegação | ✅ (rotas) | | |

## Escalação de Decisões de Skills

1. **Compose Pattern**: UI Developer decide, skill-compose guia
2. **Offline Strategy**: Data Engineer decide, skill-offline-first guia
3. **Algoritmo**: Domain Expert decide, skill-engine-developer guia

---

## Ativação via CLI

Cada skill pode ser invocado como:

```bash
@skill-compose: Implementar [tela] seguindo padrão MVVM
@skill-offline-first: Adicionar caching e offline support para [feature]
@skill-engine-developer: Otimizar algoritmo de [usecase]
```

---

## Resumo Prático

| Skill | Use | Não Use | Output |
|-------|-----|---------|--------|
| **Compose** | UI, tema, acessibilidade | Lógica negócio | Composables, screens |
| **Offline-First** | Cache, sync, retry | Lógica pura | Repositórios com sync |
| **Engine Dev** | Algoritmos, análises | UI, persistência | UseCases, modelos |
