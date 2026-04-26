package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController

@Composable
fun AboutScreen(navController: NavController) {
    val checklist = listOf(
        stringResource(id = R.string.about_item_kotlin_compose),
        stringResource(id = R.string.about_item_mvvm),
        stringResource(id = R.string.about_item_navigation),
        stringResource(id = R.string.about_item_room),
        stringResource(id = R.string.about_item_lazy_lists),
        stringResource(id = R.string.about_item_testing),
        stringResource(id = R.string.about_item_animations)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(
            text = stringResource(id = R.string.about_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
        Text(
            text = stringResource(id = R.string.about_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            itemsIndexed(checklist) { index, item ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / (index + 2) })
                ) {
                    Text(
                        text = stringResource(id = R.string.about_bullet, item),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.back_to_home))
        }
    }
}
