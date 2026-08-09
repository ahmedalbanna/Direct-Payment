package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MerchantProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface MerchantDao {
    @Query("SELECT * FROM merchant_profile WHERE id = 1 LIMIT 1")
    fun getMerchantProfile(): Flow<MerchantProfile?>

    @Query("SELECT * FROM merchant_profile WHERE id = 1 LIMIT 1")
    suspend fun getMerchantSync(): MerchantProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: MerchantProfile)

    @Query("UPDATE merchant_profile SET balance = :newBalance WHERE id = 1")
    suspend fun updateBalance(newBalance: Double)

    @Query("UPDATE merchant_profile SET isLocked = :locked WHERE id = 1")
    suspend fun setLockedState(locked: Boolean)

    @Query("UPDATE merchant_profile SET simulateApiFailures = :simulate WHERE id = 1")
    suspend fun setSimulateFailure(simulate: Boolean)
}
