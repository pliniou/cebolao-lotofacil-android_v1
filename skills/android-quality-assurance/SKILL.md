---
name: android-quality-assurance
description: Define and execute verification strategy for unit, UI, and snapshot test coverage. Use when validating behavior changes, regressions, edge cases, and release readiness gates.
---

# Android Quality Assurance

1. Identify risk surface for each change before writing tests.
2. Prioritize domain and viewmodel unit tests for behavior regressions.
3. Use UI and snapshot tests for visual and interaction stability.
4. Cover loading, content, empty, and error scenarios for modified screens.
5. Verify accessibility and localization for new user-facing strings.
6. Report gaps with direct file-level test targets.

## QA Checklist

1. List tests added or updated per changed file.
2. List uncovered high-risk paths.
3. Block release when core regressions are unverified.
