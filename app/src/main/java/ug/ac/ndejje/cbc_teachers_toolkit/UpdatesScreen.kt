package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import ug.ac.ndejje.cbc_teachers_toolkit.ui.theme.CbcTeachersToolkitTheme

@Composable
fun UpdatesScreen(
    navController: NavController,
    viewModel: SubjectViewModel
) {
    val updates by viewModel.updatesState.collectAsState()
    UpdatesScreenContent(
        updates = updates,
        onUpdateNow = { viewModel.updateResourcesNow() },
        onBack = { navController.popBackStack() }
    )
}

@Composable
private fun UpdatesScreenContent(
    updates: UpdatesUiState,
    onUpdateNow: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        Text(text = stringResource(id = R.string.updates_title), style = MaterialTheme.typography.headlineSmall)
        Text(text = stringResource(id = R.string.updates_subtitle), style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

        val statusText = when (updates.status) {
            UpdateStatus.IDLE -> stringResource(id = R.string.update_status_idle)
            UpdateStatus.UPDATING -> stringResource(id = R.string.update_status_updating)
            UpdateStatus.UPDATED -> stringResource(id = R.string.update_status_updated)
            UpdateStatus.FAILED -> stringResource(id = R.string.update_status_failed)
        }
        Text(
            text = stringResource(id = R.string.updates_status_format, statusText),
            style = MaterialTheme.typography.bodyMedium
        )
        if (updates.downloadedCount > 0) {
            Text(
                text = stringResource(id = R.string.updated_resources_count, updates.downloadedCount),
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (updates.errorMessage.isNotBlank()) {
            Text(
                text = updates.errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        OutlinedButton(
            onClick = onUpdateNow,
            modifier = Modifier.fillMaxWidth(),
            enabled = !updates.isUpdating
        ) {
            Text(text = stringResource(id = R.string.update_resources_button))
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.back))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UpdatesScreenPreview() {
    CbcTeachersToolkitTheme {
        UpdatesScreenContent(
            updates = UpdatesUiState(
                isUpdating = false,
                status = UpdateStatus.UPDATED,
                downloadedCount = 96
            ),
            onUpdateNow = {},
            onBack = {}
        )
    }
}
