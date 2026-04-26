package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController

@Composable
fun ResourceDetailScreen(
    navController: NavController,
    viewModel: SubjectViewModel,
    topicId: Int
) {
    val topic = viewModel.topicById(topicId)
    val uiState = viewModel.uiState.collectAsState().value
    val noteValue = uiState.notes[topicId].orEmpty()
    val isFavorite = uiState.favorites.contains(topicId)

    if (topic == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(text = stringResource(id = R.string.no_topics_found))
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = R.string.resource_title_format,
                            topic.title,
                            topic.classLevel,
                            topic.subject
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(dimensionResource(id = R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))) {
                    Text(text = stringResource(id = R.string.lesson_plan), style = MaterialTheme.typography.titleLarge)
                    Text(text = topic.lessonPlan, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))) {
                Text(text = stringResource(id = R.string.project_ideas), style = MaterialTheme.typography.titleLarge)
                Text(text = topic.projectIdeas, style = MaterialTheme.typography.bodyMedium)
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))) {
                Text(text = stringResource(id = R.string.assessment_rubric), style = MaterialTheme.typography.titleLarge)
                Text(text = topic.assessmentRubric, style = MaterialTheme.typography.bodyMedium)
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))) {
                Text(text = stringResource(id = R.string.teaching_tips), style = MaterialTheme.typography.titleLarge)
                Text(text = topic.teachingTips, style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.toggleFavorite(topicId) }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(id = R.string.favorite)
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_xsmall)))
                    Text(text = stringResource(id = R.string.favorite))
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.saveNote(topicId, noteValue) }
                ) {
                    Text(text = stringResource(id = R.string.add_note))
                }
            }

            OutlinedTextField(
                value = noteValue,
                onValueChange = { viewModel.saveNote(topicId, it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.saved_note)) },
                placeholder = { Text(stringResource(id = R.string.note_hint)) }
            )
        }
    }
}