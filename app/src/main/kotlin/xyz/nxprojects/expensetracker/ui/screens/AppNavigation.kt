package xyz.nxprojects.expensetracker.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // ── Home ──────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                onAddExpense = {
                    navController.navigate(Screen.AddExpense.createRoute())
                },
                onEditExpense = { id ->
                    navController.navigate(Screen.AddExpense.createRoute(id))
                },
                onViewMonthlySummary = { year, month ->
                    navController.navigate(Screen.MonthlySummary.createRoute(year, month))
                },
                onAbout = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        // ── Add / Edit Expense ────────────────────────────────────────────
        composable(
            route = Screen.AddExpense.route,
            arguments = listOf(
                navArgument("expenseId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            AddExpenseScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Monthly Summary ───────────────────────────────────────────────
        composable(
            route = Screen.MonthlySummary.route,
            arguments = listOf(
                navArgument("year") { type = NavType.IntType },
                navArgument("month") { type = NavType.IntType }
            )
        ) {
            MonthlySummaryScreen(
                onBack = { navController.popBackStack() },
                onEditExpense = { id ->
                    navController.navigate(Screen.AddExpense.createRoute(id))
                }
            )
        }
    }
}
