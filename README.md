# CBC Teachers' Toolkit

An Android app built with Kotlin and Jetpack Compose to support CBC lesson preparation for secondary education contexts in the Ndejje University community.

## Project Goal
- Provide quick offline access to sample CBC teaching resources.
- Demonstrate capstone technical requirements: MVVM, Compose navigation, LazyColumn, state management, Room persistence, and testing.

## Core Features
- Home screen with subject cards and animated entry.
- Animated splash screen with automatic transition to home.
- Subject/topic browsing with search and filter by subject/class.
- Resource detail screen with:
  - Lesson plan
  - Project ideas
  - Assessment rubric
  - Teaching tips
- Favorites and teacher notes persisted with Room.
- `My Library` screen for quick access to favorite and noted topics.
- Room database seeding for offline-first usage.
- Animations across splash, list entry, loading content, and detail interactions.

## Technical Stack
- Kotlin
- Jetpack Compose (Material 3)
- MVVM architecture
- Room Database
- Navigation Compose

## Screens (Current)
- `SplashScreen`
- `HomeScreen`
- `SubjectsScreen`
- `LibraryScreen`
- `ResourceDetailScreen`

## Testing Summary
- `toggleFavorite adds and removes topic id`
- `searchQuery filters topics by title`
- `saveNote stores note in uiState map`
- `splashTransitionsToHomeScreen` (instrumentation)

Tests are located in `SubjectViewModelTest` and `AppNavigationTest`.

## Capstone Requirement Mapping
- Kotlin + Jetpack Compose: implemented.
- MVVM architecture: implemented with `SubjectViewModel` + repository layer.
- Dynamic lists: implemented with `LazyColumn`/`LazyRow`.
- Navigation with 3+ screens: implemented (`Splash`, `Home`, `Subjects`, `Library`, `Detail`).
- State management: Compose state + `StateFlow`.
- Persistence: Room entities/DAO/repository (topics, favorites, notes).
- Tests: unit tests + one instrumentation navigation test.
- Clean `MainActivity`: entry point only.

## Team Roles
Replace placeholders with final names before submission.

- Lead Developer: `TBD`
- UI/UX Specialist: `TBD`
- Git and Quality Manager: `TBD`
- Testing and QA Engineer: `TBD`
- Documentation and Research Lead: `TBD`

## Build and Run
1. Open project in Android Studio.
2. Sync Gradle.
3. Run on emulator or Android device (min SDK 24).
4. Run tests:
   - Unit: `./gradlew testDebugUnitTest`
   - Instrumentation: `./gradlew connectedDebugAndroidTest`

## Submission Checklist
- [ ] Replace team role placeholders with actual names.
- [ ] Capture screenshots for all main screens.
- [ ] Generate and verify release/debug APK.
- [ ] Include proposal and final report PDFs.
- [ ] Ensure latest commits are pushed to GitHub.