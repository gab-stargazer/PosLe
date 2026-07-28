package org.lelestacia.posle.data.util

import kotlinx.serialization.Serializable

@Serializable
data class PdfExportInput(
    val storeName: String,
    val startDate: Long,
    val finishDate: Long
)
