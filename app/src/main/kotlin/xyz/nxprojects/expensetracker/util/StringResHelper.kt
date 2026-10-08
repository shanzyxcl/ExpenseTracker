package xyz.nxprojects.expensetracker.util

import androidx.annotation.StringRes
import xyz.nxprojects.expensetracker.R

/**
 * Maps a 1-based month number to its localised [StringRes].
 *
 * Use this everywhere getMonthName(month) was called inside a Composable,
 * replacing it with  stringResource(getMonthStringRes(month)).
 *
 * The string values live in:
 *   res/values/strings.xml        (English – default)
 *   res/values-in/strings.xml     (Indonesian)
 *
 * Android picks the correct file automatically based on the device locale.
 */
@StringRes
fun getMonthStringRes(month: Int): Int = when (month) {
    1  -> R.string.month_1
    2  -> R.string.month_2
    3  -> R.string.month_3
    4  -> R.string.month_4
    5  -> R.string.month_5
    6  -> R.string.month_6
    7  -> R.string.month_7
    8  -> R.string.month_8
    9  -> R.string.month_9
    10 -> R.string.month_10
    11 -> R.string.month_11
    else -> R.string.month_12
}
