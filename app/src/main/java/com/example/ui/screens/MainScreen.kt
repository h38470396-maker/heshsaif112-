package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TransactionViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Dashboard, 1: Transactions, 2: Inventory
    var showAddTransactionSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = when (selectedTab) {
                                1 -> "دفتر المعاملات المالية"
                                2 -> "مستودع المخازن والمنتجات"
                                else -> "الحسابات السنوية"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                // Tab 3: Inventory
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "المخزون") },
                    label = { Text("المخزون", fontSize = 12.sp) },
                    modifier = Modifier.testTag("nav_inventory")
                )

                // Tab 2: Transactions
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = "العمليات") },
                    label = { Text("العمليات", fontSize = 12.sp) },
                    modifier = Modifier.testTag("nav_transactions")
                )

                // Tab 1: Dashboard
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                    label = { Text("الرئيسية", fontSize = 12.sp) },
                    modifier = Modifier.testTag("nav_dashboard")
                )
            }
        },
        floatingActionButton = {
            if (selectedTab != 2) {
                FloatingActionButton(
                    onClick = { showAddTransactionSheet = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .testTag("fab_add_transaction")
                        .padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة عملية", modifier = Modifier.size(28.dp))
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End // Beautiful custom position aligned for visual hierarchy
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTransactions = { selectedTab = 1 }
                )
                1 -> TransactionsScreen(
                    viewModel = viewModel,
                    onAddTransactionClick = { showAddTransactionSheet = true }
                )
                2 -> InventoryScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    if (showAddTransactionSheet) {
        AddTransactionSheet(
            viewModel = viewModel,
            onDismiss = { showAddTransactionSheet = false }
        )
    }
}
