package com.uedcl.power.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uedcl.power.data.local.UedclDatabase
import com.uedcl.power.data.network.UedclApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FaultSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: UedclApiService,
    private val database: UedclDatabase
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val dao = database.faultReportDao()
        val pendingFaults = dao.getAllPendingFaults()

        if (pendingFaults.isEmpty()) {
            return@withContext Result.success()
        }

        var allSynced = true

        for (fault in pendingFaults) {
            try {
                // Submit to backend via Retrofit
                val response = apiService.submitFaultReport(
                    meterNumber = fault.meterNumber,
                    faultType = fault.faultType,
                    description = fault.description,
                    latitude = fault.latitude,
                    longitude = fault.longitude
                )

                if (response.isSuccessful) {
                    // Remove successfully synced item from Room queue
                    dao.deleteFault(fault)
                } else {
                    allSynced = false
                    dao.incrementRetryCount(fault.id)
                }
            } catch (e: Exception) {
                allSynced = false
                dao.incrementRetryCount(fault.id)
            }
        }

        return@withContext if (allSynced) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
