package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavController

@Composable
fun ResourceDetailScreen(navController: NavController) {
    Text(
        text = "Resource Detail Screen - Coming soon",
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    )
}