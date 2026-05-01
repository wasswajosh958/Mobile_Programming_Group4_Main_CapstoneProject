package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudUpload
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import ug.ac.ndejje.cbc_teachers_toolkit.R
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.UserEntity
import ug.ac.ndejje.cbc_teachers_toolkit.ui.screens.*
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.AuthViewModel
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.SubjectViewModel
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.appViewModel
import ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel.authViewModel

data class NavigationItem(
    val labelResId: Int,
    val route: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel = appViewModel()
    val authVm: AuthViewModel = authViewModel()
    val currentUser by authVm.currentUser.collectAsStateWithLifecycle()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // This list controls what shows up in our side menu
    val menuItems = buildList {
        add(NavigationItem(R.string.nav_home, "home", Icons.Default.Home))
        add(NavigationItem(R.string.nav_subjects, "subjects", Icons.AutoMirrored.Filled.ListAlt))
        add(NavigationItem(R.string.nav_library, "library", Icons.AutoMirrored.Filled.LibraryBooks))
        add(NavigationItem(R.string.nav_schemes, "scheme", Icons.Default.SettingsSuggest))
        add(NavigationItem(R.string.nav_about, "about", Icons.Default.Info))
        if (currentUser?.isAdmin == true) {
            add(NavigationItem(R.string.nav_admin, "admin", Icons.Default.CloudUpload))
        }
    }

    // This makes sure that if we are already logged in, we go straight to home
    LaunchedEffect(currentUser) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentUser != null && currentRoute == "login") {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // We only show the menu on the main screens, not on splash or login
    val showNavigation = currentDestination?.route !in listOf("splash", "login", null)
    val showTopBar = false 

    if (showNavigation) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val user = currentUser
                        if (user != null) {
                            Text(
                                text = stringResource(R.string.nav_teacher_label, user.fullName),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider()
                    menuItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(stringResource(item.labelResId)) },
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
                        label = { Text(stringResource(R.string.logout_button)) },
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            authVm.logout()
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
                                val titleRes = when (currentDestination?.route) {
                                    "home" -> R.string.nav_home
                                    "subjects" -> R.string.nav_subjects
                                    "library" -> R.string.nav_library
                                    "about" -> R.string.nav_about
                                    "scheme" -> R.string.open_scheme_builder
                                    "resource/{topicId}" -> R.string.resource_detail_fallback_title
                                    else -> R.string.app_name
                                }
                                Text(stringResource(titleRes))
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu))
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
                val contentPadding = if (showTopBar) innerPadding else androidx.compose.foundation.layout.PaddingValues(dimensionResource(id = R.dimen.zero_dp))
                Box(modifier = Modifier.padding(contentPadding)) {
                    NavContent(navController, viewModel, authVm, currentUser) {
                        scope.launch { drawerState.open() }
                    }
                }
            }
        }
    } else {
        NavContent(navController, viewModel, authVm, currentUser) {}
    }
}

@Composable
fun NavContent(
    navController: androidx.navigation.NavHostController,
    viewModel: SubjectViewModel,
    authVm: AuthViewModel,
    currentUser: UserEntity?,
    onMenuClick: () -> Unit
) {
    // This is where we define all the screens in our app
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                isLoadingSession = false, // Simplified, as collectAsStateWithLifecycle will have value
                onDone = {
                    val destination = if (currentUser == null) "login" else "home"
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("login") { LoginScreen(authVm) }
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel,
                teacherName = currentUser?.fullName ?: "Teacher",
                onMenuClick = onMenuClick,
                onLogout = {
                    authVm.logout()
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
        composable("admin") { 
            AdminUploadScreen(
                viewModel = viewModel, 
                authViewModel = authVm,
                onMenuClick = onMenuClick
            ) 
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
