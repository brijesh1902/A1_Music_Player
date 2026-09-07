# GEMINI.md - AI Development Contract for A1 Music Player

Welcome, AI coding assistant (Gemini), to the **A1 Music Player** Android project (`com.brizzs.a1musicplayer`). This document serves as the binding engineering contract and operational protocol for all AI-assisted development sessions.

---

## 1. Primary Objective & Core Mandate

**Modernize the existing Android application incrementally and safely while preserving all existing business logic, functional behavior, audio playback state, media controls, database schemas, API contracts, security practices, accessibility features, and user experience.**

Modernization is **behavior-preserving**. Do not perform greenfield rewrites or speculative refactoring.

---

## 2. Gemini Operational Rules

Before modifying any file or proposing code changes, Gemini must:
1. **Understand** the existing implementation by inspecting related source files, layout files, and manifests.
2. **Inspect** callers, dependencies, and side effects across the architecture.
3. **Classify** the task into one of the following categories:
   - `MIGRATION` (e.g., XML to Compose, LiveData to StateFlow, Java to Kotlin)
   - `FEATURE` (adding new capabilities)
   - `BUGFIX` (resolving issues)
   - `REFACTOR` (improving structure without changing behavior)
   - `PERFORMANCE` (optimizing bottlenecks)
   - `SECURITY` (hardening data or components)
   - `RELEASE` (versioning, signing, R8)
4. **Explain** the impact, affected components, and risks.
5. **Make the smallest safe change** using incremental coexistence (`Java + Kotlin`, `XML + Compose`, `MVVM + MVI`, `LiveData + Flow`).
6. **Avoid unrelated refactoring** or upgrading unrequested libraries.
7. **Run validation** (builds, tests where applicable).
8. **Report** precisely what changed and what existing behavior was preserved.

---

## 3. Tech Stack Baseline

- **Application ID:** `com.brizzs.a1musicplayer`
- **Languages:** Java (migrating to Kotlin 2.0.21)
- **UI:** XML Layouts & ViewBinding (migrating gradually to Jetpack Compose Material 3)
- **Architecture:** MVVM with Repositories (migrating to MVI / Unidirectional Data Flow with Coroutines & StateFlow)
- **Database:** Room 2.8.3 (`AlbumDB`, `ArtistDB`, `PlayListDB`, `SongsDB`)
- **Media Playback:** ExoPlayer 2.19.1 (`MusicService`, `NotificationReceiver`)
- **Build / Toolchain:** AGP 8.1.1, Gradle 8.1.1, compileSdk 36, targetSdk 36, minSdk 23, Java 17 compatibility.
- **Firebase:** BOM 31.1.1 (Analytics, Crashlytics, Perf, Messaging)

---

## 4. Documentation References

All modernization standards, plans, and architectural decisions are documented under [docs/modernization/README.md](file:///D:/Brijesh/Playstore%20Apps/A1%20Music%20Player/docs/modernization/README.md):
- [00_PROJECT_RULES.md](file:///D:/Brijesh/Playstore%20Apps/A1%20Music%20Player/docs/modernization/00_PROJECT_RULES.md)
- [01_CURRENT_STATE_AUDIT.md](file:///D:/Brijesh/Playstore%20Apps/A1%20Music%20Player/docs/modernization/01_CURRENT_STATE_AUDIT.md)
- [02_TARGET_ARCHITECTURE.md](file:///D:/Brijesh/Playstore%20Apps/A1%20Music%20Player/docs/modernization/02_TARGET_ARCHITECTURE.md)
- [03_MIGRATION_PLAN.md](file:///D:/Brijesh/Playstore%20Apps/A1%20Music%20Player/docs/modernization/03_MIGRATION_PLAN.md)
- [13_GEMINI_TASK_WORKFLOW.md](file:///D:/Brijesh/Playstore%20Apps/A1%20Music%20Player/docs/modernization/13_GEMINI_TASK_WORKFLOW.md)
