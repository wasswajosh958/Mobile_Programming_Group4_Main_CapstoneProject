package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

data class NavigationItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel = appViewModel()
    val authViewModel = authViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val menuItems = listOf(
        NavigationItem("Home", "home", Icons.Default.Home),
        NavigationItem("Subjects", "subjects", Icons.AutoMirrored.Filled.ListAlt),
        NavigationItem("My Library", "library", Icons.AutoMirrored.Filled.LibraryBooks),
        NavigationItem("Schemes", "scheme", Icons.Default.SettingsSuggest),
        NavigationItem("Updates", "updates", Icons.Default.Update),
        NavigationItem("About", "about", Icons.Default.Info)
    )

    LaunchedEffect(currentUser) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentUser != null && currentRoute == "login") {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // Determine if we should show the drawer and top bar
    val showNavigation = currentDestination?.route !in listOf("splash", "login", null)
    val showTopBar = false // All screens now have custom headers

    if (showNavigation) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "CBC Teachers' Toolkit",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (currentUser != null) {
                            Text(
                                text = "Teacher: ${currentUser?.fullName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider()
                    menuItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.label) },
                            icon = { Icon(item.icon, contentDescription = null) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text("Logout") },
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    if (showTopBar) {
                        TopAppBar(
                            title = {
                                val title = when (currentDestination?.route) {
                                    "home" -> "Home"
                                    "subjects" -> "Subjects"
                                    "library" -> "My Library"
                                    "about" -> "About"
                                    "updates" -> "Updates"
                                    "scheme" -> "Scheme Builder"
                                    "resource/{topicId}" -> "Resource Detail"
                                    "video/{encodedUrl}" -> "Video Player"
                                    else -> "CBC Toolkit"
                                }
                                Text(title)
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            ) { innerPadding ->
                val contentPadding = if (showTopBar) innerPadding else androidx.compose.foundation.layout.PaddingValues(0.dp)
                Box(modifier = Modifier.padding(contentPadding)) {
                    NavContent(navController, viewModel, authViewModel, currentUser) {
                        scope.launch { drawerState.open() }
                    }
                }
            }
        }
    } else {
        NavContent(navController, viewModel, authViewModel, currentUser) {}
    }
}

@Composable
fun NavContent(
    navController: androidx.navigation.NavHostController,
    viewModel: SubjectViewModel,
    authViewModel: AuthViewModel,
    currentUser: ug.ac.ndejje.cbc_teachers_toolkit.data.local.UserEntity?,
    onMenuClick: () -> Unit
) {
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
                onMenuClick = onMenuClick,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("subjects") { 
            SubjectsScreen(navController, viewModel, onMenuClick = onMenuClick) 
        }
        composable("library") { 
            LibraryScreen(
                navController = navController, 
                viewModel = viewModel,
                onMenuClick = onMenuClick
            ) 
        }
        composable("about") { AboutScreen(navController, onMenuClick = onMenuClick) }
        composable("updates") { UpdatesScreen(navController, viewModel, onMenuClick = onMenuClick) }
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
