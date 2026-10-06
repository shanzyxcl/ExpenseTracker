package xyz.nxprojects.expensetracker.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.nxprojects.expensetracker.data.local.dao.CategoryTotal
import xyz.nxprojects.expensetracker.data.repository.Expense
import xyz.nxprojects.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

data class DailyGroup(
    val day: Int,
    val dayName: String,
    val expenses: List<Expense>,
    val total: Double
)

data class MonthlySummaryUiState(
    val year: Int = 0,
    val month: Int = 0,
    val monthlyTotal: Double = 0.0,
    val categoryBreakdown: List<CategoryTotal> = emptyList(),
    val dailyGroups: List<DailyGroup> = emptyList(),
    val expenseCount: Int = 0
)

@HiltViewModel
class MonthlySummaryViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val year: Int = savedStateHandle["year"] ?: 2024
    private val month: Int = savedStateHandle["month"] ?: 1

    val uiState: StateFlow<MonthlySummaryUiState> = combine(
        repository.getMonthlyTotal(year, month),
        repository.getMonthlyCategoryBreakdown(year, month),
        repository.getExpensesByMonth(year, month)
    ) { total, categories, expenses ->
        val grouped = expenses
            .groupBy { it.day }
            .map { (day, dayExpenses) ->
                val cal = java.util.Calendar.getInstance()
                cal.set(year, month - 1, day)
                val days = listOf("Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu")
                DailyGroup(
                    day = day,
                    dayName = days[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1],
                    expenses = dayExpenses,
                    total = dayExpenses.sumOf { it.amount }
                )
            }
            .sortedByDescending { it.day }

        MonthlySummaryUiState(
            year = year,
            month = month,
            monthlyTotal = total,
            categoryBreakdown = categories,
            dailyGroups = grouped,
            expenseCount = expenses.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MonthlySummaryUiState(year = year, month = month)
    )

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.deleteExpense(expense)
        }
    }
}
