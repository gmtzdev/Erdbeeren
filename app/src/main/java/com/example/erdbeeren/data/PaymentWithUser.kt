package com.example.erdbeeren.data

import androidx.room.Embedded
import androidx.room.Relation

data class PaymentWithUser(
    @Embedded val payment: PaymentEntity,
    @Relation(
        parentColumn = "userToPay",
        entityColumn = "id"
    )
    val user: UserEntity
)