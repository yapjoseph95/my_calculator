package com.example.my_calculator.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.my_calculator.ThousandsVisualTransformation
import java.text.DecimalFormat
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarLoanScreen(navController: NavController) {
    var carPrice by remember { mutableStateOf("80000") }
    var downPaymentPercentage by remember { mutableStateOf("10") }
    var interestRate by remember { mutableStateOf("2.5") }
    var loanTerm by remember { mutableStateOf(9) } // Default to 9 years

    var showResults by remember { mutableStateOf(false) }
    var monthlyPayment by remember { mutableStateOf(0.0) }
    var totalInterest by remember { mutableStateOf(0.0) }
    var totalPayment by remember { mutableStateOf(0.0) }
    var loanAmount by remember { mutableStateOf(0.0) }
    var downPaymentAmount by remember { mutableStateOf(0.0) }
    var paymentBreakdownY by remember { mutableStateOf(0f) }

    val accentColor = Color(0xFF0A84FF)
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    var calculationTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(calculationTrigger) {
        if (calculationTrigger > 0) {
            coroutineScope.launch {
                scrollState.animateScrollTo(paymentBreakdownY.toInt())
            }
        }
    }

    LaunchedEffect(scrollState.isScrollInProgress) {
        if (!scrollState.isScrollInProgress && showResults && paymentBreakdownY > 0) {
            // don't snap if the user has scrolled past the breakdown
            if (scrollState.value > paymentBreakdownY + 100) return@LaunchedEffect

            val currentPosition = scrollState.value
            val halfwayPoint = paymentBreakdownY / 2

            val destination = if (currentPosition >= halfwayPoint && currentPosition < paymentBreakdownY) {
                paymentBreakdownY.toInt()
            } else {
                -1 // No snapping
            }

            if (destination != -1 && currentPosition != destination) {
                coroutineScope.launch {
                    scrollState.animateScrollTo(destination)
                }
            }
        }
    }

    fun performCalculation() {
        val price = carPrice.toDoubleOrNull() ?: 0.0
        val dpPercent = downPaymentPercentage.toDoubleOrNull() ?: 0.0
        val rate = interestRate.toDoubleOrNull() ?: 0.0

        downPaymentAmount = price * (dpPercent / 100)
        loanAmount = price - downPaymentAmount
        if (loanAmount > 0) {
            totalInterest = loanAmount * (rate / 100) * loanTerm
            totalPayment = loanAmount + totalInterest
            monthlyPayment = totalPayment / (loanTerm * 12)
            showResults = true
            calculationTrigger++
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.DirectionsCar,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.height(24.dp)
                        )
                        Text(
                            text = "Car Loan Calculator",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // --- Header ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
            }
            Spacer(modifier = Modifier.height(32.dp))

            // --- Input Section ---
            NewModernFormSection(title = "Loan Details", icon = Icons.Filled.AttachMoney) {
                InputLabel(text = "CAR PRICE")
                ModernInput(
                    value = carPrice,
                    onValueChange = { carPrice = it.filter(Char::isDigit) },
                    prefix = "RM ",
                    clearOnFocus = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickValueButton(text = "RM 50K", onClick = { carPrice = "50000" }, modifier = Modifier.weight(1f))
                    QuickValueButton(text = "RM 80K", onClick = { carPrice = "80000" }, modifier = Modifier.weight(1f))
                    QuickValueButton(text = "RM 100K", onClick = { carPrice = "100000" }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                InputLabel(text = "DOWN PAYMENT")
                ModernInput(
                    value = downPaymentPercentage,
                    onValueChange = { downPaymentPercentage = it.filter { c -> c.isDigit() || c == '.' } },
                    suffix = "%",
                    clearOnFocus = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(16.dp))
                InputLabel(text = "INTEREST RATE")
                ModernInput(
                    value = interestRate,
                    onValueChange = { interestRate = it.filter { c -> c.isDigit() || c == '.' } },
                    suffix = "%",
                    clearOnFocus = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(16.dp))
                InputLabel(text = "LOAN TERM")
                LoanTermSelector(selectedYears = loanTerm, onYearSelected = { loanTerm = it })
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Calculate Button ---
            Button(
                onClick = { performCalculation() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(56.dp).background(Brush.horizontalGradient(listOf(Color(0xFF0A84FF), Color(0xFF007AFF), Color(0xFF5AC8FA)))), contentAlignment = Alignment.Center) {
                    Text("CALCULATE PAYMENT", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            if (!showResults) {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- Results Section ---
             Column(modifier = Modifier.onGloballyPositioned { layoutCoordinates -> paymentBreakdownY = layoutCoordinates.positionInParent().y }) {
                if (showResults) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.background(Brush.verticalGradient(listOf(Color(0xFF0A84FF), Color(0xFF5AC8FA))))) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("MONTHLY PAYMENT", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.titleMedium)
                                Text("RM ${formatResult(monthlyPayment)}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("For $loanTerm Years", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        InfoCard(label = "LOAN AMOUNT", value = "RM ${formatResult(loanAmount)}", modifier = Modifier.weight(1f))
                        InfoCard(label = "DOWN PAYMENT", value = "RM ${formatResult(downPaymentAmount)}", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        InfoCard(label = "TOTAL INTEREST", value = "RM ${formatResult(totalInterest)}", modifier = Modifier.weight(1f))
                        InfoCard(label = "TOTAL PAYMENT", value = "RM ${formatResult(totalPayment)}", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    NewModernFormSection(title = "Payment Breakdown", icon = null) {
                        PaymentBreakdown(principal = loanAmount, interest = totalInterest)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    TipCard()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// --- Reusable UI Components ---

@Composable
private fun NewModernFormSection(title: String, icon: ImageVector?, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 12.dp)) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isSystemInDarkTheme()) 0.dp else 2.dp, hoveredElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun InputLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernInput(
    value: String,
    onValueChange: (String) -> Unit,
    prefix: String? = null,
    suffix: String? = null,
    clearOnFocus: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var isFocused by remember { mutableStateOf(false) }
    var hasCleared by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    if (clearOnFocus && !hasCleared) {
                        onValueChange("")
                        hasCleared = true
                    }
                } else {
                    hasCleared = false
                    // 👇 当失去焦点且输入为空时，自动填入 "0"
                    if (value.isBlank()) {
                        onValueChange("0")
                    }
                }
                isFocused = focusState.isFocused
            },
        leadingIcon = if (prefix != null) {
            { Text(prefix, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 20.sp) }
        } else null,
        trailingIcon = if (suffix != null) {
            { Text(suffix, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        } else null,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = ThousandsVisualTransformation(),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)
    )
}


@Composable
fun QuickValueButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        val labelLarge = MaterialTheme.typography.labelLarge
        var textStyle by remember(labelLarge) { mutableStateOf(labelLarge) }
        var readyToDraw by remember { mutableStateOf(false) }

        Text(
            text = text,
            style = textStyle,
            fontWeight = FontWeight.SemiBold,
            softWrap = false,
            maxLines = 1,
            modifier = Modifier.drawWithContent {
                if (readyToDraw) drawContent()
            },
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.didOverflowWidth) {
                    textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
                } else {
                    readyToDraw = true
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanTermSelector(selectedYears: Int, onYearSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val items = (1..15).toList()

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = "$selectedYears years (${selectedYears * 12} months)",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select loan term") },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { year ->
                DropdownMenuItem(
                    text = { Text("$year years (${year * 12} months)") },
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun PaymentBreakdown(principal: Double, interest: Double) {
    val total = principal + interest
    if (total <= 0) return

    val principalPercentage = (principal / total).toFloat()
    var animatedPrincipal by remember { mutableStateOf(0f) }

    LaunchedEffect(principalPercentage) {
        animatedPrincipal = principalPercentage
    }

    val principalTarget by animateFloatAsState(targetValue = animatedPrincipal, animationSpec = tween(durationMillis = 1000))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(24.dp).clip(CircleShape).background(Color(0xFFFFA500).copy(alpha = 0.3f))) {
            Box(modifier = Modifier.fillMaxWidth(principalTarget).height(24.dp).clip(CircleShape).background(Color(0xFF0A84FF)))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.height(10.dp).width(10.dp).background(Color(0xFF0A84FF), CircleShape))
                Text("Principal ${String.format("%.1f", principalPercentage * 100)}%", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.height(10.dp).width(10.dp).background(Color(0xFFFFA500), CircleShape))
                Text("Interest ${String.format("%.1f", (1 - principalPercentage) * 100)}%", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
@Composable
fun TipCard() {
    // 固定背景：浅橙色（统一色调）
    val cardBackgroundColor = Color(0xFFFFB74D).copy(alpha = 0.15f)

    // 图标统一橙色
    val iconTint = Color(0xFFFF9800)

    // 文字颜色仍随主题变化（和“CAR PRICE”一致）
    val textColor = MaterialTheme.colorScheme.onSurface

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Lightbulb,
                contentDescription = "Tip",
                tint = iconTint
            )
            Text(
                text = "A larger down payment reduces your monthly payment and total interest paid over the loan term.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}




private fun formatResult(value: Double): String {
    val pattern = if (value % 1.0 == 0.0) "#,##0" else "#,##0.00"
    return DecimalFormat(pattern).format(value)
}
