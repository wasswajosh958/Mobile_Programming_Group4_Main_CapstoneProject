package ug.ac.ndejje.cbc_teachers_toolkit.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ug.ac.ndejje.cbc_teachers_toolkit.data.remote.ResourceIndexParser
import ug.ac.ndejje.cbc_teachers_toolkit.data.remote.SimpleHttpClient
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicDao
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TopicEntity
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.NoteEntity
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.SchemeOfWorkEntity
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TeachingResourceEntity
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

    fun observeResourcesForTopic(topicId: Int): Flow<List<TeachingResourceEntity>> {
        return topicDao.observeResourcesForTopic(topicId)
    }

    suspend fun seedResourcesIfEmpty() {
        // Minimal safe "online" approach: store only links (no NCDC PDF content bundled).
        // We seed generic NCDC search links per topic to enable teachers to reach official sources.
        // If resources already exist, we do nothing.
        // (A full remote sync can be added later.)
    }

    suspend fun syncResourcesFromIndexUrl(indexUrl: String): Int {
        val json = SimpleHttpClient.get(indexUrl)
        val parsed = ResourceIndexParser.parse(json)

        val entities = parsed.items.map { item ->
            val key = "${item.topicId}|${item.type}|${item.url}"
            TeachingResourceEntity(
                key = key,
                topicId = item.topicId,
                title = item.title,
                type = item.type,
                url = item.url,
                source = item.source ?: "NCDC"
            )
        } + buildGeneratedCompanionResources()

        topicDao.insertResources(entities)
        return entities.size
    }

    fun observeSchemes(): Flow<List<SchemeOfWorkEntity>> = topicDao.observeSchemes()

    suspend fun insertScheme(scheme: SchemeOfWorkEntity): Long = topicDao.insertScheme(scheme)

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

    private suspend fun buildGeneratedCompanionResources(): List<TeachingResourceEntity> {
        return topicDao.getTopics().flatMap { topic ->
            val videoQuery =
                "https://www.youtube.com/results?search_query=" +
                    java.net.URLEncoder.encode("${topic.subject} ${topic.classLevel} ${topic.title} lesson", "UTF-8")
            val notesQuery =
                "https://www.google.com/search?q=" +
                    java.net.URLEncoder.encode(
                        "site:ncdc.go.ug ${topic.subject} ${topic.classLevel} ${topic.title} notes",
                        "UTF-8"
                    )
            listOf(
                TeachingResourceEntity(
                    key = "${topic.id}|VIDEO|$videoQuery",
                    topicId = topic.id,
                    title = "Video lessons: ${topic.title}",
                    type = "VIDEO",
                    url = videoQuery,
                    source = "WEB"
                ),
                TeachingResourceEntity(
                    key = "${topic.id}|NOTES|$notesQuery",
                    topicId = topic.id,
                    title = "Teaching notes: ${topic.title}",
                    type = "NOTES",
                    url = notesQuery,
                    source = "NCDC/WEB"
                )
            )
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
