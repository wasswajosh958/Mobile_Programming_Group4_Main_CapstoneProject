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

    @Query("SELECT * FROM topics WHERE id = :id LIMIT 1")
    fun observeTopicById(id: Int): Flow<TopicEntity?>

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
}
