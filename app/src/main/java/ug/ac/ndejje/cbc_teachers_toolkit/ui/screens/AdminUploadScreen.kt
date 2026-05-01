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

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.dimensionResource
import ug.ac.ndejje.cbc_teachers_toolkit.R

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

    val errorFillFields = stringResource(R.string.error_fill_all_fields)
    val errorProvideUrl = stringResource(R.string.error_provide_url)
    val errorSelectFile = stringResource(R.string.error_select_file)
    val statusUploadingFile = stringResource(R.string.status_uploading_file)
    val errorReadFile = stringResource(R.string.error_read_file)
    val errorUploadFailedPrefix = stringResource(R.string.error_upload_failed, "")
    val statusUpdatingIndex = stringResource(R.string.status_updating_index)
    val statusSuccess = stringResource(R.string.status_success)

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
                .clip(RoundedCornerShape(bottomStart = dimensionResource(R.dimen.header_corner_radius), bottomEnd = dimensionResource(R.dimen.header_corner_radius)))
                .background(brush = headerGradient)
        ) {
            Column(
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.padding_12dp),
                    end = dimensionResource(R.dimen.padding_20dp),
                    top = dimensionResource(R.dimen.padding_medium),
                    bottom = dimensionResource(R.dimen.padding_xlarge)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(R.string.menu),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_xsmall)))
                    Text(
                        text = stringResource(R.string.admin_panel_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = stringResource(R.string.admin_management_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text(stringResource(R.string.tab_upload), modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text(stringResource(R.string.tab_history), modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))
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
                            statusMessage = errorFillFields
                            return@UploadForm
                        }
                        if (type == "PDF_LINK" && url.isBlank()) {
                            statusMessage = errorProvideUrl
                            return@UploadForm
                        }
                        if (type != "PDF_LINK" && selectedFileUri == null) {
                            statusMessage = errorSelectFile
                            return@UploadForm
                        }

                        isUploading = true
                        scope.launch {
                            try {
                                val githubManager = GitHubAdminManager(token = githubToken)
                                var finalUrl = url

                                // 1. Upload file if needed
                                if (selectedFileUri != null) {
                                    statusMessage = statusUploadingFile
                                    val bytes = context.contentResolver.openInputStream(selectedFileUri!!)?.use { it.readBytes() }
                                    if (bytes == null) {
                                        statusMessage = errorReadFile
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
                                        statusMessage = context.getString(R.string.error_upload_failed, uploadResult.exceptionOrNull()?.message ?: "Unknown")
                                        isUploading = false
                                        return@launch
                                    }
                                }

                                // 2. Add to index
                                statusMessage = statusUpdatingIndex
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
                                    
                                    statusMessage = statusSuccess
                                    
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
                                    statusMessage = context.getString(R.string.error_generic_prefix, result.exceptionOrNull()?.message ?: "Unknown")
                                }
                            } catch (e: Exception) {
                                statusMessage = context.getString(R.string.error_generic_prefix, e.message ?: "Unknown")
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

    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))) {
        // --- Subject Selection ---
        ExposedDropdownMenuBox(
            expanded = subjectExpanded,
            onExpandedChange = { subjectExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedSubject ?: stringResource(R.string.select_subject),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.subject_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius))
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
                value = selectedClass ?: stringResource(R.string.select_class),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.class_level_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius))
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
                value = selectedTopic?.title ?: stringResource(R.string.select_topic),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.topic_title_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = topicExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius)),
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
            label = { Text(stringResource(R.string.resource_title)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius))
        )

        Text(text = stringResource(R.string.resource_type), style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
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
                label = { Text(stringResource(R.string.external_url_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius))
            )
            
            OutlinedTextField(
                value = fileSize,
                onValueChange = onFileSizeChange,
                label = { Text(stringResource(R.string.file_size_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius)),
                placeholder = { Text(stringResource(R.string.file_size_placeholder)) }
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onPickFile,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(dimensionResource(R.dimen.border_thin), MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium)))
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                    Text(
                        text = if (fileName.isBlank()) stringResource(R.string.select_file_phone) else stringResource(R.string.selected_file_format, fileName),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        OutlinedTextField(
            value = githubToken,
            onValueChange = onTokenChange,
            label = { Text(stringResource(R.string.github_token_label)) },
            placeholder = { Text(stringResource(R.string.github_token_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius)),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )

        if (statusMessage.isNotBlank()) {
            val successMessage = stringResource(R.string.status_success)
            Text(
                text = statusMessage,
                color = if (statusMessage == successMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = onUpload,
            modifier = Modifier.fillMaxWidth().height(dimensionResource(R.dimen.button_height_medium)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius)),
            enabled = !isUploading
        ) {
            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.size(dimensionResource(R.dimen.padding_large)), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
                Text(stringResource(R.string.sync_to_github))
            }
        }
    }
}

@Composable
fun UploadHistory(uploads: List<AdminResourceEntity>) {
    if (uploads.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(dimensionResource(R.dimen.padding_xlarge)), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_history_yet))
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))) {
            items(uploads) { upload ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius))
                ) {
                    Row(
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = upload.title, fontWeight = FontWeight.Bold)
                            Text(text = stringResource(R.string.upload_type_format, upload.type), style = MaterialTheme.typography.bodySmall)
                            val date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(upload.uploadDate))
                            Text(text = stringResource(R.string.upload_date_format, date), style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            text = stringResource(R.string.status_live),
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
