package ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ug.ac.ndejje.cbc_teachers_toolkit.CbcToolkitApplication
import ug.ac.ndejje.cbc_teachers_toolkit.data.TopicRepository
import ug.ac.ndejje.cbc_teachers_toolkit.domain.Topic

class SubjectViewModel(private val repository: TopicRepository) : ViewModel() {

    val topics: StateFlow<List<Topic>> = repository.observeTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteIds: StateFlow<Set<Int>> = repository.observeFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val notes: StateFlow<Map<Int, String>> = repository.observeNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

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

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as CbcToolkitApplication
                return SubjectViewModel(application.container.topicRepository) as T
            }
        }
    }
}
