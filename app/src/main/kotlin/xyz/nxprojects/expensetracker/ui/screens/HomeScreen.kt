package xyz.nxprojects.expensetracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.nxprojects.expensetracker.R
import xyz.nxprojects.expensetracker.data.repository.Expense
import xyz.nxprojects.expensetracker.ui.components.*
import xyz.nxprojects.expensetracker.ui.theme.*
import xyz.nxprojects.expensetracker.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddExpense: () -> Unit,
    onEditExpense: (Long) -> Unit,
    onViewMonthlySummary: (Int, Int) -> Unit,
    onAbout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.home_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${stringResource(getMonthStringRes(state.currentMonth))} ${state.currentYear}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onViewMonthlySummary(state.currentYear, state.currentMonth)
                    }) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = stringResource(R.string.home_monthly_summary_icon_desc),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onAbout) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = stringResource(R.string.home_about_icon_desc),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpense,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.home_fab_desc))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Monthly summary card
            item {
                MonthlyOverviewCard(
                    monthlyTotal = state.monthlyTotal,
                    todayTotal = state.todayTotal,
                    year = state.currentYear,
                    month = state.currentMonth,
                    onClick = { onViewMonthlySummary(state.currentYear, state.currentMonth) }
                )
            }

            // Day selector header
            item {
                Text(
                    stringResource(R.string.home_select_day),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Day picker
            item {
                DayPickerRow(
                    year = state.currentYear,
                    month = state.currentMonth,
                    selectedDay = state.currentDay,
                    onDaySelected = { viewModel.selectDay(it) }
                )
            }

            // Selected day header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${getDayName(state.currentYear, state.currentMonth, state.currentDay)}, ${state.currentDay} ${stringResource(getMonthStringRes(state.currentMonth))}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = state.todayTotal.toRupiah(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Expenses for selected day
            if (state.todayExpenses.isEmpty()) {
                item {
                    EmptyDayPlaceholder(onAddExpense)
                }
            } else {
                items(state.todayExpenses, key = { it.id }) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onEdit = { onEditExpense(expense.id) },
                        onDelete = { expenseToDelete = expense }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
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
// Monthly overview banner
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MonthlyOverviewCard(
    monthlyTotal: Double,
    todayTotal: Double,
    year: Int,
    month: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Primary)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        stringResource(R.string.home_monthly_total, stringResource(getMonthStringRes(month)), year),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        monthlyTotal.toRupiah(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        stringResource(R.string.home_today),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        todayTotal.toRupiah(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    stringResource(R.string.home_view_monthly_summary),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Horizontal day picker
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DayPickerRow(
    year: Int,
    month: Int,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    val lastDay = getLastDayOfMonth(year, month)
    val today = getCurrentDay()
    val currentYear = getCurrentYear()
    val currentMonth = getCurrentMonth()

    // Localised short day names resolved once per composition
    val dayNames = listOf(
        stringResource(R.string.day_sun_short),
        stringResource(R.string.day_mon_short),
        stringResource(R.string.day_tue_short),
        stringResource(R.string.day_wed_short),
        stringResource(R.string.day_thu_short),
        stringResource(R.string.day_fri_short),
        stringResource(R.string.day_sat_short)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items((1..lastDay).toList()) { day ->
            val isSelected = day == selectedDay
            val isToday = day == today && year == currentYear && month == currentMonth
            val cal = java.util.Calendar.getInstance()
            cal.set(year, month - 1, day)
            val dayName = dayNames[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1]

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            isToday -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .clickable { onDaySelected(day) }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.White
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White
                    else MaterialTheme.colorScheme.onSurface
                )
                if (isToday && !isSelected) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty state
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmptyDayPlaceholder(onAddExpense: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.home_empty_placeholder_emoji), fontSize = 52.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            stringResource(R.string.home_empty_no_expenses),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onAddExpense) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.home_empty_add_button))
        }
    }
}
