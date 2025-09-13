package com.daedan.festabook.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.domain.repository.FestivalRepository
import com.daedan.festabook.getOrAwaitValue
import com.daedan.festabook.presentation.home.HomeViewModel
import com.daedan.festabook.presentation.home.LineUpItemGroupUiModel
import com.daedan.festabook.presentation.home.LineupUiState
import com.daedan.festabook.presentation.home.adapter.FestivalUiState
import com.daedan.festabook.presentation.home.adapter.FestivalUiState.Loading
import com.daedan.festabook.presentation.home.toUiModel
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
class HomeViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var festivalRepository: FestivalRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        festivalRepository = mockk()
        coEvery { festivalRepository.getFestivalInfo() } returns Result.success(FAKE_ORGANIZATION)
        coEvery { festivalRepository.getLineUpGroupByDate() } returns
            Result.success(
                mapOf(
                    FAKE_LINEUP[0].performanceAt.toLocalDate() to FAKE_LINEUP,
                ),
            )

        homeViewModel = HomeViewModel(festivalRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `축제 정보를 불러올 수 있다`() =
        runTest {
            // given
            val expect = FestivalUiState.Success(FAKE_ORGANIZATION)

            // when
            homeViewModel.loadFestival()
            advanceUntilIdle()

            // then
            val actual = homeViewModel.festivalUiState.getOrAwaitValue()
            assertThat(actual).isEqualTo(expect)
        }

    @Test
    fun `연예인 정보를 불러올 수 있다`() =
        runTest {
            // given
            val expect =
                LineupUiState.Success(
                    LineUpItemGroupUiModel(
                        mapOf(
                            FAKE_LINEUP[0].performanceAt.toLocalDate() to FAKE_LINEUP.map { it.toUiModel() },
                        ),
                    ),
                )

            // when
            HomeViewModel(festivalRepository)
            advanceUntilIdle()

            // then
            val actual = homeViewModel.lineupUiState.getOrAwaitValue()
            assertThat(actual).isEqualTo(expect)
        }

    @Test
    fun `축제 정보를 불러오는 동안은 Loading 상태로 전환한다`() =
        runTest {
            // given
            var wasLoadingState = false
            homeViewModel.festivalUiState.observeForever { state ->
                if (state == Loading) {
                    wasLoadingState = true
                }
            }

            // when
            homeViewModel.loadFestival()
            advanceUntilIdle()

            // then
            assertThat(wasLoadingState).isTrue()
        }

    @Test
    fun `축제 정보를 불러오는 데 실패하면 Error 상태로 전환한다`() =
        runTest {
            // given
            val exception = Throwable("test")
            coEvery { festivalRepository.getFestivalInfo() } returns Result.failure(exception)

            // when
            homeViewModel.loadFestival()
            advanceUntilIdle()

            // then
            val expect = FestivalUiState.Error(exception)
            val actual = homeViewModel.festivalUiState.getOrAwaitValue()
            assertThat(actual).isEqualTo(expect)
        }
}
