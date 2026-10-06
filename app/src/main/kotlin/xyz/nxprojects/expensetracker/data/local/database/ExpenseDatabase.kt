package xyz.nxprojects.expensetracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.nxprojects.expensetracker.data.local.dao.ExpenseDao
import xyz.nxprojects.expensetracker.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "expense_tracker.db"
    }
}
