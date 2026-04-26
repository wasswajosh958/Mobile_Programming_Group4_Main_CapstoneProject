# CBC Teachers' Toolkit

An Android app built with Kotlin and Jetpack Compose to support CBC lesson preparation for secondary education contexts in the Ndejje University community.

## Project Goal
- Provide quick offline access to sample CBC teaching resources.
- Demonstrate capstone technical requirements: MVVM, Compose navigation, LazyColumn, state management, Room persistence, and testing.

## Core Features
- Home screen with subject cards and animated entry.
- Subject/topic browsing with search and filter by subject/class.
- Resource detail screen with:
  - Lesson plan
  - Project ideas
  - Assessment rubric
  - Teaching tips
- Favorites and teacher notes.
- Room database seeding for offline-first usage.
- Basic animations across list/detail UX.

## Technical Stack
- Kotlin
- Jetpack Compose (Material 3)
- MVVM architecture
- Room Database
- Navigation Compose

## Screens (Current)
- `HomeScreen`
- `SubjectsScreen`
- `ResourceDetailScreen`

## Testing Summary
- `toggleFavorite adds and removes topic id`
- `searchQuery filters topics by title`

Both tests are located in `SubjectViewModelTest`.

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