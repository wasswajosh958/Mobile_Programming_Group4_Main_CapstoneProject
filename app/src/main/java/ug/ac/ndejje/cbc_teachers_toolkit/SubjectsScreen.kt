package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun SubjectsScreen(
    navController: NavController,
    viewModel: SubjectViewModel = viewModel()
) {
    val topics by viewModel.topics.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(
            text = "Available Topics",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = topics,
                key = { it.id }
            ) { topic ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("resource")
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
                            text = "Subject: ${topic.subject}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}