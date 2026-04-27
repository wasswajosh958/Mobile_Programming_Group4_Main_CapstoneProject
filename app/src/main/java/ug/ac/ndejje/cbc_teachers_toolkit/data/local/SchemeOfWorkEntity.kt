package ug.ac.ndejje.cbc_teachers_toolkit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schemes_of_work")
data class SchemeOfWorkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val teacherName: String,
    val schoolName: String = "",
    val subject: String,
    val classLevel: String,
    val term: String,
    val week: Int,
    val topicTitle: String,
    val competency: String = "",
    val objectives: String,
    val activities: String,
    val resources: String,
    val assessment: String,
    val date: String = ""
)
