package com.daedan.festabook.setting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.domain.repository.FestivalNotificationRepository
import com.daedan.festabook.getOrAwaitValue
import com.daedan.festabook.presentation.setting.SettingViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
class SettingViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var settingViewModel: SettingViewModel

    private lateinit var festivalNotificationRepository: FestivalNotificationRepository


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        festivalNotificationRepository = mockk(relaxed = true)
        settingViewModel = SettingViewModel(festivalNotificationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `알림 허용을 클릭했을 때 알림이 허용이 안되있다면 권한 요청 이벤트를 발생시킨다`() = runTest {
        //given
        coEvery { festivalNotificationRepository.getFestivalNotificationIsAllow() } returns false
        val expected = Unit

        //when
        settingViewModel = SettingViewModel(festivalNotificationRepository)
        settingViewModel.notificationAllowClick()
        advanceUntilIdle()

        //then
        val actual = settingViewModel.permissionCheckEvent.value
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `알림 허용을 클릭했을 때 알림이 허용이 되있다면 알림id를 삭제한다`() = runTest {
        //given
        coEvery { festivalNotificationRepository.getFestivalNotificationIsAllow() } returns true

        //when
        settingViewModel = SettingViewModel(festivalNotificationRepository)
        settingViewModel.notificationAllowClick()
        advanceUntilIdle()

        //then
        val result = settingViewModel.isAllowed.getOrAwaitValue()
        coVerify { festivalNotificationRepository.setFestivalNotificationIsAllow(false) }
        coVerify { festivalNotificationRepository.deleteFestivalNotification() }
        assertThat(result).isFalse()
    }

    @Test
    fun `알림 허용을 클릭했을 때 서버에 알림 정보 저장에 실패하면 이전 상태로 원복한다`() = runTest {
        //given
        coEvery { festivalNotificationRepository.getFestivalNotificationIsAllow() } returns true
        coEvery { festivalNotificationRepository.deleteFestivalNotification() } returns Result.failure(Throwable())

        //when
        settingViewModel = SettingViewModel(festivalNotificationRepository)
        settingViewModel.notificationAllowClick()
        advanceUntilIdle()

        //then
        val result = settingViewModel.isAllowed.getOrAwaitValue()
        coVerify { festivalNotificationRepository.setFestivalNotificationIsAllow(true) }
        assertThat(result).isTrue()
    }

    @Test
    fun `알림을 허용했을 때 서버에 알림 정보 삭제에 실패하면 이전 상태로 원복한다`() = runTest {
        //given
        coEvery { festivalNotificationRepository.saveFestivalNotification() } returns Result.failure(Throwable())

        //when
        settingViewModel.saveNotificationId()
        advanceUntilIdle()

        //then
        val result = settingViewModel.isAllowed.getOrAwaitValue()
        coVerify { festivalNotificationRepository.setFestivalNotificationIsAllow(false) }
        assertThat(result).isFalse()
    }
}