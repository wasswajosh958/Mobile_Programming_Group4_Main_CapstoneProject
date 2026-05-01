package ug.ac.ndejje.cbc_teachers_toolkit.ui.screens

import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SubjectViewModel
import ug.ac.ndejje.cbc_teachers_toolkit.R

import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TeachingResourceEntity
import ug.ac.ndejje.cbc_teachers_toolkit.domain.Topic
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.CbcTeachersToolkitTheme
import ug.ac.ndejje.cbc_teachers_toolkit.util.openDownloadedFile
import ug.ac.ndejje.cbc_teachers_toolkit.util.openNotesAsPdf
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.OutlinedButton
import ug.ac.ndejje.cbc_teachers_toolkit.util.openUrl

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: SubjectViewModel,
    onMenuClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val downloadedResources by viewModel.downloadedResources.collectAsState()
    val favoriteResources by viewModel.favoriteResources.collectAsState()
    
    val selectedSubject = uiState.selectedSubject
    val resourcesToShow = if (selectedSubject != null) {
        uiState.allTopics.filter { it.subject == selectedSubject }
    } else {
        emptyList()
    }

    val favoriteTopics = uiState.allTopics.filter { uiState.favorites.contains(it.id) }
    val notedTopics = uiState.allTopics.filter { uiState.notes[it.id].isNullOrBlank().not() }

    LibraryContent(
        selectedSubject = selectedSubject,
        subjectTopics = resourcesToShow,
        favoriteTopics = favoriteTopics,
        notedTopics = notedTopics,
        downloadedResources = downloadedResources,
        favoriteResources = favoriteResources,
        allTopics = uiState.allTopics,
        notes = uiState.notes,
        onBackClick = { navController.popBackStack() },
        onMenuClick = onMenuClick,
        onSubjectSelect = { viewModel.selectSubject(it) },
        onTopicClick = { topicId -> navController.navigate("resource/$topicId") },
        onResourceClick = { resource ->
            val pathOrUrl = if (resource.isDownloaded && resource.localPath?.isNotBlank() == true) {
                if (resource.localPath.startsWith("Resources/")) "asset:///${resource.localPath}" else resource.localPath
            } else {
                resource.url
            }
            openDownloadedFile(navController.context, pathOrUrl)
        },
        onToggleResourceFavorite = { viewModel.toggleResourceFavorite(it) },
        onViewNote = { topic, note ->
            openNotesAsPdf(navController.context, topic, note)
        }
    )
}

@Composable
fun LibraryContent(
    selectedSubject: String?,
    subjectTopics: List<Topic>,
    favoriteTopics: List<Topic>,
    notedTopics: List<Topic>,
    downloadedResources: List<TeachingResourceEntity> = emptyList(),
    favoriteResources: List<TeachingResourceEntity> = emptyList(),
    allTopics: List<Topic> = emptyList(),
    notes: Map<Int, String>,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit = {},
    onSubjectSelect: (String?) -> Unit,
    onTopicClick: (Int) -> Unit,
    onResourceClick: (TeachingResourceEntity) -> Unit = {},
    onToggleResourceFavorite: (String) -> Unit = {},
    onViewNote: (Topic, String) -> Unit = { _, _ -> }
) {
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
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Teacher's Library",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (selectedSubject != null) "Resources for $selectedSubject" else "Select a subject to view books",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- Subject Selection Chips ---
            item {
                val subjects = allTopics.map { it.subject }.distinct().sorted()
                Text(
                    text = "Filter by Subject",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    item {
                        androidx.compose.material3.FilterChip(
                            selected = selectedSubject == null,
                            onClick = { onSubjectSelect(null) },
                            label = { Text("All Notes") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    items(subjects) { subject ->
                        androidx.compose.material3.FilterChip(
                            selected = selectedSubject == subject,
                            onClick = { onSubjectSelect(subject) },
                            label = { Text(subject) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            if (selectedSubject != null) {
                item {
                    Text(
                        text = "Curriculum Books & Resources",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (subjectTopics.isEmpty()) {
                    item {
                        Text(
                            text = "No books found for this subject.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(subjectTopics) { topic ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        onClick = { onTopicClick(topic.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = topic.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = topic.classLevel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // --- Favorite Resources (PDFs/Videos) ---
            if (selectedSubject == null && favoriteResources.isNotEmpty()) {
                item {
                    Text(
                        text = "Bookmarked Materials",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(favoriteResources) { resource ->
                    ResourceItemRow(
                        resource = resource,
                        onOpen = { onResourceClick(resource) },
                        onToggleFavorite = { onToggleResourceFavorite(resource.key) }
                    )
                }
            }

            // --- Notes Section (Always visible or filtered) ---
            val filteredNotedTopics = if (selectedSubject != null) {
                notedTopics.filter { it.subject == selectedSubject }
            } else {
                notedTopics
            }

            if (filteredNotedTopics.isNotEmpty()) {
                item {
                    Text(
                        text = "My Personal Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(filteredNotedTopics, key = { "note_${it.id}" }) { topic ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        onClick = { onTopicClick(topic.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = topic.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = notes[topic.id].orEmpty(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                if (selectedSubject == null) {
                                    Text(
                                        text = "${topic.subject} • ${topic.classLevel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                            IconButton(onClick = { onViewNote(topic, notes[topic.id].orEmpty()) }) {
                                Icon(
                                    imageVector = Icons.Default.DownloadDone, // Changed to show it's exportable
                                    contentDescription = "Export PDF",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            } else if (selectedSubject == null && notedTopics.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No notes or books selected yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LibraryScreenPreview() {
    CbcTeachersToolkitTheme {
        LibraryContent(
            selectedSubject = "Mathematics",
            subjectTopics = listOf(
                Topic(1, "Algebraic Expressions", "Mathematics", "Senior 1", "Plan", "Ideas", "Rubric", "Tips")
            ),
            favoriteTopics = listOf(
                Topic(1, "Algebraic Expressions", "Mathematics", "Senior 1", "Plan", "Ideas", "Rubric", "Tips"),
                Topic(2, "Photosynthesis", "Biology", "Senior 2", "Plan", "Ideas", "Rubric", "Tips")
            ),
            notedTopics = listOf(
                Topic(3, "The French Revolution", "History", "Senior 3", "Plan", "Ideas", "Rubric", "Tips")
            ),
            notes = mapOf(3 to "Important for next week's exam prep."),
            onBackClick = {},
            onSubjectSelect = {},
            onTopicClick = {}
        )
    }
}
