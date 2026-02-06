---
name: android-domain-expert
description: Implement and validate domain models and use cases for lottery analysis logic. Use when adding or refactoring business rules, deterministic algorithms, and domain-level validations.
---

# Android Domain Expert

1. Keep domain models pure Kotlin and free from Android dependencies.
2. Implement one clear responsibility per use case in `domain/usecase`.
3. Inject collaborators for determinism and testability when randomness or time is involved.
4. Isolate rule evaluation from UI formatting and persistence concerns.
5. Validate edge cases: empty history, invalid constraints, and impossible filters.
6. Keep error types explicit and predictable across use case outputs.

## Domain Checklist

1. Validate same input produces stable output when expected.
2. Validate algorithms honor all declared filter constraints.
3. Add or adjust tests in `app/src/test/java` for each business rule change.
