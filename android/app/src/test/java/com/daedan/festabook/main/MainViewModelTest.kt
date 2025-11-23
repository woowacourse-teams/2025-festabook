package com.daedan.festabook.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.domain.repository.DeviceRepository
import com.daedan.festabook.domain.repository.FestivalNotificationRepository
import com.daedan.festabook.domain.repository.FestivalRepository
import com.daedan.festabook.getOrAwaitValue
import com.daedan.festabook.presentation.main.MainViewModel
import com.google.firebase.messaging.FirebaseMessaging
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
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
        deviceRepository = mockk(relaxed = true)
        festivalNotificationRepository = mockk(relaxed = true)
        festivalRepository = mockk(relaxed = true)
        mainViewModel =
            MainViewModel(deviceRepository, festivalRepository)
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
            coEvery {
                deviceRepository.registerDevice(
                    FAKE_UUID,
                    FAKE_FCM_TOKEN,
                )
            } returns Result.success(1)

            // when
            mainViewModel.registerDeviceAndFcmToken()
            advanceUntilIdle()

            // then
            coVerify { deviceRepository.saveDeviceId(1) }
            verify { deviceRepository.getUuid() }
            verify { deviceRepository.getFcmToken() }
        }

    @Test
    fun `fcm 토큰이 없으면 새로 fcm 토큰을 등록할 수 있다`() =
        runTest {
            // given
            every { deviceRepository.getUuid() } returns FAKE_UUID
            every { deviceRepository.getFcmToken() } returns null
            mockkStatic(FirebaseMessaging::class)
            val mockFirebaseMessaging: FirebaseMessaging = mockk(relaxed = true)
            every { FirebaseMessaging.getInstance() } returns mockFirebaseMessaging

            // when
            mainViewModel.registerDeviceAndFcmToken()
            advanceUntilIdle()

            coVerify { mockFirebaseMessaging.token }
        }

    @Test
    fun `뒤로 가기를 두 번 빠르게 두 번 클릭했을 때, 종료 이벤트가 발생한다`() =
        runTest {
            // given - when
            mainViewModel.onBackPressed()
            mainViewModel.onBackPressed()

            // then
            val actual = mainViewModel.backPressEvent.getOrAwaitValue()
            assertThat(actual.peekContent()).isTrue()
        }

    @Test
    fun `뒤로 가기를 한 번만 클릭했을 때 종료 이벤트가 발생하지 않는다`() =
        runTest {
            // given - when
            mainViewModel.onBackPressed()

            // then
            val actual = mainViewModel.backPressEvent.getOrAwaitValue()
            assertThat(actual.peekContent()).isFalse()
        }

    @Test
    fun `축제 페이지의 첫 방문 여부를 확인할 수 있다`() {
        // given
        every { festivalRepository.getIsFirstVisit() } returns Result.success(true)

        // when
        mainViewModel = MainViewModel(deviceRepository, festivalRepository)
        val result = mainViewModel.isFirstVisit.value

        // then
        assert(result == true)
    }
}
