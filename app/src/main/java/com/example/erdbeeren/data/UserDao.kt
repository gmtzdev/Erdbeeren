package com.example.erdbeeren.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    fun login(username: String, password: String): UserEntity?

    @Insert()
    fun insertUser(user: UserEntity)

    @Query("DELETE FROM users")
    fun deleteAllUsers()
}