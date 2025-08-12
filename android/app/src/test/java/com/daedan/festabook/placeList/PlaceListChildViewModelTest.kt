package com.daedan.festabook.placeList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.domain.repository.PlaceListRepository
import com.daedan.festabook.getOrAwaitValue
import com.daedan.festabook.presentation.placeList.PlaceListChildViewModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
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
class PlaceListChildViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var placeListRepository: PlaceListRepository
    private lateinit var placeListChildViewModel: PlaceListChildViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        placeListRepository = mockk()
        coEvery { placeListRepository.getPlaces() } returns Result.success(FAKE_PLACES)
        coEvery { placeListRepository.getPlaceGeographies() } returns
            Result.success(
                FAKE_PLACE_GEOGRAPHIES,
            )
        coEvery { placeListRepository.getOrganizationGeography() } returns
            Result.success(
                FAKE_ORGANIZATION_GEOGRAPHY,
            )
        placeListChildViewModel =
            PlaceListChildViewModel(
                placeListRepository,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `뷰모델을 생성했을 때 모든 플레이스 정보를 불러올 수 있다`() =
        runTest {
            // given
            coEvery { placeListRepository.getPlaces() } returns Result.success(FAKE_PLACES)

            // when
            placeListChildViewModel = PlaceListChildViewModel(placeListRepository)
            advanceUntilIdle()

            // then
            val expected = FAKE_PLACES.map { it.toUiModel() }
            val actual = placeListChildViewModel.places.getOrAwaitValue()
            coVerify { placeListRepository.getPlaces() }
            assertThat(actual).isEqualTo(PlaceListUiState.Success(expected))
        }

    @Test
    fun `선택된 카테고리를 전달하면 해당 카테고리의 플레이스만 필터링 할 수 있다`() =
        runTest {
            // given
            val targetCategories =
                listOf(PlaceCategoryUiModel.FOOD_TRUCK, PlaceCategoryUiModel.BOOTH)

            // when
            placeListChildViewModel.filterPlaces(targetCategories)
            advanceUntilIdle()

            // then
            val expected =
                FAKE_PLACES
                    .filter { it.category.toUiModel() in targetCategories }
                    .map { it.toUiModel() }
            val actual = placeListChildViewModel.places.getOrAwaitValue()
            assertThat(actual).isEqualTo(PlaceListUiState.Success(expected))
        }

    @Test
    fun `선택된 카테고리가 부스, 주점, 푸드트럭에 해당되지 않을 때 전체 목록을 불러온다`() =
        runTest {
            // given
            val targetCategories =
                listOf(PlaceCategoryUiModel.SMOKING_AREA, PlaceCategoryUiModel.TOILET)

            // when
            placeListChildViewModel.filterPlaces(targetCategories)
            advanceUntilIdle()

            // then
            val expected = FAKE_PLACES.map { it.toUiModel() }
            val actual = placeListChildViewModel.places.getOrAwaitValue()
            assertThat(actual).isEqualTo(PlaceListUiState.Success(expected))
        }

    @Test
    fun `필터링을 해제하면 전체 목록을 반환한다`() =
        runTest {
            // given
            val targetCategories =
                listOf(PlaceCategoryUiModel.FOOD_TRUCK, PlaceCategoryUiModel.BOOTH)
            placeListChildViewModel.filterPlaces(targetCategories)
            advanceUntilIdle()

            // when
            placeListChildViewModel.clearPlacesFilter()
            advanceUntilIdle()

            // then
            val expected = FAKE_PLACES.map { it.toUiModel() }
            val actual = placeListChildViewModel.places.getOrAwaitValue()
            assertThat(actual).isEqualTo(PlaceListUiState.Success(expected))
        }
}
