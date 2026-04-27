package ug.ac.ndejje.cbc_teachers_toolkit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics ORDER BY subject, classLevel, title")
    fun observeTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics ORDER BY id")
    suspend fun getTopics(): List<TopicEntity>

    @Query("SELECT * FROM topics WHERE id = :id LIMIT 1")
    fun observeTopicById(id: Int): Flow<TopicEntity?>

    @Query("SELECT * FROM topics WHERE id = :id LIMIT 1")
    suspend fun getTopicById(id: Int): TopicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<TopicEntity>)

    @Query("SELECT COUNT(*) FROM topics")
    suspend fun countTopics(): Int

    @Query("SELECT topicId FROM favorites")
    fun observeFavoriteIds(): Flow<List<Int>>

    @Query("SELECT note FROM notes WHERE topicId = :topicId LIMIT 1")
    suspend fun getNote(topicId: Int): String?

    @Query("SELECT topicId, note FROM notes")
    fun observeNotes(): Flow<List<TopicNoteProjection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE topicId = :topicId")
    suspend fun deleteFavorite(topicId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE topicId = :topicId)")
    suspend fun isFavorite(topicId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE topicId = :topicId")
    suspend fun deleteNote(topicId: Int)

    @Transaction
    suspend fun toggleFavorite(topicId: Int) {
        if (isFavorite(topicId)) deleteFavorite(topicId) else insertFavorite(FavoriteEntity(topicId))
    }

    // Teaching resources
    @Query("SELECT * FROM teaching_resources WHERE topicId = :topicId ORDER BY title")
    fun observeResourcesForTopic(topicId: Int): Flow<List<TeachingResourceEntity>>

    @Query("SELECT * FROM teaching_resources WHERE isDownloaded = 1 ORDER BY title")
    fun observeDownloadedResources(): Flow<List<TeachingResourceEntity>>

    @Query("SELECT * FROM teaching_resources WHERE isDownloaded = 0 AND url NOT LIKE '%youtube.com%' AND url NOT LIKE '%google.com%' ORDER BY title")
    suspend fun getUndownloadedResources(): List<TeachingResourceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<TeachingResourceEntity>)

    @Query("UPDATE teaching_resources SET localPath = :path, isDownloaded = 1 WHERE `key` = :key")
    suspend fun updateResourceDownloadStatus(key: String, path: String)

    @Query("SELECT * FROM teaching_resources WHERE `key` = :key LIMIT 1")
    suspend fun getResourceByKey(key: String): TeachingResourceEntity?

    @Query("SELECT COUNT(*) FROM teaching_resources")
    suspend fun countResources(): Int

    // Schemes of work
    @Query("SELECT * FROM schemes_of_work ORDER BY subject, classLevel, term, week")
    fun observeSchemes(): Flow<List<SchemeOfWorkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheme(scheme: SchemeOfWorkEntity): Long
}
