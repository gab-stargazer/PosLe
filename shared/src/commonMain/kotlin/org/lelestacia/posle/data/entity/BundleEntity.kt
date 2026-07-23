package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit

@Entity(
    tableName = "bundle",
    indices = [Index("name")]
)
data class BundleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo("name")
    val name: Name,

    @ColumnInfo("image_uri")
    val imageUri: String? = null,

    @ColumnInfo("created_at")
    val createdAt: Long,

    @ColumnInfo("updated_at")
    val updatedAt: Long? = null,
)

@Entity(
    tableName = "bundle_product",
    foreignKeys = [
        ForeignKey(
            entity = BundleEntity::class,
            parentColumns = ["id"],
            childColumns = ["bundle_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("bundle_id"),
        Index("product_id")
    ]
)
data class BundleProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo("bundle_id")
    val bundleId: Int,

    @ColumnInfo("product_id")
    val productId: Int,

    @ColumnInfo("name")
    val name: Name,

    @ColumnInfo("quantity")
    val quantity: Amount,

    @ColumnInfo("unit")
    val unit: Unit,

    @ColumnInfo("sell_price")
    val sellPrice: Price,

    @ColumnInfo("created_at")
    val createdAt: Long,

    @ColumnInfo("updated_at")
    val updatedAt: Long? = null
)



data class BundleWithProductsEntity(

    @Embedded
    val bundle: BundleEntity,

    @Relation(
        entity = BundleProductEntity::class,
        parentColumn = "id",
        entityColumn = "bundle_id"
    )
    val products: List<BundleProductsWithProductsEntity>
)

data class BundleProductsWithProductsEntity(
    @Embedded
    val bundleProduct: BundleProductEntity,

    @Relation(
        entity = ProductEntity::class,
        parentColumn = "product_id",
        entityColumn = "id"
    )
    val product: ProductEntity,

    @Relation(
        entity = ProductSellPriceEntity::class,
        parentColumn = "product_id",
        entityColumn = "product_id"
    )
    val sellPriceHistorical: List<ProductSellPriceEntity>,

    @Relation(
        entity = ProductBuyPriceEntity::class,
        parentColumn = "product_id",
        entityColumn = "product_id"
    )
    val buyPriceHistorical: List<ProductBuyPriceEntity>
)