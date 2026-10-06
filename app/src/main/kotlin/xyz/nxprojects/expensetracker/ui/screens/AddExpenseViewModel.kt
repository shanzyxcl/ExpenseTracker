package xyz.nxprojects.expensetracker.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.nxprojects.expensetracker.data.repository.Expense
import xyz.nxprojects.expensetracker.data.repository.ExpenseCategory
import xyz.nxprojects.expensetracker.data.repository.ExpenseRepository
import xyz.nxprojects.expensetracker.util.getCurrentDay
import xyz.nxprojects.expensetracker.util.getCurrentMonth
import xyz.nxprojects.expensetracker.util.getCurrentYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import java.util.Calendar
import javax.inject.Inject

data class AddExpenseUiState(
    val title: String = "",
    val amount: String = "",
    val category: ExpenseCategory = ExpenseCategory.MAKANAN,
    val note: String = "",
    val selectedYear: Int = getCurrentYear(),
    val selectedMonth: Int = getCurrentMonth(),
    val selectedDay: Int = getCurrentDay(),
    val isEditing: Boolean = false,
    val editingId: Long = 0L,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState

    init {
        val expenseId = savedStateHandle.get<Long>("expenseId") ?: -1L
        if (expenseId != -1L) {
            loadExpense(expenseId)
        }
    }

    private fun loadExpense(id: Long) {
        viewModelScope.launch {
            val expense = repository.getExpenseById(id)
            expense?.let {
                _uiState.value = _uiState.value.copy(
                    title = it.title,
                    amount = it.amount.toLong().toString(),
                    category = ExpenseCategory.fromLabel(it.category),
                    note = it.note,
                    selectedYear = it.year,
                    selectedMonth = it.month,
                    selectedDay = it.day,
                    isEditing = true,
                    editingId = it.id
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value, error = null)
    }

    fun onAmountChange(value: String) {
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(amount = value, error = null)
        }
    }

    fun onCategoryChange(category: ExpenseCategory) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun onNoteChange(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun onDateChange(year: Int, month: Int, day: Int) {
        _uiState.value = _uiState.value.copy(
            selectedYear = year,
            selectedMonth = month,
            selectedDay = day
        )
    }

    fun saveExpense() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.value = state.copy(error = "Nama pengeluaran tidak boleh kosong")
            return
        }
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = state.copy(error = "Jumlah pengeluaran tidak valid")
            return
        }

        viewModelScope.launch {
            val cal = Calendar.getInstance().apply {
                set(state.selectedYear, state.selectedMonth - 1, state.selectedDay, 12, 0, 0)
            }
            val expense = Expense(
                id = if (state.isEditing) state.editingId else 0L,
                title = state.title.trim(),
                amount = amount,
                category = state.category.label,
                note = state.note.trim(),
                date = cal.timeInMillis,
                year = state.selectedYear,
                month = state.selectedMonth,
                day = state.selectedDay
            )
            if (state.isEditing) {
                repository.updateExpense(expense)
            } else {
                repository.addExpense(expense)
            }
            _uiState.value = _uiState.value.copy(saveSuccess = true)
        }
    }
}
