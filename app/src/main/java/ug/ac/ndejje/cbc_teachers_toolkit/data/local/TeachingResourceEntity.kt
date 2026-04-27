package ug.ac.ndejje.cbc_teachers_toolkit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teaching_resources")
data class TeachingResourceEntity(
    @PrimaryKey val key: String,
    val topicId: Int,
    val title: String,
    val type: String, // "NCDC_WEB", "VIDEO", "PDF_LINK", "OTHER"
    val url: String,
    val source: String = "NCDC",
    val localPath: String? = null,
    val isDownloaded: Boolean = false,
    val version: Int = 1
)
