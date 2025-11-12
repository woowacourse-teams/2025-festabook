package com.daedan.festabook.news

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.festabook.domain.model.Lost
import com.daedan.festabook.domain.repository.FAQRepository
import com.daedan.festabook.domain.repository.LostItemRepository
import com.daedan.festabook.domain.repository.NoticeRepository
import com.daedan.festabook.getOrAwaitValue
import com.daedan.festabook.presentation.news.NewsViewModel
import com.daedan.festabook.presentation.news.faq.FAQUiState
import com.daedan.festabook.presentation.news.faq.model.toUiModel
import com.daedan.festabook.presentation.news.lost.LostUiState
import com.daedan.festabook.presentation.news.lost.model.LostUiModel
import com.daedan.festabook.presentation.news.lost.model.toLostGuideItemUiModel
import com.daedan.festabook.presentation.news.lost.model.toLostItemUiModel
import com.daedan.festabook.presentation.news.notice.NoticeUiState
import com.daedan.festabook.presentation.news.notice.NoticeUiState.Companion.DEFAULT_POSITION
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import com.daedan.festabook.presentation.news.notice.model.toUiModel
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
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var noticeRepository: NoticeRepository
    private lateinit var faqRepository: FAQRepository
    private lateinit var lostItemRepository: LostItemRepository
    private lateinit var newsViewModel: NewsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        noticeRepository = mockk()
        faqRepository = mockk()
        lostItemRepository = mockk()
        coEvery { noticeRepository.fetchNotices() } returns Result.success(FAKE_NOTICES)
        coEvery { faqRepository.getAllFAQ() } returns Result.success(FAKE_FAQS)
        coEvery { lostItemRepository.getLost() } returns FAKE_LOST_ITEM

        newsViewModel =
            NewsViewModel(
                noticeRepository,
                faqRepository,
                lostItemRepository,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `공지사항을 불러올 수 있다`() =
        runTest {
            // given
            coEvery { noticeRepository.fetchNotices() } returns Result.success(FAKE_NOTICES)

            // when
            newsViewModel.loadAllNotices(NoticeUiState.InitialLoading)
            advanceUntilIdle()

            // then
            val expected = FAKE_NOTICES.map { it.toUiModel() }
            val actual = newsViewModel.noticeUiState
            coVerify { noticeRepository.fetchNotices() }
            assertThat(actual).isEqualTo(
                NoticeUiState.Success(expected, DEFAULT_POSITION),
            )
        }

    @Test
    fun `분실물을 불러올 수 있다`() =
        runTest {
            // given
            val expected =
                LostUiState.Success(
                    listOf(
                        (FAKE_LOST_ITEM[0] as Lost.Guide).toLostGuideItemUiModel(),
                        (FAKE_LOST_ITEM[1] as Lost.Item).toLostItemUiModel(),
                    ),
                )

            // when
            newsViewModel.loadAllLostItems()
            advanceUntilIdle()

            // then
            val actual = newsViewModel.lostUiState.getOrAwaitValue()
            coVerify { lostItemRepository.getLost() }
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `공지사항 로드에 실패하면 에러 상태를 표시한다`() =
        runTest {
            // given
            val exception = Throwable("테스트")
            coEvery { noticeRepository.fetchNotices() } returns Result.failure(exception)

            // when
            newsViewModel.loadAllNotices(NoticeUiState.InitialLoading)
            advanceUntilIdle()

            // then
            val expected = NoticeUiState.Error(exception)
            val actual = newsViewModel.noticeUiState
            coVerify { noticeRepository.fetchNotices() }
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `뷰모델을 생성하면 FAQ를 불러올 수 있다`() =
        runTest {
            // given
            coEvery { faqRepository.getAllFAQ() } returns Result.success(FAKE_FAQS)

            // when
            newsViewModel = NewsViewModel(noticeRepository, faqRepository, lostItemRepository)
            advanceUntilIdle()

            // then
            val expected = FAKE_FAQS.map { it.toUiModel() }
            val actual = newsViewModel.faqUiState
            coVerify { faqRepository.getAllFAQ() }
            assertThat(actual).isEqualTo(FAQUiState.Success(expected))
        }

    @Test
    fun `FAQ 로드에 실패하면 에러 상태를 표시한다`() =
        runTest {
            // given
            val exception = Throwable("테스트")
            coEvery { faqRepository.getAllFAQ() } returns Result.failure(exception)

            // when
            newsViewModel = NewsViewModel(noticeRepository, faqRepository, lostItemRepository)
            advanceUntilIdle()

            // then
            val expected = FAQUiState.Error(exception)
            val actual = newsViewModel.faqUiState
            coVerify { faqRepository.getAllFAQ() }
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `공지사항 요소를 펼치게 할 수 있다`() =
        runTest {
            // given
            val notice = FAKE_NOTICES.first().toUiModel()

            // when
            newsViewModel.toggleNoticeExpanded(notice)
            advanceUntilIdle()

            // then
            val expected =
                listOf(
                    notice.copy(isExpanded = true),
                    NoticeUiModel(
                        id = 2,
                        title = "테스트 2",
                        content = "테스트 2",
                        isPinned = true,
                        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                    ),
                )
            val actual = newsViewModel.noticeUiState
            assertThat(actual).isEqualTo(NoticeUiState.Success(expected, DEFAULT_POSITION))
        }

    @Test
    fun `FAQ 요소를 펼치게 할 수 있다`() =
        runTest {
            // given
            val faq = FAKE_FAQS.first().toUiModel()

            // when
            newsViewModel.toggleFAQExpanded(faq)
            advanceUntilIdle()

            // then
            val expected = listOf(faq.copy(isExpanded = true))
            val actual = newsViewModel.faqUiState
            assertThat(actual).isEqualTo(FAQUiState.Success(expected))
        }

    @Test
    fun `분실물 아이템의 클릭 이벤트를 발생시킬 수 있다`() =
        runTest {
            // given
            val lostItem: LostUiModel.Item = mockk()

            // when
            newsViewModel.lostItemClick(lostItem)

            // then
            val actual = newsViewModel.lostItemClickEvent.getOrAwaitValue()
            assertThat(actual.peekContent()).isEqualTo(lostItem)
        }

    @Test
    fun `처음 로드했을 때 펼처질 공지사항을 지정할 수 있다`() =
        runTest {
            // given
            coEvery { noticeRepository.fetchNotices() } returns Result.success(FAKE_NOTICES)
            val expected =
                listOf(
                    FAKE_NOTICES.first().toUiModel(),
                    FAKE_NOTICES[1].toUiModel().copy(isExpanded = true),
                )

            // when
            newsViewModel.expandNotice(2)
            advanceUntilIdle()

            // then
            val actual = newsViewModel.noticeUiState
            coVerify { noticeRepository.fetchNotices() }
            assertThat(actual).isEqualTo(NoticeUiState.Success(expected, 1))
        }

    @Test
    fun `분실물 가이드라인을 펼칠 수 있다`() =
        runTest {
            // given
            val expected =
                LostUiState.Success(
                    FAKE_LOST_ITEM_UI_MODEL.map {
                        when (it) {
                            is LostUiModel.Guide -> it.copy(isExpanded = true)
                            else -> it
                        }
                    },
                )

            // when
            newsViewModel.toggleLostGuideExpanded()

            // then
            val actual = newsViewModel.lostUiState.getOrAwaitValue()
            assertThat(actual).isEqualTo(expected)
        }
}
