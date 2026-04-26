package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel = appViewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onDone = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("home") { HomeScreen(navController, viewModel) }
        composable("subjects") { SubjectsScreen(navController, viewModel) }
        composable("library") { LibraryScreen(navController, viewModel) }
        composable("about") { AboutScreen(navController) }
        composable("updates") { UpdatesScreen(navController, viewModel) }
        composable(
            route = "resource/{topicId}",
            arguments = listOf(navArgument("topicId") { type = NavType.IntType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt("topicId") ?: 0
            ResourceDetailScreen(navController, viewModel, topicId)
        }
    }
}