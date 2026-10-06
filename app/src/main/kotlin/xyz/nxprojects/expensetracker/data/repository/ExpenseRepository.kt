package xyz.nxprojects.expensetracker.data.repository

import xyz.nxprojects.expensetracker.data.local.dao.CategoryTotal
import xyz.nxprojects.expensetracker.data.local.dao.ExpenseDao
import xyz.nxprojects.expensetracker.data.local.dao.MonthlyHistory
import xyz.nxprojects.expensetracker.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val dao: ExpenseDao
) {
    suspend fun addExpense(expense: Expense): Long {
        return dao.insertExpense(expense.toEntity())
    }

    suspend fun updateExpense(expense: Expense) {
        dao.updateExpense(expense.toEntity())
    }

    suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense.toEntity())
    }

    fun getExpensesByDay(year: Int, month: Int, day: Int): Flow<List<Expense>> {
        return dao.getExpensesByDay(year, month, day).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getExpensesByMonth(year: Int, month: Int): Flow<List<Expense>> {
        return dao.getExpensesByMonth(year, month).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getDailyTotal(year: Int, month: Int, day: Int): Flow<Double> {
        return dao.getDailyTotal(year, month, day).map { it ?: 0.0 }
    }

    fun getMonthlyTotal(year: Int, month: Int): Flow<Double> {
        return dao.getMonthlyTotal(year, month).map { it ?: 0.0 }
    }

    fun getMonthlyCategoryBreakdown(year: Int, month: Int): Flow<List<CategoryTotal>> {
        return dao.getMonthlyCategoryBreakdown(year, month)
    }

    fun getMonthlyHistory(): Flow<List<MonthlyHistory>> {
        return dao.getMonthlyHistory()
    }

    suspend fun getExpenseById(id: Long): Expense? {
        return dao.getExpenseById(id)?.toDomain()
    }
}

private fun Expense.toEntity() = ExpenseEntity(
    id = id,
    title = title,
    amount = amount,
    category = category,
    note = note,
    date = date,
    year = year,
    month = month,
    day = day
)

private fun ExpenseEntity.toDomain() = Expense(
    id = id,
    title = title,
    amount = amount,
    category = category,
    note = note,
    date = date,
    year = year,
    month = month,
    day = day
)
