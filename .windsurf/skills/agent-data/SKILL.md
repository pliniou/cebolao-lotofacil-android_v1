---
name: agent-data
description: Projete e implemente a camada de dados do Cebolão Lotofácil (Room, Retrofit, WorkManager, cache e sincronização). Use quando modelar persistência, criar repositórios, definir estratégia offline-first, migrar dados legados ou tratar consistência e integridade de dados.
---

# Agent Data

Implemente a camada de dados com foco em confiabilidade, sincronização e operação offline.

## Objetivo

- Entregar dados ao domínio via contratos definidos no `domain`.
- Priorizar `offline-first` com `Room` como fonte primária de leitura.
- Sincronizar com rede sem bloquear UI.

## Fluxo de trabalho

1. Ler contrato do repositório no domínio.
2. Modelar `Entity`, `Dao`, `DataSource` e `RepositoryImpl`.
3. Implementar mapeadores `DTO <-> Entity <-> Domain`.
4. Definir estratégia de sync (`stale-while-revalidate` + WorkManager).
5. Tratar erros, retries e idempotência.
6. Validar migrações e emissão reativa via `Flow`.

## Regras de implementação

- Ler do `Room` primeiro.
- Atualizar cache local após fetch remoto bem-sucedido.
- Expor stream com `Flow` para dados observáveis.
- Usar `suspend` para operações pontuais.
- Fazer I/O sempre fora da main thread.
- Configurar WorkManager com constraints de rede e backoff exponencial.

## Migração e seed

- Seed inicial por assets quando necessário.
- Migrar legado (`lottery_data.json`) para Room no startup.
- Remover fonte legada após migração concluída.
- Versionar schema e manter compatibilidade de parser.

## Checklist de revisão

- Repository implementa interface do domínio sem vazamento de framework?
- Mapeamento remove dependência de DTO/Entity no domínio?
- Estratégia de retry evita duplicidade em reexecuções?
- Room migration preserva dados existentes?
- Fluxos propagam atualização após sync?

## Não fazer

- Implementar regra de negócio complexa no repositório.
- Retornar exceções de baixo nível sem tradução.
- Bloquear UI aguardando rede.