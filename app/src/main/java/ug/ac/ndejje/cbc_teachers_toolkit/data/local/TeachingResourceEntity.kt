package ug.ac.ndejje.cbc_teachers_toolkit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teaching_resources")
data class TeachingResourceEntity(
    @PrimaryKey val key: String,
    val topicId: Int,
    val title: String,
    val type: String, // "VIDEO", "PDF_LINK", "PHOTO", "NOTES", "OTHER"
    val url: String,
    val source: String = "NCDC",
    val localPath: String? = null,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val fileSize: String? = null, // e.g. "4.2 MB"
    val version: Int = 1
)
