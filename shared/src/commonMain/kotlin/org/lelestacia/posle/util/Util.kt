package org.lelestacia.posle.util

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.PagingConfig

object Util {
    val pagingConfig = PagingConfig(
        pageSize = 15,
        prefetchDistance = 10,
        initialLoadSize = 30
    )

    val GridItemHeight = 128.dp
    val GridItemSpacing = 12.dp

    @Composable
    fun defaultTextFieldColor() = TextFieldDefaults.colors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    val defaultShape = RoundedCornerShape(25F)
}