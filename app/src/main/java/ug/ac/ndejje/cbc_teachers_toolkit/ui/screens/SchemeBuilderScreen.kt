package ug.ac.ndejje.cbc_teachers_toolkit.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import ug.ac.ndejje.cbc_teachers_toolkit.R
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.SchemeOfWorkEntity
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.CbcTeachersToolkitTheme
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.SuccessGreen
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.SuccessGreenContainer
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.WarningOrange
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.WarningOrangeContainer
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SchemeDraftUiState
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SchemeSaveStatus
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SubjectViewModel
import ug.ac.ndejje.cbc_teachers_toolkit.util.TextToSpeechHelper
import ug.ac.ndejje.cbc_teachers_toolkit.util.openScheme
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

/**
 * The main UI for creating and managing Schemes of Work.
 * This screen allows teachers to fill in a form, save it locally, 
 * and view or share their saved schemes.
 */
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
            title = { Text(stringResource(id = R.string.delete_scheme_title)) },
            text = { Text(stringResource(id = R.string.delete_scheme_confirmation, schemeToDelete?.topicTitle ?: "")) },
            confirmButton = {
                TextButton(
                    onClick = {
                        schemeToDelete?.let { onDeleteScheme(it) }
                        schemeToDelete = null
                    }
                ) {
                    Text(stringResource(id = R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { schemeToDelete = null }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with a nice blue gradient
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
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_xsmall)))
                    Column {
                        Text(
                            text = stringResource(id = R.string.scheme_builder_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = stringResource(id = R.string.scheme_builder_subtitle),
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
                .padding(horizontal = dimensionResource(id = R.dimen.padding_20dp))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))

            // This button shows or hides the helpful tips for making a scheme
            OutlinedButton(
                onClick = { showGuide = !showGuide },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius))
            ) {
                Icon(
                    imageVector = if (showGuide) Icons.Default.Info else Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
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
                        .padding(top = dimensionResource(id = R.dimen.padding_12dp)),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
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
                                    modifier = Modifier.width(dimensionResource(id = R.dimen.padding_large))
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

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))

            // The main form where teachers enter their lesson details
            SchemeSectionHeader(
                title = if (isEditing) stringResource(id = R.string.edit_scheme_title) else stringResource(id = R.string.general_info_title),
                icon = if (isEditing) Icons.Default.Edit else Icons.Default.Info,
                onSpeak = { onSpeak("${if (isEditing) "Edit Scheme" else "General Information"}. Teacher: ${draft.teacherName}. School: ${draft.schoolName}. Subject: ${draft.subject}. Class: ${draft.classLevel}.") }
            )

            if (isEditing) {
                OutlinedButton(
                    onClick = onCancelEdit,
                    modifier = Modifier.fillMaxWidth().padding(bottom = dimensionResource(id = R.dimen.padding_small)),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius))
                ) {
                    Text(stringResource(id = R.string.cancel_editing_button))
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
                label = stringResource(id = R.string.school_name_label)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_12dp))) {
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_12dp))) {
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
                label = stringResource(id = R.string.date_label)
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
            SchemeSectionHeader(
                title = stringResource(id = R.string.topic_details_title),
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
                label = stringResource(id = R.string.competency_label),
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

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_12dp)))

            // Save button to store the scheme in the app's database
            Button(
                onClick = onSaveScheme,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.login_logo_size)),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius))
            ) {
                Text(
                    text = if (isEditing) stringResource(id = R.string.update_scheme_button) else stringResource(id = R.string.save_scheme_button),
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
                        .padding(top = dimensionResource(id = R.dimen.padding_12dp)),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.chip_corner_radius)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (saveStatus == SchemeSaveStatus.SUCCESS)
                            SuccessGreenContainer else WarningOrangeContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_12dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (saveStatus == SchemeSaveStatus.SUCCESS)
                                Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (saveStatus == SchemeSaveStatus.SUCCESS)
                                SuccessGreen else WarningOrange
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
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
                            Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.menu), modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_xlarge)))

            // This section shows a list of schemes the teacher has already saved
            if (schemes.isNotEmpty()) {
                SchemeSectionHeader(title = stringResource(id = R.string.saved_schemes_title), icon = Icons.Default.History)

                schemes.forEach { scheme ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimensionResource(id = R.dimen.padding_xsmall).plus(dimensionResource(id = R.dimen.padding_12dp).div(12))), // approximate 6dp
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.card_elevation_small)),
                        border = BorderStroke(
                            width = dimensionResource(id = R.dimen.border_thin),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
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
                                        contentDescription = stringResource(id = R.string.edit),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { openScheme(context, scheme) }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = stringResource(id = R.string.view_pdf),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { shareScheme(context, scheme) }) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = stringResource(id = R.string.share_desc),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { schemeToDelete = scheme }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(id = R.string.delete),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.padding_small))
                            ) {
                                Text(
                                    text = stringResource(id = R.string.scheme_teacher_format, scheme.teacherName),
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small), vertical = dimensionResource(id = R.dimen.padding_xsmall)),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))
            
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius))
            ) {
                Text(text = stringResource(id = R.string.back))
            }
            
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_xxlarge).minus(dimensionResource(id = R.dimen.padding_small))))
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
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
        modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_12dp))
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_small)))
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
                contentDescription = stringResource(id = R.string.read_aloud_desc),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_small))
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
            .padding(vertical = dimensionResource(id = R.dimen.padding_xsmall).plus(dimensionResource(id = R.dimen.padding_12dp).div(12))), // approximate 6dp
        label = { Text(text = label) },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
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
