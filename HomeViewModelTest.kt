package com.uedcl.power.presentation.home

import app.cash.turbine.test
import com.uedcl.power.data.repository.IUedclRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: IUedclRepository = mockk()
    private val balanceFlow = MutableStateFlow("150.0 kWh")

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repository.observeYakaBalance() } returns balanceFlow

        viewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits updated Yaka balance correctly`() = runTest {
        viewModel.yakaBalanceState.test {
            // Check initial emission
            assertEquals("150.0 kWh", awaitItem())

            // Push a new balance update
            balanceFlow.value = "75.5 kWh"

            // Verify turbine catches the state change
            assertEquals("75.5 kWh", awaitItem())
        }
    }
}
