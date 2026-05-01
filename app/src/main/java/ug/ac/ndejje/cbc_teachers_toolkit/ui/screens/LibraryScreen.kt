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

import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource

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
        // Top part with the screen title
        val headerGradient = Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = dimensionResource(id = R.dimen.header_corner_radius), bottomEnd = dimensionResource(id = R.dimen.header_corner_radius)))
                .background(brush = headerGradient)
        ) {
            Column(
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.padding_12dp),
                    end = dimensionResource(id = R.dimen.padding_20dp),
                    top = dimensionResource(id = R.dimen.padding_medium),
                    bottom = dimensionResource(id = R.dimen.header_corner_radius)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(id = R.string.menu),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_xsmall)))
                    Column {
                        Text(
                            text = stringResource(id = R.string.library_header_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (selectedSubject != null) stringResource(id = R.string.library_resources_for, selectedSubject) else stringResource(id = R.string.library_select_subject_hint),
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
                .padding(horizontal = dimensionResource(id = R.dimen.padding_20dp)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium))) }

            // Buttons to pick a subject and filter the list
            item {
                val subjects = allTopics.map { it.subject }.distinct().sorted()
                Text(
                    text = stringResource(id = R.string.filter_subject),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
                    modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small))
                ) {
                    item {
                        androidx.compose.material3.FilterChip(
                            selected = selectedSubject == null,
                            onClick = { onSubjectSelect(null) },
                            label = { Text(stringResource(id = R.string.all_notes)) },
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.chip_corner_radius))
                        )
                    }
                    items(subjects) { subject ->
                        androidx.compose.material3.FilterChip(
                            selected = selectedSubject == subject,
                            onClick = { onSubjectSelect(subject) },
                            label = { Text(subject) },
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.chip_corner_radius))
                        )
                    }
                }
            }

            if (selectedSubject != null) {
                item {
                    Text(
                        text = stringResource(id = R.string.curriculum_books_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (subjectTopics.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.no_books_found),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(subjectTopics) { topic ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.card_elevation_small)),
                        onClick = { onTopicClick(topic.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium))
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
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

            // Show materials that the teacher has bookmarked
            if (selectedSubject == null && favoriteResources.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.bookmarked_materials),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_small))
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

            // Show notes the teacher has written for different topics
            val filteredNotedTopics = if (selectedSubject != null) {
                notedTopics.filter { it.subject == selectedSubject }
            } else {
                notedTopics
            }

            if (filteredNotedTopics.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.personal_notes_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_small))
                    )
                }

                items(filteredNotedTopics, key = { "note_${it.id}" }) { topic ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.card_elevation_small).div(2)),
                        onClick = { onTopicClick(topic.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
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
                                        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_xsmall))
                                    )
                                }
                            }
                            IconButton(onClick = { onViewNote(topic, notes[topic.id].orEmpty()) }) {
                                Icon(
                                    imageVector = Icons.Default.DownloadDone, // Changed to show it's exportable
                                    contentDescription = stringResource(id = R.string.export_pdf),
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
                            .padding(vertical = dimensionResource(id = R.dimen.padding_xxlarge)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
                        Text(
                            text = stringResource(id = R.string.library_empty_state_text),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large))) }
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
