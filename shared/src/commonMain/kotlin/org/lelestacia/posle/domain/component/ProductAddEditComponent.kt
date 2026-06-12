package org.lelestacia.posle.domain.component

import androidx.compose.foundation.text.input.TextFieldState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.state_event.ProductAddEditEvent
import org.lelestacia.posle.domain.state_event.ProductAddEditState
import org.lelestacia.posle.navigation.AddEdit
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit
import java.math.BigDecimal

class ProductAddEditComponent(
    componentContext: ComponentContext,
    mode: AddEdit,
    private val onPop: () -> kotlin.Unit,
    private val product: Product?,
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
                isProductVolatile = product?.isProductVolatile ?: false,
                mode = mode
            )
        )

    fun onEvent(event: ProductAddEditEvent) = scope.launch {
        when (event) {
            is ProductAddEditEvent.OnToggleProductVolatility -> state.update {
                it.copy(
                    isProductVolatile = event.newToggle
                )
            }

            ProductAddEditEvent.OnAddProductClicked -> {
                when (state.value.mode) {
                    AddEdit.Add -> {
                        repository.addProduct(
                            Product(
                                id = 0,
                                name = Name(state.value.name.text.toString()),
                                price = Price(BigDecimal(state.value.price.text.toString())),
                                unit = Unit(state.value.unit.text.toString()),
                                imageUri = state.value.productImageUri,
                                isProductVolatile = state.value.isProductVolatile
                            ),
                            imageByteArray = state.value.productImageByteArray
                        ).run {
                            onPop()
                        }
                    }

                    AddEdit.Edit -> {
                        repository.updateProduct(
                            Product(
                                id = product?.id ?: throw Exception("Data isn't being passed from previous screen"),
                                name = Name(state.value.name.text.toString()),
                                price = Price(BigDecimal(state.value.price.text.toString())),
                                unit = Unit(state.value.unit.text.toString()),
                                imageUri = state.value.productImageUri,
                                isProductVolatile = state.value.isProductVolatile
                            ),
                            imageByteArray = state.value.productImageByteArray
                        ).run {
                            onPop()
                        }
                    }
                }
            }

            is ProductAddEditEvent.OnImageChanged -> state.update { currentState ->
                currentState.copy(
                    productImageUri = event.uri,
                    productImageByteArray = event.bytes
                )
            }

            ProductAddEditEvent.OnDeleteProductClicked -> {
                repository.deleteProduct(
                    Product(
                        id = product?.id ?: return@launch,
                        name = Name(state.value.name.text.toString()),
                        price = Price(BigDecimal(state.value.price.text.toString())),
                        unit = Unit(state.value.unit.text.toString()),
                        imageUri = state.value.productImageUri,
                        isProductVolatile = state.value.isProductVolatile
                    )
                ).run {
                    onPop()
                }
            }
        }
    }
}