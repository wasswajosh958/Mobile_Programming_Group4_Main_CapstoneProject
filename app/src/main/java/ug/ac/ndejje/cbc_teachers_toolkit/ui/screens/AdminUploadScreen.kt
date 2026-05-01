package ug.ac.ndejje.cbc_teachers_toolkit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import ug.ac.ndejje.cbc_teachers_toolkit.domain.Topic
import ug.ac.ndejje.cbc_teachers_toolkit.data.remote.GitHubAdminManager
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.AdminResourceEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import android.provider.OpenableColumns
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SubjectViewModel
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.AuthViewModel
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SubjectsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUploadScreen(
    viewModel: SubjectViewModel,
    authViewModel: AuthViewModel,
    onMenuClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    var selectedClass by remember { mutableStateOf<String?>(null) }
    var selectedTopic by remember { mutableStateOf<Topic?>(null) }
    
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("PDF_LINK") }
    var url by remember { mutableStateOf("") }
    var fileSize by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    
    var githubToken by remember { mutableStateOf(currentUser?.githubToken ?: "") }
    
    // Auto-update token if it changes in user profile
    LaunchedEffect(currentUser?.githubToken) {
        if (githubToken.isBlank() && !currentUser?.githubToken.isNullOrBlank()) {
            githubToken = currentUser?.githubToken!!
        }
    }

    var isUploading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    
    var subjectExpanded by remember { mutableStateOf(false) }
    var classExpanded by remember { mutableStateOf(false) }
    var topicExpanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val types = listOf("PDF_LINK", "VIDEO", "PHOTO", "NOTES")
    
    // Track if we are in "Upload" or "History" tab
    var selectedTab by remember { mutableIntStateOf(0) }
    val adminUploads by viewModel.adminUploads.collectAsState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        uri?.let { 
            // Try to get filename
            val cursor = context.contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (c.moveToFirst()) {
                    fileName = c.getString(nameIndex)
                    if (title.isBlank()) title = fileName.substringBeforeLast('.')
                }
            }
        }
    }

    // Filter topics based on selection
    val filteredTopics = remember(selectedSubject, selectedClass, uiState.allTopics) {
        uiState.allTopics.filter { 
            (selectedSubject == null || it.subject == selectedSubject) &&
            (selectedClass == null || it.classLevel == selectedClass)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        val headerGradient = Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(brush = headerGradient)
        ) {
            Column(
                modifier = Modifier.padding(start = 12.dp, end = 20.dp, top = 16.dp, bottom = 32.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Admin Panel",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Admin Management",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Upload", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("History", modifier = Modifier.padding(16.dp))
                }
            }

            if (selectedTab == 0) {
                UploadForm(
                    uiState = uiState,
                    selectedSubject = selectedSubject,
                    onSubjectSelect = { selectedSubject = it },
                    selectedClass = selectedClass,
                    onClassSelect = { selectedClass = it },
                    selectedTopic = selectedTopic,
                    onTopicSelect = { selectedTopic = it },
                    filteredTopics = filteredTopics,
                    title = title,
                    onTitleChange = { title = it },
                    type = type,
                    onTypeChange = { type = it; if (it != "PDF_LINK") url = "" },
                    url = url,
                    onUrlChange = { url = it },
                    fileSize = fileSize,
                    onFileSizeChange = { fileSize = it },
                    fileName = fileName,
                    onPickFile = {
                        val mimeType = when (type) {
                            "VIDEO" -> "video/*"
                            "PHOTO" -> "image/*"
                            "NOTES" -> "application/pdf"
                            else -> "*/*"
                        }
                        filePickerLauncher.launch(mimeType)
                    },
                    githubToken = githubToken,
                    onTokenChange = { githubToken = it },
                    statusMessage = statusMessage,
                    isUploading = isUploading,
                    onUpload = {
                        if (selectedTopic == null || title.isBlank() || githubToken.isBlank()) {
                            statusMessage = "Please fill all fields."
                            return@UploadForm
                        }
                        if (type == "PDF_LINK" && url.isBlank()) {
                            statusMessage = "Please provide a URL."
                            return@UploadForm
                        }
                        if (type != "PDF_LINK" && selectedFileUri == null) {
                            statusMessage = "Please select a file to upload."
                            return@UploadForm
                        }

                        isUploading = true
                        scope.launch {
                            try {
                                val githubManager = GitHubAdminManager(token = githubToken)
                                var finalUrl = url

                                // 1. Upload file if needed
                                if (selectedFileUri != null) {
                                    statusMessage = "Uploading file to GitHub..."
                                    val bytes = context.contentResolver.openInputStream(selectedFileUri!!)?.use { it.readBytes() }
                                    if (bytes == null) {
                                        statusMessage = "Failed to read local file."
                                        isUploading = false
                                        return@launch
                                    }
                                    
                                    val uploadResult = githubManager.uploadFile(
                                        fileName = fileName,
                                        fileBytes = bytes
                                    )
                                    
                                    if (uploadResult.isSuccess) {
                                        finalUrl = uploadResult.getOrThrow()
                                    } else {
                                        statusMessage = "Upload Error: ${uploadResult.exceptionOrNull()?.message}"
                                        isUploading = false
                                        return@launch
                                    }
                                }

                                // 2. Add to index
                                statusMessage = "Updating Resource Index..."
                                val result = githubManager.addResourceToGitHub(
                                    topicId = selectedTopic!!.id,
                                    title = title,
                                    type = type,
                                    url = finalUrl,
                                    fileSize = if (fileSize.isNotBlank()) fileSize else null
                                )
                                
                                if (result.isSuccess) {
                                    // Save token for future use
                                    authViewModel.saveGithubToken(githubToken)
                                    
                                    statusMessage = "Success! Resource is now live."
                                    
                                    // Save to history in DB
                                    viewModel.insertAdminUpload(AdminResourceEntity(
                                        key = "res_${System.currentTimeMillis()}",
                                        topicId = selectedTopic!!.id,
                                        title = title,
                                        type = type,
                                        url = finalUrl,
                                        fileSize = if (fileSize.isNotBlank()) fileSize else null
                                    ))

                                    title = ""; url = ""; fileName = ""; selectedFileUri = null; fileSize = ""
                                } else {
                                    statusMessage = "Error: ${result.exceptionOrNull()?.message}"
                                }
                            } catch (e: Exception) {
                                statusMessage = "Error: ${e.message}"
                            } finally {
                                isUploading = false
                            }
                        }
                    }
                )
            } else {
                UploadHistory(adminUploads)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadForm(
    uiState: SubjectsUiState,
    selectedSubject: String?,
    onSubjectSelect: (String) -> Unit,
    selectedClass: String?,
    onClassSelect: (String) -> Unit,
    selectedTopic: Topic?,
    onTopicSelect: (Topic) -> Unit,
    filteredTopics: List<Topic>,
    title: String,
    onTitleChange: (String) -> Unit,
    type: String,
    onTypeChange: (String) -> Unit,
    url: String,
    onUrlChange: (String) -> Unit,
    fileSize: String,
    onFileSizeChange: (String) -> Unit,
    fileName: String,
    onPickFile: () -> Unit,
    githubToken: String,
    onTokenChange: (String) -> Unit,
    statusMessage: String,
    isUploading: Boolean,
    onUpload: () -> Unit
) {
    var subjectExpanded by remember { mutableStateOf(false) }
    var classExpanded by remember { mutableStateOf(false) }
    var topicExpanded by remember { mutableStateOf(false) }
    val types = listOf("PDF_LINK", "VIDEO", "PHOTO", "NOTES")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // --- Subject Selection ---
        ExposedDropdownMenuBox(
            expanded = subjectExpanded,
            onExpandedChange = { subjectExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedSubject ?: "Select Subject",
                onValueChange = {},
                readOnly = true,
                label = { Text("Subject") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = subjectExpanded,
                onDismissRequest = { subjectExpanded = false }
            ) {
                uiState.availableSubjects.forEach { subject ->
                    DropdownMenuItem(
                        text = { Text(subject) },
                        onClick = {
                            onSubjectSelect(subject)
                            subjectExpanded = false
                        }
                    )
                }
            }
        }

        // --- Class Selection ---
        ExposedDropdownMenuBox(
            expanded = classExpanded,
            onExpandedChange = { classExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedClass ?: "Select Class",
                onValueChange = {},
                readOnly = true,
                label = { Text("Class Level") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = classExpanded,
                onDismissRequest = { classExpanded = false }
            ) {
                uiState.availableClassLevels.forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level) },
                        onClick = {
                            onClassSelect(level)
                            classExpanded = false
                        }
                    )
                }
            }
        }

        // --- Topic Selection ---
        ExposedDropdownMenuBox(
            expanded = topicExpanded,
            onExpandedChange = { topicExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedTopic?.title ?: "Select Topic",
                onValueChange = {},
                readOnly = true,
                label = { Text("Topic") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = topicExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = filteredTopics.isNotEmpty()
            )
            ExposedDropdownMenu(
                expanded = topicExpanded,
                onDismissRequest = { topicExpanded = false }
            ) {
                filteredTopics.forEach { topic ->
                    DropdownMenuItem(
                        text = { Text(topic.title) },
                        onClick = {
                            onTopicSelect(topic)
                            topicExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Resource Title") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Text(text = "Resource Type", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { t ->
                FilterChip(
                    selected = type == t,
                    onClick = { onTypeChange(t) },
                    label = { Text(t) }
                )
            }
        }

        if (type == "PDF_LINK") {
            OutlinedTextField(
                value = url,
                onValueChange = onUrlChange,
                label = { Text("External URL (GitHub Release, Archive.org, etc)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            OutlinedTextField(
                value = fileSize,
                onValueChange = onFileSizeChange,
                label = { Text("File Size (e.g. 15MB, 1.2GB)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Helps teachers manage data") }
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onPickFile,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (fileName.isBlank()) "Select File from Phone" else "Selected: $fileName",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        OutlinedTextField(
            value = githubToken,
            onValueChange = onTokenChange,
            label = { Text("GitHub Access Token") },
            placeholder = { Text("Token is saved after first success") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )

        if (statusMessage.isNotBlank()) {
            Text(
                text = statusMessage,
                color = if (statusMessage.contains("Success")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = onUpload,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isUploading
        ) {
            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sync to GitHub")
            }
        }
    }
}

@Composable
fun UploadHistory(uploads: List<AdminResourceEntity>) {
    if (uploads.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No upload history yet.")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uploads) { upload ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = upload.title, fontWeight = FontWeight.Bold)
                            Text(text = "Type: ${upload.type}", style = MaterialTheme.typography.bodySmall)
                            val date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(upload.uploadDate))
                            Text(text = "Date: $date", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            text = "LIVE", 
                            color = MaterialTheme.colorScheme.primary, 
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
