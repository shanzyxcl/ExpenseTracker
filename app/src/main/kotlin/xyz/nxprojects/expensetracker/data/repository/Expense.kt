package xyz.nxprojects.expensetracker.data.repository

data class Expense(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val year: Int,
    val month: Int,
    val day: Int
)

enum class ExpenseCategory(val label: String, val emoji: String) {
    MAKANAN("Makanan & Minuman", "🍔"),
    TRANSPORTASI("Transportasi", "🚗"),
    BELANJA("Belanja", "🛍️"),
    KESEHATAN("Kesehatan", "💊"),
    HIBURAN("Hiburan", "🎮"),
    PENDIDIKAN("Pendidikan", "📚"),
    TAGIHAN("Tagihan & Utilitas", "💡"),
    LAINNYA("Lainnya", "💰");

    companion object {
        fun fromLabel(label: String): ExpenseCategory =
            values().find { it.label == label } ?: LAINNYA
    }
}
