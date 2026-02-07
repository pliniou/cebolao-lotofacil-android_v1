---
name: android-performance-specialist
description: Optimize Compose rendering, memory, and runtime performance in this Android app. Use when addressing recomposition hotspots, lazy rendering, startup timing, and measurable performance regressions.
---

# Android Performance Specialist

1. Identify hotspots in `ui/` and `viewmodels/` before changing code.
2. Reduce unnecessary recomposition with `remember`, `derivedStateOf`, and stable keys.
3. Prefer lazy containers for lists and defer expensive UI work when possible.
4. Keep long operations off main thread and apply bounded timeouts.
5. Keep performance instrumentation behind debug-only switches.
6. Propose measurable targets before optimization changes.

## Performance Checklist

1. State expected metric impact per change.
2. Validate no UX regressions after optimization.
3. Document files that need benchmark or profile coverage.
