package xyz.nxprojects.expensetracker.ui.screens

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddExpense : Screen("add_expense?expenseId={expenseId}") {
        fun createRoute(expenseId: Long = -1L) = "add_expense?expenseId=$expenseId"
    }
    object DailyDetail : Screen("daily_detail/{year}/{month}/{day}") {
        fun createRoute(year: Int, month: Int, day: Int) = "daily_detail/$year/$month/$day"
    }
    object MonthlySummary : Screen("monthly_summary/{year}/{month}") {
        fun createRoute(year: Int, month: Int) = "monthly_summary/$year/$month"
    }
    object History : Screen("history")
    object About : Screen("about")
}
