package com.example.erdbeeren.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients")
    fun getAllClients(): List<ClientEntity>

    @Query("SELECT * FROM clients WHERE id = :clientId")
    fun getClientById(clientId: Int): ClientEntity?

    @Query("SELECT * FROM clients WHERE clientName = :clientName")
    fun getClientByName(clientName: String): ClientEntity?

    @Query("SELECT * FROM clients WHERE clientName LIKE :clientName")
    fun getClientsByName(clientName: String): List<ClientEntity>

    @Transaction
    @Query("SELECT * FROM clients")
    fun getClientsWithSales(): List<ClientWithSales>

    @Insert
    fun insertClient(client: ClientEntity)
}