package ug.ac.ndejje.cbc_teachers_toolkit.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Organizes files into folders: Resources/[Subject]/[Class]/[FileName]
 */
fun getResourceDestinationFile(context: Context, subject: String, classLevel: String, fileName: String): File {
    val baseDir = context.getExternalFilesDir(null) // App-specific external storage
    val folder = File(baseDir, "Resources/${subject.replace(" ", "_")}/${classLevel.replace(" ", "_")}")
    if (!folder.exists()) {
        folder.mkdirs()
    }
    return File(folder, fileName)
}

/**
 * Asynchronous download using system DownloadManager.
 * Registers a receiver to notify when the download is complete.
 */
fun downloadFile(
    context: Context, 
    url: String, 
    destinationFile: File, 
    onDownloadComplete: (String) -> Unit
) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading ${destinationFile.name}")
            .setDescription("CBC Teachers Toolkit Resource")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(destinationFile))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()

        // Register receiver to listen for completion
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor != null && cursor.moveToFirst()) {
                        val actualStatusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (actualStatusIndex != -1) {
                            val status = cursor.getInt(actualStatusIndex)
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                onDownloadComplete(destinationFile.absolutePath)
                            } else if (status == DownloadManager.STATUS_FAILED) {
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(ctx, "Download failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    cursor?.close()
                    try {
                        context.unregisterReceiver(this)
                    } catch (e: Exception) {
                        // Already unregistered or context invalid
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun openDownloadedFile(context: Context, filePath: String) {
    try {
        val file = if (filePath.startsWith("asset:///")) {
            // Handle asset by copying to cache
            val assetPath = filePath.substring("asset:///".length)
            val cacheFile = File(context.cacheDir, File(assetPath).name)
            context.assets.open(assetPath).use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }
            cacheFile
        } else {
            File(filePath)
        }

        if (!file.exists() || file.length() == 0L) {
            Toast.makeText(context, "File is missing or corrupted. Please redownload.", Toast.LENGTH_LONG).show()
            return
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString())
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase()) 
            ?: when (file.extension.lowercase()) {
                "pdf" -> "application/pdf"
                "mp4" -> "video/mp4"
                else -> "*/*"
            }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        val chooser = Intent.createChooser(intent, "Open with...")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Synchronous download for WorkManager.
 */
fun downloadFileSync(urlString: String, destinationFile: File): Boolean {
    var connection: HttpURLConnection? = null
    return try {
        val url = URL(urlString)
        connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 15000
        connection.readTimeout = 30000
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            return false
        }

        // Ensure parent directories exist
        destinationFile.parentFile?.mkdirs()

        connection.inputStream.use { input ->
            FileOutputStream(destinationFile).use { output ->
                input.copyTo(output)
            }
        }
        // Verify file size is greater than 0
        destinationFile.exists() && destinationFile.length() > 0
    } catch (e: Exception) {
        e.printStackTrace()
        false
    } finally {
        connection?.disconnect()
    }
}
