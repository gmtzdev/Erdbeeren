package com.example.erdbeeren.data

import androidx.room.Embedded
import androidx.room.Relation

data class ClientWithSales(
    @Embedded val client: ClientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "clientId"
    )
    val sales: List<SaleEntity>
)