package com.daedan.festabook.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.domain.repository.DeviceRepository
import com.daedan.festabook.domain.repository.FestivalNotificationRepository
import com.daedan.festabook.domain.repository.FestivalRepository
import com.daedan.festabook.presentation.main.MainViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
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
    private lateinit var mainViewModel: MainViewModel

    private lateinit var festivalRepository: FestivalRepository
    private lateinit var festivalNotificationRepository: FestivalNotificationRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        deviceRepository = mockk()
        festivalNotificationRepository = mockk()
        festivalRepository = mockk()

        every { festivalRepository.getIsFirstVisit() } returns Result.success(true)

        mainViewModel =
            MainViewModel(deviceRepository, festivalRepository, festivalNotificationRepository)

        coEvery {
            deviceRepository.registerDevice(
                FAKE_UUID,
                FAKE_FCM_TOKEN,
            )
        } returns Result.success(1)

        coEvery {
            deviceRepository.saveDeviceId(1)
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
            every { deviceRepository.getUuid() } returns FAKE_UUID
            every { deviceRepository.getFcmToken() } returns FAKE_FCM_TOKEN

            // when
            mainViewModel.registerDeviceAndFcmToken()
            advanceUntilIdle()

            // then
            verify { deviceRepository.getUuid() }
            verify { deviceRepository.getFcmToken() }
        }

    @Test
    fun `축제 페이지의 첫 방문 여부를 확인할 수 있다`() {
        // given
        every { festivalRepository.getIsFirstVisit() } returns Result.success(true)

        // when
        val result = mainViewModel.isFirstVisit.value

        // then
        assert(result == true)
    }
}
