package ug.ac.ndejje.cbc_teachers_toolkit.data.remote

import org.json.JSONArray
import org.json.JSONObject

object ResourceIndexParser {
    fun parse(json: String): ResourceIndexResponse {
        val root = JSONObject(json)
        val version = root.optInt("version", 1)
        val updatedAtIso = root.optString("updatedAtIso", "")
        val itemsJson = root.optJSONArray("items") ?: JSONArray()

        val items = buildList {
            for (i in 0 until itemsJson.length()) {
                val item = itemsJson.getJSONObject(i)
                val sourceValue = if (item.has("source") && !item.isNull("source")) {
                    item.getString("source")
                } else {
                    null
                }
                add(
                    ResourceIndexItem(
                        topicId = item.getInt("topicId"),
                        title = item.getString("title"),
                        type = item.getString("type"),
                        url = item.getString("url"),
                        source = sourceValue
                    )
                )
            }
        }

        return ResourceIndexResponse(
            version = version,
            updatedAtIso = updatedAtIso,
            items = items
        )
    }
}
