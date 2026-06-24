package org.lelestacia.posle.util

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
}