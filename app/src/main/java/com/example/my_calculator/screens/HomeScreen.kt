package com.example.my_calculator.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.my_calculator.RecentCalculatorsDataStore
import com.example.my_calculator.ThemeDataStore
import kotlinx.coroutines.launch

data class CalculatorItemData(
    val route: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconBgColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val themeDataStore = ThemeDataStore(context)
    val recentCalculatorsDataStore = RecentCalculatorsDataStore(context)
    val isDarkMode by themeDataStore.isDarkModeFlow.collectAsState(initial = isSystemInDarkTheme())
    val recentCalculators by recentCalculatorsDataStore.recentCalculatorsFlow.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }

    val generalCalculatorItem = CalculatorItemData(
        "general_calculator",
        "General Calculator",
        "Perform basic calculations",
        Icons.Filled.Calculate,
        Color(0xFF5AC8FA)
    )

    val allCalculators = listOf(
        CalculatorItemData("car_loan", "Car Loan Calculator", "Calculate monthly car payments", Icons.Filled.DirectionsCar, Color(0xFF0A84FF)),
        CalculatorItemData("housing_loan", "Housing Loan Calculator", "Estimate home loan repayments", Icons.Filled.Home, Color(0xFF30D158)),
        CalculatorItemData("dsr", "DSR Calculator", "Check your Debt Service Ratio", Icons.Filled.PieChart, Color(0xFFFF9F0A)),
        CalculatorItemData("fd", "Fixed Deposit Calculator", "Calculate FD returns & interest", Icons.Filled.AccountBalance, Color(0xFFBF5AF2)),
        CalculatorItemData("legal_fees", "Legal Fees Calculator", "Calculate legal fees", Icons.Filled.Gavel, Color(0xFF64D2FF)),
        CalculatorItemData("late_payment", "Late Payment Calculator", "Calculate late payment interest", Icons.Filled.Warning, Color(0xFFFF453A)),
        CalculatorItemData("rpgt", "RPGT Calculator", "Calculate Real Property Gains Tax", Icons.Filled.TrendingUp, Color(0xFFFFD60A)),
        CalculatorItemData("selling_price_calculator", "Selling Price Calculator", "Cost Price * Profit Margin % = Selling Price", Icons.Filled.AttachMoney, Color(0xFFFF9F0A))
    )

    val filteredCalculators = allCalculators.filter {
        it.title.contains(searchText, ignoreCase = true) ||
                it.description.contains(searchText, ignoreCase = true)
    }

    val recentUsedList = recentCalculators
        .filter { it != "general_calculator" } // 跳过 General Calculator
        .mapNotNull { route -> allCalculators.find { it.route == route } }
        .take(2)

    val animatedBackgroundColor by animateColorAsState(targetValue = MaterialTheme.colorScheme.background, animationSpec = tween(300))
    val animatedSurfaceColor by animateColorAsState(targetValue = if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface, animationSpec = tween(300))
    val animatedSearchFieldColor by animateColorAsState(targetValue = MaterialTheme.colorScheme.surfaceVariant, animationSpec = tween(300))

    Scaffold(
        containerColor = animatedBackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("My Calculator", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            themeDataStore.saveTheme(!isDarkMode)
                        }
                    }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Toggle Dark Mode"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Column {
                    Text("Hello! 👋", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text("What would you like to calculate today?", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Search calculators...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(25.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            if (searchText.isNotBlank()) {
                                filteredCalculators.firstOrNull()?.let { calculator ->
                                    navController.navigate(calculator.route)
                                }
                            }
                        }),
                        colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = animatedSearchFieldColor)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (searchText.isBlank()) {
                item {
                    Text("Recently Used", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (recentUsedList.isEmpty()) {
                        Text("No recently used calculators 😅", color = Color.Gray)
                        Spacer(modifier = Modifier.height(24.dp))
                    } else {
                        recentUsedList.forEach { calc ->
                            CalculatorRow(navController, calc, recentCalculatorsDataStore, animatedSurfaceColor)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                item {
                    Text("All Calculators", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    CalculatorRow(navController, generalCalculatorItem, recentCalculatorsDataStore, animatedSurfaceColor)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                items(allCalculators) { calculator ->
                    if (calculator.route != generalCalculatorItem.route) {
                        CalculatorRow(navController, calculator, recentCalculatorsDataStore, animatedSurfaceColor)
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            } else {
                if (filteredCalculators.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No calculator found 😅", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    items(filteredCalculators) { calculator ->
                        CalculatorRow(navController, calculator, recentCalculatorsDataStore, animatedSurfaceColor)
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorRow(
    navController: NavController,
    item: CalculatorItemData,
    dataStore: RecentCalculatorsDataStore,
    surfaceColor: Color
) {
    val coroutineScope = rememberCoroutineScope()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                coroutineScope.launch {
                    dataStore.saveRecentCalculator(item.route)
                }
                navController.navigate(item.route)
            },
        shape = RoundedCornerShape(20.dp),
        shadowElevation = if (isSystemInDarkTheme()) 0.dp else 4.dp,
        tonalElevation = 0.dp,
        color = surfaceColor
    ) {
        Row(
            modifier = Modifier.padding(26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(item.iconBgColor, item.iconBgColor.copy(alpha = 0.6f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = item.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
