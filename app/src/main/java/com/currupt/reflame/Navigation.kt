package com.currupt.reflame

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.currupt.reflame.feature.about.AboutScreen
import com.currupt.reflame.feature.admin.*
import com.currupt.reflame.feature.home.*
import com.currupt.reflame.feature.projects.ProjectDetailsScreen
import com.currupt.reflame.feature.projects.ProjectsScreen
import com.currupt.reflame.feature.releases.ReleasesScreen
import com.currupt.reflame.ui.component.AppShell
import com.currupt.reflame.ui.motion.MotionSystem

/**
 * Centralized route definitions for CURRUPT. Studio.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
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
    AppShell(navController = navController) { innerPadding ->
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
                fadeIn(animationSpec = tween(MotionSystem.DurationShort)) + 
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(MotionSystem.DurationShort))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(MotionSystem.DurationShort)) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(MotionSystem.DurationShort))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(MotionSystem.DurationShort)) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(MotionSystem.DurationShort))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(MotionSystem.DurationShort)) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(MotionSystem.DurationShort))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onContentClick = { slug ->
                        navController.navigate(Screen.ProjectDetails.createRoute(slug))
                    }
                )
            }

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

            // Admin Routes
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
