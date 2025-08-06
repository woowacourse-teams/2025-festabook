package com.daedan.festabook.placeDetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.domain.repository.PlaceDetailRepository
import com.daedan.festabook.getOrAwaitValue
import com.daedan.festabook.placeList.FAKE_PLACES
import com.daedan.festabook.presentation.placeDetail.PlaceDetailViewModel
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiState
import com.daedan.festabook.presentation.placeDetail.model.toUiModel
import com.daedan.festabook.presentation.placeList.model.toUiModel
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
class PlaceDetailViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var placeDetailRepository: PlaceDetailRepository
    private lateinit var placeDetailViewModel: PlaceDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        placeDetailRepository = mockk()
        coEvery { placeDetailRepository.getPlaceDetail(any()) } returns Result.success(FAKE_PLACE_DETAIL)
        placeDetailViewModel = PlaceDetailViewModel(placeDetailRepository, FAKE_PLACES.first().toUiModel())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `플레이스 상세 정보를 불러올 수 있다`() =
        runTest {
            // given
            coEvery { placeDetailRepository.getPlaceDetail(any()) } returns Result.success(FAKE_PLACE_DETAIL)

            // when
            placeDetailViewModel.loadPlaceDetail()
            advanceUntilIdle()

            // then
            val expected = FAKE_PLACE_DETAIL.toUiModel()
            val actual = placeDetailViewModel.placeDetail.value
            coVerify { placeDetailRepository.getPlaceDetail(FAKE_PLACES.first().id) }
            assertThat(actual).isEqualTo(PlaceDetailUiState.Success(expected))
        }

    @Test
    fun `플레이스 상세 정보 로드에 실파하면 에러 상태를 표시한다`() =
        runTest {
            // given
            val exception = Throwable("테스트")
            coEvery { placeDetailRepository.getPlaceDetail(any()) } returns Result.failure(exception)

            // then
            placeDetailViewModel.loadPlaceDetail()
            advanceUntilIdle()

            // then
            val expected = PlaceDetailUiState.Error(exception)
            val actual = placeDetailViewModel.placeDetail.getOrAwaitValue()
            coVerify { placeDetailRepository.getPlaceDetail(FAKE_PLACES.first().id) }
            assertThat(actual).isEqualTo(expected)
        }
}
