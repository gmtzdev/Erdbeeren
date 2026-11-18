package com.example.erdbeeren.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales")
    fun getAllSales(): List<SaleEntity>

    @Query("SELECT * FROM sales WHERE clientId = :clientId")
    fun getSalesByClientId(clientId: Int): List<SaleEntity>

    @Transaction
    @Query("SELECT * FROM sales")
    fun getSalesWithClient(): List<SalesWithClient>

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :saleId")
    fun getSaleWithClientById(saleId: Int): SalesWithClient?

    @Insert
    fun insertSale(sale: SaleEntity)

    @Update
    fun updateSale(sale: SaleEntity)

    @Query("UPDATE sales SET pending = :pendingAmount WHERE id = :saleId")
    fun updatePendingAmount(saleId: Int, pendingAmount: Double)

    @Query("UPDATE sales SET decrease = :decreaseAmount WHERE id = :saleId")
    fun updateDecreaseAmount(saleId: Int, decreaseAmount: Double)
}