# 00_PROJECT_RULES.md - Project-Wide Modernization Rules

## 1. Core Rule: Incremental & Behavior-Preserving
**Modernization is incremental and behavior-preserving.**
- The application's core business logic, audio playback engine (`MusicService` / ExoPlayer), background notifications, media controls, Room database schemas, permissions handling, and user preferences (`SharePreference`) **must remain unchanged** in behavior.
- No existing user-visible features or playback states may be broken during refactoring or migration.

## 2. Coexistence & Interoperability
- Temporary coexistence of legacy and modern tech stacks is fully supported and encouraged to de-risk migration:
  - Java and Kotlin classes can coexist in the same module.
  - XML views and Jetpack Compose can coexist using `ComposeView` inside fragments/activities.
  - MVVM and MVI patterns can coexist across screens.
  - LiveData and Kotlin Flows can coexist via adapters (`asFlow()` / `asLiveData()`).
- Old implementations must **not** be deleted until the new implementation is fully verified, tested, and proven stable.

## 3. Coding Principles
- **Idiomatic Kotlin**: Write clean, null-safe Kotlin code. Avoid `!!` assertions, platform-type carelessness, and unnecessary mutable state.
- **Unidirectional Data Flow (UDF / MVI)**: Adopt state-in, events-out patterns for all modernized screens and ViewModels.
- **Coroutines & Flow**: Use structured concurrency (`viewModelScope`, `lifecycleScope`) instead of raw threads or callbacks wherever possible.
- **Single Responsibility**: Keep ViewModels focused on UI state management. Delegate business and data operations to Use Cases and Repositories.

## 4. Dependency & Build Rules
- Do not add new third-party dependencies unless strictly necessary and approved.
- Maintain compatibility across Gradle (`8.1.1`), AGP (`8.1.1`), Kotlin (`2.0.21`), Room (`2.8.3`), ExoPlayer (`2.19.1`), and compileSdk / targetSdk (`36`).
- Avoid deprecated APIs where modern Jetpack alternatives exist.

## 5. Security & Privacy Rules
- Never weaken existing permissions or security configurations (`AndroidManifest.xml`).
- Protect user data stored in Room DB and SharedPreferences.
- Ensure proper Exported / Unexported flags on all BroadcastReceivers (`NotificationReceiver`) and Services (`MusicService`, `AppUpdateService`).

## 6. Testing & Quality Gates
- Critical audio playback and database workflows must have regression test coverage before risky migration steps.
- All builds must compile without fatal lint errors (`abortOnError false` can be tightened later during CI/CD hardening).
