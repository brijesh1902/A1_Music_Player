# 14_DEPENDENCY_MODERNIZATION.md - Dependency Modernization Matrix

| Dependency | Current Version | Purpose | Current Status | Target | Migration Risk |
| ---------- | --------------- | ------- | -------------- | ------ | -------------- |
| Kotlin BOM | `2.0.21` | Kotlin stdlib & coroutines | Good | Keep / Latest stable | Low |
| Firebase BOM | `31.1.1` | Analytics, Crashlytics, Perf, Messaging | Stable | Update incrementally | Low |
| AndroidX AppCompat | `1.7.1` | UI base | Stable | Keep | Low |
| Material Components | `1.13.0` | Material 3 / Views | Stable | Keep / Compose Material 3 | Low |
| Room DB | `2.8.3` | Local SQLite database | Stable | Keep / Coroutine support | Low |
| ExoPlayer | `2.19.1` | Audio playback | Legacy | Migrate to Media3 ExoPlayer (`androidx.media3:media3-exoplayer`) | Medium |
| Glide | `5.0.5` | Image loading | Stable | Keep / Coil (optional Compose) | Low |
| Lottie | `6.7.1` | Animations | Stable | Keep | Low |
| Dexter | `6.2.3` | Runtime permissions | Deprecated | Migrate to ActivityResultContracts | Low |
