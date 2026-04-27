package ug.ac.ndejje.cbc_teachers_toolkit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ug.ac.ndejje.cbc_teachers_toolkit.util.downloadFile
import ug.ac.ndejje.cbc_teachers_toolkit.util.getResourceDestinationFile
import java.io.File
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ug.ac.ndejje.cbc_teachers_toolkit.data.TopicRepository
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.SchemeOfWorkEntity
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TeachingResourceEntity
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
    val status: UpdateStatus = UpdateStatus.IDLE,
    val downloadedCount: Int = 0,
    val errorMessage: String = ""
)

data class SchemeDraftUiState(
    val teacherName: String = "",
    val schoolName: String = "",
    val subject: String = "",
    val classLevel: String = "",
    val term: String = "",
    val week: String = "",
    val topicTitle: String = "",
    val competency: String = "",
    val objectives: String = "",
    val activities: String = "",
    val resources: String = "",
    val assessment: String = "",
    val date: String = ""
)

enum class SchemeSaveStatus {
    NONE,
    VALIDATION_ERROR,
    SUCCESS
}

enum class UpdateStatus {
    IDLE,
    UPDATING,
    UPDATED,
    FAILED
}

class SubjectViewModel(
    private val repository: TopicRepository
) : ViewModel() {

    private val selectedSubject = MutableStateFlow<String?>(null)
    private val selectedClassLevel = MutableStateFlow<String?>(null)
    private val searchQuery = MutableStateFlow("")
    private val filtersFlow = combine(
        selectedSubject,
        selectedClassLevel,
        searchQuery
    ) { subject, classLevel, query ->
        Triple(subject, classLevel, query)
    }
    private val teacherDataFlow = combine(
        repository.observeFavoriteIds(),
        repository.observeNotes()
    ) { favoriteIds, noteMap ->
        favoriteIds to noteMap
    }

    val uiState: StateFlow<SubjectsUiState> = combine(
        repository.observeTopics(),
        filtersFlow,
        teacherDataFlow
    ) { topics, filters, teacherData ->
        val (subject, classLevel, query) = filters
        val (favoriteIds, noteMap) = teacherData
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
    private val _schemeDraftState = MutableStateFlow(SchemeDraftUiState())
    val schemeDraftState: StateFlow<SchemeDraftUiState> = _schemeDraftState
    private val _schemeSaveStatus = MutableStateFlow(SchemeSaveStatus.NONE)
    val schemeSaveStatus: StateFlow<SchemeSaveStatus> = _schemeSaveStatus

    val schemes = repository.observeSchemes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = emptyList()
    )

    val downloadedResources = repository.observeDownloadedResources().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = emptyList()
    )

    // Hosted JSON index (safe: metadata + links only). You can replace this later with your own hosting.
    private val resourceIndexUrl =
        "https://raw.githubusercontent.com/wasswajosh958/Mobile_Programming_Group4_Main_CapstoneProject/main/resources/resource_index.json"

    fun updateResourcesNow() {
        viewModelScope.launch {
            _updatesState.value = UpdatesUiState(isUpdating = true, status = UpdateStatus.UPDATING)
            try {
                val count = repository.syncResourcesFromIndexUrl(resourceIndexUrl)
                _updatesState.value = UpdatesUiState(
                    isUpdating = false,
                    status = UpdateStatus.UPDATED,
                    downloadedCount = count
                )
            } catch (e: Exception) {
                _updatesState.value = UpdatesUiState(
                    isUpdating = false,
                    status = UpdateStatus.FAILED,
                    errorMessage = e.message ?: "Update failed"
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

    fun observeResourcesForTopic(topicId: Int) = repository.observeResourcesForTopic(topicId)

    fun updateSchemeDraft(update: (SchemeDraftUiState) -> SchemeDraftUiState) {
        _schemeDraftState.value = update(_schemeDraftState.value)
    }

    fun downloadResource(context: Context, resource: TeachingResourceEntity) {
        viewModelScope.launch {
            val topic = topicById(resource.topicId) ?: return@launch
            
            val extension = when (resource.type) {
                "VIDEO" -> "mp4"
                "PDF_LINK", "NOTES" -> "pdf"
                else -> "dat"
            }
            val fileName = "${resource.key.hashCode()}.$extension"
            val destinationFile = getResourceDestinationFile(context, topic.subject, topic.classLevel, fileName)
            
            downloadFile(context, resource.url, destinationFile) { savedPath ->
                // CRITICAL FIX: To prevent "corrupted" errors, we now update DB ONLY when download actually finishes.
                viewModelScope.launch {
                    repository.updateResourceDownloadStatus(resource.key, savedPath)
                }
            }
        }
    }

    fun prefillSchemeFromTopic(topicId: Int) {
        val topic = topicById(topicId) ?: return
        val current = _schemeDraftState.value
        if (current.subject.isBlank() && current.classLevel.isBlank() && current.topicTitle.isBlank()) {
            _schemeDraftState.value = current.copy(
                subject = topic.subject,
                classLevel = topic.classLevel,
                topicTitle = topic.title
            )
        }
    }

    fun saveSchemeDraft() {
        val draft = _schemeDraftState.value
        val weekValue = draft.week.toIntOrNull()
        if (draft.teacherName.isBlank() ||
            draft.subject.isBlank() ||
            draft.classLevel.isBlank() ||
            draft.term.isBlank() ||
            weekValue == null ||
            draft.topicTitle.isBlank() ||
            draft.objectives.isBlank() ||
            draft.activities.isBlank() ||
            draft.resources.isBlank() ||
            draft.assessment.isBlank()
        ) {
            _schemeSaveStatus.value = SchemeSaveStatus.VALIDATION_ERROR
            return
        }

        viewModelScope.launch {
            repository.insertScheme(
                SchemeOfWorkEntity(
                    teacherName = draft.teacherName.trim(),
                    schoolName = draft.schoolName.trim(),
                    subject = draft.subject.trim(),
                    classLevel = draft.classLevel.trim(),
                    term = draft.term.trim(),
                    week = weekValue,
                    topicTitle = draft.topicTitle.trim(),
                    competency = draft.competency.trim(),
                    objectives = draft.objectives.trim(),
                    activities = draft.activities.trim(),
                    resources = draft.resources.trim(),
                    assessment = draft.assessment.trim(),
                    date = draft.date.trim()
                )
            )
            _schemeSaveStatus.value = SchemeSaveStatus.SUCCESS
            _schemeDraftState.value = SchemeDraftUiState(
                teacherName = draft.teacherName,
                schoolName = draft.schoolName
            )
        }
    }

    fun clearSchemeSaveStatus() {
        _schemeSaveStatus.value = SchemeSaveStatus.NONE
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