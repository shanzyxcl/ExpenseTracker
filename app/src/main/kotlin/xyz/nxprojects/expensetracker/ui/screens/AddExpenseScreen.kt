package xyz.nxprojects.expensetracker.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.nxprojects.expensetracker.data.repository.ExpenseCategory
import xyz.nxprojects.expensetracker.ui.components.CategoryChip
import xyz.nxprojects.expensetracker.util.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBack: () -> Unit,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditing) "Edit Pengeluaran" else "Tambah Pengeluaran",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error banner
            state.error?.let { err ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(err, color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Title field
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Nama Pengeluaran") },
                placeholder = { Text("Contoh: Makan siang, Bensin, dll") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Amount field
            OutlinedTextField(
                value = state.amount,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Jumlah (Rp)") },
                placeholder = { Text("0") },
                leadingIcon = { Text("Rp", modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Category selector
            Text(
                "Kategori",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExpenseCategory.values().forEach { cat ->
                    CategoryChip(
                        category = cat,
                        selected = state.category == cat,
                        onClick = { viewModel.onCategoryChange(cat) }
                    )
                }
            }

            // Date selector
            Text(
                "Tanggal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            DatePickerRow(
                year = state.selectedYear,
                month = state.selectedMonth,
                day = state.selectedDay,
                onDateChange = viewModel::onDateChange
            )

            // Note field
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Catatan (Opsional)") },
                placeholder = { Text("Tambahkan catatan...") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            // Save button
            Button(
                onClick = viewModel::saveExpense,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    if (state.isEditing) Icons.Default.Save else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (state.isEditing) "Simpan Perubahan" else "Simpan Pengeluaran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Inline date picker row (year / month / day dropdowns)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerRow(
    year: Int,
    month: Int,
    day: Int,
    onDateChange: (Int, Int, Int) -> Unit
) {
    val currentYear = getCurrentYear()
    val years = (currentYear - 1..currentYear + 1).toList()
    val months = (1..12).toList()
    val lastDay = getLastDayOfMonth(year, month)
    val days = (1..lastDay).toList()

    var yearExpanded by remember { mutableStateOf(false) }
    var monthExpanded by remember { mutableStateOf(false) }
    var dayExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Day
        Box(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = day.toString(),
                onValueChange = {},
                label = { Text("Tgl") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, null,
                        Modifier.clickable { dayExpanded = true })
                }
            )
            DropdownMenu(expanded = dayExpanded, onDismissRequest = { dayExpanded = false }) {
                days.forEach { d ->
                    DropdownMenuItem(
                        text = { Text(d.toString()) },
                        onClick = {
                            onDateChange(year, month, d)
                            dayExpanded = false
                        }
                    )
                }
            }
        }

        // Month
        Box(modifier = Modifier.weight(2f)) {
            OutlinedTextField(
                value = getMonthName(month).take(3),
                onValueChange = {},
                label = { Text("Bulan") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, null,
                        Modifier.clickable { monthExpanded = true })
                }
            )
            DropdownMenu(expanded = monthExpanded, onDismissRequest = { monthExpanded = false }) {
                months.forEach { m ->
                    DropdownMenuItem(
                        text = { Text(getMonthName(m)) },
                        onClick = {
                            val safeDay = day.coerceAtMost(getLastDayOfMonth(year, m))
                            onDateChange(year, m, safeDay)
                            monthExpanded = false
                        }
                    )
                }
            }
        }

        // Year
        Box(modifier = Modifier.weight(1.5f)) {
            OutlinedTextField(
                value = year.toString(),
                onValueChange = {},
                label = { Text("Tahun") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, null,
                        Modifier.clickable { yearExpanded = true })
                }
            )
            DropdownMenu(expanded = yearExpanded, onDismissRequest = { yearExpanded = false }) {
                years.forEach { y ->
                    DropdownMenuItem(
                        text = { Text(y.toString()) },
                        onClick = {
                            val safeDay = day.coerceAtMost(getLastDayOfMonth(y, month))
                            onDateChange(y, month, safeDay)
                            yearExpanded = false
                        }
                    )
                }
            }
        }
    }
}
