package org.lelestacia.posle.domain.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.SnackbarHostState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.state_event.ProductAddEditEvent
import org.lelestacia.posle.domain.state_event.ProductAddEditState
import org.lelestacia.posle.navigation.AddEdit
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.msg_error_name_cannot_be_empty
import posle.shared.generated.resources.msg_error_price_cannot_be_empty
import posle.shared.generated.resources.msg_error_price_cannot_contain_alphabet
import posle.shared.generated.resources.msg_error_unit_cannot_be_empty
import java.math.BigDecimal
import org.lelestacia.posle.util.Unit as PosLeUnit

class ProductAddEditComponent(
    componentContext: ComponentContext,
    mode: AddEdit,
    private val onPop: () -> kotlin.Unit,
    private val product: Product?,
    private val snackbarHostState: SnackbarHostState,
    private val repository: ProductRepository
) : ComponentContext by componentContext {

    val scope = CoroutineScope(Dispatchers.Main.immediate)

    val state: Value<ProductAddEditState>
        field = MutableValue(
            ProductAddEditState(
                name = TextFieldState(product?.name?.value.orEmpty()),
                unit = TextFieldState(product?.unit?.value.orEmpty()),
                price = TextFieldState(product?.price?.value?.toString() ?: ""),
                productImageUri = product?.imageUri,
                mode = mode
            )
        )

    fun onEvent(event: ProductAddEditEvent) = scope.launch {
        when (event) {

            is ProductAddEditEvent.OnImageChanged -> state.update {
                it.copy(
                    productImageUri = event.uri,
                    productImageByteArray = event.bytes
                )
            }

            ProductAddEditEvent.OnAddProductClicked -> {
                validate(
                    onSuccess = {
                        when (state.value.mode) {
                            AddEdit.Add -> repository.addProduct(
                                product = buildProduct(id = 0),
                                imageByteArray = state.value.productImageByteArray
                            )

                            AddEdit.Edit -> repository.updateProduct(
                                product = buildProduct(
                                    id = product?.id
                                        ?: throw Exception("Data isn't being passed from previous screen")
                                ),
                                imageByteArray = state.value.productImageByteArray
                            )
                        }
                        onPop()
                    }
                )
            }

            ProductAddEditEvent.OnDeleteProductClicked -> {
                repository.deleteProduct(
                    product = buildProduct(id = product?.id ?: return@launch)
                )
                onPop()
            }
        }
    }

    private fun buildProduct(id: Int): Product {
        val currentState = state.value
        return Product(
            id = id,
            name = Name(currentState.name.text.toString()),
                price = Price(BigDecimal(currentState.price.text.toString())),
            unit = PosLeUnit(currentState.unit.text.toString()),
            imageUri = currentState.productImageUri,
        )
    }

    private suspend fun validate(onSuccess: suspend () -> Unit) {
        val errorMessage = getValidationError() ?: run {
            onSuccess()
            return
        }

        snackbarHostState.showSnackbar(getString(errorMessage))
    }

    private fun getValidationError(): StringResource? {
        val currentState = state.value
        return when {
            currentState.name.text.toString().isBlank() ->
                Res.string.msg_error_name_cannot_be_empty

            currentState.unit.text.toString().isBlank() ->
                Res.string.msg_error_unit_cannot_be_empty

            currentState.price.text.toString().isBlank() ->
                Res.string.msg_error_price_cannot_be_empty

            currentState.price.text.toString().any { it.isLetter() } ->
                Res.string.msg_error_price_cannot_contain_alphabet

            else -> null
        }
    }
}