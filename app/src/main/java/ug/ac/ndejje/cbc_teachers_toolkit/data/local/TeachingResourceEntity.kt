package ug.ac.ndejje.cbc_teachers_toolkit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teaching_resources")
data class TeachingResourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Int,
    val title: String,
    val type: String, // "NCDC_WEB", "VIDEO", "PDF_LINK", "OTHER"
    val url: String,
    val source: String = "NCDC"
)
