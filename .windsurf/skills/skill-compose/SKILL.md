---
name: skill-compose
description: Desenvolva a camada de apresentação em Jetpack Compose + Material 3 no Cebolão Lotofácil. Use quando criar telas, componentes, navegação, estados de UI em MVVM e melhorias de acessibilidade/UX na interface.
---

# Skill Compose

Construa interfaces reativas, acessíveis e consistentes com a arquitetura.

## Objetivo

- Implementar UI em Compose com Material 3.
- Manter padrão MVVM com estado unidirecional.
- Separar apresentação de regra de negócio.

## Fluxo de trabalho

1. Mapear estado de tela (`UiState`) e eventos (`UiEvent`).
2. Implementar ViewModel orquestrando use cases.
3. Criar composables stateless e fazer state hoisting.
4. Conectar navegação (`NavHost`) e argumentos de rota.
5. Validar loading/empty/error/content.
6. Revisar acessibilidade e performance de recomposição.

## Regras de implementação

- Usar `MaterialTheme` e tokens visuais; evitar hardcode de cores/tipografia.
- Coletar estado com `collectAsStateWithLifecycle` quando aplicável.
- Delegar ações para ViewModel via callbacks.
- Usar `LazyColumn/LazyGrid` para listas e chaves estáveis.
- Centralizar strings em recursos.
- Garantir touch targets adequados e `contentDescription` em elementos interativos.

## Checklist de revisão

- Composable contém regra de negócio? (não deve)
- Tela cobre estados de erro e vazio?
- Navegação evita acoplamento indevido entre telas?
- Tema e componentes seguem padrão visual do app?
- Recompose excessivo foi mitigado?

## Não fazer

- Buscar dados direto da UI em DAO/API.
- Guardar estado de negócio dentro de composable.
- Misturar lógica de domínio com formatação visual.