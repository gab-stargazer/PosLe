package org.lelestacia.posle.util

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.insert
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class RupiahOutputTransformation : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        val digits = toString().filter { it.isDigit() }
        if (digits.isEmpty()) return
        insert(0, "Rp ")
        val digitStart = 3
        val len = digits.length
        var dotPosition = len % 3
        if (dotPosition == 0) dotPosition = 3

        var insertAt = digitStart + dotPosition
        while (insertAt < length) {
            insert(insertAt, ".")
            insertAt += 4
        }
    }
}

class RupiahVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }

        if (digits.isEmpty()) {
            return TransformedText(
                AnnotatedString(""),
                OffsetMapping.Identity
            )
        }

        val formatted = buildString {
            append("Rp ")
            val firstGroup = digits.length % 3
            val initial = if (firstGroup == 0) 3 else firstGroup
            append(digits.substring(0, initial))
            var index = initial
            while (index < digits.length) {
                append('.')
                append(digits.substring(index, minOf(index + 3, digits.length)))
                index += 3
            }
        }

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                var transformed = offset + 3
                if (offset == 0) return 3
                transformed += (offset - 1) / 3
                return transformed
            }

            override fun transformedToOriginal(offset: Int): Int {
                val transformed = offset - 3
                if (transformed <= 0) return 0
                val dotsBefore = transformed / 4
                return (transformed - dotsBefore)
                    .coerceIn(0, digits.length)
            }
        }

        return TransformedText(
            AnnotatedString(formatted),
            offsetMapping
        )
    }
}