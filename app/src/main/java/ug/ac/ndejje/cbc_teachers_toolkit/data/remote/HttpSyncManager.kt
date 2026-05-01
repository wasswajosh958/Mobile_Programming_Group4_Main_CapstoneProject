package ug.ac.ndejje.cbc_teachers_toolkit.data.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TeachingResourceEntity
import java.net.URL

data class JsonResource(
    val topicId: Int,
    val title: String,
    val type: String,
    val url: String,
    val version: Int
)

class HttpSyncManager {
    suspend fun fetchResources(indexUrl: String): List<TeachingResourceEntity> = withContext(Dispatchers.IO) {
        try {
            val json = URL(indexUrl).readText()
            val typeToken = object : TypeToken<List<JsonResource>>() {}.type
            val resources: List<JsonResource> = Gson().fromJson(json, typeToken)
            
            resources.map { res ->
                TeachingResourceEntity(
                    key = "${res.topicId}|${res.type}|${res.url}",
                    topicId = res.topicId,
                    title = res.title,
                    type = res.type,
                    url = res.url,
                    source = "Remote Server",
                    version = res.version
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
