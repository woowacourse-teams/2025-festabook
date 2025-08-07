package com.daedan.festabook.presentation.placeDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ActivityPlaceDetailBinding
import com.daedan.festabook.presentation.common.getObject
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.adapter.PlaceImageViewPagerAdapter
import com.daedan.festabook.presentation.placeDetail.adapter.PlaceNoticeAdapter
import com.daedan.festabook.presentation.placeDetail.model.ImageUiModel
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import timber.log.Timber

class PlaceDetailActivity : AppCompatActivity(R.layout.activity_place_detail) {
    private val placeNoticeAdapter by lazy {
        PlaceNoticeAdapter()
    }

    private val placeImageAdapter by lazy {
        PlaceImageViewPagerAdapter()
    }

    private val binding: ActivityPlaceDetailBinding by lazy {
        DataBindingUtil.inflate(
            layoutInflater,
            R.layout.activity_place_detail,
            null,
            false,
        )
    }
    private lateinit var place: PlaceUiModel

    private val viewModel by viewModels<PlaceDetailViewModel> { PlaceDetailViewModel.factory(place) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.ncvRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        place =
            intent?.getObject<PlaceUiModel>(PLACE_DETAIL_ACTIVITY) ?: return
        setUpBinding()
        setUpObserver()
    }

    private fun setUpBinding() {
        binding.lifecycleOwner = this
        binding.rvPlaceNotice.adapter = placeNoticeAdapter
        binding.vpPlaceImages.adapter = placeImageAdapter
        binding.tvLocation.setExpandedWhenClicked()
        binding.tvHost.setExpandedWhenClicked()
    }

    private fun setUpObserver() {
        viewModel.placeDetail.observe(this) { result ->
            when (result) {
                is PlaceDetailUiState.Error -> {
                    Timber.d("PlaceDetail: ${result.throwable.message}")
                    showErrorSnackBar(result.throwable)
                }

                is PlaceDetailUiState.Loading -> {
                    showSkeleton()
                    Timber.d("Loading")
                }

                is PlaceDetailUiState.Success -> {
                    hideSkeleton()
                    loadPlaceDetail(result.placeDetail)
                }
            }
        }
    }

    private fun loadPlaceDetail(placeDetail: PlaceDetailUiModel) {
        binding.placeDetail = placeDetail

        if (placeDetail.images.isEmpty()) {
            placeImageAdapter.submitList(
                listOf(ImageUiModel()),
            )
        } else {
            placeImageAdapter.submitList(placeDetail.images)
        }

        if (placeDetail.notices.isEmpty()) {
            binding.rvPlaceNotice.visibility = View.GONE
            binding.tvNoNoticeDescription.visibility = View.VISIBLE
        } else {
            placeNoticeAdapter.submitList(placeDetail.notices)
        }
    }

    private fun showSkeleton() {
        binding.layoutContent.visibility = View.GONE
        binding.sflScheduleSkeleton.visibility = View.VISIBLE
        binding.sflScheduleSkeleton.startShimmer()
    }

    private fun hideSkeleton() {
        binding.layoutContent.visibility = View.VISIBLE
        binding.sflScheduleSkeleton.visibility = View.GONE
        binding.sflScheduleSkeleton.stopShimmer()
    }

    private fun TextView.setExpandedWhenClicked() {
        setOnClickListener {
            maxLines =
                if (maxLines == DEFAULT_MAX_LINES) {
                    Integer.MAX_VALUE
                } else {
                    DEFAULT_MAX_LINES
                }
        }
    }

    companion object {
        private const val DEFAULT_MAX_LINES = 1
        private const val PLACE_DETAIL_ACTIVITY = "placeDetailFragment"

        fun newIntent(
            context: Context,
            place: PlaceUiModel,
        ) = Intent(context, PlaceDetailActivity::class.java).apply {
            putExtra(PLACE_DETAIL_ACTIVITY, place)
        }
    }
}
