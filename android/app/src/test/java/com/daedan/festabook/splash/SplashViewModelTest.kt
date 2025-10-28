package com.daedan.festabook.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.presentation.splash.NavigationState
import com.daedan.festabook.presentation.splash.SplashViewModel
import io.mockk.coEvery
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
class SplashViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var festivalLocalDataSource: FestivalLocalDataSource
    private lateinit var splashViewModel: SplashViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        festivalLocalDataSource = mockk(relaxed = true)
        splashViewModel = SplashViewModel(festivalLocalDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `뷰모델을 생성할 때 현재 접속한 대학교가 있다면 MainActivity로 이동한다`() =
        runTest {
            //given
            coEvery { festivalLocalDataSource.getFestivalId() } returns 1
            val expected = NavigationState.NavigateToMain(1)

            //when
            splashViewModel = SplashViewModel(festivalLocalDataSource)
            advanceUntilIdle()

            //then
            val actual = splashViewModel.navigationState.value
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `뷰모델을 생성할 때 현재 접속한 대학교가 없다면 ExploreActivity로 이동한다`() =
        runTest {
            //given
            coEvery { festivalLocalDataSource.getFestivalId() } returns null
            val expected = NavigationState.NavigateToExplore

            //when
            splashViewModel = SplashViewModel(festivalLocalDataSource)
            advanceUntilIdle()

            //then
            val actual = splashViewModel.navigationState.value
            assertThat(actual).isEqualTo(expected)
        }
}