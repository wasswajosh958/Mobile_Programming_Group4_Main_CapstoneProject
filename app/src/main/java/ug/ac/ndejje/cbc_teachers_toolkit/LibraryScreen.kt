package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: SubjectViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteTopics = uiState.allTopics.filter { uiState.favorites.contains(it.id) }
    val notedTopics = uiState.allTopics.filter { uiState.notes[it.id].isNullOrBlank().not() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(
            text = stringResource(id = R.string.my_library),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
        Text(
            text = stringResource(id = R.string.library_summary, favoriteTopics.size, notedTopics.size),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        if (favoriteTopics.isEmpty() && notedTopics.isEmpty()) {
            Text(text = stringResource(id = R.string.library_empty))
            return
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            if (favoriteTopics.isNotEmpty()) {
                item { Text(text = stringResource(id = R.string.favorite_topics_title), style = MaterialTheme.typography.titleMedium) }
                items(favoriteTopics, key = { "fav_${it.id}" }) { topic ->
                    AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.card_elevation)),
                            onClick = { navController.navigate("resource/${topic.id}") }
                        ) {
                            Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
                                Text(text = topic.title, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    text = stringResource(id = R.string.subject_and_class, topic.subject, topic.classLevel),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            if (notedTopics.isNotEmpty()) {
                item { Text(text = stringResource(id = R.string.noted_topics_title), style = MaterialTheme.typography.titleMedium) }
                items(notedTopics, key = { "note_${it.id}" }) { topic ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate("resource/${topic.id}") }
                    ) {
                        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
                            Text(text = topic.title, style = MaterialTheme.typography.titleSmall)
                            Text(text = uiState.notes[topic.id].orEmpty(), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
