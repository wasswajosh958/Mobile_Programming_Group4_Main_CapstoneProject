package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
                    label = { Text(stringResource(id = R.string.password_label)) }
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

                OutlinedButton(
                    onClick = onSwitchMode,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (state.mode == AuthMode.LOGIN) {
                            stringResource(id = R.string.switch_to_register)
                        } else {
                            stringResource(id = R.string.switch_to_login)
                        }
                    )
                }
                Text(
                    text = stringResource(id = R.string.default_login_hint),
                    style = MaterialTheme.typography.bodySmall
                )
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
