package com.currupt.reflame

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.currupt.reflame.account.AccountManager
import com.currupt.reflame.account.model.UserEntitlement
import com.currupt.reflame.account.state.AccountState
import com.currupt.reflame.client.runtime.ClientRuntimeManager
import com.currupt.reflame.client.runtime.OverlayPermissionChecker
import com.currupt.reflame.core.config.ConfigManager
import com.currupt.reflame.core.overlay.AndroidOverlayController
import com.currupt.reflame.feature.about.AboutScreen
import com.currupt.reflame.feature.admin.*
import com.currupt.reflame.feature.games.CurruptGamesScreen
import com.currupt.reflame.feature.games.GameManager
import com.currupt.reflame.feature.games.detection.AndroidUsageStatsDetector
import com.currupt.reflame.feature.games.detection.GameDetectionManager
import com.currupt.reflame.feature.home.CurruptHomeScreen
import com.currupt.reflame.feature.home.StudioAnnouncementsScreen
import com.currupt.reflame.feature.home.StudioDevelopmentScreen
import com.currupt.reflame.feature.projects.ProjectDetailsScreen
import com.currupt.reflame.feature.projects.ProjectsScreen
import com.currupt.reflame.feature.releases.ReleasesScreen
import com.currupt.reflame.feature.settings.CurruptSettingsScreen
import com.currupt.reflame.ui.component.CurruptBackground
import com.currupt.reflame.ui.component.CurruptBottomNavigation
import com.currupt.reflame.ui.design.CurruptColors
import com.currupt.reflame.ui.motion.MotionSystem

sealed class Screen(val route: String) {
    object Home : Screen("currupt/home")
    object Games : Screen("currupt/games")
    object Settings : Screen("currupt/settings")

    // Legacy routes
    object Projects : Screen("projects")
    object ProjectDetails : Screen("projects/{slug}") {
        fun createRoute(slug: String) = "projects/$slug"
    }
    object Releases : Screen("releases")
    object About : Screen("about")
    object Announcements : Screen("announcements")
    object Development : Screen("development")
    object Admin : Screen("admin")
    object AdminContent : Screen("admin/content")
    object AdminContentEditor : Screen("admin/content/edit?id={id}") {
        fun createRoute(id: String?) = if (id != null) "admin/content/edit?id=$id" else "admin/content/edit?id="
    }
    object AdminCategories : Screen("admin/categories")
    object AdminCanvas : Screen("admin/canvas")
    object AdminMedia : Screen("admin/media")
}

@Composable
fun CorruptNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val configManager = remember { ConfigManager() }
    val gameManager = remember { GameManager() }
    val accountManager = remember { AccountManager() }
    val detectionManager = remember { GameDetectionManager(detector = AndroidUsageStatsDetector(context)) }
    val permissionChecker = remember { OverlayPermissionChecker(context) }
    val overlayController = remember { AndroidOverlayController(context) }
    val runtimeManager = remember {
        ClientRuntimeManager(
            permissionChecker = permissionChecker,
            overlayController = overlayController,
            configManager = configManager,
            gameDetectionManager = detectionManager
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val accountState by accountManager.state.collectAsState()
    val currentEntitlement = if (accountState is AccountState.Authenticated) {
        (accountState as AccountState.Authenticated).account.entitlement
    } else {
        UserEntitlement.FREE
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CurruptColors.BackgroundBlack,
        bottomBar = {
            CurruptBottomNavigation(
                currentRoute = currentRoute,
                entitlement = currentEntitlement,
                onNavigate = { targetRoute ->
                    navController.navigate(targetRoute) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { innerPadding ->
        CurruptBackground {
            val layoutDirection = LocalLayoutDirection.current

            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = modifier.padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection)
                ),
                enterTransition = {
                    fadeIn(animationSpec = tween(MotionSystem.DurationShort))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(MotionSystem.DurationShort))
                }
            ) {
                composable(Screen.Home.route) {
                    CurruptHomeScreen(
                        runtimeManager = runtimeManager,
                        configManager = configManager,
                        gameManager = gameManager,
                        detectionManager = detectionManager,
                        accountState = accountState,
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                    )
                }

                composable(Screen.Games.route) {
                    CurruptGamesScreen(
                        gameManager = gameManager
                    )
                }

                composable(Screen.Settings.route) {
                    CurruptSettingsScreen(
                        runtimeManager = runtimeManager,
                        configManager = configManager,
                        detectionManager = detectionManager,
                        accountState = accountState
                    )
                }

                // Legacy routes
                composable(Screen.Projects.route) {
                    ProjectsScreen(
                        onContentClick = { slug ->
                            navController.navigate(Screen.ProjectDetails.createRoute(slug))
                        }
                    )
                }

                composable(Screen.ProjectDetails.route) { backStackEntry ->
                    val slug = backStackEntry.arguments?.getString("slug") ?: ""
                    ProjectDetailsScreen(
                        slug = slug,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.Releases.route) {
                    ReleasesScreen(
                        onContentClick = { slug ->
                            navController.navigate(Screen.ProjectDetails.createRoute(slug))
                        }
                    )
                }

                composable(Screen.About.route) {
                    AboutScreen()
                }

                composable(Screen.Announcements.route) {
                    StudioAnnouncementsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.Development.route) {
                    StudioDevelopmentScreen(
                        onContentClick = { slug ->
                            navController.navigate(Screen.ProjectDetails.createRoute(slug))
                        }
                    )
                }

                composable(Screen.Admin.route) {
                    AdminDashboard(
                        onNavigateToContent = { navController.navigate(Screen.AdminContent.route) },
                        onNavigateToCanvas = { navController.navigate(Screen.AdminCanvas.route) },
                        onNavigateToCategories = { navController.navigate(Screen.AdminCategories.route) },
                        onNavigateToMedia = { navController.navigate(Screen.AdminMedia.route) }
                    )
                }

                composable(Screen.AdminContent.route) {
                    ContentManagementScreen(
                        onBackClick = { navController.popBackStack() },
                        onEditContent = { id -> navController.navigate(Screen.AdminContentEditor.createRoute(id)) }
                    )
                }

                composable(Screen.AdminContentEditor.route) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")?.takeIf { it.isNotBlank() }
                    ContentEditorScreen(
                        contentId = id,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.AdminCategories.route) {
                    CategoryManagementScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.AdminCanvas.route) {
                    CanvasManagementScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.AdminMedia.route) {
                    MediaManagementScreen()
                }
            }
        }
    }
}
