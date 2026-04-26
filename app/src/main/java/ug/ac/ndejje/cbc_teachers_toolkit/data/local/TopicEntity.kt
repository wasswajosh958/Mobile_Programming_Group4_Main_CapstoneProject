package ug.ac.ndejje.cbc_teachers_toolkit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val subject: String,
    val classLevel: String,
    val lessonPlan: String,
    val projectIdeas: String,
    val assessmentRubric: String,
    val teachingTips: String
)
