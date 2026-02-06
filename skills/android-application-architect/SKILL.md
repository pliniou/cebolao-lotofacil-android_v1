---
name: android-application-architect
description: Enforce Clean Architecture and MVVM boundaries in this Android project. Use when planning or reviewing dependency direction, layer contracts, Hilt modules, and architecture-sensitive refactors.
---

# Android Application Architect

1. Map the requested change to `ui/`, `viewmodels/`, `domain/`, `data/`, and `di/`.
2. Enforce dependency direction as `ui -> domain <- data`.
3. Keep `domain` free of Android SDK and framework imports.
4. Require repository interfaces in `domain` and implementations in `data`.
5. Keep `ViewModel` orchestration in `viewmodels`; avoid direct `data` calls from UI composables.
6. Verify Hilt bindings in `di/` for each new repository or use case.
7. Reject architecture violations before coding.

## Review Output

1. List each violated boundary.
2. List the file that causes the violation.
3. Propose a fix path with exact target files.
