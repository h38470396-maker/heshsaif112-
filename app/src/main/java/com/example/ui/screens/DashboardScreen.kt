package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.Localization
import com.example.ui.TransactionViewModel

@Composable
fun DashboardScreen(
    viewModel: TransactionViewModel,
    onNavigateToTransactions: () -> Unit
) {
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val netProfit by viewModel.netProfit.collectAsState()
    val annualTarget by viewModel.annualTarget.collectAsState()
    val monthlyData by viewModel.monthlyAnalysis.collectAsState()

    var showTargetDialog by remember { mutableStateOf(false) }
    var newTargetInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    // Main layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("dashboard_root"),
        horizontalAlignment = Alignment.End
    ) {
        // Welcome Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "الحسابات السنوية لعام رصين",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تابع أرباحك، مداخيلك، مصروفاتك ومخزونك بكل أمان",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                    textAlign = TextAlign.Right
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large Profit Score Screen
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profit_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "صافي الأرباح الحالية",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = Localization.formatCurrency(netProfit),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = if (netProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.padding(vertical = 4.dp),
                    textAlign = TextAlign.Right
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metric Row (Income & Expense)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Expense Card
            Card(
                modifier = Modifier
                    .weight(1.5f)
                    .testTag("expense_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFB7185))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "إجمالي المصاريف",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = Localization.formatCurrency(totalExpense),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFB7185)
                    )
                }
            }

            // Income Card
            Card(
                modifier = Modifier
                    .weight(1.5f)
                    .testTag("income_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "إجمالي المداخيل",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = Localization.formatCurrency(totalIncome),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF10B981)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Annual Target Progress Section
        annualTarget?.let { target ->
            val ratio = if (target.targetAmount > 0) (netProfit / target.targetAmount).coerceIn(0.0, 1.0) else 0.0
            val percentage = (ratio * 100).toInt()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("target_progress_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                newTargetInput = target.targetAmount.toInt().toString()
                                showTargetDialog = true
                            },
                            modifier = Modifier.testTag("edit_target_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "تعديل الهدف",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "تحدي الهدف المالي السنوي",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = Localization.formatCurrency(target.targetAmount),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "المستهدف:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = ratio.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "تم إنجاز $percentage% من هدفك المالي السنوي بنجاح!",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Graphical Analytics Header
        Text(
            text = "الأداء المالي الشهري لعام ٢٠٢٦",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Custom canvas draw bar chart representation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .testTag("chart_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val negativeColor = MaterialTheme.colorScheme.tertiary
            val textPaintColor = MaterialTheme.colorScheme.onSurface.toArgb()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val width = size.width
                    val height = size.height
                    
                    val values = monthlyData.values.toList()
                    if (values.isEmpty()) return@Canvas

                    val maxVal = values.maxOfOrNull { kotlin.math.abs(it) }?.takeIf { it > 0.0 } ?: 1000.0
                    
                    val halfHeight = height / 2f
                    
                    // Draw baseline axis
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(0f, halfHeight),
                        end = Offset(width, halfHeight),
                        strokeWidth = 2f
                    )

                    val barSpacing = width / 12f

                    for (month in 0..11) {
                        val profitValue = monthlyData[month] ?: 0.0
                        // Normalize size to fit upper or lower half
                        val barHeight = ((profitValue / maxVal) * (halfHeight - 20f)).toFloat()
                        val barWidth = barSpacing * 0.5f

                        val left = month * barSpacing + (barSpacing - barWidth) / 2
                        
                        val top = if (profitValue >= 0f) {
                            halfHeight - barHeight
                        } else {
                            halfHeight
                        }

                        val absHeight = kotlin.math.abs(barHeight).coerceAtLeast(4f)

                        // Draw visual rect bar
                        drawRect(
                            color = if (profitValue >= 0) primaryColor else negativeColor,
                            topLeft = Offset(left, top),
                            size = Size(barWidth, absHeight)
                        )

                        // Draw text label abbreviated Arabic
                        val monthLabel = (month + 1).toString()
                        drawContext.canvas.nativeCanvas.drawText(
                            monthLabel,
                            left + (barWidth / 2f) - 6f,
                            height - 10f,
                            android.graphics.Paint().apply {
                                color = textPaintColor
                                textSize = 24f
                                isFakeBoldText = true
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp)) // Padding to allow scrolling over quick action bottom bars
    }

    // Dialog to edit Target amount
    if (showTargetDialog) {
        Dialog(onDismissRequest = { showTargetDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "تعديل الهدف السنوي",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = newTargetInput,
                        onValueChange = { newTargetInput = it },
                        label = { Text("الهدف السنوي الجديد (ر.س)", textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("target_input_field"),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                val amtDouble = newTargetInput.toDoubleOrNull() ?: 50000.0
                                viewModel.updateAnnualTargetAmount(amtDouble)
                                showTargetDialog = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("confirm_target_btn")
                        ) {
                            Text("تعديل")
                        }
                        OutlinedButton(
                            onClick = { showTargetDialog = false },
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
