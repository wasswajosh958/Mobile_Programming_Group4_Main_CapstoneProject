package ug.ac.ndejje.cbc_teachers_toolkit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_uploads")
data class AdminResourceEntity(
    @PrimaryKey val key: String,
    val topicId: Int,
    val title: String,
    val type: String,
    val url: String,
    val fileSize: String? = null,
    val uploadDate: Long = System.currentTimeMillis()
)
