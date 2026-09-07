# 02_TARGET_ARCHITECTURE.md - Target MVI & Clean Architecture

## 1. Architectural Overview

The target architecture for **A1 Music Player** transitions from legacy Java MVVM to a modern, reactive, Unidirectional Data Flow (MVI) architecture using Jetpack Compose, Kotlin Coroutines, Kotlin Flow (`StateFlow` / `SharedFlow`), and Clean Architecture principles.

```text
Jetpack Compose UI (Stateless UI + State hoisting)
    ↓ (User Intent / Events)
MVI ViewModel (viewModelScope, MutableStateFlow<UiState>)
    ↓ (Coroutines / suspend functions)
Use Case / Domain Interactor (Business Logic)
    ↓ (Flow / Suspend)
Repository (Source of Truth: Room DB / MediaStore / Firebase)
```

---

## 2. Layer Definitions

### A. Presentation Layer (Jetpack Compose + MVI)
- **UI State (`UiState`)**: An immutable data class representing the exact state of the screen at any given moment.
- **Intents / Events (`UiIntent`)**: User actions or system events dispatched from the UI to the ViewModel (e.g., `PlaySong(id)`, `ToggleFavorite`, `SearchQueryChanged`).
- **Effects (`UiEffect`)**: One-time side effects (e.g., navigation events, toast messages, error snackbars) emitted via `SharedFlow`.
- **ViewModel**: Observes intents, executes domain logic, and updates `StateFlow<UiState>`.

### B. Domain Layer (Optional Interactors / Use Cases)
- Contains pure Kotlin business logic (e.g., sorting songs, filtering playlists, managing playback queues).
- Independent of Android framework dependencies.

### C. Data Layer (Repositories & Data Sources)
- **Repositories**: Coordinate between local data sources (Room DAOs, `SharePreference`, MediaStore) and remote data sources (Firebase).
- **Room DAOs**: Upgraded to return `Flow<List<Entity>>` and suspend functions.

---

## 3. Package Structure (Target)

```text
com.brizzs.a1musicplayer/
├── data/
│   ├── dao/
│   ├── db/
│   ├── model/
│   └── repository/
├── domain/
│   ├── model/
│   └── usecase/
├── ui/
│   ├── common/
│   ├── main/
│   ├── player/
│   ├── playlist/
│   └── theme/
├── service/
└── utils/
```
