package xyz.nxprojects.expensetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val note: String = "",
    val date: Long = System.currentTimeMillis(), // stored as epoch millis
    val year: Int,
    val month: Int,  // 1-12
    val day: Int
)
