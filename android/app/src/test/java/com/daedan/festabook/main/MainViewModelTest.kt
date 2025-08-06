package com.daedan.festabook.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.data.datasource.local.AppPreferencesManager
import com.daedan.festabook.domain.repository.DeviceRepository
import com.daedan.festabook.presentation.main.MainViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var deviceRepository: DeviceRepository
    private lateinit var appPreferencesManager: AppPreferencesManager
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        deviceRepository = mockk()
        appPreferencesManager = mockk()
        mainViewModel = MainViewModel(deviceRepository, appPreferencesManager)

        coEvery {
            deviceRepository.registerDevice(
                FAKE_UUID,
                FAKE_FCM_TOKEN,
            )
        } returns Result.success(1)

        coEvery {
            appPreferencesManager.saveDeviceId(1)
        } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uuid와 fcmToken으로 기기를 등록 요청을 할 수 있다`() =
        runTest {
            // given

            // when
            mainViewModel.registerDevice(
                FAKE_UUID,
                FAKE_FCM_TOKEN,
            )
            advanceUntilIdle()

            // then
            coVerify { deviceRepository.registerDevice(FAKE_UUID, FAKE_FCM_TOKEN) }
            coVerify { appPreferencesManager.saveDeviceId(1) }
        }
}
