package org.lelestacia.posle.screen.product_add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.toRupiah
import java.math.BigDecimal

@Composable
fun VariantViewItemAdd(
    variant: Variant,
    isSelected: Boolean,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1F)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )

            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    "Nama Variasi: ${variant.name.value}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    "Harga Tambahan: ${variant.priceAdjustment.value.toRupiah()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        AnimatedVisibility(!isSelected) {
            IconButton(
                onClick = onAdd
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}

@Composable
fun VariantViewItemRemove(
    variant: Variant,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1F)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )

            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    "Nama Variasi: ${variant.name.value}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    "Harga Tambahan: ${variant.priceAdjustment.value.toRupiah()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        IconButton(
            onClick = onRemove
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.errorContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewVariantItemAdd() {
    AppTheme {
        VariantViewItemAdd(
            variant = Variant(
                id = 0,
                name = Name("Karung"),
                priceAdjustment = Price(BigDecimal.ZERO)
            ),
            isSelected = true,
            onAdd = {},
        )
    }
}