package org.lelestacia.posle.domain.component.product_add_edit

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.SnackbarHostState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.VariantRepository
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.Navigation
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.Navigation.OnPop
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnAddProductClicked
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnDeleteProductClicked
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnImageChanged
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnVariantSelected
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditNavigation
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditState
import org.lelestacia.posle.navigation.AddEdit
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.coroutineScope
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
    private val navigation: ProductAddEditNavigation,
    private val product: Product?,
    private val snackbarHostState: SnackbarHostState,
    private val productRepository: ProductRepository,
    private val variantRepository: VariantRepository,
) : ComponentContext by componentContext {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    init {
        if (product != null) {
            scope.launch {
                variantRepository
                    .readVariantByProductId(product.id)
                    .collectLatest { variants ->
                        state.update {
                            it.copy(
                                variants = variants
                            )
                        }
                    }
            }
        }
    }

    val state: Value<ProductAddEditState>
        field = MutableValue(
            ProductAddEditState(
                name = TextFieldState(product?.name?.value.orEmpty()),
                unit = TextFieldState(product?.unit?.value.orEmpty()),
                price = TextFieldState(product?.price?.value?.toString() ?: ""),
                variants = product?.variants ?: emptyList(),
                productImageUri = product?.imageUri,
                mode = mode
            )
        )

    fun onEvent(event: ProductAddEditEvent) {
        when (event) {

            is OnImageChanged -> state.update {
                it.copy(
                    productImageUri = event.uri,
                    productImageByteArray = event.bytes
                )
            }

            OnAddProductClicked -> {
                scope.launch {
                    validate(
                        onSuccess = {
                            when (state.value.mode) {
                                AddEdit.Add -> productRepository.addProduct(
                                    product = buildProduct(id = 0),
                                    imageByteArray = state.value.productImageByteArray
                                )

                                AddEdit.Edit -> {
                                    val original: Map<Int, Variant> = product
                                        ?.variants
                                        ?.associateBy { it.id }
                                        ?: return@validate

                                    val modified = state
                                        .value
                                        .variants
                                        .associateBy { it.id }

                                    val variantsToAdd =
                                        modified.filter { it.key !in original }.map { it.value }
                                    val variantsToRemove =
                                        original.filter { it.key !in modified }.map { it.value }

                                    productRepository.updateProduct(
                                        product = buildProduct(id = product.id),
                                        variantsToAdd = variantsToAdd.toList(),
                                        variantsToRemove = variantsToRemove.toList(),
                                        imageByteArray = state.value.productImageByteArray
                                    )
                                }
                            }

                            onNavigationEvent(OnPop)
                        }
                    )
                }
            }

            is OnVariantSelected -> {
                val variants = state.value.variants.toMutableList()
                variants.removeAll(state.value.variants)
                variants.addAll(event.variants)

                state.update { currentState ->
                    currentState.copy(
                        variants = variants.distinctBy { it.id }
                    )
                }
            }

            OnDeleteProductClicked -> {
                scope.launch {
                    productRepository.deleteProduct(
                        product = buildProduct(id = product?.id ?: return@launch)
                    )

                    onNavigationEvent(OnPop)
                }
            }

            is Navigation -> onNavigationEvent(event)
        }
    }

    private fun onNavigationEvent(event: Navigation) {
        when (event) {
            is Navigation.OnNavigateToVariantView -> {
                navigation.onNavigateToVariantSelection(
                    config = event.config,
                    onResult = { newlySelectedVariant ->
                        onEvent(OnVariantSelected(newlySelectedVariant))
                    }
                )
            }

            OnPop -> {
                navigation.onPop()
            }
        }
    }

    private fun buildProduct(id: Int): Product {
        val currentState = state.value
        return Product(
            id = id,
            name = Name(currentState.name.text.toString()),
            price = Price(BigDecimal(currentState.price.text.toString())),
            stock = Amount(0F),
            unit = PosLeUnit(currentState.unit.text.toString()),
            imageUri = currentState.productImageUri,
            variants = currentState.variants
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