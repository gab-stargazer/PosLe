package org.lelestacia.posle.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.toDomain
import org.lelestacia.posle.util.Amount
import java.math.BigDecimal

data class ProductWithVariantsAndStock(
    @Embedded
    val product: ProductEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "product_id"
    )
    val buyPriceHistorical: List<ProductBuyPriceEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "product_id"
    )
    val sellPriceHistorical: List<ProductSellPriceEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "product_id"
    )
    val stock: List<StockMovementEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = VariantJunction::class,
            parentColumn = "product_id",
            entityColumn = "variant_id"
        )
    )
    val variants: List<VariantEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProductCategoryJunction::class,
            parentColumn = "product_id",
            entityColumn = "category_id"
        )
    )
    val categories: List<CategoryEntity>
)

fun ProductWithVariantsAndStock.toDomain(): Product {
    return Product(
        id = product.id,
        name = product.name,
        stock = Amount(
            stock.fold(BigDecimal.ZERO) { acc, next ->
                acc.add(next.amount.value)
            }
        ),
        buyPrice = buyPriceHistorical.maxBy { it.createdAt }.price,
        sellPrice = sellPriceHistorical.maxBy { it.createdAt }.price,
        unit = product.unit,
        skuNumber = product.skuNumber,
        imageUri = product.imageUri,
        variants = variants.map { it.toDomain() }.sortedBy { it.name.value },
        categories = categories.map { it.toDomain() }.sortedBy { it.name.value }
    )
}
