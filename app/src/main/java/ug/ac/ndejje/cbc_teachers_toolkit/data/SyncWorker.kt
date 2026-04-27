package ug.ac.ndejje.cbc_teachers_toolkit.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ug.ac.ndejje.cbc_teachers_toolkit.CbcToolkitApplication
import ug.ac.ndejje.cbc_teachers_toolkit.util.downloadFileSync
import ug.ac.ndejje.cbc_teachers_toolkit.util.getResourceDestinationFile

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val repository = (applicationContext as CbcToolkitApplication).container.topicRepository
        val resourceIndexUrl =
            "https://raw.githubusercontent.com/wasswajosh958/Mobile_Programming_Group4_Main_CapstoneProject/main/resources/resource_index.json"

        try {
            // 1. Sync metadata
            repository.syncResourcesFromIndexUrl(resourceIndexUrl)

            // 2. Automatically download new resources (limit to 10 at a time to save data/bandwidth)
            val resourcesToDownload = repository.getUndownloadedResources().take(10)
            
            for (resource in resourcesToDownload) {
                val topic = repository.getTopicSync(resource.topicId) ?: continue
                
                val extension = when (resource.type) {
                    "VIDEO" -> "mp4"
                    "PDF_LINK", "NOTES" -> "pdf"
                    else -> "dat"
                }
                val fileName = "${resource.key.hashCode()}.$extension"
                val destinationFile = getResourceDestinationFile(
                    applicationContext,
                    topic.subject,
                    topic.classLevel,
                    fileName
                )

                if (downloadFileSync(resource.url, destinationFile)) {
                    repository.updateResourceDownloadStatus(resource.key, destinationFile.absolutePath)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error during background sync", e)
            Result.retry()
        }
    }
}
