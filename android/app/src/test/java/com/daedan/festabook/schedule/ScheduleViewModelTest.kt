package com.daedan.festabook.schedule

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.domain.model.ScheduleEventStatus
import com.daedan.festabook.domain.repository.ScheduleRepository
import com.daedan.festabook.getOrAwaitValue
import com.daedan.festabook.presentation.schedule.ScheduleEventsUiState
import com.daedan.festabook.presentation.schedule.ScheduleViewModel
import com.daedan.festabook.presentation.schedule.model.toUiModel
import com.daedan.festabook.setUpTestLiveData
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val dateId = 1L
    private lateinit var scheduleRepository: ScheduleRepository
    private lateinit var scheduleViewModel: ScheduleViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        scheduleRepository = mockk()

        coEvery { scheduleRepository.fetchAllScheduleDates() } returns
            Result.success(
                FAKE_SCHEDULE_DATES,
            )
        coEvery { scheduleRepository.fetchScheduleEventsById(dateId) } returns
            Result.success(
                FAKE_SCHEDULE_EVENTS,
            )

        scheduleViewModel = ScheduleViewModel(scheduleRepository, dateId)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `선택한 북마크의 상태가 변경된다`() =
        runTest {
            // given
            val id = 1L

            setUpTestLiveData(
                item = FAKE_SCHEDULE_EVENTS_UI_STATE,
                fieldName = "_scheduleEventsUiState",
                viewModel = scheduleViewModel,
            )

            // when
            scheduleViewModel.updateBookmark(id)

            // then
            coVerify { scheduleRepository.fetchAllScheduleDates() }
            coVerify { scheduleRepository.fetchScheduleEventsById(dateId) }

            val events =
                when (val result = scheduleViewModel.scheduleEventsUiState.getOrAwaitValue()) {
                    is ScheduleEventsUiState.Success -> result.events
                    else -> emptyList()
                }

            val expected = true
            val result = events.find { it.id == id }?.isBookmarked
            assertEquals(expected, result)
        }

    @Test
    fun `해당 날짜에 맞는 일정을 불러온다`() =
        runTest {
            // given

            // when
            scheduleViewModel.loadScheduleByDate()
            advanceUntilIdle()

            // then
            coVerify { scheduleRepository.fetchAllScheduleDates() }
            coVerify { scheduleRepository.fetchScheduleEventsById(dateId) }

            val state = scheduleViewModel.scheduleEventsUiState.value
            assertTrue(state is ScheduleEventsUiState.Success)

            val expected = FAKE_SCHEDULE_EVENTS.map { it.toUiModel() }
            val result = (state as ScheduleEventsUiState.Success).events
            assertEquals(expected, result)
        }

    @Test
    fun `현재 진행중인 일정의 인덱스를 불러올 수 있다`() =
        runTest {
            // given
            // 2번째 FAKE_SCHEDULE_EVENTS의 아이템이 현재 진행중입니다

            // when
            scheduleViewModel.loadScheduleByDate()
            advanceUntilIdle()

            // then
            val state = scheduleViewModel.scheduleEventsUiState.getOrAwaitValue()
            assertTrue(state is ScheduleEventsUiState.Success)

            val expected = 1
            val actual = (state as ScheduleEventsUiState.Success).currentEventPosition
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `현재 진행중인 행사가 없다면 가장 첫 번쨰 일정의 인덱스를 불러온다`() =
        runTest {
            // given
            coEvery { scheduleRepository.fetchScheduleEventsById(dateId) } returns
                Result.success(
                    listOf(
                        ScheduleEvent(
                            id = 1L,
                            status = ScheduleEventStatus.UPCOMING,
                            startTime = "2025-07-26T10:00:00",
                            endTime = "2025-07-26T11:00:00",
                            title = "안드로이드 스터디",
                            location = "서울 강남구 어딘가",
                        ),
                    ),
                )

            // when
            scheduleViewModel.loadScheduleByDate()
            advanceUntilIdle()

            // then
            val state = scheduleViewModel.scheduleEventsUiState.getOrAwaitValue()
            assertTrue(state is ScheduleEventsUiState.Success)

            val expected = 0
            val actual = (state as ScheduleEventsUiState.Success).currentEventPosition
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `dateId에 유효하지 않은 값을 넣고 뷰모델을 생성하면 일정을 불러오지 않는다`() =
        runTest {
            // given
            val dateId = ScheduleViewModel.INVALID_ID

            // when
            scheduleViewModel = ScheduleViewModel(scheduleRepository, dateId)
            advanceUntilIdle()

            // then
            coVerify(exactly = 0) { scheduleRepository.fetchScheduleEventsById(dateId) }
        }
}
