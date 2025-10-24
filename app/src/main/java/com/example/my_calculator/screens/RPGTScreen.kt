package com.example.my_calculator.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.my_calculator.ThousandsVisualTransformation
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RPGTScreen(navController: NavController) {
    var purchasePrice by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    var purchasedYear by remember { mutableStateOf(currentYear.toString()) }
    var rpgt by remember { mutableStateOf("") }
    var showYearPicker by remember { mutableStateOf(false) }

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
            Text("RPGT Calculator", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(if (isSystemInDarkTheme()) 0.dp else 4.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = purchasePrice,
                        onValueChange = { newValue ->
                            if (newValue.count { it == '.' } <= 1) {
                                purchasePrice = newValue.filter { char -> char.isDigit() || char == '.' }
                            }
                        },
                        label = { Text("Purchase Price") },
                        leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        visualTransformation = ThousandsVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sellingPrice,
                        onValueChange = { newValue ->
                            if (newValue.count { it == '.' } <= 1) {
                                sellingPrice = newValue.filter { char -> char.isDigit() || char == '.' }
                            }
                        },
                        label = { Text("Selling Price") },
                        leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        visualTransformation = ThousandsVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.clickable { showYearPicker = true }) {
                        OutlinedTextField(
                            value = purchasedYear,
                            onValueChange = {}, // Not editable directly
                            label = { Text("Purchased Year") },
                            leadingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    val buy = purchasePrice.replace(",", "").toDoubleOrNull() ?: 0.0
                    val sell = sellingPrice.replace(",", "").toDoubleOrNull() ?: 0.0
                    val pYear = purchasedYear.toIntOrNull() ?: currentYear
                    val yearsHeld = currentYear - pYear
                    val gain = sell - buy

                    if (gain > 0) {
                        val rate = when {
                            yearsHeld <= 3 -> 0.30
                            yearsHeld == 4 -> 0.20
                            yearsHeld == 5 -> 0.15
                            else -> 0.05
                        }
                        val tax = gain * rate
                        rpgt = String.format("%,.2f", tax)
                    } else {
                        rpgt = String.format("%,.2f", 0.0)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Calculate", style = MaterialTheme.typography.titleMedium)
            }
            if (rpgt.isNotEmpty()) {
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
                        Text("RPGT to be Paid: RM $rpgt", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        if (showYearPicker) {
            Dialog(onDismissRequest = { showYearPicker = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    val years = (currentYear downTo 1957).toList()
                    LazyColumn {
                        items(years) { year ->
                            Text(
                                text = year.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        purchasedYear = year.toString()
                                        showYearPicker = false
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}