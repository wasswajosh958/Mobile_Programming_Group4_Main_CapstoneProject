package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController

@Composable
fun SubjectsScreen(
    navController: NavController,
    viewModel: SubjectViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(
            text = stringResource(id = R.string.available_topics),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.search_hint)) }
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

        Text(
            text = stringResource(id = R.string.filter_subject),
            style = MaterialTheme.typography.bodyMedium
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            item {
                AssistChip(
                    onClick = { viewModel.selectSubject(null) },
                    label = { Text(stringResource(id = R.string.all_subjects)) }
                )
            }
            items(uiState.availableSubjects) { subject ->
                AssistChip(
                    onClick = { viewModel.selectSubject(subject) },
                    label = { Text(subject) }
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

        Text(
            text = stringResource(id = R.string.filter_class),
            style = MaterialTheme.typography.bodyMedium
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            item {
                AssistChip(
                    onClick = { viewModel.selectClassLevel(null) },
                    label = { Text(stringResource(id = R.string.all_classes)) }
                )
            }
            items(uiState.availableClassLevels) { classLevel ->
                AssistChip(
                    onClick = { viewModel.selectClassLevel(classLevel) },
                    label = { Text(classLevel) }
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        AnimatedContent(
            targetState = uiState.isLoading,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "topics_loading_animation"
        ) { loading ->
            if (loading) {
                Text(text = stringResource(id = R.string.loading_topics))
            } else if (uiState.filteredTopics.isEmpty()) {
                Text(text = stringResource(id = R.string.no_topics_found))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                ) {
                    items(
                        items = uiState.filteredTopics,
                        key = { it.id }
                    ) { topic ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                navController.navigate("resource/${topic.id}")
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                            ) {
                                Text(
                                    text = topic.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = stringResource(
                                        id = R.string.subject_and_class,
                                        topic.subject,
                                        topic.classLevel
                                    ),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}