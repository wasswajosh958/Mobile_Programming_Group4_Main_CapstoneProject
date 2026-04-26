package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Best practice: Small data class for better type safety and keys
data class SubjectItem(
    val name: String,
    val route: String = "subjects"   // We will improve navigation later
)

@Composable
fun HomeScreen(navController: NavController) {
    val subjects = listOf(
        SubjectItem("Biology"),
        SubjectItem("Mathematics"),
        SubjectItem("English"),
        SubjectItem("Chemistry"),
        SubjectItem("Physics")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(
            text = stringResource(id = R.string.hello_android),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome, Teacher!",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // BEST PRACTICE LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = subjects,
                key = { it.name }               // Very important for performance
            ) { subject ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    onClick = {
                        navController.navigate(subject.route)
                    }
                ) {
                    Text(
                        text = subject.name,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}