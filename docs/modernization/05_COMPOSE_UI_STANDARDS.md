# 05_COMPOSE_UI_STANDARDS.md - Jetpack Compose UI Standards

## 1. Composable Architecture Pattern
Every modernized screen follows the stateless/stateful pattern:

```kotlin
Route (collects ViewModel state & dispatches intents)
 ↓
Screen (Stateless Composable taking state and callback lambdas)
 ↓
Content Components (Reusable UI building blocks)
```

## 2. State Hoisting & Collection
- Always collect flows using lifecycle-aware extension: `collectAsStateWithLifecycle()`.
- Hoist state to the lowest common ancestor or ViewModel so stateless components remain testable and previewable.

## 3. Material 3 & Theming
- Use Material 3 color schemes, typography, and shape tokens.
- Support dynamic color where appropriate while respecting the app's custom music player branding.

## 4. Previews & Testing
- Every screen and reusable component must provide `@Preview` annotations for Light and Dark themes.
- Use Compose UI testing (`createComposeRule()`) for UI interaction validation.
