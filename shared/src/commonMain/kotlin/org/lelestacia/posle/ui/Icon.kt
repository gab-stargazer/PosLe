package org.lelestacia.posle.ui

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.Icon
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.sp

val icon = mapOf(
    "arrow" to InlineTextContent(
        Placeholder(20.sp, 20.sp, PlaceholderVerticalAlign.Center)
    ) {
        Icon(Icons.Default.SubdirectoryArrowRight, contentDescription = null)
    }
)