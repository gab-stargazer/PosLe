package org.lelestacia.posle.domain.component.product_add_edit

import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewState
import org.lelestacia.posle.util.coroutineScope

class ProductAddVariantViewComponent(
    componentContext: ComponentContext,
    initialSelectedVariants: List<Variant> = emptyList(),
    private val onVariantsConfirmed: (List<Variant>) -> Unit,
    private val repository: ProductRepository
) : ComponentContext by componentContext {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    val state: Value<ProductVariantViewState>
        field = MutableValue(
            ProductVariantViewState(
                selectedVariant = initialSelectedVariants
            )
        )

    val variant = repository
        .readVariant()
        .cachedIn(scope)

    fun onEvent(event: ProductVariantViewEvent) = scope.launch {
        when (event) {
            is ProductVariantViewEvent.OnVariantClicked -> {
                val selected = state.value.selectedVariant.toMutableList()

                if (event.variant in selected) {
                    selected.remove(event.variant)
                } else {
                    selected.add(event.variant)
                }

                state.update { currentState ->
                    currentState.copy(
                        selectedVariant = selected.sortedBy { it.name.value }
                    )
                }
            }

            is ProductVariantViewEvent.OnAddVariant -> {
                repository.addVariant(variant = event.variant)
            }

            is ProductVariantViewEvent.OnTabChanged -> {
                state.update { it.copy(currentTab = event.index) }
            }

            ProductVariantViewEvent.OnConfirmed -> onVariantsConfirmed(state.value.selectedVariant)
        }
    }
}