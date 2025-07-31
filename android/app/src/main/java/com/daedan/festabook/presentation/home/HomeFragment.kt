package com.daedan.festabook.presentation.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentHomeBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.home.adapter.CenterItemMotionEnlarger
import com.daedan.festabook.presentation.home.adapter.PosterAdapter

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private lateinit var adapter: PosterAdapter
    private val posters =
        listOf(
            R.drawable.sample_poster,
            R.drawable.sample_poster2,
            R.drawable.sample_poster3,
            R.drawable.sample_poster4,
        )

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupPosterRecyclerView()
    }

    private fun setupPosterRecyclerView() {
        setupAdapter()
        attachSnapHelper()
        scrollToInitialPosition()
        addScrollEffectListener()
    }

    private fun setupAdapter() {
        adapter = PosterAdapter(posters)
        binding.rvHomePoster.adapter = adapter
    }

    private fun attachSnapHelper() {
        PagerSnapHelper().attachToRecyclerView(binding.rvHomePoster)
    }

    private fun scrollToInitialPosition() {
        val safeMaxValue = Int.MAX_VALUE / INFINITE_SCROLL_SAFETY_FACTOR
        val initialPosition = safeMaxValue - (safeMaxValue % posters.size)

        binding.rvHomePoster.scrollToPosition(initialPosition)
    }

    private fun addScrollEffectListener() {
        binding.rvHomePoster.addOnScrollListener(CenterItemMotionEnlarger())
    }

    override fun onDestroyView() {
        binding.rvHomePoster.clearOnScrollListeners()
        super.onDestroyView()
    }

    companion object {
        private const val INFINITE_SCROLL_SAFETY_FACTOR = 4
    }
}
