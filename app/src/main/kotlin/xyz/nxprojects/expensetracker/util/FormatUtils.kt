package xyz.nxprojects.expensetracker.util

import android.content.Context
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Currency always uses Indonesian Rupiah format regardless of locale
val indonesianLocale = Locale("id", "ID")

fun Double.toRupiah(): String {
    val formatter = NumberFormat.getCurrencyInstance(indonesianLocale)
    formatter.maximumFractionDigits = 0
    return formatter.format(this)
}

/**
 * Returns the month name in the device's current language.
 * Uses the device locale automatically (e.g. "Januari" in ID, "January" in EN).
 */
fun getMonthName(month: Int): String {
    val cal = Calendar.getInstance()
    cal.set(Calendar.MONTH, month - 1)
    return SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.time)
}

/**
 * Returns the full day name in the device's current language.
 * Uses the device locale automatically (e.g. "Senin" in ID, "Monday" in EN).
 */
fun getDayName(year: Int, month: Int, day: Int): String {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, day)
    return SimpleDateFormat("EEEE", Locale.getDefault()).format(cal.time)
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