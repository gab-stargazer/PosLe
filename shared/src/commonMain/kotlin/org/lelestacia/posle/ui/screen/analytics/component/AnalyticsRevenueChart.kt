package org.lelestacia.posle.ui.screen.analytics.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.line.LinePlot2
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.DoubleLinearAxisModel
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.analytics.TimeSeriesPoint
import org.lelestacia.posle.ui.theme.BurgundyRed
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_no_data
import posle.shared.generated.resources.label_revenue_trend
import kotlin.math.max

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun AnalyticsRevenueChart(
    timeSeries: List<TimeSeriesPoint>,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.label_revenue_trend),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (timeSeries.isEmpty()) {
                Text(
                    text = stringResource(Res.string.label_no_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                // X-axis: day index 0..n-1; Y-axis: revenue in Rp.
                // Using a linear X axis with a custom label lambda gives KoalaPlot's
                // auto tick-spacing, which thins the labels so they never overlap.
                val pointCount = timeSeries.size
                val xRange = 0.0..(pointCount - 1).toDouble().coerceAtLeast(1.0)
                val points = timeSeries.mapIndexed { index, point ->
                    Point(
                        x = index.toDouble(),
                        y = point.revenue.toDouble(),
                    )
                }
                val maxRevenue = timeSeries.maxOf { it.revenue }.toDouble().coerceAtLeast(1.0)
                val yRange = 0.0..(maxRevenue * 1.1)

                XYGraph<Double, Double>(
                    xAxisModel = DoubleLinearAxisModel(range = xRange),
                    yAxisModel = DoubleLinearAxisModel(range = yRange),
                    xAxisContent = AxisContent(
                        style = rememberAxisStyle(),
                        labels = { dayIndex: Double ->
                            val index = dayIndex.toInt().coerceIn(0, pointCount - 1)
                            val label = timeSeries[index].date
                            Text(
                                text = "${label.day} ${label.month.name.lowercase().take(3)}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        title = {},
                    ),
                    yAxisContent = AxisContent(
                        style = rememberAxisStyle(),
                        labels = { value: Double ->
                            Text(
                                text = compactRp(value),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        title = {},
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    LinePlot2(
                        data = points,
                        lineStyle = LineStyle(
                            brush = SolidColor(BurgundyRed),
                            strokeWidth = 2.dp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Formats a rupiah value compactly for axis labels, e.g. 150000 -> "150rb".
 */
private fun compactRp(value: Double): String {
    val absolute = kotlin.math.abs(value)
    return when {
        absolute >= 1_000_000 -> {
            val juta = value / 1_000_000
            if (juta == juta.toLong().toDouble()) "${juta.toLong()}jt" else "${"%.1f".format(juta)}jt"
        }
        absolute >= 1_000 -> {
            val ribu = value / 1_000
            if (ribu == ribu.toLong().toDouble()) "${ribu.toLong()}rb" else "${"%.1f".format(ribu)}rb"
        }
        else -> value.toInt().toString()
    }
}
