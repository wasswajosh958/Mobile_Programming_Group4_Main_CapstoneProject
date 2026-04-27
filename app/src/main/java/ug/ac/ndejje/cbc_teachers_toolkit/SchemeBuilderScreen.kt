package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import ug.ac.ndejje.cbc_teachers_toolkit.util.shareScheme

@Composable
fun SchemeBuilderScreen(
    navController: NavController,
    viewModel: SubjectViewModel,
    topicId: Int
) {
    val draft by viewModel.schemeDraftState.collectAsState()
    val saveStatus by viewModel.schemeSaveStatus.collectAsState()
    val schemes by viewModel.schemes.collectAsState()
    val context = LocalContext.current
    var showGuide by remember { mutableStateOf(true) }

    LaunchedEffect(topicId) {
        if (topicId > 0) {
            viewModel.prefillSchemeFromTopic(topicId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        Text(
            text = stringResource(id = R.string.scheme_builder_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(id = R.string.scheme_builder_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedButton(
            onClick = { showGuide = !showGuide },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (showGuide) {
                    stringResource(id = R.string.hide_scheme_guide)
                } else {
                    stringResource(id = R.string.show_scheme_guide)
                }
            )
        }

        AnimatedVisibility(
            visible = showGuide,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_xsmall))
                ) {
                    Text(
                        text = stringResource(id = R.string.scheme_guide_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = stringResource(id = R.string.scheme_guide_step_1))
                    Text(text = stringResource(id = R.string.scheme_guide_step_2))
                    Text(text = stringResource(id = R.string.scheme_guide_step_3))
                    Text(text = stringResource(id = R.string.scheme_guide_step_4))
                    Text(text = stringResource(id = R.string.scheme_guide_step_5))
                }
            }
        }

        OutlinedTextField(
            value = draft.teacherName,
            onValueChange = { value ->
                viewModel.updateSchemeDraft { it.copy(teacherName = value) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.teacher_name_label)) }
        )
        OutlinedTextField(
            value = draft.subject,
            onValueChange = { value ->
                viewModel.updateSchemeDraft { it.copy(subject = value) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.subject_label)) }
        )
        OutlinedTextField(
            value = draft.classLevel,
            onValueChange = { value ->
                viewModel.updateSchemeDraft { it.copy(classLevel = value) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.class_level_label)) }
        )
        OutlinedTextField(
            value = draft.term,
            onValueChange = { value ->
                viewModel.updateSchemeDraft { it.copy(term = value) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.term_label)) }
        )
        OutlinedTextField(
            value = draft.week,
            onValueChange = { value ->
                viewModel.updateSchemeDraft { it.copy(week = value) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.week_label)) }
        )
        OutlinedTextField(
            value = draft.topicTitle,
            onValueChange = { value ->
                viewModel.updateSchemeDraft { it.copy(topicTitle = value) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.topic_title_label)) }
        )
        OutlinedTextField(
            value = draft.objectives,
            onValueChange = { value ->
                viewModel.updateSchemeDraft { it.copy(objectives = value) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.objectives_label)) }
        )
        OutlinedTextField(
            value = draft.activities,
            onValueChange = { value ->
                viewModel.updateSchemeDraft { it.copy(activities = value) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.activities_label)) }
        )
        OutlinedTextField(
            value = draft.resources,
            onValueChange = { value ->
                viewModel.updateSchemeDraft { it.copy(resources = value) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.resources_label)) }
        )
        OutlinedTextField(
            value = draft.assessment,
            onValueChange = { value ->
                viewModel.updateSchemeDraft { it.copy(assessment = value) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.assessment_label)) }
        )

        OutlinedButton(
            onClick = { viewModel.saveSchemeDraft() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.save_scheme_button))
        }

        AnimatedVisibility(
            visible = saveStatus != SchemeSaveStatus.NONE,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            AssistChip(
                onClick = { viewModel.clearSchemeSaveStatus() },
                label = {
                    Text(
                        text = when (saveStatus) {
                            SchemeSaveStatus.SUCCESS -> stringResource(id = R.string.scheme_saved_message)
                            SchemeSaveStatus.VALIDATION_ERROR -> stringResource(id = R.string.scheme_validation_message)
                            SchemeSaveStatus.NONE -> ""
                        }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
        Text(
            text = stringResource(id = R.string.saved_schemes_title),
            style = MaterialTheme.typography.titleMedium
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.scheme_list_height)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            items(schemes, key = { it.id }) { scheme ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_xsmall))
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.scheme_summary_format,
                                scheme.subject,
                                scheme.classLevel,
                                scheme.term,
                                scheme.week
                            ),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = scheme.topicTitle,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(id = R.string.scheme_teacher_format, scheme.teacherName),
                            style = MaterialTheme.typography.bodySmall
                        )
                        OutlinedButton(
                            onClick = { shareScheme(context, scheme) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(id = R.string.share_scheme))
                        }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.back))
        }
    }
}
