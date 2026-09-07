# 07_TESTING_STRATEGY.md - Testing Strategy

## 1. Testing Layers
- **Unit Tests**: Test ViewModels, Use Cases, and Repositories using JUnit 5, Mockito/MockK, and Turbine for Coroutine Flows.
- **Database Tests**: Test Room DAOs using `Room.inMemoryDatabaseBuilder`.
- **UI Tests**: Test Jetpack Compose screens using `androidx.compose.ui.test` and `createComposeRule()`.

## 2. Regression Testing Requirement
- Before modifying legacy components (such as `MusicService` or Room DB queries), write regression unit tests capturing current behavior.
- Ensure all tests pass successfully in CI/CD before merging any migration PR.
