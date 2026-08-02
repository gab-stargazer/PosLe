package org.lelestacia.posle.util

import kotlin.test.Test
import kotlin.test.assertEquals

class TextParseTest {

    @Test
    fun digitsOnly_keepsDigitsOnly() {
        assertEquals("123", "12a3".digitsOnly())
        assertEquals("123", "abc123xyz".digitsOnly())
    }

    @Test
    fun digitsOnly_stripsSymbolsAndSpaces() {
        assertEquals("1234", "1-2.3, 4".digitsOnly())
        assertEquals("42000", "Rp 42.000".digitsOnly())
    }

    @Test
    fun digitsOnly_emptyInputStaysEmpty() {
        assertEquals("", "".digitsOnly())
        assertEquals("", "abc".digitsOnly())
    }

    @Test
    fun digitsOnly_preservesOrder() {
        assertEquals("908", "9x0y8".digitsOnly())
    }
}
