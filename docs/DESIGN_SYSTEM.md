# Design System - Cebolão Lotofácil

## Visão Geral

O Design System utiliza **Material Design 3** com tema customizado em púrpura (Lotofácil) e suporte completo a tema escuro.

## Tema Visual

### Cores Principais

**Light Theme:**
- Primary (Purple): `#5D4E8F`
- Secondary: `#7C6FA5`
- Tertiary: `#8B7DB8`
- Background: `#FAFAFA`
- Surface: `#FFFFFF`
- Error: `#B3261E`

**Dark Theme:**
- Primary: `#E8D7FF`
- Secondary: `#D1B3FF`
- Tertiary: `#C9A8FF`
- Background: `#121212`
- Surface: `#1E1E1E`
- Error: `#F2B8B5`

### Uso de Cores

Todas as cores são aplicadas via `MaterialTheme` para garantir consistência:

```kotlin
// ✅ Correto
Box(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant))
Text("Title", color = MaterialTheme.colorScheme.onSurface)

// ❌ Evitar
Box(modifier = Modifier.background(Color(0xFF5D4E8F)))
Text("Title", color = Color.Black)
```

## Tipografia

**Typography Scale (Material 3):**

| Estilo | Tamanho | Peso | Uso |
|--------|--------|------|-----|
| DisplayLarge | 57sp | Regular | Headlines principais |
| HeadlineMedium | 28sp | Regular | Títulos de seções |
| TitleLarge | 22sp | Regular | Títulos de componentes |
| BodyLarge | 16sp | Regular | Corpo de texto |
| BodyMedium | 14sp | Regular | Texto secundário |
| LabelSmall | 12sp | Medium | Labels e badges |

**Font Family:**
- Padrão: Configurável via `Typography(defaultFontFamily = customFont)`

## Componentes

### Buttons

```kotlin
// Primary Action
Button(onClick = { }) {
    Text("Gerar Jogos")
}

// Secondary Action
OutlinedButton(onClick = { }) {
    Text("Cancelar")
}

// Icon Button
IconButton(onClick = { }) {
    Icon(Icons.Default.Settings, contentDescription = "Configurações")
}
```

### Cards

```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Título", style = MaterialTheme.typography.titleMedium)
        Text("Conteúdo", style = MaterialTheme.typography.bodyMedium)
    }
}
```

### Dialogs & Alerts

```kotlin
if (showDialog) {
    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text("Confirmar") },
        text = { Text("Deseja continuar?") },
        confirmButton = {
            Button(onClick = { /* action */ }) {
                Text("Sim")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { showDialog = false }) {
                Text("Não")
            }
        }
    )
}
```

### Lists

```kotlin
LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(16.dp)
) {
    items(
        items = games,
        key = { it.id }  // Chave estável
    ) { game ->
        GameCard(game = game)
    }
}
```

## Spacing & Layout

**Grid de Spacing (Material 3):**
- Base: 4dp
- Múltiplos comuns: 8dp, 12dp, 16dp, 24dp, 32dp

**Padrão de Padding:**
```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)  // Padding externo
) {
    // Content com spacedBy
}
```

## Acessibilidade

### Requisitos Obrigatórios

1. **Content Descriptions**: Todo elemento visual interativo precisa

```kotlin
Icon(
    painter = painterResource(R.drawable.ic_filter),
    contentDescription = "Abrir filtros"  // Necessário!
)
```

2. **Touch Targets**: Mínimo 48dp

```kotlin
IconButton(
    onClick = { },
    modifier = Modifier
        .size(48.dp)  // Tamanho adequado
        .semantics { contentDescription = "Ação" }
)
```

3. **Contrast**: Mínimo WCAG AA
- Texto sobre background: 4.5:1
- Componentes gráficos: 3:1

### Color Helpers

Para garantir contraste adequado quando usando cores customizadas:

```kotlin
// Verificar contraste em runtime
val contrast = calculateContrast(color1, color2)
if (contrast < 4.5f) {
    // Ajustar cor para melhor contraste
}
```

## Animações

### Motion Guidelines

- **Quick animations**: 100-150ms (feedback imediato)
- **Transition animations**: 300-400ms (navegação)
- **Attention animations**: 500ms+ (entrada de dados)

### Exemplos

```kotlin
// Fade in/out
AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn(),
    exit = fadeOut()
) {
    Text("Content")
}

// Scale animation
AnimatedContent(
    targetState = state,
    transitionSpec = {
        scaleIn() with scaleOut()
    }
) { targetState ->
    StateContent(targetState)
}
```

## States & Interactions

### Button States

```
Normal → Pressed (ripple) → Disabled (opacity 38%)
```

### Loading State

```kotlin
if (isLoading) {
    CircularProgressIndicator(
        modifier = Modifier.size(24.dp),
        strokeWidth = 2.dp
    )
} else {
    // Content
}
```

### Empty State

```kotlin
if (items.isEmpty()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_empty),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Nenhum jogo salvo",
            style = MaterialTheme.typography.bodyLarge
        )
    }
} else {
    // List content
}
```

### Error State

```kotlin
if (error != null) {
    ErrorCard(
        message = error.message,
        onRetry = { onRetryClick() }
    )
} else {
    // Normal content
}
```

## Modo Escuro

Suporte automático para Dark Theme via Material 3:

```kotlin
// Material3 automaticamente aplica cores do dark theme
val darkColors = darkColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

MaterialTheme(
    colorScheme = if (isDark) darkColors else lightColors,
    typography = typography,
    shapes = shapes,
    content = content
)
```

**Testes com Dark Mode:**
```bash
adb shell cmd uimode night yes    # Ativar
adb shell cmd uimode night no     # Desativar
```

## Componentes Customizados do App

### GameCard

```kotlin
GameCard(
    game: Game,
    onClick: (Game) -> Unit,
    onPin: (Game) -> Unit
)
```

Exibe um jogo com opções de ação.

### CheckResultCard

```kotlin
CheckResultCard(
    result: CheckResult
)
```

Exibe resultado da conferência com estatísticas.

### FilterRangeSlider

```kotlin
FilterRangeSlider(
    value = rangeValue,
    onValueChange = { newRange -> },
    min = 1,
    max = 25
)
```

Slider para seleção de intervalo em filtros.

### StatCard

```kotlin
StatCard(
    title: String,
    value: String,
    icon: Painter
)
```

Exibe estatísticas em card compacto.

## Breakpoints (Responsive)

Para suportar diferentes tamanhos de tela:

```kotlin
val windowWidthSizeClass = calculateWindowSizeClass(this).widthSizeClass

when (windowWidthSizeClass) {
    WindowWidthSizeClass.Compact -> {
        // Phone portrait: < 600dp
    }
    WindowWidthSizeClass.Medium -> {
        // Phone landscape / tablet portrait: 600-840dp
    }
    WindowWidthSizeClass.Expanded -> {
        // Tablet landscape: > 840dp
    }
}
```

## Checklist de Design Review

- [ ] Todas as cores vêm de `MaterialTheme.colorScheme.*`
- [ ] Tipografia usa `MaterialTheme.typography.*`
- [ ] Spacing segue múltiplos de 4dp/8dp
- [ ] Todos elementos interativos ≥ 48dp
- [ ] Content descriptions presentes em elementos visuais
- [ ] Estados (loading/empty/error) cobertos
- [ ] Dark theme testado
- [ ] Animações suaves e apropriadas
