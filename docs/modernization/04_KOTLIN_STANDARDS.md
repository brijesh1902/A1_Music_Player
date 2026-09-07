# 04_KOTLIN_STANDARDS.md - Kotlin Coding Standards

## 1. Null Safety & Operator Guidelines
- **Strictly prohibit `!!` (non-null asserted call)** unless nullability is mathematically impossible and documented.
- Prefer safe calls (`?.`), Elvis operators (`?:`), and standard library functions (`let`, `run`, `apply`, `also`).

## 2. Immutability & Data Classes
- Use `val` by default for all local variables and properties. Use `var` only when state mutation is strictly necessary.
- Represent models and UI states using immutable Kotlin `data class` or `sealed interface` / `sealed class`.

## 3. Coroutines & Structured Concurrency
- Never use `GlobalScope`. Always bind coroutines to lifecycle scopes (`lifecycleScope`, `viewModelScope`).
- Dispatch blocking tasks (Room DB queries, MediaStore file scanning) to `Dispatchers.IO`.
- Expose state streams via `StateFlow` and one-time events via `SharedFlow`.

## 4. Anti-Patterns to Avoid
- Global mutable singleton state.
- Giant God ViewModels or classes exceeding 500 lines.
- Business logic embedded inside UI Composables or Android Views.
