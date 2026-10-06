package xyz.nxprojects.expensetracker.util

import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

val indonesianLocale = Locale("id", "ID")

fun Double.toRupiah(): String {
    val formatter = NumberFormat.getCurrencyInstance(indonesianLocale)
    formatter.maximumFractionDigits = 0
    return formatter.format(this)
}

fun getMonthName(month: Int): String {
    val months = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    return months.getOrElse(month - 1) { "Unknown" }
}

fun getDayName(year: Int, month: Int, day: Int): String {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, day)
    val days = listOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    return days[cal.get(Calendar.DAY_OF_WEEK) - 1]
}

fun getLastDayOfMonth(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, 1)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)
fun getCurrentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1
fun getCurrentDay(): Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

fun isCurrentMonth(year: Int, month: Int): Boolean {
    return year == getCurrentYear() && month == getCurrentMonth()
}
