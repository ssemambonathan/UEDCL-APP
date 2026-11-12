package com.uedcl.power.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import app.cash.turbine.test
import com.uedcl.power.data.local.UedclDatabase
import com.uedcl.power.data.local.dao.FaultReportDao
import com.uedcl.power.data.local.entity.FaultReportEntity
import com.uedcl.power.data.network.UedclApiService
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class FaultSyncWorkerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val context: Context = mockk(relaxed = true)
    private val workerParams: WorkerParameters = mockk(relaxed = true)
    private val apiService: UedclApiService = mockk()
    private val database: UedclDatabase = mockk()
    private val faultDao: FaultReportDao = mockk()

    private lateinit var worker: FaultSyncWorker

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { database.faultReportDao() } returns faultDao

        worker = FaultSyncWorker(
            appContext = context,
            workerParams = workerParams,
            apiService = apiService,
            database = database
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `doWork returns success when pending faults sync successfully`() = runTest {
        val mockFault = FaultReportEntity(
            id = 1L,
            meterNumber = "123456",
            faultType = "No Power",
            description = "Transformer tripped",
            latitude = 0.3476,
            longitude = 32.5825
        )

        coEvery { faultDao.getAllPendingFaults() } returns listOf(mockFault)
        coEvery { 
            apiService.submitFaultReport(any(), any(), any(), any(), any()) 
        } returns Response.success(true)
        coEvery { faultDao.deleteFault(mockFault) } just Runs

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 1) { faultDao.deleteFault(mockFault) }
    }
}
