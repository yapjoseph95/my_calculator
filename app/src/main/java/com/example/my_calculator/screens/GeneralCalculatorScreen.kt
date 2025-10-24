package com.example.my_calculator.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.DecimalFormat

private sealed class CalculatorAction {
    data class Number(val value: Int) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Decimal : CalculatorAction()
    object Calculate : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction()
    object Percentage : CalculatorAction()
}

private sealed class CalculatorOperation(val symbol: String) {
    object Add : CalculatorOperation("+")
    object Subtract : CalculatorOperation("-")
    object Multiply : CalculatorOperation("×")
    object Divide : CalculatorOperation("÷")
}

private data class CalculatorState(
    val number1: String = "",
    val number2: String = "",
    val operation: CalculatorOperation? = null,
    val equation: String = "",
    val lastOperation: CalculatorOperation? = null,
    val lastOperand: String = ""
) {
    val display: String
        get() {
            val currentNumber = if (operation == null) number1 else number2
            return if (currentNumber.isNotEmpty()) currentNumber else "0"
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralCalculatorScreen(navController: NavController) {
    var state by remember { mutableStateOf(CalculatorState()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("General Calculator") },
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
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            // Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = state.equation,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.End,
                    maxLines = 3,
                    lineHeight = 40.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                val formattedDisplay = formatNumber(state.display)
                val displayFontSize = when (formattedDisplay.length) {
                    in 0..6 -> 80.sp
                    in 7..9 -> 60.sp
                    in 10..15 -> 40.sp
                    else -> 20.sp
                }

                Text(
                    text = formattedDisplay,
                    fontSize = displayFontSize,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    lineHeight = displayFontSize * 1.2
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Buttons
            CalculatorButtons(state = state) {
                state = performCalculatorAction(it, state)
            }
        }
    }
}

@Composable
private fun CalculatorButtons(state: CalculatorState, onAction: (CalculatorAction) -> Unit) {
    val buttonSpacing = 12.dp

    val operatorColor = Color(0xFFFFA500)
    val specialFunctionColor = Color(0xFFD1D3D4)
    val numberColor = Color(0xFFF1F3F5)
    val equalColor = Color(0xFF4285F4)

    Column(verticalArrangement = Arrangement.spacedBy(buttonSpacing)) {
        Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalculatorButton(symbol = "AC", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Clear) }, color = specialFunctionColor, textColor = operatorColor, fontSize = 28.sp)
            CalculatorButton(icon = Icons.Default.Backspace, modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Delete) }, color = specialFunctionColor)
            CalculatorButton(symbol = "%", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Percentage) }, color = specialFunctionColor, textColor = operatorColor)
            CalculatorButton(symbol = "÷", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Operation(CalculatorOperation.Divide)) }, isSelected = state.operation == CalculatorOperation.Divide, color = operatorColor)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalculatorButton(symbol = "7", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Number(7)) }, color = numberColor)
            CalculatorButton(symbol = "8", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Number(8)) }, color = numberColor)
            CalculatorButton(symbol = "9", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Number(9)) }, color = numberColor)
            CalculatorButton(symbol = "×", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Operation(CalculatorOperation.Multiply)) }, isSelected = state.operation == CalculatorOperation.Multiply, color = operatorColor)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalculatorButton(symbol = "4", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Number(4)) }, color = numberColor)
            CalculatorButton(symbol = "5", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Number(5)) }, color = numberColor)
            CalculatorButton(symbol = "6", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Number(6)) }, color = numberColor)
            CalculatorButton(symbol = "-", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Operation(CalculatorOperation.Subtract)) }, isSelected = state.operation == CalculatorOperation.Subtract, color = operatorColor)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalculatorButton(symbol = "1", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Number(1)) }, color = numberColor)
            CalculatorButton(symbol = "2", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Number(2)) }, color = numberColor)
            CalculatorButton(symbol = "3", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Number(3)) }, color = numberColor)
            CalculatorButton(symbol = "+", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Operation(CalculatorOperation.Add)) }, isSelected = state.operation == CalculatorOperation.Add, color = operatorColor)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalculatorButton(symbol = "0", modifier = Modifier.weight(2f), onAction = { onAction(CalculatorAction.Number(0)) }, color = numberColor)
            CalculatorButton(symbol = ".", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Decimal) }, color = numberColor)
            CalculatorButton(symbol = "=", modifier = Modifier.weight(1f), onAction = { onAction(CalculatorAction.Calculate) }, color = equalColor)
        }
    }
}

@Composable
private fun CalculatorButton(
    modifier: Modifier = Modifier,
    onAction: () -> Unit,
    symbol: String? = null,
    icon: ImageVector? = null,
    color: Color = Color.White,
    textColor: Color = Color.Black,
    isSelected: Boolean = false,
    fontSize: TextUnit = 36.sp
) {
    val animatedColor by animateColorAsState(if (isSelected) Color.White else color)
    val animatedTextColor by animateColorAsState(if (isSelected) color else textColor)

    Box(
        modifier = modifier
            .aspectRatio(if (symbol == "0") 2f else 1f)
            .clip(CircleShape)
            .background(animatedColor)
            .clickable { onAction() },
        contentAlignment = Alignment.Center
    ) {
        if (symbol != null) {
            Text(text = symbol, fontSize = fontSize, color = animatedTextColor, fontWeight = FontWeight.Medium)
        } else if (icon != null) {
            Icon(icon, contentDescription = null, tint = animatedTextColor)
        }
    }
}

private fun formatNumber(number: String): String {
    if (number.isBlank() || number == "-") return "0"
    if (number.endsWith(".")) return "${formatNumber(number.dropLast(1))}."
    val parts = number.split('.')
    val integer = parts[0]
    val decimal = if (parts.size > 1) ".${parts[1]}" else ""
    val formatter = DecimalFormat("#,###")
    return try {
        formatter.format(integer.toLong()) + decimal
    } catch (e: NumberFormatException) {
        integer + decimal
    }
}

private fun formatResult(result: Double): String {
    if (result.isNaN() || result.isInfinite()) return "Error"
    val formatter = if (result % 1.0 == 0.0) {
        DecimalFormat("#,###")
    } else {
        DecimalFormat("#,###.##########")
    }
    return formatter.format(result)
}

private fun performCalculatorAction(action: CalculatorAction, state: CalculatorState): CalculatorState {
    return when (action) {
        is CalculatorAction.Number -> {
            if (state.operation == null && state.equation.contains("=")) {
                return CalculatorState(number1 = action.value.toString())
            }
            val currentNumber = if (state.operation == null) state.number1 else state.number2
            if (currentNumber.length >= 100) return state

            return if (state.operation == null) {
                state.copy(number1 = state.number1 + action.value)
            } else {
                state.copy(number2 = state.number2 + action.value)
            }
        }
        is CalculatorAction.Decimal -> {
            if (state.operation == null) {
                if (!state.number1.contains(".")) return state.copy(number1 = state.number1 + ".")
            } else {
                if (!state.number2.contains(".")) return state.copy(number2 = state.number2 + ".")
            }
            return state
        }
        is CalculatorAction.Clear -> return CalculatorState()
        is CalculatorAction.Operation -> {
            if (state.number1.isNotBlank() && state.number2.isNotBlank()) {
                val tempState = performCalculatorAction(CalculatorAction.Calculate, state)
                return tempState.copy(operation = action.operation, equation = "${tempState.number1} ${action.operation.symbol}")
            }
            if (state.number1.isNotBlank()) {
                return state.copy(operation = action.operation, equation = "${formatNumber(state.number1)} ${action.operation.symbol}")
            }
            return state
        }
        is CalculatorAction.Calculate -> {
            val n1 = state.number1.replace(",", "").toDoubleOrNull()
            if (n1 == null) return state

            val n2: Double
            val op: CalculatorOperation?
            val num2String: String

            if (state.number2.isNotBlank() && state.operation != null) {
                n2 = state.number2.replace(",", "").toDoubleOrNull() ?: return state
                op = state.operation
                num2String = state.number2
            } else {
                if (state.lastOperand.isNotBlank() && state.lastOperation != null) {
                    n2 = state.lastOperand.replace(",", "").toDoubleOrNull() ?: return state
                    op = state.lastOperation
                    num2String = state.lastOperand
                } else if (state.operation != null) {
                    n2 = n1
                    op = state.operation
                    num2String = state.number1
                } else {
                    return state
                }
            }

            val result = when (op) {
                CalculatorOperation.Add -> n1 + n2
                CalculatorOperation.Subtract -> n1 - n2
                CalculatorOperation.Multiply -> n1 * n2
                CalculatorOperation.Divide -> if (n2 != 0.0) n1 / n2 else Double.NaN
            }

            val resultString = formatResult(result)
            val originalNum1Formatted = formatNumber(state.number1)
            val num2Formatted = formatNumber(num2String)

            return CalculatorState(
                number1 = resultString,
                equation = "$originalNum1Formatted ${op.symbol} $num2Formatted =",
                lastOperation = op,
                lastOperand = num2String
            )
        }
        is CalculatorAction.Delete -> {
            return when {
                state.number2.isNotEmpty() -> state.copy(number2 = state.number2.dropLast(1))
                state.operation != null -> state.copy(operation = null, equation = "")
                state.number1.isNotEmpty() -> state.copy(number1 = state.number1.dropLast(1))
                else -> state
            }
        }
        is CalculatorAction.Percentage -> {
            return if (state.number2.isNotBlank()) {
                state.copy(number2 = (state.number2.toDouble() / 100).toString())
            } else if (state.number1.isNotBlank()) {
                state.copy(number1 = (state.number1.toDouble() / 100).toString())
            } else state
        }
    }
}
