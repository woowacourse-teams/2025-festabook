package com.daedan.festabook.presentation.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentHomeBinding
import com.daedan.festabook.presentation.common.BaseFragment
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
        val initialPosition = Int.MAX_VALUE / START_POSITION_CENTER
        val offset = initialPosition % posters.size
        binding.rvHomePoster.scrollToPosition(initialPosition - offset)
    }

    private fun addScrollEffectListener() {
        binding.rvHomePoster.addOnScrollListener(CenterItemMotionEnlarger())
    }

    companion object {
        private const val START_POSITION_CENTER = 2
    }
}
