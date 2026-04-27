package ug.ac.ndejje.cbc_teachers_toolkit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ug.ac.ndejje.cbc_teachers_toolkit.data.TopicRepository
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.SchemeOfWorkEntity
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TeachingResourceEntity
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicDao
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicEntity
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicNoteProjection

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleFavorite adds and removes topic id`() = runTest {
        val fakeDao = FakeTopicDao()
        fakeDao.insertAll(seedTopics())
        val repository = TopicRepository(fakeDao)

        repository.toggleFavorite(1)
        advanceUntilIdle()
        assertTrue(repository.observeFavoriteIds().first().contains(1))

        repository.toggleFavorite(1)
        advanceUntilIdle()
        assertTrue(!repository.observeFavoriteIds().first().contains(1))
    }

    @Test
    fun `observeTopics returns seeded topics`() = runTest {
        val fakeDao = FakeTopicDao()
        fakeDao.insertAll(seedTopics())
        val repository = TopicRepository(fakeDao)
        val topics = repository.observeTopics().first()
        assertEquals(2, topics.size)
        assertEquals("Photosynthesis", topics.first().title)
    }

    @Test
    fun `saveNote stores note in uiState map`() = runTest {
        val fakeDao = FakeTopicDao()
        fakeDao.insertAll(seedTopics())
        val repository = TopicRepository(fakeDao)

        repository.saveNote(2, "Revise worked examples for this lesson")
        advanceUntilIdle()

        val note = repository.observeNotes().first()[2]
        assertEquals("Revise worked examples for this lesson", note)
    }

    private fun seedTopics(): List<TopicEntity> = listOf(
        TopicEntity(
            id = 1,
            title = "Photosynthesis",
            subject = "Biology",
            classLevel = "S2",
            lessonPlan = "Plan",
            projectIdeas = "Project",
            assessmentRubric = "Rubric",
            teachingTips = "Tips"
        ),
        TopicEntity(
            id = 2,
            title = "Linear Equations",
            subject = "Mathematics",
            classLevel = "S2",
            lessonPlan = "Plan",
            projectIdeas = "Project",
            assessmentRubric = "Rubric",
            teachingTips = "Tips"
        )
    )
}

private class FakeTopicDao : TopicDao {
    private val state = MutableStateFlow<List<TopicEntity>>(emptyList())
    private val favoritesState = MutableStateFlow<List<Int>>(emptyList())
    private val notesState = MutableStateFlow<List<TopicNoteProjection>>(emptyList())
    private val resourcesState = MutableStateFlow<List<TeachingResourceEntity>>(emptyList())
    private val schemesState = MutableStateFlow<List<SchemeOfWorkEntity>>(emptyList())

    override fun observeTopics(): Flow<List<TopicEntity>> = state

    override suspend fun getTopics(): List<TopicEntity> = state.value

    override fun observeTopicById(id: Int): Flow<TopicEntity?> {
        return MutableStateFlow(state.value.firstOrNull { it.id == id })
    }

    override suspend fun insertAll(topics: List<TopicEntity>) {
        state.value = topics
    }

    override suspend fun countTopics(): Int = state.value.size

    override fun observeFavoriteIds(): Flow<List<Int>> = favoritesState

    override suspend fun getNote(topicId: Int): String? {
        return notesState.value.firstOrNull { it.topicId == topicId }?.note
    }

    override fun observeNotes(): Flow<List<TopicNoteProjection>> = notesState

    override suspend fun insertFavorite(favorite: ug.ac.ndejje.cbc_teachers_toolkit.data.local.FavoriteEntity) {
        favoritesState.value = (favoritesState.value + favorite.topicId).distinct()
    }

    override suspend fun deleteFavorite(topicId: Int) {
        favoritesState.value = favoritesState.value.filterNot { it == topicId }
    }

    override suspend fun isFavorite(topicId: Int): Boolean {
        return favoritesState.value.contains(topicId)
    }

    override suspend fun upsertNote(note: ug.ac.ndejje.cbc_teachers_toolkit.data.local.NoteEntity) {
        val updated = notesState.value.filterNot { it.topicId == note.topicId } +
            TopicNoteProjection(note.topicId, note.note)
        notesState.value = updated
    }

    override suspend fun deleteNote(topicId: Int) {
        notesState.value = notesState.value.filterNot { it.topicId == topicId }
    }

    override fun observeResourcesForTopic(topicId: Int): Flow<List<TeachingResourceEntity>> {
        return MutableStateFlow(resourcesState.value.filter { it.topicId == topicId })
    }

    override suspend fun insertResources(resources: List<TeachingResourceEntity>) {
        val byKey = (resourcesState.value + resources).associateBy { it.key }
        resourcesState.value = byKey.values.toList()
    }

    override fun observeSchemes(): Flow<List<SchemeOfWorkEntity>> = schemesState

    override suspend fun insertScheme(scheme: SchemeOfWorkEntity): Long {
        val nextId = (schemesState.value.maxOfOrNull { it.id } ?: 0L) + 1L
        schemesState.value = schemesState.value + scheme.copy(id = nextId)
        return nextId
    }
}
