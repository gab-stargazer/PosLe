package org.lelestacia.posle.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.skydoves.compose.stability.runtime.TraceRecomposition

@TraceRecomposition
@Composable
fun AnimatedIcon(
    condition: Boolean,
    onTrue: ImageVector,
    onFalse: ImageVector,
) {
    AnimatedContent(condition) { onCondition ->
        when (onCondition) {
            true -> {
                Icon(
                    imageVector = onTrue,
                    contentDescription = null
                )
            }

            false -> {
                Icon(
                    imageVector = onFalse,
                    contentDescription = null
                )
            }
        }
    }
}