package com.example.erdbeeren.data

data class PendingAccount(
    val id: Int,
    val clientName: String,
    val totalAmount: Double,
    val pendingAmount: Double
)