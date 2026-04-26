package ug.ac.ndejje.cbc_teachers_toolkit.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicDao
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicEntity
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.NoteEntity
import ug.ac.ndejje.cbc_teachers_toolkit.domain.Topic

class TopicRepository(
    private val topicDao: TopicDao
) {
    fun observeTopics(): Flow<List<Topic>> {
        return topicDao.observeTopics().map { entities -> entities.map { it.toDomain() } }
    }

    fun observeTopicById(id: Int): Flow<Topic?> {
        return topicDao.observeTopicById(id).map { it?.toDomain() }
    }

    fun observeFavoriteIds(): Flow<Set<Int>> {
        return topicDao.observeFavoriteIds().map { it.toSet() }
    }

    fun observeNotes(): Flow<Map<Int, String>> {
        return topicDao.observeNotes().map { projections ->
            projections.associate { it.topicId to it.note }
        }
    }

    suspend fun seedIfEmpty() {
        if (topicDao.countTopics() == 0) {
            topicDao.insertAll(CbcSeedData.topics)
        }
    }

    suspend fun toggleFavorite(topicId: Int) {
        topicDao.toggleFavorite(topicId)
    }

    suspend fun saveNote(topicId: Int, note: String) {
        if (note.isBlank()) {
            topicDao.deleteNote(topicId)
        } else {
            topicDao.upsertNote(NoteEntity(topicId, note.trim()))
        }
    }
}

private fun TopicEntity.toDomain(): Topic {
    return Topic(
        id = id,
        title = title,
        subject = subject,
        classLevel = classLevel,
        lessonPlan = lessonPlan,
        projectIdeas = projectIdeas,
        assessmentRubric = assessmentRubric,
        teachingTips = teachingTips
    )
}
