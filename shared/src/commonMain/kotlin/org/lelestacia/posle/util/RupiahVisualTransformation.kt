package org.lelestacia.posle.util

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.insert

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