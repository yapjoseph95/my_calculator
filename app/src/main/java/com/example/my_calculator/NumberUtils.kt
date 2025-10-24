package com.example.my_calculator

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat

class ThousandsVisualTransformation(private val decimalFormat: DecimalFormat = DecimalFormat("#,###.##########")) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val parts = original.split(".", limit = 2)
        val integerPart = parts[0]
        val decimalPart = parts.getOrElse(1) { "" }

        val formattedInteger = try {
            if (integerPart.isNotEmpty()) {
                decimalFormat.format(integerPart.toLong())
            } else {
                ""
            }
        } catch (e: NumberFormatException) {
            integerPart // Fallback for invalid integer part during typing
        }

        val formattedString = if (original.contains(".")) {
            "$formattedInteger.$decimalPart"
        } else {
            formattedInteger
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val originalPrefix = original.take(offset)
                val originalPrefixParts = originalPrefix.split(".", limit = 2)
                val originalIntegerPrefix = originalPrefixParts[0]
                val originalDecimalPrefix = originalPrefixParts.getOrElse(1) { "" }

                val transformedIntegerPrefixLength = try {
                    if (originalIntegerPrefix.isNotEmpty()) {
                        decimalFormat.format(originalIntegerPrefix.toLong()).length
                    } else {
                        0
                    }
                } catch (e: NumberFormatException) {
                    originalIntegerPrefix.length
                }

                return if (originalPrefix.contains(".")) {
                    transformedIntegerPrefixLength + 1 + originalDecimalPrefix.length
                } else {
                    transformedIntegerPrefixLength
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                val transformedPrefix = formattedString.take(offset)
                val transformedPrefixParts = transformedPrefix.split(".", limit = 2)
                val transformedIntegerPrefix = transformedPrefixParts[0]
                val transformedDecimalPrefix = transformedPrefixParts.getOrElse(1) { "" }

                val originalIntegerPrefixLength = transformedIntegerPrefix.replace(",", "").length

                return if (transformedPrefix.contains(".")) {
                    originalIntegerPrefixLength + 1 + transformedDecimalPrefix.length
                } else {
                    originalIntegerPrefixLength
                }
            }
        }

        return TransformedText(AnnotatedString(formattedString), offsetMapping)
    }
}