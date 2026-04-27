package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.CbcTeachersToolkitTheme

@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    val state by authViewModel.uiState.collectAsState()
    LoginScreenContent(
        state = state,
        onFullNameChanged = authViewModel::updateFullName,
        onUsernameChanged = authViewModel::updateUsername,
        onPasswordChanged = authViewModel::updatePassword,
        onSubmit = { authViewModel.authenticate() },
        onSwitchMode = {
            authViewModel.switchMode(
                if (state.mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN
            )
        }
    )
}

@Composable
private fun LoginScreenContent(
    state: AuthUiState,
    onFullNameChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onSwitchMode: () -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                ) {
                    Surface(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.login_logo_size)),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.login_logo_short),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Column {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = stringResource(id = R.string.splash_subtitle),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Text(
                    text = stringResource(id = R.string.login_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = stringResource(id = R.string.login_subtitle),
                    style = MaterialTheme.typography.bodyMedium
                )

                if (state.mode == AuthMode.REGISTER) {
                    OutlinedTextField(
                        value = state.fullName,
                        onValueChange = onFullNameChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(id = R.string.full_name_label)) }
                    )
                }

                OutlinedTextField(
                    value = state.username,
                    onValueChange = onUsernameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.username_label)) }
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.password_label)) },
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPassword) {
                                    stringResource(id = R.string.hide_password)
                                } else {
                                    stringResource(id = R.string.show_password)
                                }
                            )
                        }
                    }
                )

                if (state.message.isNotBlank()) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                OutlinedButton(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isBusy
                ) {
                    Text(
                        text = if (state.mode == AuthMode.LOGIN) {
                            stringResource(id = R.string.login_button)
                        } else {
                            stringResource(id = R.string.register_button)
                        }
                    )
                }

                AnimatedContent(
                    targetState = state.mode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "auth_mode_transition"
                ) { mode ->
                    OutlinedButton(
                        onClick = onSwitchMode,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (mode == AuthMode.LOGIN) {
                                stringResource(id = R.string.switch_to_register)
                            } else {
                                stringResource(id = R.string.switch_to_login)
                            }
                        )
                    }
                }
                AnimatedContent(
                    targetState = state.mode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "auth_hint_transition"
                ) { mode ->
                    Text(
                        text = if (mode == AuthMode.LOGIN) {
                            stringResource(id = R.string.default_login_hint)
                        } else {
                            stringResource(id = R.string.register_hint)
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    CbcTeachersToolkitTheme {
        LoginScreenContent(
            state = AuthUiState(
                mode = AuthMode.LOGIN,
                username = "teacher"
            ),
            onFullNameChanged = {},
            onUsernameChanged = {},
            onPasswordChanged = {},
            onSubmit = {},
            onSwitchMode = {}
        )
    }
}
