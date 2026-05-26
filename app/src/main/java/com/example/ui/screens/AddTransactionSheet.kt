package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.Localization
import com.example.ui.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    viewModel: TransactionViewModel,
    onDismiss: () -> Unit
) {
    val products by viewModel.products.collectAsState()

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var txType by remember { mutableStateOf("INCOME") } // "INCOME" or "EXPENSE"
    var category by remember { mutableStateOf(Localization.categories.first()) }
    var selectedProductId by remember { mutableStateOf<Int?>(null) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var productExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_transaction_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.End
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    Text(
                        text = "إضافة عملية جديدة",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Right
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Toggle Type (Income / Expense)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (txType == "EXPENSE") MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { txType = "EXPENSE" }
                            .testTag("type_expense_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "مصروف",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (txType == "EXPENSE") MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (txType == "INCOME") MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { txType = "INCOME" }
                            .testTag("type_income_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "دخل",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (txType == "INCOME") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Amount Text Field
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("المبلغ (ر.س)", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_amount_field"),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description Text Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف / بيان العملية", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_desc_field"),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category Option Selection Dropdown
                Text(
                    text = "التصنيف",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { categoryExpanded = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                        Text(text = category, style = MaterialTheme.typography.bodyLarge)
                    }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        Localization.categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // If INCOME & Sell Option: optional product linking
                if (txType == "INCOME" && products.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "ربط بمنتج مباع (اختياري - سيقلل المخزون)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { productExpanded = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                            val selectedProdText = products.find { it.id == selectedProductId }?.let { "${it.name} (${it.sellPrice} ر.س)" } ?: "لا يوجد ربط"
                            Text(text = selectedProdText, style = MaterialTheme.typography.bodyLarge)
                        }

                        DropdownMenu(
                            expanded = productExpanded,
                            onDismissRequest = { productExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("لا يوجد ربط", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right) },
                                onClick = {
                                    selectedProductId = null
                                    productExpanded = false
                                }
                            )
                            products.forEach { prod ->
                                DropdownMenuItem(
                                    text = { Text("${prod.name} (المتوفر: ${prod.stock})", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right) },
                                    onClick = {
                                        selectedProductId = prod.id
                                        productExpanded = false
                                        // Auto fill description and price!
                                        if (description.isBlank()) {
                                            description = "بيع منتج: ${prod.name}"
                                        }
                                        if (amount.isBlank()) {
                                            amount = prod.sellPrice.toString()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Submit Button
                Button(
                    onClick = {
                        val amtVal = amount.toDoubleOrNull() ?: 0.0
                        if (amtVal > 0.0 && description.isNotBlank()) {
                            viewModel.addTransaction(
                                type = txType,
                                amount = amtVal,
                                description = description,
                                category = category,
                                productId = selectedProductId
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_transaction_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (txType == "INCOME") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text(
                        text = "تسجيل العملية",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}
