package org.lelestacia.posle.ui.screen.product_add

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.item_variant_menu_delete
import posle.shared.generated.resources.item_variant_menu_edit
import posle.shared.generated.resources.item_variant_name
import posle.shared.generated.resources.item_variant_price
import java.math.BigDecimal

@Composable
fun VariantViewItemAdd(
    variant: Variant,
    isSelected: Boolean,
    isEnableContextMenu: Boolean,
    onCheckedChange: (Variant, Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    var height by remember {
        mutableIntStateOf(0)
    }

    var menuOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .onPlaced {
                height = it.size.height
            }
            .indication(interactionSource, ripple())
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        menuOffset = offset
                        isExpanded = true
                    },
                    onPress = {
                        val press = PressInteraction.Press(it)
                        interactionSource.tryEmit(press)
                        tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(press))
                    }
                )
            }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        if (isEnableContextMenu) {
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = {
                    isExpanded = false
                },
                offset = with(LocalDensity.current) {
                    DpOffset(
                        x = menuOffset.x.toDp(),
                        y = (menuOffset.y - height).toDp()
                    )
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Text(
                                text = stringResource(resource = Res.string.item_variant_menu_edit),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    },
                    onClick = {
                        onEdit()
                        isExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Text(
                                text = stringResource(resource = Res.string.item_variant_menu_delete),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    },
                    onClick = {
                        onDelete()
                        isExpanded = false
                    }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    stringResource(Res.string.item_variant_name, variant.name.value),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    stringResource(
                        Res.string.item_variant_price,
                        variant.priceAdjustment.value.toRupiah()
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Checkbox(
                checked = isSelected,
                onCheckedChange = { isChecked ->
                    onCheckedChange(variant, isChecked)
                }
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
                id = "0",
                name = Name("Karung"),
                priceAdjustment = Price(BigDecimal.ZERO)
            ),
            isSelected = true,
            isEnableContextMenu = true,
            onCheckedChange = { _, _ -> },
            onEdit = {},
            onDelete = {}
        )
    }
}