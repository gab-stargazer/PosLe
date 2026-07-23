package org.lelestacia.posle.data.entity

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.jetbrains.compose.resources.StringResource
import org.lelestacia.posle.domain.model.StockMovement
import org.lelestacia.posle.ui.theme.DeepTeal
import org.lelestacia.posle.ui.theme.LilacAsh
import org.lelestacia.posle.ui.theme.Periwinkle
import org.lelestacia.posle.ui.theme.SkyBlue
import org.lelestacia.posle.ui.theme.TropicalTeal
import org.lelestacia.posle.ui.theme.onSurfaceLightHighContrast
import org.lelestacia.posle.ui.theme.surfaceContainerLowestLightHighContrast
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Unit
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.description_product_adjustment_increase
import posle.shared.generated.resources.description_product_purchase
import posle.shared.generated.resources.description_product_return
import posle.shared.generated.resources.description_product_sale
import posle.shared.generated.resources.title_product_adjustment_decrease
import posle.shared.generated.resources.title_product_adjustment_increase
import posle.shared.generated.resources.title_product_purchase
import posle.shared.generated.resources.title_product_return
import posle.shared.generated.resources.title_product_sale

@Entity(
    tableName = "stock"
)
data class StockEntity(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("product_id")
    val productId: Int,
    @ColumnInfo("stock")
    val stock: Amount,
    @ColumnInfo("updated_at")
    val updatedAt: Long?
)

@Entity(
    tableName = "stock_movement",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("product_id")]
)
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    @ColumnInfo("product_id")
    val productId: Int,

    @ColumnInfo("product_name")
    val productName: Name,

    @ColumnInfo("product_unit")
    val productUnit: Unit,

    @ColumnInfo("movement_type")
    val movementType: StockMovementType,

    @ColumnInfo("amount")
    val amount: Amount,

    @ColumnInfo("note")
    val note: String? = null,

    @ColumnInfo("created_at")
    val createdAt: Long,
)

enum class StockMovementType(
    val title: StringResource,
    val description: StringResource,
    val backgroundColor: Color,
    val textColor: Color
) {
    Purchase(
        title = Res.string.title_product_purchase,
        description = Res.string.description_product_purchase,
        backgroundColor = DeepTeal,
        textColor = surfaceContainerLowestLightHighContrast
    ),
    Sale(
        title = Res.string.title_product_sale,
        description = Res.string.description_product_sale,
        backgroundColor = TropicalTeal,
        textColor = surfaceContainerLowestLightHighContrast
    ),
    Return(
        title = Res.string.title_product_return,
        description = Res.string.description_product_return,
        backgroundColor = SkyBlue,
        textColor = onSurfaceLightHighContrast
    ),
    AdjustmentIncrease(
        title = Res.string.title_product_adjustment_increase,
        description = Res.string.description_product_adjustment_increase,
        backgroundColor = Periwinkle,
        textColor = onSurfaceLightHighContrast
    ),
    AdjustmentDecrease(
        title = Res.string.title_product_adjustment_decrease,
        description = Res.string.title_product_adjustment_decrease,
        backgroundColor = LilacAsh,
        textColor = surfaceContainerLowestLightHighContrast
    )
}

fun StockMovementEntity.toDomain(): StockMovement {
    return StockMovement(
        id = id,
        productId = productId,
        productName = productName,
        productUnit = productUnit,
        movementType = movementType,
        amount = amount,
        note = note,
        createdAt = createdAt
    )
}