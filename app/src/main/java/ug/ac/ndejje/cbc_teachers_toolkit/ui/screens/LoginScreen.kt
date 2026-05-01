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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.AuthViewModel
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.AuthUiState
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.AuthMode
import ug.ac.ndejje.cbc_teachers_toolkit.R

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    val state by authViewModel.uiState.collectAsState()
    LoginScreenContent(
        state = state,
        onFullNameChanged = authViewModel::updateFullName,
        onUsernameChanged = authViewModel::updateUsername,
        onPasswordChanged = authViewModel::updatePassword,
        onToggleSubject = authViewModel::toggleSubject,
        onSubmit = { authViewModel.authenticate() },
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
    onFullNameChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onToggleSubject: (String) -> Unit,
    onSubmit: () -> Unit,
    onSwitchMode: () -> Unit
) {
    val availableSubjects = listOf(
        "English Language", "Mathematics", "Biology", "Chemistry", "Physics",
        "Geography", "History & Political Education", "Religious Education",
        "Kiswahili", "Agriculture", "Entrepreneurship Education", "Performing Arts",
        "Art and Design", "ICT", "Nutrition & Food Technology", "Technology and Design",
        "Physical Education", "Local Language", "Foreign Languages"
    )
    var showPassword by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Background Decorative Element
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo / Header Section
            Surface(
                modifier = Modifier.size(80.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (state.mode == AuthMode.LOGIN) "Sign In" else "Create Account",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (state.mode == AuthMode.REGISTER) {
                        OutlinedTextField(
                            value = state.fullName,
                            onValueChange = onFullNameChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Full Name") },
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text(
                            text = "Select your Subjects of Interest:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        label = { Text("Username") },
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = onPasswordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        shape = RoundedCornerShape(12.dp),
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

                    if (state.message.isNotBlank()) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isBusy
                    ) {
                        Text(
                            text = if (state.mode == AuthMode.LOGIN) "Login" else "Register",
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
                            text = if (state.mode == AuthMode.LOGIN) "New here?" else "Already have an account?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        androidx.compose.material3.TextButton(onClick = onSwitchMode) {
                            Text(
                                text = if (state.mode == AuthMode.LOGIN) "Register" else "Login",
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
            onFullNameChanged = {},
            onUsernameChanged = {},
            onPasswordChanged = {},
            onToggleSubject = {},
            onSubmit = {},
            onSwitchMode = {}
        )
    }
}
