# 06_DATA_NETWORK_STANDARDS.md - Data & Network Standards

## 1. Repository Pattern
- Repositories act as the single source of truth, combining local Room database caches and device MediaStore / Firebase sources.
- Expose data as Kotlin `Flow` streams or suspend functions executing on `Dispatchers.IO`.

## 2. Room Database
- Room DAOs must return `Flow<List<Entity>>` for reactive lists and `suspend` functions for single-shot CRUD operations.
- Database migrations (`Migration`) must be explicitly defined and tested when modifying schemas.

## 3. Error Handling & Result Wrapper
- Wrap data operations in a sealed `Result<T>` (`Success`, `Error`, `Loading`) to ensure robust error handling across UI and data layers without crashing the app.
- Preserve existing Firebase Crashlytics reporting for unhandled exceptions.
