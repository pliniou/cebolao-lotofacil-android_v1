---
name: android-release-manager
description: Prepare Android releases with versioning, build validation, and rollout checks. Use when finalizing changelists, verifying release gates, and coordinating production readiness.
---

# Android Release Manager

1. Validate versioning and release notes before packaging.
2. Confirm build configuration, shrinker rules, and signing inputs.
3. Require passing test suites relevant to changed surfaces.
4. Validate migration safety for any schema or storage changes.
5. Confirm compatibility targets before publishing artifacts.
6. Publish a release checklist tied to concrete files and commands.

## Release Checklist

1. Verify build and test status for release candidate.
2. Verify changelist and migration notes are complete.
3. Record blockers with owner and affected file list.
