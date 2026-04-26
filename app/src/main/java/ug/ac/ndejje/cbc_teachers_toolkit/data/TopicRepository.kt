package ug.ac.ndejje.cbc_teachers_toolkit.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicDao
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicEntity
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

    suspend fun seedIfEmpty() {
        if (topicDao.countTopics() == 0) {
            topicDao.insertAll(CbcSeedData.topics)
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
