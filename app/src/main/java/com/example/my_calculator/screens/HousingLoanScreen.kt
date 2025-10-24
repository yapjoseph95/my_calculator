package com.example.my_calculator.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.my_calculator.ThousandsVisualTransformation
import java.text.DecimalFormat
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HousingLoanScreen(navController: NavController) {
    var propertyPrice by remember { mutableStateOf("500000") }
    var downPayment by remember { mutableStateOf("10") }
    var interestRate by remember { mutableStateOf("4.0") }
    var loanYears by remember { mutableStateOf(35) }
    var isDownPaymentPercentage by remember { mutableStateOf(true) }

    var monthlyInstallment by remember { mutableStateOf(0.0) }
    var totalInterest by remember { mutableStateOf(0.0) }
    var totalPayment by remember { mutableStateOf(0.0) }
    var actualDownPayment by remember { mutableStateOf(0.0) }
    var showResults by remember { mutableStateOf(false) }

    val accentColor = Color(0xFF0A84FF)
    val focusManager = LocalFocusManager.current

    fun calculate() {
        val price = propertyPrice.replace(",", "").toDoubleOrNull() ?: 0.0
        val rate = interestRate.toDoubleOrNull() ?: 0.0
        val years = loanYears
        val down = downPayment.toDoubleOrNull() ?: 0.0

        val dp = if (isDownPaymentPercentage) price * (down / 100) else down
        val loanAmount = price - dp

        if (loanAmount <= 0 || rate <= 0 || years <= 0) {
            showResults = false
            return
        }

        val monthlyRate = rate / 100 / 12
        val n = years * 12
        val monthly = (loanAmount * monthlyRate) / (1 - (1 + monthlyRate).pow(-n))
        val totalPay = monthly * n
        val totalInt = totalPay - loanAmount

        actualDownPayment = dp
        monthlyInstallment = monthly
        totalPayment = totalPay
        totalInterest = totalInt
        showResults = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Home, contentDescription = null, tint = accentColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Housing Loan Calculator", style = MaterialTheme.typography.titleLarge)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT SECTION (uses local section composable) ---
            ModernFormSectionLocal(title = "Loan Details", icon = Icons.Filled.CreditCard) {
                LoanInputLabel("PROPERTY PRICE")
                ModernInput(
                    value = propertyPrice,
                    onValueChange = { propertyPrice = it.filter(Char::isDigit) },
                    prefix = "RM ",
                    clearOnFocus = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() }),

                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        LoanInputLabel(if (isDownPaymentPercentage) "DOWN PAYMENT (%)" else "DOWN PAYMENT (RM)")
                        ModernInput(
                            value = downPayment,
                            onValueChange = { downPayment = it.filter { c -> c.isDigit() || c == '.' } },
                            suffix = if (isDownPaymentPercentage) "%" else "",
                            prefix = if (!isDownPaymentPercentage) "RM " else null,
                            clearOnFocus = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),

                        )
                    }
                    // aligned with text field height
                    ElevatedButton(
                        onClick = { isDownPaymentPercentage = !isDownPaymentPercentage },
                        modifier = Modifier
                            .height(56.dp)
                            .align(Alignment.Bottom),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(if (isDownPaymentPercentage) "RM" else "%", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                LoanInputLabel("INTEREST RATE (%)")
                ModernInput(
                    value = interestRate,
                    onValueChange = { interestRate = it.filter { c -> c.isDigit() || c == '.' } },
                    suffix = "%",
                    clearOnFocus = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(12.dp))
                LoanInputLabel("LOAN TERM")

                // Use existing LoanTermSelector in project (it will show up to 35 if it's implemented that way).
                LoanTermSelector(
                    selectedYears = loanYears,
                    onYearSelected = { loanYears = it }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- CALCULATE BUTTON ---
            Button(
                onClick = { calculate() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0A84FF), Color(0xFF007AFF), Color(0xFF5AC8FA))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("CALCULATE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // --- RESULTS SECTION ---
            AnimatedVisibility(visible = showResults) {
                Column(
                    modifier = Modifier.padding(top = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoCard(label = "MONTHLY PAYMENT", value = "RM ${formatResult(monthlyInstallment)}")
                    InfoCard(label = "DOWN PAYMENT", value = "RM ${formatResult(actualDownPayment)}")

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        InfoCard(label = "TOTAL INTEREST", value = "RM ${formatResult(totalInterest)}", modifier = Modifier.weight(1f))
                        InfoCard(label = "TOTAL PAYMENT", value = "RM ${formatResult(totalPayment)}", modifier = Modifier.weight(1f))
                    }

                    PaymentBreakdownBar(
                        principal = totalPayment - totalInterest,
                        interest = totalInterest
                    )

                    TipCard()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Local section composable — named uniquely to avoid conflict with other files.
 * Mirrors structure/visual of your project's NewModernFormSection but keeps scope local to this file.
 */
@Composable
fun ModernFormSectionLocal(title: String, icon: ImageVector?, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            if (icon != null) Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) { content() }
        }
    }
}

/** small label used above inputs */
@Composable
fun LoanInputLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

/** Payment breakdown bar */
@Composable
private fun PaymentBreakdownBar(principal: Double, interest: Double) {
    val total = principal + interest
    val principalPercent = if (total > 0) (principal / total * 100).toFloat() else 0f
    val interestPercent = if (total > 0) (interest / total * 100).toFloat() else 0f

    Column {
        Text("Payment Breakdown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = principalPercent / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = Color(0xFF0A84FF),
            trackColor = Color(0xFFFF4D4D)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Principal ${formatResult(principal)} (${String.format("%.1f", principalPercent)}%) | Interest ${formatResult(interest)} (${String.format("%.1f", interestPercent)}%)",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun formatResult(value: Double): String {
    val pattern = if (value % 1.0 == 0.0) "#,##0" else "#,##0.00"
    return DecimalFormat(pattern).format(value)
}
