package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceDetailScreen(
    navController: NavController,
    viewModel: SubjectViewModel,
    topicId: Int
) {
    val topic = viewModel.topicById(topicId)
    val uiState = viewModel.uiState.collectAsState().value
    val persistedNote = uiState.notes[topicId].orEmpty()
    val isFavorite = uiState.favorites.contains(topicId)
    var noteDraft by remember(topicId) { mutableStateOf("") }
    var showSavedHint by remember { mutableStateOf(false) }
    var showLessonPlan by rememberSaveable(topicId) { mutableStateOf(true) }
    var showProjectIdeas by rememberSaveable(topicId) { mutableStateOf(true) }
    var showAssessment by rememberSaveable(topicId) { mutableStateOf(true) }
    var showTeachingTips by rememberSaveable(topicId) { mutableStateOf(true) }

    LaunchedEffect(persistedNote, topicId) {
        noteDraft = persistedNote
    }
    LaunchedEffect(showSavedHint) {
        if (showSavedHint) {
            delay(1500)
            showSavedHint = false
        }
    }

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
            DetailSectionCard(
                title = stringResource(id = R.string.lesson_plan),
                content = topic.lessonPlan,
                expanded = showLessonPlan,
                onToggle = { showLessonPlan = !showLessonPlan }
            )

            DetailSectionCard(
                title = stringResource(id = R.string.project_ideas),
                content = topic.projectIdeas,
                expanded = showProjectIdeas,
                onToggle = { showProjectIdeas = !showProjectIdeas }
            )

            DetailSectionCard(
                title = stringResource(id = R.string.assessment_rubric),
                content = topic.assessmentRubric,
                expanded = showAssessment,
                onToggle = { showAssessment = !showAssessment }
            )

            DetailSectionCard(
                title = stringResource(id = R.string.teaching_tips),
                content = topic.teachingTips,
                expanded = showTeachingTips,
                onToggle = { showTeachingTips = !showTeachingTips }
            )

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
                    onClick = {
                        viewModel.saveNote(topicId, noteDraft)
                        showSavedHint = true
                    }
                ) {
                    Text(text = stringResource(id = R.string.save_note))
                }
            }

            OutlinedTextField(
                value = noteDraft,
                onValueChange = { noteDraft = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.saved_note)) },
                placeholder = { Text(stringResource(id = R.string.note_hint)) }
            )

            AnimatedVisibility(
                visible = showSavedHint,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                modifier = Modifier.animateContentSize()
            ) {
                Text(
                    text = stringResource(id = R.string.note_saved_message),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    content: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onToggle() },
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (expanded) {
                    stringResource(id = R.string.collapse_section)
                } else {
                    stringResource(id = R.string.expand_section)
                }
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 5 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 5 })
        ) {
            Text(text = content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}