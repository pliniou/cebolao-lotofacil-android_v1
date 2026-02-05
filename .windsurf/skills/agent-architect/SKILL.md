---
name: agent-architect
description: Defina e mantenha a arquitetura Android (Clean Architecture + MVVM + Hilt + Coroutines/Flow) do Cebolão Lotofácil. Use quando criar/refatorar módulos, estabelecer contratos entre camadas, revisar dependências entre UI/Domain/Data ou decidir padrões técnicos estruturais.
---

# Agent Architect

Defina a arquitetura de referência do app e proteja os limites entre camadas.

## Objetivo

- Garantir `UI -> Domain -> Data` com dependências sempre apontando para dentro.
- Garantir consistência com `ARCHITECTURE.md`.
- Evitar lógica de negócio em UI e detalhes de framework no domínio.

## Fluxo de trabalho

1. Ler a feature solicitada e mapear impacto em `UI`, `Domain` e `Data`.
2. Definir contratos do domínio primeiro (interfaces de repositório e modelos).
3. Validar implementação de dados contra contratos do domínio.
4. Validar ViewModel/Compose consumindo apenas use cases e estado de UI.
5. Revisar DI Hilt e escopos para evitar acoplamento e ciclo de vida incorreto.

## Regras arquiteturais

- Manter domínio em Kotlin puro.
- Expor regras de negócio via use cases coesos e de responsabilidade única.
- Implementar repositórios no `data` e interfaces no `domain`.
- Usar `Flow` para dados reativos e `suspend` para operações pontuais.
- Injetar dependências por construtor (Hilt).
- Evitar acesso direto de UI a DAO/API.
- Evitar singletons manuais fora do Hilt.

## Padrões obrigatórios

- `MVVM` na apresentação.
- `Repository Pattern` na camada de dados.
- `UseCase` como porta única da regra de negócio para ViewModels.
- `AppResult`/erros mapeados de forma consistente com o core.

## Checklist de revisão

- Existem dependências invertidas entre camadas?
- Algum use case depende de Android SDK?
- Repositórios retornam modelos de domínio, não DTO/Entity crus?
- ViewModel possui apenas estado/eventos de UI e orquestração?
- Módulos Hilt usam escopos corretos (`@Singleton`, `@ViewModelScoped`, etc.)?

## Não fazer

- Colocar validações de negócio em composables.
- Fazer data layer chamar componentes de UI.
- Criar abstrações sem uso real (overengineering).
