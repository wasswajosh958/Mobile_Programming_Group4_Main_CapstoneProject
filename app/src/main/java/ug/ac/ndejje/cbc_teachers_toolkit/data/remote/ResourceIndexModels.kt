package ug.ac.ndejje.cbc_teachers_toolkit.data.remote

data class ResourceIndexItem(
    val topicId: Int,
    val title: String,
    val type: String,
    val url: String,
    val source: String? = null
)

data class ResourceIndexResponse(
    val version: Int,
    val updatedAtIso: String,
    val items: List<ResourceIndexItem>
)
