package xyz.nxprojects.expensetracker.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.nxprojects.expensetracker.data.local.dao.MonthlyHistory
import xyz.nxprojects.expensetracker.data.repository.Expense
import xyz.nxprojects.expensetracker.data.repository.ExpenseRepository
import xyz.nxprojects.expensetracker.util.getCurrentDay
import xyz.nxprojects.expensetracker.util.getCurrentMonth
import xyz.nxprojects.expensetracker.util.getCurrentYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val currentYear: Int = getCurrentYear(),
    val currentMonth: Int = getCurrentMonth(),
    val currentDay: Int = getCurrentDay(),
    val todayExpenses: List<Expense> = emptyList(),
    val todayTotal: Double = 0.0,
    val monthlyTotal: Double = 0.0,
    val monthlyExpenses: List<Expense> = emptyList(),
    val recentHistory: List<MonthlyHistory> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _selectedYear = MutableStateFlow(getCurrentYear())
    private val _selectedMonth = MutableStateFlow(getCurrentMonth())
    private val _selectedDay = MutableStateFlow(getCurrentDay())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = combine(
        _selectedYear,
        _selectedMonth,
        _selectedDay
    ) { year, month, day -> Triple(year, month, day) }
        .flatMapLatest { (year, month, day) ->
            combine(
                repository.getExpensesByDay(year, month, day),
                repository.getDailyTotal(year, month, day),
                repository.getMonthlyTotal(year, month),
                repository.getExpensesByMonth(year, month),
                repository.getMonthlyHistory()
            ) { todayExpenses, todayTotal, monthlyTotal, monthlyExpenses, history ->
                HomeUiState(
                    currentYear = year,
                    currentMonth = month,
                    currentDay = day,
                    todayExpenses = todayExpenses,
                    todayTotal = todayTotal,
                    monthlyTotal = monthlyTotal,
                    monthlyExpenses = monthlyExpenses,
                    recentHistory = history.take(6)
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun selectDay(day: Int) {
        _selectedDay.value = day
    }
}
