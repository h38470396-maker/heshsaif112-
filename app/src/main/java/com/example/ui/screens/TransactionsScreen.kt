package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.ui.Localization
import com.example.ui.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionsScreen(
    viewModel: TransactionViewModel,
    onAddTransactionClick: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("ALL") } // "ALL", "INCOME", "EXPENSE"

    val filteredTransactions = remember(transactions, searchQuery, selectedTypeFilter) {
        transactions.filter { tx ->
            val matchesSearch = tx.description.contains(searchQuery, ignoreCase = true) ||
                    tx.category.contains(searchQuery, ignoreCase = true)
            val matchesType = when (selectedTypeFilter) {
                "INCOME" -> tx.type == "INCOME"
                "EXPENSE" -> tx.type == "EXPENSE"
                else -> true
            }
            matchesSearch && matchesType
        }
    }

    val dateFormatter = remember { SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale("ar")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("transactions_root"),
        horizontalAlignment = Alignment.End
    ) {
        // Stats Label
        Text(
            text = "دفتر العمليات والحسابات",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ابحث عن عملية أو تصنيف...", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_transactions_field"),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Toggle Type Filter Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ALL Filter
            FilterChipItem(
                text = "الكل",
                selected = selectedTypeFilter == "ALL",
                onClick = { selectedTypeFilter = "ALL" },
                modifier = Modifier.weight(1f).testTag("filter_all_chip")
            )
            // INCOME Filter
            FilterChipItem(
                text = "المداخيل",
                selected = selectedTypeFilter == "INCOME",
                onClick = { selectedTypeFilter = "INCOME" },
                modifier = Modifier.weight(1f).testTag("filter_income_chip")
            )
            // EXPENSE Filter
            FilterChipItem(
                text = "المصاريف",
                selected = selectedTypeFilter == "EXPENSE",
                onClick = { selectedTypeFilter = "EXPENSE" },
                modifier = Modifier.weight(1f).testTag("filter_expense_chip")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ledger List
        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "لا توجد عمليات مسجلة متطابقة",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "اضغط على زر الإضافة لتسجيل معاملة محاسبية جديدة",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("transactions_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredTransactions, key = { it.id }) { tx ->
                    TransactionItemRow(
                        transaction = tx,
                        dateString = dateFormatter.format(Date(tx.timestamp)),
                        onDeleteClick = { viewModel.deleteTransaction(tx) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChipItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TransactionItemRow(
    transaction: Transaction,
    dateString: String,
    onDeleteClick: () -> Unit
) {
    val isIncome = transaction.type == "INCOME"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transaction_item_${transaction.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Delete Action
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.testTag("delete_transaction_btn_${transaction.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف العملية",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Text info
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(3f)
            ) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Right
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = transaction.category,
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Indicator dot & Price
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${if (isIncome) "+" else "-"}${Localization.formatCurrency(transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                )
            }
        }
    }
}
