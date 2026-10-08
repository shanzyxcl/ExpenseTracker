package xyz.nxprojects.expensetracker.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.nxprojects.expensetracker.R
import xyz.nxprojects.expensetracker.data.local.dao.CategoryTotal
import xyz.nxprojects.expensetracker.data.repository.Expense
import xyz.nxprojects.expensetracker.data.repository.ExpenseCategory
import xyz.nxprojects.expensetracker.ui.components.*
import xyz.nxprojects.expensetracker.ui.theme.*
import xyz.nxprojects.expensetracker.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlySummaryScreen(
    onBack: () -> Unit,
    onEditExpense: (Long) -> Unit,
    viewModel: MonthlySummaryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.monthly_summary_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${stringResource(getMonthStringRes(state.month))} ${state.year}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.monthly_summary_back_desc)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Grand total card
            item {
                GrandTotalCard(
                    total = state.monthlyTotal,
                    month = state.month,
                    year = state.year,
                    expenseCount = state.expenseCount
                )
            }

            // Category breakdown
            if (state.categoryBreakdown.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.monthly_summary_by_category),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                item {
                    CategoryBreakdownCard(
                        categories = state.categoryBreakdown,
                        totalAmount = state.monthlyTotal
                    )
                }
            }

            // Daily breakdown
            if (state.dailyGroups.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.monthly_summary_by_day),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                state.dailyGroups.forEach { group ->
                    item(key = "header_${group.day}") {
                        DayGroupHeader(
                            day = group.day,
                            dayName = group.dayName,
                            month = state.month,
                            year = state.year,
                            total = group.total
                        )
                    }
                    items(group.expenses, key = { "exp_${it.id}" }) { expense ->
                        ExpenseCard(
                            expense = expense,
                            onEdit = { onEditExpense(expense.id) },
                            onDelete = { expenseToDelete = expense },
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            } else {
                item {
                    EmptyMonthPlaceholder()
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    expenseToDelete?.let { expense ->
        DeleteConfirmDialog(
            expenseTitle = expense.title,
            onConfirm = {
                viewModel.deleteExpense(expense)
                expenseToDelete = null
            },
            onDismiss = { expenseToDelete = null }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Grand total card — displayed prominently
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun GrandTotalCard(
    total: Double,
    month: Int,
    year: Int,
    expenseCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("💰", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.monthly_summary_total_label),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                "${stringResource(getMonthStringRes(month))} $year",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                total.toRupiah(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Text(
                    stringResource(R.string.monthly_summary_transactions, expenseCount),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Category breakdown with progress bars
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CategoryBreakdownCard(
    categories: List<CategoryTotal>,
    totalAmount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categories.forEach { cat ->
                val expCat = ExpenseCategory.values().find { it.label == cat.category }
                    ?: ExpenseCategory.LAINNYA
                val fraction = if (totalAmount > 0) (cat.total / totalAmount).toFloat() else 0f
                val color = getCategoryColor(expCat)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(expCat.emoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                cat.category,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                cat.total.toRupiah(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = color
                            )
                            Text(
                                "${(fraction * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = color,
                        trackColor = color.copy(alpha = 0.15f)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Day group header
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DayGroupHeader(
    day: Int,
    dayName: String,
    month: Int,
    year: Int,
    total: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    day.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    dayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "$day ${stringResource(getMonthStringRes(month))} $year",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
        Text(
            total.toRupiah(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty state
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmptyMonthPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📭", fontSize = 52.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.monthly_summary_empty),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}
