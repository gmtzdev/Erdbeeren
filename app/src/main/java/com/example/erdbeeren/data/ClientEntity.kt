package com.example.erdbeeren.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clientName: String = "",
    val phoneNumber: String = ""
) {
    override fun toString(): String {
        return if (phoneNumber.isNotEmpty()) {
            "$clientName ($phoneNumber)"
        } else {
            "$clientName (#$id)"
        }
    }
}