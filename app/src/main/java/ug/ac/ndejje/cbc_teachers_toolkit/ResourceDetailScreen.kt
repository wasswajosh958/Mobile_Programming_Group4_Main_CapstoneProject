package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TeachingResourceEntity
import ug.ac.ndejje.cbc_teachers_toolkit.domain.Topic
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.CbcTeachersToolkitTheme
import ug.ac.ndejje.cbc_teachers_toolkit.util.openUrl

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

    ResourceDetailContent(
        topic = topic,
        isFavorite = isFavorite,
        persistedNote = persistedNote,
        downloadedResources = resources,
        onBackClick = { navController.popBackStack() },
        onToggleFavorite = { viewModel.toggleFavorite(topicId) },
        onSaveNote = { note -> viewModel.saveNote(topicId, note) },
        onOpenResource = { resource ->
            if (resource.type == "VIDEO") {
                val encoded = java.net.URLEncoder.encode(resource.url, "UTF-8")
                navController.navigate("video/$encoded")
            } else {
                // Handled via openUrl in content
            }
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
    onOpenResource: (TeachingResourceEntity) -> Unit,
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
            SectionHeader(title = "Resources", icon = Icons.Default.Topic)
            
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val query = "site:ncdc.go.ug ${topic.subject} ${topic.classLevel} ${topic.title}"
                    openUrl(context, "https://www.google.com/search?q=" + java.net.URLEncoder.encode(query, "UTF-8"))
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Link, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search NCDC for ${topic.title}")
            }

            if (downloadedResources.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                downloadedResources.forEach { resource ->
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onOpenResource(resource) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = when (resource.type) {
                                "VIDEO" -> Icons.Filled.PlayCircle
                                "NOTES" -> Icons.AutoMirrored.Filled.MenuBook
                                else -> Icons.Filled.Link
                            },
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = resource.title)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Collapsible Sections ---
            ExpandableSection(
                title = "Lesson Plan",
                content = topic.lessonPlan,
                expanded = showLessonPlan,
                onToggle = { showLessonPlan = !showLessonPlan }
            )

            ExpandableSection(
                title = "Project Ideas",
                content = topic.projectIdeas,
                expanded = showProjectIdeas,
                onToggle = { showProjectIdeas = !showProjectIdeas }
            )

            ExpandableSection(
                title = "Assessment Rubric",
                content = topic.assessmentRubric,
                expanded = showAssessment,
                onToggle = { showAssessment = !showAssessment }
            )

            ExpandableSection(
                title = "Teaching Tips",
                content = topic.teachingTips,
                expanded = showTeachingTips,
                onToggle = { showTeachingTips = !showTeachingTips }
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
            OutlinedTextField(
                value = noteDraft,
                onValueChange = { noteDraft = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("My Teaching Notes") },
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
                        Icon(Icons.Default.Save, contentDescription = "Save Note", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )

            AnimatedVisibility(visible = showSavedHint) {
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
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
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
                    fontWeight = FontWeight.Bold
                )
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
