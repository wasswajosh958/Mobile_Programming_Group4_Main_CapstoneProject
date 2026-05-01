package ug.ac.ndejje.cbc_teachers_toolkit.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ug.ac.ndejje.cbc_teachers_toolkit.R
import ug.ac.ndejje.cbc_teachers_toolkit.domain.Topic
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SubjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onNavigateToAbout: () -> Unit,
    viewModel: SubjectViewModel = viewModel(factory = SubjectViewModel.Factory)
) {
    val topics by viewModel.topics.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(Icons.Default.Info, contentDescription = "About")
                    }
                }
            )
        }
    ) { padding ->
        if (topics.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(topics) { topic ->
                    TopicItem(
                        topic = topic,
                        isFavorite = favoriteIds.contains(topic.id),
                        onFavoriteToggle = { viewModel.toggleFavorite(topic.id) },
                        onClick = { navController.navigate("resource_detail/${topic.id}") }
                    )
                }
            }
        }
    }
}

@Composable
fun TopicItem(
    topic: Topic,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = topic.title, style = MaterialTheme.typography.titleMedium)
                Text(text = "${topic.subject} - ${topic.classLevel}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
