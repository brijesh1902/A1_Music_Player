# 15_ARCHITECTURE_DECISIONS.md - Architecture Decision Records (ADR)

## ADR-001: Incremental Coexistence Strategy (Java + Kotlin & XML + Compose)
- **Date:** 2025-02-24
- **Status:** Approved
- **Context:** The existing codebase is fully written in Java with XML layouts and MVVM. Rewriting everything at once carries high regression risk.
- **Decision:** Adopt an incremental modernization approach where Java and Kotlin, XML views and Jetpack Compose, LiveData and Flow coexist.
- **Consequences:** Lowers migration risk and allows continuous app delivery during refactoring.

## ADR-002: Transition from ExoPlayer 2.x to Media3 ExoPlayer
- **Date:** 2025-02-24
- **Status:** Planned (Phase 9)
- **Context:** ExoPlayer 2.19.1 is used in `MusicService.java`. AndroidX Media3 is the modern standard for media playback.
- **Decision:** Migrate `MusicService` to Media3 ExoPlayer during Phase 9 while preserving all background playback and notification controls.
