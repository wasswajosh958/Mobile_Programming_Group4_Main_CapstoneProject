package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel = appViewModel()
    val authViewModel = authViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentUser != null && currentRoute == "login") {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onDone = {
                    val destination = if (currentUser == null) "login" else "home"
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("login") { LoginScreen(authViewModel) }
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel,
                teacherName = currentUser?.fullName ?: "Teacher",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("subjects") { SubjectsScreen(navController, viewModel) }
        composable("library") { LibraryScreen(navController, viewModel) }
        composable("about") { AboutScreen(navController) }
        composable("updates") { UpdatesScreen(navController, viewModel) }
        composable(
            route = "video/{encodedUrl}",
            arguments = listOf(navArgument("encodedUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("encodedUrl").orEmpty()
            VideoPlayerScreen(navController = navController, encodedUrl = encodedUrl)
        }
        composable(
            route = "scheme?topicId={topicId}",
            arguments = listOf(
                navArgument("topicId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt("topicId") ?: -1
            SchemeBuilderScreen(navController, viewModel, topicId)
        }
        composable(
            route = "resource/{topicId}",
            arguments = listOf(navArgument("topicId") { type = NavType.IntType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt("topicId") ?: 0
            ResourceDetailScreen(navController, viewModel, topicId)
        }
    }
}