package org.lelestacia.posle.ui.screen.qr_scanner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.lelestacia.posle.ui.theme.BurgundyRed

@Composable
fun QrScannerOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Semi-transparent background with a hole in the middle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val rectSize = canvasWidth * 0.7f
            val rectTop = (canvasHeight - rectSize) / 2
            val rectLeft = (canvasWidth - rectSize) / 2
            val rectRight = rectLeft + rectSize
            val rectBottom = rectTop + rectSize

            val rectPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(rectLeft, rectTop, rectRight, rectBottom),
                        cornerRadius = CornerRadius(24.dp.toPx())
                    )
                )
            }

            clipPath(rectPath, clipOp = ClipOp.Difference) {
                drawRect(Color.Black.copy(alpha = 0.7f))
            }

            // Reticle Border
            drawRoundRect(
                color = BurgundyRed,
                topLeft = Offset(rectLeft, rectTop),
                size = Size(rectSize, rectSize),
                cornerRadius = CornerRadius(24.dp.toPx()),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // Top Content
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scanning...",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Align barcode within the frame",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Close Button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}
