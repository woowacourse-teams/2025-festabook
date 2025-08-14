package com.daedan.festabook.placeList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.domain.repository.PlaceDetailRepository
import com.daedan.festabook.domain.repository.PlaceListRepository
import com.daedan.festabook.getOrAwaitValue
import com.daedan.festabook.placeDetail.FAKE_PLACE_DETAIL
import com.daedan.festabook.presentation.common.Event
import com.daedan.festabook.presentation.placeDetail.model.toUiModel
import com.daedan.festabook.presentation.placeList.PlaceListViewModel
import com.daedan.festabook.presentation.placeList.model.InitialMapSettingUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.daedan.festabook.presentation.placeList.model.SelectedPlaceUiState
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
class PlaceListViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var placeListRepository: PlaceListRepository
    private lateinit var placeDetailRepository: PlaceDetailRepository
    private lateinit var placeListViewModel: PlaceListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        placeListRepository = mockk()
        placeDetailRepository = mockk()
        coEvery { placeListRepository.getPlaces() } returns Result.success(FAKE_PLACES)
        coEvery { placeListRepository.getPlaceGeographies() } returns
            Result.success(
                FAKE_PLACE_GEOGRAPHIES,
            )
        coEvery { placeListRepository.getOrganizationGeography() } returns
            Result.success(
                FAKE_ORGANIZATION_GEOGRAPHY,
            )
        placeListViewModel =
            PlaceListViewModel(
                placeListRepository,
                placeDetailRepository,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `뷰모델을 생성했을 때 모든 플레이스의 지도 좌표 정보를 불러올 수 있다`() =
        runTest {
            // given
            coEvery { placeListRepository.getPlaceGeographies() } returns
                Result.success(
                    FAKE_PLACE_GEOGRAPHIES,
                )

            // when
            placeListViewModel = PlaceListViewModel(placeListRepository, placeDetailRepository)
            advanceUntilIdle()

            // then
            val expected = FAKE_PLACE_GEOGRAPHIES.map { it.toUiModel() }
            val actual = placeListViewModel.placeGeographies.getOrAwaitValue()
            coVerify { placeListRepository.getPlaceGeographies() }
            assertThat(actual).isEqualTo(PlaceListUiState.Success(expected))
        }

    @Test
    fun `뷰모델을 생성했을 때 초기 학교 지리 정보를 불러올 수 있다`() =
        runTest {
            // given
            coEvery { placeListRepository.getOrganizationGeography() } returns
                Result.success(
                    FAKE_ORGANIZATION_GEOGRAPHY,
                )

            // when
            placeListViewModel = PlaceListViewModel(placeListRepository, placeDetailRepository)
            advanceUntilIdle()

            // then
            val expected = FAKE_ORGANIZATION_GEOGRAPHY.toUiModel()
            val actual = placeListViewModel.initialMapSetting.getOrAwaitValue()
            assertThat(actual).isEqualTo(PlaceListUiState.Success(expected))
        }

    @Test
    fun `뷰모델을 생성했을 때 정보 로드에 실패하면 독립적으로 에러 상태를 표시한다`() =
        runTest {
            // given
            val exception = Throwable("테스트")
            coEvery { placeListRepository.getPlaces() } returns Result.failure(exception)
            coEvery { placeListRepository.getOrganizationGeography() } returns
                Result.success(
                    FAKE_ORGANIZATION_GEOGRAPHY,
                )
            coEvery { placeListRepository.getPlaceGeographies() } returns Result.failure(exception)

            // when
            placeListViewModel = PlaceListViewModel(placeListRepository, placeDetailRepository)
            advanceUntilIdle()

            // then
            val expected2 =
                PlaceListUiState.Success<InitialMapSettingUiModel>(FAKE_ORGANIZATION_GEOGRAPHY.toUiModel())
            val actual2 = placeListViewModel.initialMapSetting.getOrAwaitValue()

            val expected3 = PlaceListUiState.Error<PlaceUiModel>(exception)
            val actual3 = placeListViewModel.placeGeographies.getOrAwaitValue()

            assertThat(actual2).isEqualTo(expected2)
            assertThat(actual3).isEqualTo(expected3)
        }

    @Test
    fun `플레이스의 아이디와 카테고리가 있으면 플레이스 상세를 선택할 수 있다`() =
        runTest {
            // given
            coEvery { placeDetailRepository.getPlaceDetail(1) } returns Result.success(FAKE_PLACE_DETAIL)

            // when
            placeListViewModel.selectPlace(1, PlaceCategoryUiModel.FOOD_TRUCK)
            advanceUntilIdle()

            // then
            coVerify { placeDetailRepository.getPlaceDetail(1) }

            val expected = SelectedPlaceUiState.Success(FAKE_PLACE_DETAIL.toUiModel())
            val actual = placeListViewModel.selectedPlace.getOrAwaitValue()
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `카테고리가 기타시설이라면 플레이스 상세가 선택되지 않는다`() =
        runTest {
            // given
            coEvery { placeDetailRepository.getPlaceDetail(1) } returns Result.success(FAKE_PLACE_DETAIL)

            // when
            placeListViewModel.selectPlace(1, PlaceCategoryUiModel.TOILET)
            advanceUntilIdle()

            // then
            val expected = null
            val actual = placeListViewModel.selectedPlace.value
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `플레이스 상세 선택을 해제할 수 있다`() =
        runTest {
            // given
            coEvery { placeDetailRepository.getPlaceDetail(1) } returns Result.success(FAKE_PLACE_DETAIL)
            placeListViewModel.selectPlace(1, PlaceCategoryUiModel.FOOD_TRUCK)
            advanceUntilIdle()

            // when
            placeListViewModel.unselectPlace()
            advanceUntilIdle()

            // then
            val expected = SelectedPlaceUiState.Empty
            val actual = placeListViewModel.selectedPlace.getOrAwaitValue()
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `초기 위치로 돌아가기 버튼 클릭 시 이벤트가 방출된다`() =
        runTest {
            // given

            // when
            placeListViewModel.onBackToInitialPositionClicked()
            advanceUntilIdle()

            // then
            val actual = placeListViewModel.backToInitialPositionClicked.getOrAwaitValue()
            assertThat(actual).isInstanceOf(Event::class.java)
        }

    @Test
    fun `학교로 돌아가기 버튼이 나타나지 않는 임계값을 넣을 수 있다`() =
        runTest {
            // given
            val isExceededMaxLength = true

            // when
            placeListViewModel.setIsExceededMaxLength(isExceededMaxLength)

            // then
            val actual = placeListViewModel.isExceededMaxLength.getOrAwaitValue()
            assertThat(actual).isEqualTo(isExceededMaxLength)
        }

    @Test
    fun `선택된 카테고리 값을 넣을 수 있다`() =
        runTest {
            // given
            val categories = listOf(PlaceCategoryUiModel.FOOD_TRUCK, PlaceCategoryUiModel.BOOTH)

            // when
            placeListViewModel.setSelectedCategories(categories)

            // then
            val actual = placeListViewModel.selectedCategories.getOrAwaitValue()
            assertThat(actual).isEqualTo(categories)
        }
}
