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
import androidx.navigation.NavController

@Composable
fun UpdatesScreen(
    navController: NavController,
    viewModel: SubjectViewModel
) {
    val updates by viewModel.updatesState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        Text(text = stringResource(id = R.string.updates_title), style = MaterialTheme.typography.headlineSmall)
        Text(text = stringResource(id = R.string.updates_subtitle), style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

        Text(
            text = stringResource(id = R.string.updates_status_format, updates.status),
            style = MaterialTheme.typography.bodyMedium
        )
        if (updates.message.isNotBlank()) {
            Text(text = updates.message, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        OutlinedButton(
            onClick = { viewModel.updateResourcesNow() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !updates.isUpdating
        ) {
            Text(text = stringResource(id = R.string.update_resources_button))
        }

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.back))
        }
    }
}
