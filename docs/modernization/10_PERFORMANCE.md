# 10_PERFORMANCE.md - Performance Standards

## 1. Core Rule: Measure Before Optimizing
- Do not introduce speculative performance optimizations. Use Android Studio Profiler (CPU, Memory, Energy) and Perfetto traces to identify actual bottlenecks.

## 2. Compose Recomposition Optimization
- Use stable types in Compose states.
- Avoid unnecessary recompositions by leveraging lambda parameter passing and avoiding inline calculations inside heavy `LazyColumn` items.

## 3. Media & Memory Management
- Properly release Glide image request targets and ExoPlayer player instances when activities/services are destroyed to prevent memory leaks.
