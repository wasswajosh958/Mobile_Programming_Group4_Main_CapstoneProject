package ug.ac.ndejje.cbc_teachers_toolkit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminResourceDao {
    @Query("SELECT * FROM admin_uploads ORDER BY uploadDate DESC")
    fun getAllUploads(): Flow<List<AdminResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpload(upload: AdminResourceEntity)

    @Query("DELETE FROM admin_uploads WHERE `key` = :key")
    suspend fun deleteUpload(key: String)
}
