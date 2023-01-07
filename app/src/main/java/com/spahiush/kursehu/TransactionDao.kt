package com.spahiush.kursehu

import androidx.room.*

@Dao
interface TransactionDao {
    @Query("select * from transactions")
    fun getAll(): List<Transaction>

    @Insert
    fun insertAll(vararg transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Update
    fun update(vararg transaction: Transaction)
}

