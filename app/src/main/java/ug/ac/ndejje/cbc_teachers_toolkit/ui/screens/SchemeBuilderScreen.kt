package ug.ac.ndejje.cbc_teachers_toolkit.ui.screens

import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SubjectViewModel
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SchemeDraftUiState
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SchemeSaveStatus
import ug.ac.ndejje.cbc_teachers_toolkit.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Share
import ug.ac.ndejje.cbc_teachers_toolkit.util.TextToSpeechHelper
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.SchemeOfWorkEntity
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.CbcTeachersToolkitTheme
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.SuccessGreen
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.SuccessGreenContainer
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.WarningOrange
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.WarningOrangeContainer
import ug.ac.ndejje.cbc_teachers_toolkit.util.openScheme
import ug.ac.ndejje.cbc_teachers_toolkit.util.shareScheme
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

@Composable
fun SchemeBuilderScreen(
    navController: NavController,
    viewModel: SubjectViewModel,
    topicId: Int
) {
    val draft by viewModel.schemeDraftState.collectAsState()
    val saveStatus by viewModel.schemeSaveStatus.collectAsState()
    val schemes by viewModel.schemes.collectAsState()
    val editingId by viewModel.editingSchemeId.collectAsState()

    LaunchedEffect(topicId) {
        if (topicId > 0) {
            viewModel.prefillSchemeFromTopic(topicId)
        }
    }

    val context = LocalContext.current
    val ttsHelper = remember { TextToSpeechHelper(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    SchemeBuilderContent(
        draft = draft,
        saveStatus = saveStatus,
        schemes = schemes,
        isEditing = editingId != null,
        onBackClick = { navController.popBackStack() },
        onUpdateDraft = { viewModel.updateSchemeDraft(it) },
        onSaveScheme = { viewModel.saveSchemeDraft() },
        onClearStatus = { viewModel.clearSchemeSaveStatus() },
        onEditScheme = { viewModel.setEditScheme(it) },
        onDeleteScheme = { viewModel.deleteScheme(it) },
        onCancelEdit = { viewModel.setEditScheme(null) },
        onSpeak = { text -> ttsHelper.speak(text) }
    )
}

@Composable
fun SchemeBuilderContent(
    draft: SchemeDraftUiState,
    saveStatus: SchemeSaveStatus,
    schemes: List<SchemeOfWorkEntity>,
    isEditing: Boolean = false,
    onBackClick: () -> Unit,
    onUpdateDraft: ((SchemeDraftUiState) -> SchemeDraftUiState) -> Unit,
    onSaveScheme: () -> Unit,
    onClearStatus: () -> Unit,
    onEditScheme: (SchemeOfWorkEntity) -> Unit = {},
    onDeleteScheme: (SchemeOfWorkEntity) -> Unit = {},
    onCancelEdit: () -> Unit = {},
    onSpeak: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var showGuide by remember { mutableStateOf(false) }
    var schemeToDelete by remember { mutableStateOf<SchemeOfWorkEntity?>(null) }

    if (schemeToDelete != null) {
        AlertDialog(
            onDismissRequest = { schemeToDelete = null },
            title = { Text("Delete Scheme") },
            text = { Text("Are you sure you want to delete this scheme for '${schemeToDelete?.topicTitle}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        schemeToDelete?.let { onDeleteScheme(it) }
                        schemeToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { schemeToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

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
                            text = "Scheme Builder",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Generate and manage schemes of work",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- Guide Toggle ---
            OutlinedButton(
                onClick = { showGuide = !showGuide },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = if (showGuide) Icons.Default.Info else Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
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
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.scheme_guide_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        val steps = listOf(
                            R.string.scheme_guide_step_1,
                            R.string.scheme_guide_step_2,
                            R.string.scheme_guide_step_3,
                            R.string.scheme_guide_step_4,
                            R.string.scheme_guide_step_5
                        )
                        steps.forEachIndexed { index, stepRes ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "${index + 1}.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(24.dp)
                                )
                                Text(
                                    text = stringResource(id = stepRes),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Form Section ---
            SchemeSectionHeader(
                title = if (isEditing) "Edit Scheme" else "General Information",
                icon = if (isEditing) Icons.Default.Edit else Icons.Default.Info,
                onSpeak = { onSpeak("General Information. Teacher: ${draft.teacherName}. School: ${draft.schoolName}. Subject: ${draft.subject}. Class: ${draft.classLevel}.") }
            )

            if (isEditing) {
                OutlinedButton(
                    onClick = onCancelEdit,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Cancel Editing")
                }
            }

            SchemeInputField(
                value = draft.teacherName,
                onValueChange = { value -> onUpdateDraft { it.copy(teacherName = value) } },
                label = stringResource(id = R.string.teacher_name_label)
            )

            SchemeInputField(
                value = draft.schoolName,
                onValueChange = { value -> onUpdateDraft { it.copy(schoolName = value) } },
                label = "School Name"
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    SchemeInputField(
                        value = draft.subject,
                        onValueChange = { value -> onUpdateDraft { it.copy(subject = value) } },
                        label = stringResource(id = R.string.subject_label)
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    SchemeInputField(
                        value = draft.classLevel,
                        onValueChange = { value -> onUpdateDraft { it.copy(classLevel = value) } },
                        label = stringResource(id = R.string.class_level_label)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    SchemeInputField(
                        value = draft.term,
                        onValueChange = { value -> onUpdateDraft { it.copy(term = value) } },
                        label = stringResource(id = R.string.term_label)
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    SchemeInputField(
                        value = draft.week,
                        onValueChange = { value -> onUpdateDraft { it.copy(week = value) } },
                        label = stringResource(id = R.string.week_label)
                    )
                }
            }

            SchemeInputField(
                value = draft.date,
                onValueChange = { value -> onUpdateDraft { it.copy(date = value) } },
                label = "Date"
            )

            Spacer(modifier = Modifier.height(16.dp))
            SchemeSectionHeader(
                title = "Topic Details",
                icon = Icons.Default.Add,
                onSpeak = { onSpeak("Topic Details. Topic: ${draft.topicTitle}. Competency: ${draft.competency}. Objectives: ${draft.objectives}. Activities: ${draft.activities}. Resources: ${draft.resources}. Assessment: ${draft.assessment}.") }
            )

            SchemeInputField(
                value = draft.topicTitle,
                onValueChange = { value -> onUpdateDraft { it.copy(topicTitle = value) } },
                label = stringResource(id = R.string.topic_title_label)
            )
            SchemeInputField(
                value = draft.competency,
                onValueChange = { value -> onUpdateDraft { it.copy(competency = value) } },
                label = "Competency / Theme",
                singleLine = false
            )
            SchemeInputField(
                value = draft.objectives,
                onValueChange = { value -> onUpdateDraft { it.copy(objectives = value) } },
                label = stringResource(id = R.string.objectives_label),
                singleLine = false
            )
            SchemeInputField(
                value = draft.activities,
                onValueChange = { value -> onUpdateDraft { it.copy(activities = value) } },
                label = stringResource(id = R.string.activities_label),
                singleLine = false
            )
            SchemeInputField(
                value = draft.resources,
                onValueChange = { value -> onUpdateDraft { it.copy(resources = value) } },
                label = stringResource(id = R.string.resources_label),
                singleLine = false
            )
            SchemeInputField(
                value = draft.assessment,
                onValueChange = { value -> onUpdateDraft { it.copy(assessment = value) } },
                label = stringResource(id = R.string.assessment_label),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Save Button ---
            Button(
                onClick = onSaveScheme,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isEditing) "Update Scheme" else stringResource(id = R.string.save_scheme_button),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(
                visible = saveStatus != SchemeSaveStatus.NONE,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (saveStatus == SchemeSaveStatus.SUCCESS)
                            SuccessGreenContainer else WarningOrangeContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (saveStatus == SchemeSaveStatus.SUCCESS)
                                Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (saveStatus == SchemeSaveStatus.SUCCESS)
                                SuccessGreen else WarningOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (saveStatus) {
                                SchemeSaveStatus.SUCCESS -> stringResource(id = R.string.scheme_saved_message)
                                SchemeSaveStatus.VALIDATION_ERROR -> stringResource(id = R.string.scheme_validation_message)
                                SchemeSaveStatus.NONE -> ""
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (saveStatus == SchemeSaveStatus.SUCCESS)
                                SuccessGreen else WarningOrange
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onClearStatus) {
                            Icon(Icons.Default.Add, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- History Section ---
            if (schemes.isNotEmpty()) {
                SchemeSectionHeader(title = stringResource(id = R.string.saved_schemes_title), icon = Icons.Default.History)

                schemes.forEach { scheme ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = scheme.topicTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(
                                            id = R.string.scheme_summary_format,
                                            scheme.subject,
                                            scheme.classLevel,
                                            scheme.term,
                                            scheme.week
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { onEditScheme(scheme) }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { openScheme(context, scheme) }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = "View PDF",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { shareScheme(context, scheme) }) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { schemeToDelete = scheme }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.scheme_teacher_format, scheme.teacherName),
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = stringResource(id = R.string.back))
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SchemeSectionHeader(
    title: String,
    icon: ImageVector,
    onSpeak: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onSpeak) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Read Aloud",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SchemeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        label = { Text(text = label) },
        shape = RoundedCornerShape(16.dp),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SchemeBuilderPreview() {
    CbcTeachersToolkitTheme {
        SchemeBuilderContent(
            draft = SchemeDraftUiState(
                teacherName = "John Doe",
                subject = "Mathematics",
                classLevel = "P.3",
                term = "Term 1",
                week = "2",
                topicTitle = "Addition of 4-digit numbers",
                objectives = "Pupils should be able to...",
                activities = "Group work, peer teaching",
                resources = "Abacus, Chalkboard",
                assessment = "Mental math test"
            ),
            saveStatus = SchemeSaveStatus.NONE,
            schemes = listOf(
                SchemeOfWorkEntity(
                    id = 1,
                    teacherName = "John Doe",
                    subject = "Mathematics",
                    classLevel = "P.3",
                    term = "Term 1",
                    week = 1,
                    topicTitle = "Place Value",
                    objectives = "...",
                    activities = "...",
                    resources = "...",
                    assessment = "..."
                )
            ),
            onBackClick = {},
            onUpdateDraft = {},
            onSaveScheme = {},
            onClearStatus = {}
        )
    }
}
