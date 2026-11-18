package com.example.erdbeeren.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment WHERE saleId = :saleId")
    fun getPaymentsBySale(saleId: Int): List<PaymentEntity>

    @Insert
    fun insertPayment(payment: PaymentEntity)
}