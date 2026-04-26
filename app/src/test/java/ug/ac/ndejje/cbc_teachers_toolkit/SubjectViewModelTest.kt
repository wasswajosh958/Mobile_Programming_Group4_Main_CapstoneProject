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
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicDao
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicEntity

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
        val viewModel = SubjectViewModel(repository)

        advanceUntilIdle()
        viewModel.toggleFavorite(1)
        assertTrue(viewModel.uiState.value.favorites.contains(1))

        viewModel.toggleFavorite(1)
        assertTrue(!viewModel.uiState.value.favorites.contains(1))
    }

    @Test
    fun `searchQuery filters topics by title`() = runTest {
        val fakeDao = FakeTopicDao()
        fakeDao.insertAll(seedTopics())
        val repository = TopicRepository(fakeDao)
        val viewModel = SubjectViewModel(repository)

        advanceUntilIdle()
        viewModel.updateSearchQuery("photo")
        advanceUntilIdle()

        val topics = viewModel.uiState.first().filteredTopics
        assertEquals(1, topics.size)
        assertEquals("Photosynthesis", topics.first().title)
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

    override fun observeTopics(): Flow<List<TopicEntity>> = state

    override fun observeTopicById(id: Int): Flow<TopicEntity?> {
        return MutableStateFlow(state.value.firstOrNull { it.id == id })
    }

    override suspend fun insertAll(topics: List<TopicEntity>) {
        state.value = topics
    }

    override suspend fun countTopics(): Int = state.value.size
}
