package ug.ac.ndejje.cbc_teachers_toolkit.ui.screens

import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Description
import androidx.compose.foundation.clickable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TeachingResourceEntity
import ug.ac.ndejje.cbc_teachers_toolkit.domain.Topic
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.CbcTeachersToolkitTheme
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import ug.ac.ndejje.cbc_teachers_toolkit.util.openNotesAsPdf
import ug.ac.ndejje.cbc_teachers_toolkit.util.shareNotes
import ug.ac.ndejje.cbc_teachers_toolkit.util.openDownloadedFile
import ug.ac.ndejje.cbc_teachers_toolkit.util.openUrl
import ug.ac.ndejje.cbc_teachers_toolkit.util.TextToSpeechHelper
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SubjectViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ResourceDetailScreen(
    navController: NavController,
    viewModel: SubjectViewModel,
    topicId: Int
) {
    val topic = viewModel.topicById(topicId)
    val uiState by viewModel.uiState.collectAsState()
    val persistedNote = uiState.notes[topicId].orEmpty()
    val isFavorite = uiState.favorites.contains(topicId)
    val resources by viewModel.observeResourcesForTopic(topicId).collectAsState(initial = emptyList())

    val context = LocalContext.current
    val ttsHelper = remember { TextToSpeechHelper(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    ResourceDetailContent(
        topic = topic,
        isFavorite = isFavorite,
        persistedNote = persistedNote,
        downloadedResources = resources,
        onBackClick = { navController.popBackStack() },
        onToggleFavorite = { viewModel.toggleFavorite(topicId) },
        onSaveNote = { note -> viewModel.saveNote(topicId, note) },
        onShareNote = { note -> 
            topic?.let { shareNotes(navController.context, it, note) }
        },
        onViewNote = { note ->
            topic?.let { openNotesAsPdf(navController.context, it, note) }
        },
        onSpeak = { text -> ttsHelper.speak(text) },
        onOpenResource = { resource ->
            val pathOrUrl = if (resource.isDownloaded && resource.localPath?.isNotBlank() == true) {
                if (resource.localPath.startsWith("Resources/")) "asset:///${resource.localPath}" else resource.localPath
            } else {
                resource.url
            }
            
            // Force use of external viewer for everything as requested
            openDownloadedFile(navController.context, pathOrUrl)
        },
        onToggleResourceFavorite = { resourceKey ->
            viewModel.toggleResourceFavorite(resourceKey)
        },
        onOpenScheme = {
            navController.navigate("scheme?topicId=$topicId")
        }
    )
}

@Composable
fun ResourceDetailContent(
    topic: Topic?,
    isFavorite: Boolean,
    persistedNote: String,
    downloadedResources: List<TeachingResourceEntity> = emptyList(),
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSaveNote: (String) -> Unit,
    onShareNote: (String) -> Unit = {},
    onViewNote: (String) -> Unit = {},
    onSpeak: (String) -> Unit = {},
    onOpenResource: (TeachingResourceEntity) -> Unit,
    onToggleResourceFavorite: (String) -> Unit = {},
    onOpenScheme: () -> Unit
) {
    var noteDraft by remember(topic?.id) { mutableStateOf(persistedNote) }
    var showSavedHint by remember { mutableStateOf(false) }
    var showLessonPlan by rememberSaveable(topic?.id) { mutableStateOf(true) }
    var showProjectIdeas by rememberSaveable(topic?.id) { mutableStateOf(false) }
    var showAssessment by rememberSaveable(topic?.id) { mutableStateOf(false) }
    var showTeachingTips by rememberSaveable(topic?.id) { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(persistedNote) {
        noteDraft = persistedNote
    }

    LaunchedEffect(showSavedHint) {
        if (showSavedHint) {
            delay(1500)
            showSavedHint = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Header Section ---
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
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 32.dp
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = topic?.title ?: "Resource Detail",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (topic != null) "${topic.subject} • ${topic.classLevel}" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        if (topic == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Topic not found", style = MaterialTheme.typography.bodyLarge)
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- Official Resources Section ---
            if (downloadedResources.isNotEmpty()) {
                SectionHeader(title = "Educational Materials", icon = Icons.Default.Folder)

                val grouped = downloadedResources.groupBy { it.type }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Video Folder
                    ResourceFolder(
                        title = "Videos",
                        count = grouped["VIDEO"]?.size ?: 0,
                        icon = Icons.Default.PlayCircle,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onClick = { /* Could scroll to section or open dialog */ }
                    )
                    
                    // Notes Folder
                    ResourceFolder(
                        title = "Notes",
                        count = (grouped["NOTES"]?.size ?: 0) + (grouped["PDF_LINK"]?.size ?: 0),
                        icon = Icons.Default.Description,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { /* Could scroll to section or open dialog */ }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                grouped.forEach { (type, resourcesForType) ->
                    val typeLabel = when(type) {
                        "VIDEO" -> "Video Lessons"
                        "NOTES" -> "Teaching Notes"
                        "PDF_LINK" -> "NCDC PDF Guides"
                        else -> "Supplementary"
                    }
                    
                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    resourcesForType.forEach { resource ->
                        ResourceItemRow(
                            resource = resource,
                            onOpen = { onOpenResource(resource) },
                            onToggleFavorite = { onToggleResourceFavorite(resource.key) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Collapsible Sections ---
            ExpandableSection(
                title = "Lesson Plan",
                content = topic.lessonPlan,
                expanded = showLessonPlan,
                onToggle = { showLessonPlan = !showLessonPlan },
                onSpeak = { onSpeak(topic.lessonPlan) }
            )

            ExpandableSection(
                title = "Project Ideas",
                content = topic.projectIdeas,
                expanded = showProjectIdeas,
                onToggle = { showProjectIdeas = !showProjectIdeas },
                onSpeak = { onSpeak(topic.projectIdeas) }
            )

            ExpandableSection(
                title = "Assessment Rubric",
                content = topic.assessmentRubric,
                expanded = showAssessment,
                onToggle = { showAssessment = !showAssessment },
                onSpeak = { onSpeak(topic.assessmentRubric) }
            )

            ExpandableSection(
                title = "Teaching Tips",
                content = topic.teachingTips,
                expanded = showTeachingTips,
                onToggle = { showTeachingTips = !showTeachingTips },
                onSpeak = { onSpeak(topic.teachingTips) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Quick Actions ---
            SectionHeader(title = "Tools", icon = Icons.Default.Save)
            
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenScheme,
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Generate Scheme of Work")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- My Notes ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "My Teaching Notes",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    if (noteDraft.isNotBlank()) {
                        IconButton(onClick = { onSpeak(noteDraft) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Read Aloud",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { onShareNote(noteDraft) }) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { onViewNote(noteDraft) }) {
                            Icon(
                                Icons.Default.PictureAsPdf,
                                contentDescription = "View as PDF",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = noteDraft,
                onValueChange = { noteDraft = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                placeholder = { Text("Add your observations or adjustments for this topic...") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        onSaveNote(noteDraft)
                        showSavedHint = true
                    }) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = "Save Note",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

            if (showSavedHint) {
                Text(
                    text = "Note saved successfully!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ResourceFolder(
    title: String,
    count: Int,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$count items",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ResourceItemRow(
    resource: TeachingResourceEntity,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit = {}
) {
    val typeIcon = when(resource.type) {
        "VIDEO" -> Icons.Default.PlayCircle
        "NOTES", "PDF_LINK" -> Icons.Default.Description
        else -> Icons.Default.Link
    }

    Surface(
        onClick = onOpen,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = typeIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resource.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = resource.source,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (resource.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ExpandableSection(
    title: String,
    content: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSpeak: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        onClick = onToggle
    ) {
        Column(modifier = Modifier.padding(16.dp).animateContentSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = { onSpeak() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Read Aloud",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResourceDetailPreview() {
    CbcTeachersToolkitTheme {
        ResourceDetailContent(
            topic = Topic(
                id = 1,
                title = "Counting Numbers",
                subject = "Mathematics",
                classLevel = "P.1",
                lessonPlan = "1. Introduction to numbers 1-10\n2. Practical counting using bottle tops\n3. Group activities.",
                projectIdeas = "Create a number line using locally available materials.",
                assessmentRubric = "Can identify numbers: 5pts\nCan count objects: 5pts",
                teachingTips = "Use songs and rhymes to make counting fun for P.1 pupils."
            ),
            isFavorite = true,
            persistedNote = "Pupils enjoyed the bottle top activity.",
            onBackClick = {},
            onToggleFavorite = {},
            onSaveNote = {},
            onOpenResource = {},
            onOpenScheme = {}
        )
    }
}
