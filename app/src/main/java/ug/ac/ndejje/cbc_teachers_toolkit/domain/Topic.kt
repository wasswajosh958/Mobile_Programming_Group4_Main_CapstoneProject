package ug.ac.ndejje.cbc_teachers_toolkit.domain

data class Topic(
    val id: Int,
    val title: String,
    val subject: String,
    val classLevel: String,
    val lessonPlan: String,
    val projectIdeas: String,
    val assessmentRubric: String,
    val teachingTips: String
)
