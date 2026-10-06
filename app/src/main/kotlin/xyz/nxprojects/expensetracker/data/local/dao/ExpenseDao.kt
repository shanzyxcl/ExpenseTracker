package xyz.nxprojects.expensetracker.data.local.dao

import androidx.room.*
import xyz.nxprojects.expensetracker.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE year = :year AND month = :month AND day = :day ORDER BY date DESC")
    fun getExpensesByDay(year: Int, month: Int, day: Int): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE year = :year AND month = :month ORDER BY day ASC, date DESC")
    fun getExpensesByMonth(year: Int, month: Int): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expenses WHERE year = :year AND month = :month AND day = :day")
    fun getDailyTotal(year: Int, month: Int, day: Int): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE year = :year AND month = :month")
    fun getMonthlyTotal(year: Int, month: Int): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE year = :year AND month = :month AND day = :day AND category = :category")
    fun getCategoryTotalByDay(year: Int, month: Int, day: Int, category: String): Flow<Double?>

    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE year = :year AND month = :month GROUP BY category ORDER BY total DESC")
    fun getMonthlyCategoryBreakdown(year: Int, month: Int): Flow<List<CategoryTotal>>

    @Query("SELECT year, month, SUM(amount) as total FROM expenses GROUP BY year, month ORDER BY year DESC, month DESC")
    fun getMonthlyHistory(): Flow<List<MonthlyHistory>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): ExpenseEntity?

    @Query("SELECT DISTINCT year, month, day FROM expenses WHERE year = :year AND month = :month ORDER BY day ASC")
    fun getDaysWithExpenses(year: Int, month: Int): Flow<List<DayEntry>>
}

data class CategoryTotal(
    val category: String,
    val total: Double
)

data class MonthlyHistory(
    val year: Int,
    val month: Int,
    val total: Double
)

data class DayEntry(
    val year: Int,
    val month: Int,
    val day: Int
)
