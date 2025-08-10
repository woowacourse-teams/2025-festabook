package com.daedan.festabook.presentation.news.lost

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentLostItemBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.news.NewsViewModel
import com.daedan.festabook.presentation.news.lost.LostItemModalDialogFragment.Companion.TAG_MODAL_DIALOG_LOST_ITEM_FRAGMENT
import com.daedan.festabook.presentation.news.lost.adapter.LostItemAdapter
import com.daedan.festabook.presentation.news.lost.model.LostItemUiModel
import com.daedan.festabook.presentation.news.notice.adapter.OnNewsClickListener

class LostItemFragment : BaseFragment<FragmentLostItemBinding>(R.layout.fragment_lost_item) {
    private val adapter by lazy {
        LostItemAdapter(requireParentFragment() as OnNewsClickListener)
    }

    private val viewModel: NewsViewModel by viewModels({ requireParentFragment() }) {
        NewsViewModel.Factory
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvLostItemList.layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
        binding.rvLostItemList.adapter = adapter

        val spacingInPx = resources.getDimensionPixelSize(R.dimen.lost_item_spacing_16dp)
        binding.rvLostItemList.addItemDecoration(
            LostItemDecoration(
                spanCount = SPAN_COUNT,
                spacing = spacingInPx,
            ),
        )
        setupObservers()
        onSwipeRefreshLostItemsListener()
    }

    private fun setupObservers() {
        viewModel.lostItemUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LostItemUiState.InitialLoading -> {
                    binding.srlLostItemList.isRefreshing = false
                }

                is LostItemUiState.Refreshing -> {
                    binding.srlLostItemList.isRefreshing = true
                }

                is LostItemUiState.Success -> {
                    binding.srlLostItemList.isRefreshing = false
                    adapter.submitList(state.lostItems)
                }

                is LostItemUiState.Error -> {
                    binding.srlLostItemList.isRefreshing = false
                }
            }
        }

        viewModel.lostItemClickEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { lostItem ->
                showLostItemModalDialog(lostItem)
            }
        }
    }

    private fun showLostItemModalDialog(lostItem: LostItemUiModel) {
        LostItemModalDialogFragment
            .newInstance(lostItem)
            .show(childFragmentManager, TAG_MODAL_DIALOG_LOST_ITEM_FRAGMENT)
    }

    private fun onSwipeRefreshLostItemsListener() {
        binding.srlLostItemList.setOnRefreshListener {
            viewModel.loadPendingLostItems(LostItemUiState.Refreshing)
        }
    }

    companion object {
        private const val SPAN_COUNT: Int = 2

        fun newInstance() = LostItemFragment()
    }
}
