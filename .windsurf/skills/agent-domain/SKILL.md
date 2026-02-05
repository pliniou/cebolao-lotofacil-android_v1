---
name: agent-domain
description: Defina e evolua o domínio do Cebolão Lotofácil em Kotlin puro. Use quando criar regras de negócio, use cases, validações, contratos de repositório ou modelos de erro/resultado independentes de Android.
---

# Agent Domain

Implemente o núcleo de negócio com foco em pureza, corretude e testabilidade.

## Objetivo

- Centralizar todas as regras de negócio no domínio.
- Manter camada de domínio independente de Android/frameworks.
- Expor contratos claros para UI e Data.

## Fluxo de trabalho

1. Identificar regra de negócio e invariantes.
2. Modelar entidades/VOs e erros de domínio.
3. Criar use case de responsabilidade única.
4. Definir interface de repositório no domínio (quando necessário).
5. Cobrir com testes unitários e casos de borda.

## Regras de implementação

- Usar Kotlin puro no domínio.
- Evitar `Context`, `LiveData`, classes Android e DTOs externos.
- Implementar validações e cálculos somente aqui.
- Retornar resultados explícitos (`AppResult`, sealed class ou equivalente).
- Injetar dependências não determinísticas (tempo, aleatoriedade, etc.).

## Contratos com outras camadas

- Definir interfaces no domínio; implementar no data.
- Expor saídas previsíveis para ViewModel (sucesso, vazio, erro, loading quando aplicável).
- Evitar regras duplicadas em UI ou repositório.

## Checklist de revisão

- Existe lógica de negócio fora do domínio?
- Use case possui escopo único e nome orientado à ação?
- Resultado cobre cenários de falha importantes?
- Testes cobrem bordas e comportamento determinístico?

## Não fazer

- Acoplar domínio a Room/Retrofit/Compose.
- Colocar regra de negócio em ViewModel/composable.
- Criar use case genérico demais e sem foco.