package com.daedan.festabook.schedule

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
}
