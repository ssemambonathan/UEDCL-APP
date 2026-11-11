package com.uedcl.power.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FaultReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFault(fault: FaultReportEntity)

    @Query("SELECT * FROM pending_fault_reports ORDER BY timestamp ASC")
    suspend fun getAllPendingFaults(): List<FaultReportEntity>

    @Delete
    suspend fun deleteFault(fault: FaultReportEntity)

    @Query("UPDATE pending_fault_reports SET retryCount = retryCount + 1 WHERE id = :faultId")
    suspend fun incrementRetryCount(faultId: Long)
}
