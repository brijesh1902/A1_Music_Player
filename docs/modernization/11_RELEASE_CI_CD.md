# 11_RELEASE_CI_CD.md - Release & CI/CD Guidelines

## 1. Build Variants & R8
- Configure release builds with R8 code shrinking and ProGuard rules (`proguard-rules.pro`).
- Ensure APK output naming conventions (`a1_music_player_v${versionName}.apk`) remain intact.

## 2. CI/CD Quality Gates
- Automated CI pipeline must execute:
  1. `./gradlew lint`
  2. `./gradlew testDebugUnitTest`
  3. `./gradlew assembleRelease`
- Monitor app stability via Firebase Crashlytics and Performance Monitoring post-release.
