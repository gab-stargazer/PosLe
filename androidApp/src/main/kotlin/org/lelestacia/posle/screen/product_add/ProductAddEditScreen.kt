package org.lelestacia.posle.screen.product_add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.ProductAddEditComponent
import org.lelestacia.posle.domain.state_event.ProductAddEditEvent
import org.lelestacia.posle.domain.state_event.ProductAddEditEvent.OnAddProductClicked
import org.lelestacia.posle.domain.state_event.ProductAddEditEvent.OnDeleteProductClicked
import org.lelestacia.posle.domain.state_event.ProductAddEditEvent.OnImageChanged
import org.lelestacia.posle.domain.state_event.ProductAddEditState
import org.lelestacia.posle.navigation.AddEdit.Add
import org.lelestacia.posle.navigation.AddEdit.Edit
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.RupiahOutputTransformation
import org.lelestacia.posle.util.handleImagePick
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_product
import posle.shared.generated.resources.btn_delete_product
import posle.shared.generated.resources.btn_update_product
import posle.shared.generated.resources.label_product_name
import posle.shared.generated.resources.label_product_price
import posle.shared.generated.resources.label_product_unit

@Composable
fun ProductAddEditScreen(
    component: ProductAddEditComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    ProductAddEditUI(
        state = state,
        onEvent = component::onEvent,
        modifier = modifier
    )
}

@Composable
private fun ProductAddEditUI(
    state: ProductAddEditState,
    onEvent: (ProductAddEditEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imagePicker = rememberFilePickerLauncher(
        type = FileKitType.Image
    ) {
        scope.launch {
            context.handleImagePick(
                file = it,
                onPicked = { uri, bytes ->
                    onEvent(OnImageChanged(uri, bytes))
                }
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                state = state.name,
                label = {
                    Text(
                        text = stringResource(Res.string.label_product_name),
                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .padding(horizontal = 12.dp)
            )

            OutlinedTextField(
                state = state.unit,
                label = {
                    Text(
                        text = stringResource(Res.string.label_product_unit),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .padding(horizontal = 12.dp)
            )

            OutlinedTextField(
                state = state.price,
                label = {
                    Text(
                        text = stringResource(Res.string.label_product_price),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                outputTransformation = RupiahOutputTransformation(),
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .padding(horizontal = 12.dp)
            )

            AnimatedVisibility(state.productImageUri != null) {
                AsyncImage(
                    model = state.productImageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(25F))
                )
            }

            AddEditDeleteImageButton(
                isEditMode = state.productImageUri != null,
                onAddOrChange = {
                    imagePicker.launch()
                },
                onDelete = {
                    onEvent(OnImageChanged(null, null))
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            )

            Button(
                onClick = {
                    onEvent(OnAddProductClicked)
                },
                shape = RoundedCornerShape(25F),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 6.dp)
            ) {
                Text(
                    text =
                    when(state.mode) {
                        Add -> stringResource(resource = Res.string.btn_add_product)
                        Edit -> stringResource(resource = Res.string.btn_update_product)
                    },
                    style = MaterialTheme.typography.labelMediumEmphasized.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            if (state.mode == Edit) {
                Button(
                    onClick = {
                        onEvent(OnDeleteProductClicked)
                    },
                    shape = RoundedCornerShape(25F),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 6.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.btn_delete_product),
                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductAddEditUI() {
    AppTheme {
        var state by remember {
            mutableStateOf(ProductAddEditState(mode = Add))
        }

        ProductAddEditUI(
            state = state,
            onEvent = {},
        )
    }
}