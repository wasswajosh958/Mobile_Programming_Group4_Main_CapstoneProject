package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
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

data class UpdatesUiState(
    val isUpdating: Boolean = false,
    val status: String = "Idle",
    val message: String = ""
)

class SubjectViewModel(
    private val repository: TopicRepository
) : ViewModel() {

    private val selectedSubject = MutableStateFlow<String?>(null)
    private val selectedClassLevel = MutableStateFlow<String?>(null)
    private val searchQuery = MutableStateFlow("")
    val uiState: StateFlow<SubjectsUiState> = combine(
        repository.observeTopics(),
        selectedSubject,
        selectedClassLevel,
        searchQuery,
        repository.observeFavoriteIds(),
        repository.observeNotes()
    ) { flowArray ->
        val topics = flowArray[0] as List<Topic>
        val subject = flowArray[1] as String?
        val classLevel = flowArray[2] as String?
        val query = flowArray[3] as String
        val favoriteIds = flowArray[4] as Set<Int>
        val noteMap = flowArray[5] as Map<Int, String>

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

    private val _updatesState = MutableStateFlow(UpdatesUiState())
    val updatesState: StateFlow<UpdatesUiState> = _updatesState

    // Hosted JSON index (safe: metadata + links only). You can replace this later with your own hosting.
    private val resourceIndexUrl =
        "https://raw.githubusercontent.com/wasswajosh958/Mobile_Programming_Group4_Main_CapstoneProject/main/resources/resource_index.json"

    fun updateResourcesNow() {
        viewModelScope.launch {
            _updatesState.value = UpdatesUiState(isUpdating = true, status = "Updating", message = "")
            try {
                val count = repository.syncResourcesFromIndexUrl(resourceIndexUrl)
                _updatesState.value = UpdatesUiState(
                    isUpdating = false,
                    status = "Updated",
                    message = "Downloaded $count resources for offline use."
                )
            } catch (e: Exception) {
                _updatesState.value = UpdatesUiState(
                    isUpdating = false,
                    status = "Failed",
                    message = e.message ?: "Update failed"
                )
            }
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
        viewModelScope.launch {
            repository.toggleFavorite(topicId)
        }
    }

    fun saveNote(topicId: Int, note: String) {
        viewModelScope.launch {
            repository.saveNote(topicId, note)
        }
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