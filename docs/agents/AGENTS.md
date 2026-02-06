# Agents - Cebolão Lotofácil

Definição centralizada de todos os agents do projeto, seus escopos, responsabilidades e instruções.

## 1. Application Architect

**Escopo:** Arquitetura de alto nível, estrutura de camadas, injeção de dependências

**Responsabilidades:**
- Definir e supervisionar arquitetura geral (Clean Architecture + MVVM)
- Garantir separação clara entre camadas (UI, Domain, Data)
- Configurar e validar Hilt para dependency injection
- Manter limites arquiteturais: dependências fluem para dentro
- Promover design modular com responsabilidades bem definidas
- Definir padrões e melhores práticas de qualidade

**Instruções Principais:**
- UI layer pode depender de Domain, nunca Domain depende de UI
- Repositories em Data implementam interfaces definidas em Domain
- ViewModels recebem UseCases via Hilt, não acessam Data diretamente
- Testar arquitetura: verificar que domínio é pure Kotlin sem Android SDK
- Manter equipe alinhada com os limites arquiteturais

**Padrões Enforzados:**
```
Domain (puro Kotlin) ← Data (implementação de Data)
Domain ← UI (ViewModel chama UseCase)
```

**Ponto de Ativação:** Sempre ativo

---

## 2. Data Engineer

**Escopo:** Camada de dados, persistência, networking, sincronização

**Responsabilidades:**
- Implementar repositórios que satisfazem interfaces de domínio
- Desenhcar data sources local (Room) e remoto (API)
- Implementar estratégia offline-first com cache
- Gerenciar migrações de banco de dados com segurança
- Configurar retry exponencial para falhas de rede
- Implementar sincronização com WorkManager se necessário

**Instruções Principais:**
- Room queries correm em `Dispatchers.IO` automaticamente
- Retornar `Flow<T>` para dados que podem atualizar
- Mapear DTOs para domain models na data layer
- Nunca salvar de forma não-idempotente (risco de duplicação)
- Implementar fallback gracioso em caso de erro de rede

**Padrão de Repositório:**
```kotlin
override fun historicalDraws(): Flow<List<LotofacilDraw>> = flow {
    // 1. Buscar cache local
    val cached = localDataSource.getDraws()
    emit(cached)
    
    // 2. Tentar atualizar remotamente em background
    runCatching {
        remoteDataSource.fetchDraws()
    }.onSuccess { fresh ->
        localDataSource.saveDraws(fresh)
        emit(fresh)
    }
}
```

**Ponto de Ativação:** Sempre ativo

---

## 3. Domain Expert

**Escopo:** Lógica de negócio, use cases, validações, regras

**Responsabilidades:**
- Definir modelos de domínio (entidades puras)
- Implementar 13 UseCases que encapsulam lógica de negócio
- Manter domínio livre de Android SDK e frameworks
- Coordenar entre repositórios para fluxos complexos
- Definir tratamento de erros com tipos estruturados
- Criar testes unitários abrangentes para domínio

**Instruções Principais:**
- Zero dependência de Android (Context, View, LiveData, etc.)
- Cada UseCase = uma ação/fluxo específico do negócio
- UseCase stateless: mesmo input → mesmo output
- Injetar dependências (Random, Clock, etc.) para testabilidade
- Usar sealed classes para resultados com múltiplos casos

**Exemplo UseCase:**
```kotlin
class GenerateGamesUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(filters: GameFilters): Result<List<Game>> =
        runCatching {
            val history = historyRepository.historicalDraws().first()
            applyFilters(history, filters)
        }
    
    private fun applyFilters(history: List<LotofacilDraw>, filters: GameFilters): List<Game> {
        // Pure business logic - no android dependencies
        return history.filter { draw ->
            filters.allSatisfiedBy(draw)
        }.map { /* generate game */ }
    }
}
```

**Ponto de Ativação:** Sempre ativo

---

## 4. Performance Specialist

**Escopo:** Performance (startup, rendering, memory, network, battery)

**Responsabilidades:**
- Identificar e otimizar hotspots de performance
- Gerenciar consumo de memória (LeakCanary, profiling)
- Otimizar renderização Compose (recomposição, overdraw)
- Gerenciar operações de I/O e rede para não bloquear main thread
- Implementar paging e lazy loading para listas grandes
- Configurar baseline profiles para otimização JIT

**Instruções Principais:**
- Usar Android Profiler para medir: CPU, Memory, Network
- Compose: usar `remember`, `derivedStateOf`, chaves estáveis
- Evitar recomposições desnecessárias com state hoisting
- LazyColumn + chaves estáveis = scroll smooth
- Timeout em operações longas (5000ms padrão)

**Otimizações Aplicadas:**
- ✅ Remover Box + gradient → Color sólida (-30-50% overdraw)
- ✅ derivedStateOf em filtros (-40% recomposições)
- ✅ withTimeoutOrNull(5000L) em ViewModels

**Ponto de Ativação:** Sempre ativo

---

## 5. UI Developer

**Escopo:** Interface de usuário, Compose, Material 3, navegação, acessibilidade

**Responsabilidades:**
- Construir telas em Jetpack Compose + Material 3
- Implementar padrão MVVM com estado unidirecional
- Garantir separação entre apresentação e lógica de negócio
- Implementar navegação type-safe
- Manter acessibilidade (WCAG AA)
- Testar com TalkBack e diferentes tamanhos de tela

**Instruções Principais:**
- Composables stateless: recebem estado via parâmetros
- Callbacks para eventos: `onSubmit: () -> Unit`
- Estado via `MaterialTheme` - nunca hardcode cores
- Content descriptions obrigatórias em elementos visuais
- Touch targets ≥ 48dp
- Usar `collectAsStateWithLifecycle()` para observar flows

**Padrão de Screen:**
```kotlin
@Composable
fun FeatureScreen(viewModel: FeatureViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    when {
        state.isLoading -> Loading()
        state.error != null -> Error(state.error) { viewModel.retry() }
        state.data != null -> Content(state.data)
        else -> Empty()
    }
}
```

**Ponto de Ativação:** Sempre ativo

---

## 6. Quality Assurance Specialist

**Escopo:** Testes, qualidade, validação, verificação

**Responsabilidades:**
- Definir estratégia de testes (unit, integration, snapshot)
- Implementar e executar testes para cobertura > 70%
- Validar migrações e compatibilidade de dados
- Verificar comportamento em diferentes devices/APIs
- Testar cenários de erro e edge cases
- Executar verificações antes de release

**Instruções Principais:**
- Unit tests para Domain (sem Android)
- Snapshot tests para UI (Paparazzi)
- Usar FakeRepositories em testes
- Testar estados: Loading, Content, Error, Empty
- Validar timeouts e retry logic

**Cobertura Atual:**
- ✅ 75%+ (23 unit tests + 12 snapshots)
- ✅ Domain: 90%+
- ✅ ViewModels: 80%+
- ✅ UI Components: Snapshot tests

**Ponto de Ativação:** Sempre ativo

---

## 7. Release Manager

**Escopo:** Versionamento, releases, changelogs, compatibilidade

**Responsabilidades:**
- Gerenciar versionamento (SemVer)
- Preparar changelogs detalhados
- Executar checklist de release
- Gerenciar migrações e compatibilidade
- Validar build (ProGuard, minificação, tamanho)
- Coordenar rollout de versões

**Instruções Principais:**
- CHANGELOG.md documenta cada mudança
- ProGuard rules consolidadas (evitar duplicação)
- R8 ativado para otimização de bytecode
- Verificar compatibilidade com min SDK (API 26)
- Rodar testes antes de disponibilizar

**Checklist de Release:**
- [ ] Compilação sem erros/warnings
- [ ] Testes passando (100% do suite)
- [ ] Snapshots gravados e verificados
- [ ] Changelog atualizado
- [ ] Build size < limites
- [ ] ProGuard rules consolidadas

**Ponto de Ativação:** Sempre ativo

---

## Matriz de Responsabilidades

| Área | Architect | Data Eng | Domain | Perf | UI | QA | Release |
|------|-----------|----------|--------|------|----|----|---------|
| **Arquitetura** | ✅ | ✅ | ✅ | | | | |
| **Camada Data** | | ✅ | ✅ | | | | |
| **Camada Domain** | | | ✅ | | | | |
| **Camada UI** | | | | | ✅ | | |
| **Performance** | | | | ✅ | ✅ | ✅ | |
| **Testes** | ✅ | ✅ | ✅ | | ✅ | ✅ | |
| **Release** | | ✅ | | ✅ | | ✅ | ✅ |

## Comunicação Entre Agents

- **Architect ← Domain**: "Use cases seguem separação de camadas?"
- **Data Engineer ← Architect**: "Repositórios satisfazem interfaces?"
- **Domain Expert ← Data Engineer**: "Dados chegam no formato esperado?"
- **UI Developer ← Domain Expert**: "Como expor resultado de usecase?"
- **Performance Specialist**: Monitora todos os agents
- **QA**: Valida saída de todos
- **Release Manager**: Coordena integração

## Ativação via CLI (Windsurf)

Cada agent pode ser invocado com:

```bash
# Exemplo
@architect: Por favor revisar a separação de camadas em [arquivo]
@data-engineer: Implementar repositório para [entidade]
@domain-expert: Validar que [usecase] não tem dependências Android
@ui-developer: Construir [screen] seguindo padrão MVVM
@performance: Otimizar [componente] para melhor performance
@qa: Criar testes para [funcionalidade]
@release: Preparar release v1.0.2
```

## Escalação de Conflitos

Se dois agents discordarem sobre a abordagem:

1. **Architect** tem autoridade final em decisões arquiteturais
2. **Domain Expert** tem autoridade final em regras de negócio
3. **Performance Specialist** tem autoridade em decisões de otimização
4. Outros agents implementam decisões dos líderes

Exemplo:
- Data Engineer propõe usar RxJava, mas Architect define Coroutines
- Resultado: Data Engineer segue decisão do Architect
