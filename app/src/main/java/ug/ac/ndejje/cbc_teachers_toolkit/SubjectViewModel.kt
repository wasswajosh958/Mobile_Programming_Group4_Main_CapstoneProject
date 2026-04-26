package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TopicItem(
    val id: Int,
    val title: String,
    val subject: String
)

class SubjectViewModel : ViewModel() {

    private val _topics = MutableStateFlow<List<TopicItem>>(emptyList())
    val topics: StateFlow<List<TopicItem>> = _topics.asStateFlow()

    init {
        // Hard-coded real CBC sample content (we will expand this later)
        _topics.value = listOf(
            TopicItem(1, "Photosynthesis", "Biology"),
            TopicItem(2, "Cell Division", "Biology"),
            TopicItem(3, "Human Reproduction", "Biology"),
            TopicItem(4, "Set Theory", "Mathematics"),
            TopicItem(5, "Linear Equations", "Mathematics"),
            TopicItem(6, "Grammar and Composition", "English")
        )
    }
}