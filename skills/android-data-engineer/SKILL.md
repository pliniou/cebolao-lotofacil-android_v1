---
name: android-data-engineer
description: Build and review data layer flows for local cache, remote sync, and repository implementations. Use when implementing Room, Retrofit, retry/backoff, offline-first behavior, and data mapping.
---

# Android Data Engineer

1. Implement repositories in `data/repository` behind `domain/repository` interfaces.
2. Apply local-first reads and background refresh for history and stats flows.
3. Keep DTO-to-domain mapping inside `data`.
4. Use coroutine-safe I/O and avoid blocking main thread.
5. Apply retry with bounded exponential backoff for remote fetches.
6. Keep persistence writes idempotent when syncing repeated payloads.
7. Update Hilt bindings when adding or renaming repositories.

## Data Checklist

1. Confirm local cache emits before remote refresh.
2. Confirm API failures degrade gracefully with cached data.
3. Confirm schema changes have explicit migrations.
