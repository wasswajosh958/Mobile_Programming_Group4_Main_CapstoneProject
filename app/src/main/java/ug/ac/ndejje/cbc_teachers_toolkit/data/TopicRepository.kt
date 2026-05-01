package ug.ac.ndejje.cbc_teachers_toolkit.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ug.ac.ndejje.cbc_teachers_toolkit.data.remote.ResourceIndexParser
import ug.ac.ndejje.cbc_teachers_toolkit.data.remote.SimpleHttpClient
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.*
import kotlinx.coroutines.flow.emptyFlow
import ug.ac.ndejje.cbc_teachers_toolkit.data.remote.FirebaseSyncManager
import ug.ac.ndejje.cbc_teachers_toolkit.domain.Topic

class TopicRepository(
    private val context: Context,
    private val topicDao: TopicDao,
    private val userDao: UserDao,
    private val adminResourceDao: AdminResourceDao,
    private val firebaseSyncManager: FirebaseSyncManager
) {
    fun observeTopics(): Flow<List<Topic>> {
        return topicDao.observeTopics().map { entities -> entities.map { it.toDomain() } }
    }

    fun observeTopicById(id: Int): Flow<Topic?> {
        return topicDao.observeTopicById(id).map { it?.toDomain() }
    }

    suspend fun getTopicSync(id: Int): Topic? {
        return topicDao.getTopicById(id)?.toDomain()
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
        if (topicDao.countResources() == 0) {
            val fromAssets = loadStarterResourcesFromAssets()
            val combined = (fromAssets + CbcSeedData.starterResources).distinctBy { it.key }
            if (combined.isNotEmpty()) {
                topicDao.insertResources(combined)
            } else {
                topicDao.insertResources(buildOfflineStarterResources())
            }
        }
    }

    private fun loadStarterResourcesFromAssets(): List<TeachingResourceEntity> {
        return try {
            val jsonString = context.assets.open("starter_resources.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<StarterResource>>() {}.type
            val starterList: List<StarterResource> = Gson().fromJson(jsonString, type)
            starterList.map { item ->
                TeachingResourceEntity(
                    key = "${item.topicId}|${item.type}|${item.url}",
                    topicId = item.topicId,
                    title = item.title,
                    type = item.type,
                    url = item.url,
                    source = item.source
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private data class StarterResource(
        val topicId: Int,
        val title: String,
        val type: String,
        val url: String,
        val source: String
    )

    fun observeResourcesForTopic(topicId: Int): Flow<List<TeachingResourceEntity>> {
        return topicDao.observeResourcesForTopic(topicId)
    }

    fun observeDownloadedResources(): Flow<List<TeachingResourceEntity>> {
        return topicDao.observeDownloadedResources()
    }

    fun observeAdminUploads(): Flow<List<AdminResourceEntity>> {
        return adminResourceDao.getAllUploads()
    }

    suspend fun insertAdminUpload(upload: AdminResourceEntity) {
        adminResourceDao.insertUpload(upload)
    }

    suspend fun getCurrentUser(): UserEntity? {
        return userDao.getCurrentUser()
    }

    suspend fun updateGithubToken(token: String) {
        userDao.updateCurrentToken(token)
    }

    suspend fun getUndownloadedResources(): List<TeachingResourceEntity> {
        return topicDao.getUndownloadedResources()
    }

    suspend fun seedResourcesIfEmpty() {
        // Minimal safe "online" approach: store only links (no NCDC PDF content bundled).
        // We seed generic NCDC search links per topic to enable teachers to reach official sources.
        // If resources already exist, we do nothing.
        // (A full remote sync can be added later.)
    }

    suspend fun syncResourcesFromFirebase(): Int {
        val entities = firebaseSyncManager.fetchResourcesFromFirestore()
        if (entities.isNotEmpty()) {
            topicDao.insertResources(entities)
        }
        return entities.size
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

    suspend fun updateScheme(scheme: SchemeOfWorkEntity) = topicDao.updateScheme(scheme)

    suspend fun deleteScheme(scheme: SchemeOfWorkEntity) = topicDao.deleteScheme(scheme)

    suspend fun updateResourceDownloadStatus(key: String, path: String) {
        topicDao.updateResourceDownloadStatus(key, path)
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

    private suspend fun buildOfflineStarterResources(): List<TeachingResourceEntity> {
        return topicDao.getTopics().flatMap { topic ->
            val q1 = "${topic.subject} ${topic.classLevel} ${topic.title} lesson"
            val q2 = "${topic.title} ${topic.classLevel} ${topic.subject} tutorial"
            val q3 = "${topic.subject} ${topic.title} CBC Uganda"
            val video1 =
                "https://www.youtube.com/results?search_query=" + java.net.URLEncoder.encode(q1, "UTF-8")
            val video2 =
                "https://www.youtube.com/results?search_query=" + java.net.URLEncoder.encode(q2, "UTF-8")
            val video3 =
                "https://www.youtube.com/results?search_query=" + java.net.URLEncoder.encode(q3, "UTF-8")
            val notesQuery =
                "https://www.google.com/search?q=" +
                    java.net.URLEncoder.encode(
                        "site:ncdc.go.ug ${topic.subject} ${topic.classLevel} ${topic.title} notes",
                        "UTF-8"
                    )
            val pdfQuery =
                "https://www.google.com/search?q=" +
                    java.net.URLEncoder.encode(
                        "site:ncdc.go.ug filetype:pdf ${topic.subject} ${topic.classLevel} ${topic.title}",
                        "UTF-8"
                    )
            listOf(
                TeachingResourceEntity(
                    key = "${topic.id}|VIDEO|$video1",
                    topicId = topic.id,
                    title = "Video (lesson): ${topic.title}",
                    type = "VIDEO",
                    url = video1,
                    source = "WEB"
                ),
                TeachingResourceEntity(
                    key = "${topic.id}|VIDEO|$video2",
                    topicId = topic.id,
                    title = "Video (tutorial): ${topic.title}",
                    type = "VIDEO",
                    url = video2,
                    source = "WEB"
                ),
                TeachingResourceEntity(
                    key = "${topic.id}|VIDEO|$video3",
                    topicId = topic.id,
                    title = "Video (CBC context): ${topic.title}",
                    type = "VIDEO",
                    url = video3,
                    source = "WEB"
                ),
                TeachingResourceEntity(
                    key = "${topic.id}|NOTES|$notesQuery",
                    topicId = topic.id,
                    title = "Teacher notes: ${topic.title}",
                    type = "NOTES",
                    url = notesQuery,
                    source = "NCDC/WEB"
                ),
                TeachingResourceEntity(
                    key = "${topic.id}|PDF_LINK|$pdfQuery",
                    topicId = topic.id,
                    title = "Downloadable PDF references: ${topic.title}",
                    type = "PDF_LINK",
                    url = pdfQuery,
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
