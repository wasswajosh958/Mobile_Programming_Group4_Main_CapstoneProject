package ug.ac.ndejje.cbc_teachers_toolkit

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import ug.ac.ndejje.cbc_teachers_toolkit.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.PlayCircle
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TeachingResourceEntity
import ug.ac.ndejje.cbc_teachers_toolkit.domain.Topic
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.CbcTeachersToolkitTheme
import ug.ac.ndejje.cbc_teachers_toolkit.util.openDownloadedFile
import ug.ac.ndejje.cbc_teachers_toolkit.util.openNotesAsPdf
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.OutlinedButton
import ug.ac.ndejje.cbc_teachers_toolkit.util.openUrl

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: SubjectViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val downloadedResources by viewModel.downloadedResources.collectAsState()
    val favoriteTopics = uiState.allTopics.filter { uiState.favorites.contains(it.id) }
    val notedTopics = uiState.allTopics.filter { uiState.notes[it.id].isNullOrBlank().not() }

    LibraryContent(
        favoriteTopics = favoriteTopics,
        notedTopics = notedTopics,
        downloadedResources = downloadedResources,
        notes = uiState.notes,
        onBackClick = { navController.popBackStack() },
        onTopicClick = { topicId -> navController.navigate("resource/$topicId") },
        onResourceClick = { resource ->
            if (resource.type == "VIDEO") {
                val pathOrUrl = if (resource.isDownloaded && resource.localPath?.isNotBlank() == true) {
                    resource.localPath
                } else {
                    resource.url
                }
                val encoded = java.net.URLEncoder.encode(pathOrUrl, "UTF-8")
                navController.navigate("video/$encoded")
            } else {
                if (resource.isDownloaded && resource.localPath?.isNotBlank() == true) {
                    openDownloadedFile(navController.context, resource.localPath)
                } else {
                    openUrl(navController.context, resource.url)
                }
            }
        },
        onViewNote = { topic, note ->
            openNotesAsPdf(navController.context, topic, note)
        }
    )
}

@Composable
fun LibraryContent(
    favoriteTopics: List<Topic>,
    notedTopics: List<Topic>,
    downloadedResources: List<TeachingResourceEntity> = emptyList(),
    notes: Map<Int, String>,
    onBackClick: () -> Unit,
    onTopicClick: (Int) -> Unit,
    onResourceClick: (TeachingResourceEntity) -> Unit = {},
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
                            text = stringResource(id = R.string.my_library),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = stringResource(id = R.string.library_summary, favoriteTopics.size, notedTopics.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        if (favoriteTopics.isEmpty() && notedTopics.isEmpty() && downloadedResources.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.library_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Go to Topics")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }

                if (downloadedResources.isNotEmpty()) {
                    item {
                        Text(
                            text = "Offline Resources",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(downloadedResources) { resource ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            onClick = { onResourceClick(resource) }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (resource.type == "VIDEO") Icons.Default.PlayCircle else Icons.Default.DownloadDone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = resource.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "Available Offline",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                if (favoriteTopics.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.favorite_topics_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(favoriteTopics, key = { "fav_${it.id}" }) { topic ->
                        AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                ),
                                onClick = { onTopicClick(topic.id) }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = topic.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = stringResource(id = R.string.subject_and_class, topic.subject, topic.classLevel),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                if (notedTopics.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.noted_topics_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(notedTopics, key = { "note_${it.id}" }) { topic ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            ),
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
                                }
                                IconButton(onClick = { onViewNote(topic, notes[topic.id].orEmpty()) }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = "View as PDF",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LibraryScreenPreview() {
    CbcTeachersToolkitTheme {
        LibraryContent(
            favoriteTopics = listOf(
                Topic(1, "Algebraic Expressions", "Mathematics", "Senior 1", "Plan", "Ideas", "Rubric", "Tips"),
                Topic(2, "Photosynthesis", "Biology", "Senior 2", "Plan", "Ideas", "Rubric", "Tips")
            ),
            notedTopics = listOf(
                Topic(3, "The French Revolution", "History", "Senior 3", "Plan", "Ideas", "Rubric", "Tips")
            ),
            notes = mapOf(3 to "Important for next week's exam prep."),
            onBackClick = {},
            onTopicClick = {}
        )
    }
}
