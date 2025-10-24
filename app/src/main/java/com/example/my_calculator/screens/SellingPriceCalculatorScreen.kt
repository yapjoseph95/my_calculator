package com.example.my_calculator.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.my_calculator.ThousandsVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellingPriceCalculatorScreen(navController: NavController) {
    var costPrice by remember { mutableStateOf("") }
    var profitMargin by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selling Price Calculator") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = costPrice,
                onValueChange = { newValue ->
                    if (newValue.count { it == '.' } <= 1) {
                        costPrice = newValue.filter { char -> char.isDigit() || char == '.' }
                    }
                },
                label = { Text("Cost Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                visualTransformation = ThousandsVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = profitMargin,
                onValueChange = { newValue ->
                    if (newValue.count { it == '.' } <= 1) {
                        profitMargin = newValue.filter { char -> char.isDigit() || char == '.' }
                    }
                },
                label = { Text("Profit Margin (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val cost = costPrice.replace(",", "").toDoubleOrNull() ?: 0.0
                    val margin = profitMargin.toDoubleOrNull() ?: 0.0
                    val result = cost * (1 + margin / 100)
                    sellingPrice = String.format("%,.2f", result)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate")
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (sellingPrice.isNotEmpty()) {
                Text("Selling Price: $sellingPrice", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}