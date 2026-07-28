package org.lelestacia.posle.util

fun interface PdfNotificationHandler {
    fun onPdfGenerated(fileName: String, success: Boolean)
}
