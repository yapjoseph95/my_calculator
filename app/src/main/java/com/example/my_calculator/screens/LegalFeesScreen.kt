package com.example.my_calculator.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.my_calculator.ThousandsVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalFeesScreen(navController: NavController) {
    var propertyPrice by remember { mutableStateOf("") }
    var legalFee by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Calculate, contentDescription = "FinCalc Logo", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FinCalc", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Legal Fees Calculator", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(if (isSystemInDarkTheme()) 0.dp else 4.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = propertyPrice,
                        onValueChange = { propertyPrice = it.filter { char -> char.isDigit() || char == '.' } },
                        label = { Text("Property Price") },
                        leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        visualTransformation = ThousandsVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    val price = propertyPrice.replace(",", "").toDoubleOrNull() ?: 0.0
                    if (price > 0) {
                        var fee = 0.0
                        var remainingPrice = price

                        if (remainingPrice > 0) {
                            val tier1 = minOf(remainingPrice, 500000.0)
                            fee += tier1 * 0.01
                            remainingPrice -= tier1
                        }
                        if (remainingPrice > 0) {
                            val tier2 = minOf(remainingPrice, 500000.0)
                            fee += tier2 * 0.008
                            remainingPrice -= tier2
                        }
                        if (remainingPrice > 0) {
                            val tier3 = minOf(remainingPrice, 2000000.0)
                            fee += tier3 * 0.007
                            remainingPrice -= tier3
                        }
                        if (remainingPrice > 0) {
                            val tier4 = minOf(remainingPrice, 2000000.0)
                            fee += tier4 * 0.006
                            remainingPrice -= tier4
                        }
                        if (remainingPrice > 0) {
                            fee += remainingPrice * 0.005
                        }

                        legalFee = String.format("%,.2f", fee)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Calculate", style = MaterialTheme.typography.titleMedium)
            }
            if (legalFee.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(if (isSystemInDarkTheme()) 0.dp else 4.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Results", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Estimated Legal Fee: RM $legalFee", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
