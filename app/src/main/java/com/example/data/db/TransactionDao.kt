package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TransactionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transaction_records ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transaction_records WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionRecord): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionRecord)

    @Query("DELETE FROM transaction_records")
    suspend fun clearAllHistory()
}
