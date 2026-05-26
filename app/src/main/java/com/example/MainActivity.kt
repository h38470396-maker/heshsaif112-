package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.TransactionViewModel
import com.example.ui.screens.MainScreen
import com.example.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Supports full edge-to-edge content layout
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        setContent {
            AppTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
