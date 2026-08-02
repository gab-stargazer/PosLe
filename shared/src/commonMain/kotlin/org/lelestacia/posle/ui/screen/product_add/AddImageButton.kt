package org.lelestacia.posle.ui.screen.product_add

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.App
import org.lelestacia.posle.ui.theme.Cerulean
import org.lelestacia.posle.ui.theme.CharcoalBlue
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_image
import posle.shared.generated.resources.msg_info_image_ratio

@Composable
fun ProductAddEditDeleteImageButton(
    isEditMode: Boolean,
    onAddOrChange: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(isEditMode, modifier = modifier.fillMaxWidth()) {
        when (it) {
            true -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onAddOrChange,
                        shape = RoundedCornerShape(25F),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Cerulean,
                            contentColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                        modifier = Modifier.weight(1F)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }

                    Button(
                        onClick = onDelete,
                        shape = RoundedCornerShape(25F),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CharcoalBlue,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        modifier = Modifier
                            .weight(1F)

                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                }
            }

            false -> {
                Column {
                    Button(
                        onClick = onAddOrChange,
                        shape = RoundedCornerShape(25F),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CharcoalBlue,
                            contentColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null
                            )

                            Text(stringResource(Res.string.btn_add_image))
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null
                        )

                        Text(
                            text = stringResource(Res.string.msg_info_image_ratio),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductAddEditDeleteImageButton() {
    App {
        ProductAddEditDeleteImageButton(
            isEditMode = false,
            onAddOrChange = {},
            onDelete = {},
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEditEditDeleteImageButtonProduct() {
    App {
        ProductAddEditDeleteImageButton(
            isEditMode = true,
            onAddOrChange = {},
            onDelete = {},
            modifier = Modifier.padding(12.dp)
        )
    }
}