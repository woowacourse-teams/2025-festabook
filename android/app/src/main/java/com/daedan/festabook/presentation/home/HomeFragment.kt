package com.daedan.festabook.presentation.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentHomeBinding
import com.daedan.festabook.domain.repository.BookmarkRepository
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showToast
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val prefsManager by lazy {
        (requireActivity().application as FestaBookApp).appContainer.preferencesManager
    }
    private var bookmarkedID: Long? = null

    private val bookmarkRepository: BookmarkRepository by lazy {
        (requireActivity().application as FestaBookApp).appContainer.bookmarkRepository
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        bookmarkedID = prefsManager.getBookmarkId()
        Timber.d("불러온 북마크 ID: $bookmarkedID")

        setupSubscriptionButtons()
    }

    private fun setupSubscriptionButtons() {
        binding.btnSub.setOnClickListener {
            lifecycleScope.launch {
                val result =
                    bookmarkRepository.saveOrganizationBookmark(
                        organizationId = 1L,
                        deviceId = prefsManager.getDeviceId(),
                    )

                result
                    .onSuccess { bookmarkId ->
                        bookmarkedID = bookmarkId
                        prefsManager.saveBookmarkId(bookmarkId)
                        Timber.d("✅ 북마크 등록 성공: $bookmarkId")
                        requireContext().showToast("북마크 등록 성공")
                    }.onFailure {
                        Timber.e("❌ 북마크 등록 실패: ${it.message}")
                        requireContext().showToast("북마크 등록 실패")
                    }
            }
        }

        binding.btnUnsub.setOnClickListener {
            lifecycleScope.launch {
                Timber.d("$bookmarkedID")
                val bookmarkId = bookmarkedID ?: return@launch

                val result = bookmarkRepository.deleteOrganizationBookmark(bookmarkId)

                result
                    .onSuccess {
                        bookmarkedID = null
                        prefsManager.clearBookmarkId()
                        Timber.d("✅ 북마크 삭제 성공")
                        requireContext().showToast("북마크 삭제 성공")
                    }.onFailure {
                        Timber.e("❌ 북마크 삭제 실패: ${it.message}")
                        requireContext().showToast("북마크 삭제 실패")
                    }
            }
        }
    }
}
