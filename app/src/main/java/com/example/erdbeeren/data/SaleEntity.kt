package com.example.erdbeeren.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "sales",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["clientId"])]
)
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clientId: Int = 0,
    val kg: Double = 0.0,
    val price: Double = 0.0,
    val total: Double = 0.0,
    val pending: Double = 0.0,
    val decrease: Double = 0.0,
    val cAra: Int = 0,
    val cArG: Int = 0,
    val cMar: Int = 0,
    val cCaj: Int = 0,
    val date: Instant = Instant.now()
)