package com.daedan.festabook.placeList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.domain.model.TimeTag
import com.daedan.festabook.domain.repository.PlaceListRepository
import com.daedan.festabook.getOrAwaitValue
import com.daedan.festabook.presentation.placeMap.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeMap.model.PlaceListUiState
import com.daedan.festabook.presentation.placeMap.model.PlaceUiModel
import com.daedan.festabook.presentation.placeMap.model.toUiModel
import com.daedan.festabook.presentation.placeMap.placeList.PlaceListViewModel
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
class PlaceListViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var placeListRepository: PlaceListRepository
    private lateinit var PlaceListViewModel: PlaceListViewModel

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
        PlaceListViewModel =
            PlaceListViewModel(
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
            PlaceListViewModel = PlaceListViewModel(placeListRepository)
            advanceUntilIdle()

            // then
            val expected = FAKE_PLACES.map { it.toUiModel() }
            val actual = PlaceListViewModel.places.getOrAwaitValue()
            coVerify { placeListRepository.getPlaces() }
            assertThat(actual).isEqualTo(PlaceListUiState.PlaceLoaded(expected))
        }

    @Test
    fun `선택된 카테고리를 전달하면 해당 카테고리의 플레이스만 필터링 할 수 있다`() =
        runTest {
            // given
            val targetCategories =
                listOf(PlaceCategoryUiModel.FOOD_TRUCK, PlaceCategoryUiModel.BOOTH)
            PlaceListViewModel.updatePlacesByTimeTag(TimeTag.EMPTY.timeTagId)

            // when
            PlaceListViewModel.updatePlacesByCategories(targetCategories)

            // then
            val expected =
                FAKE_PLACES
                    .filter { it.category.toUiModel() in targetCategories }
                    .map { it.toUiModel() }
            val actual = PlaceListViewModel.places.getOrAwaitValue()
            assertThat(actual).isEqualTo(PlaceListUiState.Success(expected))
        }

    @Test
    fun `선택된 카테고리가 부스, 주점, 푸드트럭에 해당되지 않을 때 전체 목록을 불러온다`() =
        runTest {
            // given
            val targetCategories =
                listOf(PlaceCategoryUiModel.SMOKING_AREA, PlaceCategoryUiModel.TOILET)
            PlaceListViewModel.updatePlacesByTimeTag(TimeTag.EMPTY.timeTagId)

            // when
            PlaceListViewModel.updatePlacesByCategories(targetCategories)

            // then
            val expected = FAKE_PLACES.map { it.toUiModel() }
            val actual = PlaceListViewModel.places.getOrAwaitValue()
            assertThat(actual).isEqualTo(PlaceListUiState.Success(expected))
        }

    @Test
    fun `필터링을 해제하면 전체 목록을 반환한다`() =
        runTest {
            // given
            val targetCategories =
                listOf(PlaceCategoryUiModel.FOOD_TRUCK, PlaceCategoryUiModel.BOOTH)
            PlaceListViewModel.updatePlacesByTimeTag(TimeTag.EMPTY.timeTagId)
            PlaceListViewModel.updatePlacesByCategories(targetCategories)

            // when
            PlaceListViewModel.clearPlacesFilter()

            // then
            val expected = FAKE_PLACES.map { it.toUiModel() }
            val actual = PlaceListViewModel.places.getOrAwaitValue()
            assertThat(actual).isEqualTo(PlaceListUiState.Success(expected))
        }

    @Test
    fun `타임 태그를 기준으로 필터링 할 수 있다`() =
        runTest {
            // given
            val expected =
                listOf(
                    FAKE_PLACES.first().toUiModel(),
                )

            // when
            PlaceListViewModel.updatePlacesByTimeTag(1)

            // then
            val actual = PlaceListViewModel.places.getOrAwaitValue()
            assertThat(actual).isEqualTo(PlaceListUiState.Success(expected))
        }

    @Test
    fun `타임 태그가 없을 때 전체 목록을 반환한다`() =
        runTest {
            // given
            val expected = FAKE_PLACES.map { it.toUiModel() }
            val emptyTimeTag = TimeTag.EMPTY

            // when
            PlaceListViewModel.updatePlacesByTimeTag(emptyTimeTag.timeTagId)

            // then
            val actual = PlaceListViewModel.places.getOrAwaitValue()
            assertThat(actual).isEqualTo(PlaceListUiState.Success(expected))
        }

    @Test
    fun `플레이스의 모든 정보가 로드가 완료되었을 때 이벤트를 발생시킬 수 있다`() =
        runTest {
            // given
            val expected = PlaceListUiState.Complete<List<PlaceUiModel>>()

            // when
            PlaceListViewModel.setPlacesStateComplete()

            // then
            val actual = PlaceListViewModel.places.getOrAwaitValue()
            assertThat(actual).isInstanceOf(expected::class.java)
        }
}
