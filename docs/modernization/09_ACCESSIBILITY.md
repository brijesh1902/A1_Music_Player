# 09_ACCESSIBILITY.md - Accessibility Standards

## 1. TalkBack & Semantics
- Every interactive icon, button, and media control must have descriptive content descriptions (`contentDescription` in XML, `Modifier.semantics` or `contentDescription` in Compose).
- Ensure custom music playback sliders and buttons are fully navigable via TalkBack.

## 2. Touch Targets & Contrast
- Minimum touch target size must be at least `48x48dp`.
- Ensure high color contrast ratios between text and background across both Light and Dark themes.
