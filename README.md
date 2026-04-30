CBC Teachers' Toolkit

An Android app built with Kotlin and Jetpack Compose to support CBC lesson preparation for secondary education contexts in the Ndejje University community.

Project Goal
- Provide quick offline access to sample CBC teaching resources.
- Demonstrate capstone technical requirements: MVVM, Compose navigation, LazyColumn, state management, Room persistence, and testing

Core Features
- Home screen with subject cards and animated entry
- Animated splash screen with automatic transition to home
- Subject/topic browsing with search and filter by subject/class
- Collapsible animated sections in topic detail content
- Resource detail screen with;
  - Lesson plan
  - Project ideas
  - Assessment rubric
  - Teaching tips
- Favorites and teacher notes persisted with Room
- Scheme of Work builder with in-app design guide and offline saves
- Share saved schemes via Android share sheet
- My Library screen for quick access to favorite and noted topics
- Room database seeding for offline-first usage
- Manual resource update flow: download links while online and use them offline
- Downloaded topic resources include official links, teaching video links, and notes-search links
- Animations across splash, list entry, loading content, detail interactions, and scheme guide transitions

Technical Stack
- Kotlin
- Jetpack Compose (Material 3)
- MVVM architecture
- Room Database
- Navigation Compose

Screens (Current)
- `plashScreen
- HomeScreen
- SubjectsScreen
- LibraryScreen
- AboutScreen
- UpdatesScreen
- SchemeBuilderScreen
- ResourceDetailScreen

Testing Summary
- toggleFavorite adds and removes topic id
- observeTopics returns seeded topics
- saveNote stores note in uiState map
- splashTransitionsToHomeScreen

Capstone Requirement Mapping
- Kotlin + Jetpack Compose: implemented.
- MVVM architecture: implemented with SubjectViewModel and repository layer
- Dynamic lists: implemented with LazyColumn
- Navigation with 3+ screens: implemented that is Splash, Home, Subjects, Library, Detailes
- State management: Compose state and StateFlow
- Persistence: Room entities/DAO/repository (topics, favorites, notes).
- Persistence: Room entities/DAO/repository  with topics, favorites, notes, resources, schemes
- Tests: unit tests + one instrumentation navigation test


