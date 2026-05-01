package ug.ac.ndejje.cbc_teachers_toolkit.data.remote

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class GitHubAdminManager(
    private val token: String, // Your GitHub Personal Access Token
    private val repoOwner: String = "wasswajosh958",
    private val repoName: String = "Mobile_Programming_Group4_Main_CapstoneProject"
) {
    private val gson = Gson()
    private val indexPath = "resources/resource_index.json"
    private val baseApiUrl = "https://api.github.com/repos/$repoOwner/$repoName/contents"

    /**
     * Uploads a file to the GitHub repository and returns its raw URL.
     */
    suspend fun uploadFile(
        fileName: String,
        fileBytes: ByteArray,
        folder: String = "resources/media"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val uploadPath = "$folder/$fileName"
            val url = "$baseApiUrl/$uploadPath"
            
            // Check if file exists to get SHA (for overwrite) - though we'll assume new files for now
            // For simplicity, we'll just try to PUT. If it exists, it fails without SHA.
            // But let's be robust.
            val sha = getFileSha(uploadPath)

            val encodedContent = Base64.encodeToString(fileBytes, Base64.NO_WRAP)
            
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "PUT"
            conn.doOutput = true
            conn.setRequestProperty("Authorization", "token $token")
            conn.setRequestProperty("Content-Type", "application/json")

            val body = JsonObject().apply {
                addProperty("message", "Admin: Uploaded resource file $fileName")
                addProperty("content", encodedContent)
                if (sha != null) addProperty("sha", sha)
            }

            conn.outputStream.use { it.write(gson.toJson(body).toByteArray(StandardCharsets.UTF_8)) }

            if (conn.responseCode == 200 || conn.responseCode == 201) {
                val rawUrl = "https://raw.githubusercontent.com/$repoOwner/$repoName/main/$uploadPath"
                Result.success(rawUrl)
            } else {
                val errorMsg = conn.errorStream?.bufferedReader()?.readText() ?: conn.responseMessage
                Result.failure(Exception("Upload failed: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getFileSha(path: String): String? = withContext(Dispatchers.IO) {
        try {
            val url = "$baseApiUrl/$path"
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "token $token")
            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val fileInfo = gson.fromJson(response, JsonObject::class.java)
                fileInfo.get("sha").asString
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addResourceToGitHub(
        topicId: Int,
        title: String,
        type: String,
        url: String,
        fileSize: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // ... existing code to fetch index ...
            val indexUrl = "$baseApiUrl/$indexPath"
            val conn = URL(indexUrl).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "token $token")
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json")

            if (conn.responseCode != 200) {
                return@withContext Result.failure(Exception("Failed to fetch index: ${conn.responseMessage}"))
            }

            val response = conn.inputStream.bufferedReader().use { it.readText() }
            val fileInfo = gson.fromJson(response, JsonObject::class.java)
            val sha = fileInfo.get("sha").asString
            val encodedContent = fileInfo.get("content").asString.replace("\n", "").replace("\r", "")
            val currentJson = String(Base64.decode(encodedContent, Base64.DEFAULT), StandardCharsets.UTF_8)

            // 2. Parse and update JSON
            val root = gson.fromJson(currentJson, JsonObject::class.java)
            val items = root.getAsJsonArray("items")
            
            val newItem = JsonObject().apply {
                addProperty("topicId", topicId)
                addProperty("title", title)
                addProperty("type", type)
                addProperty("url", url)
                if (fileSize != null) addProperty("fileSize", fileSize)
                addProperty("source", "Admin App")
                addProperty("key", "res_${System.currentTimeMillis()}")
            }
            items.add(newItem)
            
            root.addProperty("updatedAtIso", java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date()))

            val updatedJson = gson.toJson(root)
            val newEncodedContent = Base64.encodeToString(updatedJson.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)

            // 3. PUT the updated index back
            val putConn = URL(indexUrl).openConnection() as HttpURLConnection
            putConn.requestMethod = "PUT"
            putConn.doOutput = true
            putConn.setRequestProperty("Authorization", "token $token")
            putConn.setRequestProperty("Content-Type", "application/json")

            val body = JsonObject().apply {
                addProperty("message", "Admin: Added resource - $title")
                addProperty("content", newEncodedContent)
                addProperty("sha", sha)
            }

            putConn.outputStream.use { it.write(gson.toJson(body).toByteArray(StandardCharsets.UTF_8)) }

            if (putConn.responseCode == 200 || putConn.responseCode == 201) {
                Result.success("Successfully updated GitHub index!")
            } else {
                val errorMsg = putConn.errorStream?.bufferedReader()?.readText() ?: putConn.responseMessage
                Result.failure(Exception("Index update failed: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
