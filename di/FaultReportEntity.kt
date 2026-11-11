package com.uedcl.power.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_fault_reports")
data class FaultReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val meterNumber: String,
    val faultType: String,
    val description: String,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long = System.currentTimeMillis(),
    val retryCount: Int = 0
)
