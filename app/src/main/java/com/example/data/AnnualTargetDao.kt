package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnualTargetDao {
    @Query("SELECT * FROM annual_targets WHERE id = 1 LIMIT 1")
    fun getAnnualTargetFlow(): Flow<AnnualTarget?>

    @Query("SELECT * FROM annual_targets WHERE id = 1 LIMIT 1")
    suspend fun getAnnualTarget(): AnnualTarget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnualTarget(annualTarget: AnnualTarget)
}
