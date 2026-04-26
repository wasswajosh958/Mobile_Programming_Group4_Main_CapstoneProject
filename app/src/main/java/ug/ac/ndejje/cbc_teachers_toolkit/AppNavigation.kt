package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("subjects") { SubjectsScreen(navController) }
        composable("resource") { ResourceDetailScreen(navController) }
    }
}