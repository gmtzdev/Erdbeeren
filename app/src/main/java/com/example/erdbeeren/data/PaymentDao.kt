package com.example.erdbeeren.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment WHERE saleId = :saleId")
    fun getPaymentsBySale(saleId: Int): List<PaymentEntity>

    @Transaction
    @Query("SELECT * FROM payment WHERE saleId = :saleId")
    fun getPaymentsWithUserBySale(saleId: Int): List<PaymentWithUser>

    @Insert
    fun insertPayment(payment: PaymentEntity)
}