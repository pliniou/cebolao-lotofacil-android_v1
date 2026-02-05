---
name: skill-offline-first
description: Aplique estratégia offline-first com stale-while-revalidate no Cebolão Lotofácil. Use quando implementar leitura local prioritária, seed/migração inicial, sincronização resiliente em background e sinalização de staleness para o usuário.
---

# Skill Offline First

Garanta funcionamento confiável sem rede e atualização transparente quando a conectividade voltar.

## Princípios

- Guiar UI por dados locais.
- Usar Room como fonte primária persistente.
- Sincronizar em background com WorkManager.
- Adotar `stale-while-revalidate`.

## Fluxo de implementação

1. Ler dados locais e emitir imediatamente para UI.
2. Verificar staleness por modalidade/concurso.
3. Disparar sync assíncrono quando houver rede.
4. Mapear DTO para entidade/domínio antes de persistir.
5. Atualizar Room e propagar novo estado via Flow.

## Seed e migração

- Carregar seed de assets na primeira execução.
- Detectar `lottery_data.json` legado e migrar para Room.
- Remover artefato legado após migração concluída.
- Registrar versão de schema dos assets e fallback controlado.

## Confiabilidade

- Aplicar retry com backoff exponencial.
- Garantir idempotência de sync.
- Registrar logs/telemetria para falhas recorrentes.
- Não bloquear UI em operações de I/O/rede.

## Checklist de revisão

- Aplicativo funciona com internet desligada?
- Sync atualiza cache sem quebrar leitura local?
- Usuário recebe indicação de dado desatualizado quando necessário?
- Falhas de rede não causam perda de dados?

## Não fazer

- Tornar rede pré-condição para abrir tela principal.
- Escrever direto no estado de UI sem persistir no local.
- Executar migração em thread principal.