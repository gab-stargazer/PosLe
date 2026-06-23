package org.lelestacia.posle.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.toDomain

data class ProductWithVariantsAndStock(
    @Embedded
    val product: ProductEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "product_id"
    )
    val priceHistorical: List<ProductPriceEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "product_id"
    )
    val stock: StockEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = VariantJunction::class,
            parentColumn = "product_id",
            entityColumn = "variant_id"
        )
    )
    val variants: List<VariantEntity>
)

fun ProductWithVariantsAndStock.toDomain(): Product {
    return Product(
        id = product.id,
        name = product.name,
        stock = stock.stock,
        price = priceHistorical.maxBy { it.createdAt }.price,
        unit = product.unit,
        skuNumber = product.skuNumber,
        imageUri = product.imageUri,
        variants = variants.map { it.toDomain() }.sortedBy { it.name.value }
    )
}
