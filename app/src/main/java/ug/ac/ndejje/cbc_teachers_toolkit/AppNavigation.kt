package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ug.ac.ndejje.cbc_teachers_toolkit.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                onNavigateToAbout = { navController.navigate("about") }
            )
        }

        composable("about") {
            AboutScreen(
                navController = navController,
                onMenuClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "pdf_viewer/{url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            PdfViewerScreen(
                navController = navController,
                encodedUrl = url
            )
        }

        composable("subjects") {
            SubjectsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("library") {
            LibraryScreen()
        }

        composable("updates") {
            UpdatesScreen()
        }

        composable("scheme_builder") {
            SchemeBuilderScreen()
        }

        composable(
            route = "resource_detail/{topicId}",
            arguments = listOf(navArgument("topicId") { type = NavType.IntType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt("topicId") ?: 0
            ResourceDetailScreen(
                topicId = topicId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
