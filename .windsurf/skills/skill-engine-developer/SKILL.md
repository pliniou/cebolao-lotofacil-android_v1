---
name: skill-engine-developer
description: Desenvolva engines e algoritmos de domínio em Kotlin para geração/análise do Cebolão Lotofácil. Use quando implementar regras estatísticas, filtros, cálculo de indicadores, otimização algorítmica e testes de corretude/performance.
---

# Skill Engine Developer

Implemente lógica algorítmica com foco em corretude, previsibilidade e eficiência.

## Objetivo

- Criar engines reutilizáveis para geração e análise de jogos.
- Garantir resultados determinísticos quando exigido.
- Equilibrar precisão estatística, legibilidade e performance.

## Fluxo de trabalho

1. Definir regra matemática/estatística e entradas/saídas.
2. Modelar API da engine em Kotlin puro.
3. Implementar algoritmo com tratamento explícito de bordas.
4. Criar testes unitários abrangentes.
5. Medir custo (tempo/memória) e otimizar pontos críticos.
6. Integrar via use case no domínio.

## Regras de implementação

- Isolar engine de Android e de infraestrutura.
- Evitar efeitos colaterais ocultos.
- Documentar premissas e limitações da heurística.
- Tratar entradas inválidas com erros de domínio claros.
- Priorizar estruturas de dados adequadas ao volume de concursos.

## Checklist de revisão

- Algoritmo reproduz resultado esperado em casos conhecidos?
- Bordas (nulo, vazio, limites) estão cobertas?
- Existe risco de explosão combinatória sem guarda?
- Código está pronto para benchmark ou profiling direcionado?

## Não fazer

- Misturar engine com persistência, rede ou UI.
- Otimizar prematuramente sem medição.
- Introduzir aleatoriedade sem estratégia de seed/teste.