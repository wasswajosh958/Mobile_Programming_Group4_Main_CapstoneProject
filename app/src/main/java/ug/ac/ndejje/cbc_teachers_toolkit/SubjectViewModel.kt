package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ug.ac.ndejje.cbc_teachers_toolkit.data.TopicRepository
import ug.ac.ndejje.cbc_teachers_toolkit.domain.Topic

data class SubjectsUiState(
    val isLoading: Boolean = true,
    val allTopics: List<Topic> = emptyList(),
    val selectedSubject: String? = null,
    val selectedClassLevel: String? = null,
    val searchQuery: String = "",
    val availableSubjects: List<String> = emptyList(),
    val availableClassLevels: List<String> = emptyList(),
    val filteredTopics: List<Topic> = emptyList(),
    val favorites: Set<Int> = emptySet(),
    val notes: Map<Int, String> = emptyMap()
)

class SubjectViewModel(
    private val repository: TopicRepository
) : ViewModel() {

    private val selectedSubject = MutableStateFlow<String?>(null)
    private val selectedClassLevel = MutableStateFlow<String?>(null)
    private val searchQuery = MutableStateFlow("")
    private val favorites = MutableStateFlow<Set<Int>>(emptySet())
    private val notes = MutableStateFlow<Map<Int, String>>(emptyMap())

    val uiState: StateFlow<SubjectsUiState> = combine(
        repository.observeTopics(),
        selectedSubject,
        selectedClassLevel,
        searchQuery,
        favorites,
        notes
    ) { topics, subject, classLevel, query, favoriteIds, noteMap ->
        val normalizedQuery = query.trim().lowercase()
        val filtered = topics.filter { topic ->
            val subjectMatches = subject.isNullOrBlank() || topic.subject == subject
            val classMatches = classLevel.isNullOrBlank() || topic.classLevel == classLevel
            val queryMatches = normalizedQuery.isBlank() ||
                topic.title.lowercase().contains(normalizedQuery) ||
                topic.subject.lowercase().contains(normalizedQuery) ||
                topic.classLevel.lowercase().contains(normalizedQuery)

            subjectMatches && classMatches && queryMatches
        }

        SubjectsUiState(
            isLoading = false,
            allTopics = topics,
            selectedSubject = subject,
            selectedClassLevel = classLevel,
            searchQuery = query,
            availableSubjects = topics.map { it.subject }.distinct(),
            availableClassLevels = topics.map { it.classLevel }.distinct().sorted(),
            filteredTopics = filtered,
            favorites = favoriteIds,
            notes = noteMap
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectsUiState()
    )

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
        }
    }

    fun selectSubject(subject: String?) {
        selectedSubject.value = subject
    }

    fun selectClassLevel(classLevel: String?) {
        selectedClassLevel.value = classLevel
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun toggleFavorite(topicId: Int) {
        val current = favorites.value.toMutableSet()
        if (current.contains(topicId)) current.remove(topicId) else current.add(topicId)
        favorites.value = current
    }

    fun saveNote(topicId: Int, note: String) {
        val updated = notes.value.toMutableMap()
        if (note.isBlank()) updated.remove(topicId) else updated[topicId] = note
        notes.value = updated
    }

    fun topicById(topicId: Int): Topic? {
        return uiState.value.allTopics.firstOrNull { it.id == topicId }
    }

    class Factory(
        private val repository: TopicRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SubjectViewModel::class.java)) {
                return SubjectViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}