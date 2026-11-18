package com.example.erdbeeren.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant


@Entity(
    tableName = "payment",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userToPay"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["saleId"])])
data class PaymentEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val saleId: Int = 0,
    val userToPay: Int = 0,
    val amount: Double = 0.0,
    val date: Instant = Instant.now()
)