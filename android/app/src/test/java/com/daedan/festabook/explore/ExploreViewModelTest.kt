package com.daedan.festabook.explore

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.domain.repository.ExploreRepository
import com.daedan.festabook.getOrAwaitValue
import com.daedan.festabook.presentation.explore.ExploreViewModel
import com.daedan.festabook.presentation.explore.model.SearchResultUiModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class ExploreViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var exploreRepository: ExploreRepository
    private lateinit var exploreViewModel: ExploreViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        exploreRepository = mockk(relaxed = true)
        exploreViewModel = ExploreViewModel(exploreRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `뷰모델을 생성하면 저장된 축제 id가 있는지 확인한다`() =
        runTest {
            // given
            coEvery { exploreRepository.getFestivalId() } returns 1
            coEvery { exploreRepository.search(any()) } returns Result.success(emptyList())

            // when
            exploreViewModel = ExploreViewModel(exploreRepository)
            advanceUntilIdle()

            // then
            val result = exploreViewModel.hasFestivalId.getOrAwaitValue()
            coVerify { exploreRepository.getFestivalId() }
            coVerify { exploreRepository.search(any()) }
            assertThat(result).isTrue()
        }

    @Test
    fun `대학교가 선택되었을 때 축제 Id를 저장하고 Main으로 이동하는 이벤트를 발생시킨다`() =
        runTest {
            // given
            coEvery { exploreRepository.saveFestivalId(any()) } returns Unit
            coEvery { exploreRepository.search(any()) } returns Result.success(emptyList())

            val searchResult =
                SearchResultUiModel(
                    1,
                    "테스트대학교",
                    "테스트축제",
                )

            // when
            exploreViewModel.onUniversitySelected(searchResult)
            advanceUntilIdle()

            // then
            val result = exploreViewModel.navigateToMain.value
            coVerify { exploreRepository.saveFestivalId(searchResult.festivalId) }
            coVerify { exploreRepository.search(any()) }
            assertThat(result).isEqualTo(searchResult)
        }

    @Test
    fun `검색 입력값이 달라지면 특정 텀을 두고 검색을 수행한다`() =
        runTest {
            // given
            coEvery { exploreRepository.search(any()) } returns Result.success(emptyList())

            // when
            exploreViewModel.onTextInputChanged("테스트")
            exploreViewModel.onTextInputChanged("테스트")
            exploreViewModel.onTextInputChanged("테스트")
            advanceUntilIdle()
            exploreViewModel.onTextInputChanged("테스트1")
            exploreViewModel.onTextInputChanged("테스트2")
            advanceTimeBy(100)

            // then
            coVerify(exactly = 1) { exploreRepository.search("테스트") }
            coVerify(exactly = 0) { exploreRepository.search("테스트1") }
            coVerify(exactly = 0) { exploreRepository.search("테스트2") }
        }
}
