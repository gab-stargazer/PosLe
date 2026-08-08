package org.lelestacia.posle.data.util

import kotlinx.serialization.Serializable

@Serializable
data class ProductImportInput(
    val filePath: String,
    val title: String
)
