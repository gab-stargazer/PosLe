package org.lelestacia.posle.data.util

import kotlinx.serialization.Serializable
import org.lelestacia.posle.domain.model.Transaction

@Serializable
data class PdfExportInput(
    val storeName: String,
    val startDate: Long,
    val finishDate: Long,
    val transactions: List<Transaction>
)
