package com.example.erdbeeren.data

import androidx.room.Embedded
import androidx.room.Relation

data class SalesWithClient(
    @Embedded val sale: SaleEntity,
    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: ClientEntity
)