package ug.ac.ndejje.cbc_teachers_toolkit.ui.screens

import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.CbcTeachersToolkitTheme

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.AuthViewModel
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.AuthUiState
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.AuthMode
import androidx.compose.ui.platform.LocalContext
import ug.ac.ndejje.cbc_teachers_toolkit.R

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    val state by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val availableSubjects = remember {
        context.resources.getStringArray(R.array.available_subjects).toList()
    }

    LoginScreenContent(
        state = state,
        availableSubjects = availableSubjects,
        onFullNameChanged = authViewModel::updateFullName,
        onUsernameChanged = authViewModel::updateUsername,
        onPasswordChanged = authViewModel::updatePassword,
        onToggleSubject = authViewModel::toggleSubject,
        onSubmit = { authViewModel.authenticate(context) },
        onSwitchMode = {
            authViewModel.switchMode(
                if (state.mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN
            )
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LoginScreenContent(
    state: AuthUiState,
    availableSubjects: List<String>,
    onFullNameChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onToggleSubject: (String) -> Unit,
    onSubmit: () -> Unit,
    onSwitchMode: () -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // This adds a light blue shade at the top of the login screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(dimensionResource(id = R.dimen.padding_large)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // This is the round logo icon with "CBC" text
            Surface(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large) + dimensionResource(id = R.dimen.padding_medium)),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(id = R.string.login_logo_short),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(id = R.string.splash_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_xlarge)))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.padding_large)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.card_elevation))
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                ) {
                    Text(
                        text = if (state.mode == AuthMode.LOGIN) stringResource(id = R.string.sign_in) else stringResource(id = R.string.register_button),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // If the teacher is registering, we show the name field and subject chips
                    if (state.mode == AuthMode.REGISTER) {
                        OutlinedTextField(
                            value = state.fullName,
                            onValueChange = onFullNameChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(id = R.string.full_name_label)) },
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.padding_12dp))
                        )

                        Text(
                            text = stringResource(id = R.string.select_subjects_interest),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_small))
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                        ) {
                            availableSubjects.forEach { subject ->
                                FilterChip(
                                    selected = state.selectedSubjects.contains(subject),
                                    onClick = { onToggleSubject(subject) },
                                    label = { Text(subject) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.username,
                        onValueChange = onUsernameChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(id = R.string.username_label)) },
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.padding_12dp))
                    )

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = onPasswordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(id = R.string.password_label)) },
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.padding_12dp)),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    // This text shows error messages if login fails
                    if (state.message.isNotBlank()) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.login_logo_size)),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.padding_12dp)),
                        enabled = !state.isBusy
                    ) {
                        Text(
                            text = if (state.mode == AuthMode.LOGIN) stringResource(id = R.string.login_title_button) else stringResource(id = R.string.register_title_button),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.mode == AuthMode.LOGIN) stringResource(id = R.string.new_here) else stringResource(id = R.string.already_have_account),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        androidx.compose.material3.TextButton(onClick = onSwitchMode) {
                            Text(
                                text = if (state.mode == AuthMode.LOGIN) stringResource(id = R.string.register_title_button) else stringResource(id = R.string.login_title_button),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    CbcTeachersToolkitTheme {
        LoginScreenContent(
            state = AuthUiState(
                mode = AuthMode.LOGIN,
                username = "ian_teacher",
                message = ""
            ),
            availableSubjects = listOf("English", "Math"),
            onFullNameChanged = {},
            onUsernameChanged = {},
            onPasswordChanged = {},
            onToggleSubject = {},
            onSubmit = {},
            onSwitchMode = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    CbcTeachersToolkitTheme {
        LoginScreenContent(
            state = AuthUiState(
                mode = AuthMode.REGISTER,
                fullName = "Ian Teacher",
                username = "ian_teacher",
                message = "Passwords do not match"
            ),
            availableSubjects = listOf("English", "Math"),
            onFullNameChanged = {},
            onUsernameChanged = {},
            onPasswordChanged = {},
            onToggleSubject = {},
            onSubmit = {},
            onSwitchMode = {}
        )
    }
}
