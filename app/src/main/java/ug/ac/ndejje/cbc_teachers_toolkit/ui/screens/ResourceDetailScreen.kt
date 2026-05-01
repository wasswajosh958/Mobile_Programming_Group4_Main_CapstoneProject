package ug.ac.ndejje.cbc_teachers_toolkit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SubjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceDetailScreen(
    topicId: Int,
    onNavigateBack: () -> Unit,
    viewModel: SubjectViewModel = viewModel(factory = SubjectViewModel.Factory)
) {
    val topics by viewModel.topics.collectAsState()
    val topic = topics.find { it.id == topicId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topic?.title ?: "Topic Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (topic == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text("Topic not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Subject: ${topic.subject}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Class: ${topic.classLevel}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                DetailSection(title = "Lesson Plan", content = topic.lessonPlan)
                DetailSection(title = "Project Ideas", content = topic.projectIdeas)
                DetailSection(title = "Assessment Rubric", content = topic.assessmentRubric)
                DetailSection(title = "Teaching Tips", content = topic.teachingTips)
            }
        }
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
