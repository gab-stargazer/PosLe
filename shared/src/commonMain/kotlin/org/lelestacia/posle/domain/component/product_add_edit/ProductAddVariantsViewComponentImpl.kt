package org.lelestacia.posle.domain.component.product_add_edit

import androidx.compose.foundation.text.input.TextFieldState
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.repository.VariantRepository
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent.OnVariantDialogDismissed
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewState
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewState.VariantAddEditState
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.UuidProvider
import org.lelestacia.posle.util.coroutineScope
import java.math.BigDecimal

class ProductAddVariantsViewComponentImpl(
    componentContext: ComponentContext,
    initialSelectedVariants: List<Variant> = emptyList(),
    private val onVariantsConfirmed: (List<Variant>) -> Unit,
    private val onPop: () -> Unit,
    private val repository: VariantRepository
) : ComponentContext by componentContext, ProductAddVariantsViewComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    private val variants = repository
        .getAllVariants()
        .cachedIn(scope)

    override val state: Value<ProductVariantViewState>
        field = MutableValue(
            ProductVariantViewState(
                selectedVariants = initialSelectedVariants,

                variants = variants
            )
        )

    override fun onEvent(event: ProductVariantViewEvent) {
        when (event) {
            ProductVariantViewEvent.OnConfirmed -> {
                onVariantsConfirmed(state.value.selectedVariants)
            }

            is ProductVariantViewEvent.OnCheckedChange -> {
                when (event.isChecked) {
                    true -> {
                        val selectedVariants = state.value.selectedVariants.toMutableList()
                        selectedVariants.add(event.variant)
                        state.update { currentState ->
                            currentState.copy(
                                selectedVariants = selectedVariants
                            )
                        }
                    }

                    false -> {
                        val selectedVariants = state.value.selectedVariants.toMutableList()
                        selectedVariants.remove(event.variant)
                        state.update { currentState ->
                            currentState.copy(
                                selectedVariants = selectedVariants
                            )
                        }
                    }
                }
            }


            ProductVariantViewEvent.OnCancel -> {
                onPop()
            }

            is ProductVariantViewEvent.OnAddVariant -> {
                state.update { currentState ->
                    currentState.copy(
                        addEditVariantDialogState = VariantAddEditState(),
                        isAddEditVariantDialogShown = true
                    )
                }
            }

            is ProductVariantViewEvent.OnEditVariant -> {
                state.update { currentState ->
                    currentState.copy(
                        addEditVariantDialogState = VariantAddEditState(
                            selectedVariant = event.variant,
                            variantNameState = TextFieldState(event.variant.name.value),
                            variantPriceState = TextFieldState(event.variant.priceAdjustment.value.toString())
                        ),
                        isAddEditVariantDialogShown = true
                    )
                }
            }

            OnVariantDialogDismissed -> {
                state.update { currentState ->
                    currentState.copy(
                        addEditVariantDialogState = VariantAddEditState(),
                        isAddEditVariantDialogShown = false
                    )
                }
            }

            ProductVariantViewEvent.OnSaveVariant -> {
                when (state.value.addEditVariantDialogState.selectedVariant == null) {
                    true -> {
                        val newVariant = Variant(
                            id = UuidProvider.newUuid(),
                            name = Name(state.value.addEditVariantDialogState.variantNameState.text.toString()),
                            priceAdjustment = Price(
                                state.value.addEditVariantDialogState.variantPriceState.text
                                    .toString()
                                    .ifEmpty { "0" }
                                    .toBigDecimal()
                            )
                        )

                        if (newVariant.name.value.isBlank()) {

                            return
                        }

                        scope.launch {
                            repository.createVariant(newVariant)
                        }

                        onEvent(OnVariantDialogDismissed)
                    }

                    false -> {
                        // Edit
                        val updatedVariant = Variant(
                            id = state.value.addEditVariantDialogState.selectedVariant?.id
                                ?: return,
                            name = Name(state.value.addEditVariantDialogState.variantNameState.text.toString()),
                            priceAdjustment = Price(
                                state.value.addEditVariantDialogState.variantPriceState.text
                                    .toString()
                                    .ifEmpty { "0" }
                                    .toBigDecimal()
                            )
                        )

                        if (updatedVariant.name.value.isBlank()) {

                            return
                        }

                        if (updatedVariant.priceAdjustment.value <= BigDecimal.ZERO) {

                            return
                        }

                        scope.launch {
                            repository.updateVariant(updatedVariant)
                        }

                        onEvent(OnVariantDialogDismissed)
                    }
                }
            }

            is ProductVariantViewEvent.OnDeleteVariant -> scope.launch {
                if (event.variant in state.value.selectedVariants) {
                    val selectedVariants = state.value.selectedVariants.toMutableList()
                    selectedVariants.remove(event.variant)

                    state.update { currentState ->
                        currentState.copy(
                            selectedVariants = selectedVariants
                        )
                    }
                }

                repository.deleteVariant(event.variant)
            }
        }
    }
}
