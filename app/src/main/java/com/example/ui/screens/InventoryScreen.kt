package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Product
import com.example.ui.Localization
import com.example.ui.TransactionViewModel

@Composable
fun InventoryScreen(
    viewModel: TransactionViewModel
) {
    val products by viewModel.products.collectAsState()

    var showAddProductDialog by remember { mutableStateOf(false) }

    var pName by remember { mutableStateOf("") }
    var pStock by remember { mutableStateOf("") }
    var pCostPrice by remember { mutableStateOf("") }
    var pSellPrice by remember { mutableStateOf("") }
    var pSku by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("inventory_root"),
        horizontalAlignment = Alignment.End
    ) {
        // Welcoming header with addition button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showAddProductDialog = true },
                modifier = Modifier.testTag("add_product_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة منتج")
            }
            Text(
                text = "إدارة المخزون والمنتجات",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total stock metric highlight card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Number of unique items
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("إجمالي الموديلات", style = MaterialTheme.typography.labelMedium)
                    Text("${products.size}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
                // Combined units total
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("إجمالي قطع المخزون", style = MaterialTheme.typography.labelMedium)
                    Text("${products.sumOf { it.stock }}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "المخازن فارغة حالياً",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "يمكنك إضافة منتجات السوبر ماركت أو المتجر هنا لبيعها بنقرة سريعة",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("inventory_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductItemRow(
                        product = product,
                        onQuickSell = { viewModel.quickSellProduct(product, 1) },
                        onDelete = { viewModel.deleteProduct(product.id) }
                    )
                }
            }
        }
    }

    // Add Product Dialog Form
    if (showAddProductDialog) {
        Dialog(onDismissRequest = { showAddProductDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "إضافة منتج جديد للمخزن",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    OutlinedTextField(
                        value = pName,
                        onValueChange = { pName = it },
                        label = { Text("اسم المنتج", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("p_name_field"),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = pCostPrice,
                            onValueChange = { pCostPrice = it },
                            label = { Text("سعر التكلفة", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("p_cost_field"),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right)
                        )
                        OutlinedTextField(
                            value = pSellPrice,
                            onValueChange = { pSellPrice = it },
                            label = { Text("سعر البيع المقترح", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("p_sell_field"),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pStock,
                        onValueChange = { pStock = it },
                        label = { Text("الكمية المتاحة بالمخزن", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("p_stock_field"),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                val cost = pCostPrice.toDoubleOrNull() ?: 0.0
                                val sell = pSellPrice.toDoubleOrNull() ?: 0.0
                                val qty = pStock.toIntOrNull() ?: 0
                                if (pName.isNotBlank() && sell > 0.0 && qty >= 0) {
                                    viewModel.addProduct(
                                        name = pName,
                                        stock = qty,
                                        costPrice = cost,
                                        sellPrice = sell,
                                        sku = pSku
                                    )
                                    // Clear fields
                                    pName = ""
                                    pCostPrice = ""
                                    pSellPrice = ""
                                    pStock = ""
                                    pSku = ""
                                    showAddProductDialog = false
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_product_btn")
                        ) {
                            Text("إضافة")
                        }
                        OutlinedButton(
                            onClick = { showAddProductDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("إلغاء")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItemRow(
    product: Product,
    onQuickSell: () -> Unit,
    onDelete: () -> Unit
) {
    val profitMargin = product.sellPrice - product.costPrice

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_item_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_product_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "مسح المنتج",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Name and Description SKU
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "الرمز: ${product.sku.ifBlank { "غير مسجل" }}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Margin calculation
                Column(horizontalAlignment = Alignment.Start) {
                    Text("هامش الربح لكل قطعة", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "+${Localization.formatCurrency(profitMargin)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF10B981)
                    )
                }

                // Rates cost & sell details
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "سعر البيع: ${Localization.formatCurrency(product.sellPrice)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "سعر التكلفة: ${Localization.formatCurrency(product.costPrice)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sell triggers
                Button(
                    onClick = onQuickSell,
                    enabled = product.stock > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("quick_sell_product_btn_${product.id}")
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("بيع قطعة واحدة", fontSize = 12.sp)
                }

                // Balance of stock
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "قطعة",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    product.stock == 0 -> MaterialTheme.colorScheme.errorContainer
                                    product.stock < 5 -> Color(0xFFFEF3C7) // Warning Amber
                                    else -> MaterialTheme.colorScheme.primaryContainer
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${product.stock}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                            color = when {
                                product.stock == 0 -> MaterialTheme.colorScheme.onErrorContainer
                                product.stock < 5 -> Color(0xFF92400E)
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "المخزون الحالي:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
