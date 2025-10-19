package com.itb.notesapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.itb.notesapp.ui.view.TaskDetailView
import com.itb.notesapp.ui.view.TaskListView

sealed class Screen(val route: String) {
    object TaskList : Screen("task_list")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Int) = "task_detail/$taskId"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.TaskList.route) {
        composable(Screen.TaskList.route) {
            TaskListView(
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                })
        }
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            TaskDetailView(
                taskId = taskId, navController = navController
            )
        }
    }
}