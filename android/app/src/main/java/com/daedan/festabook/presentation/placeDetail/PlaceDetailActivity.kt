package com.daedan.festabook.presentation.placeDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ActivityPlaceDetailBinding
import com.daedan.festabook.di.appGraph
import com.daedan.festabook.logging.logger
import com.daedan.festabook.presentation.common.getObject
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.news.faq.model.FAQItemUiModel
import com.daedan.festabook.presentation.news.lost.model.LostUiModel
import com.daedan.festabook.presentation.news.notice.adapter.NewsClickListener
import com.daedan.festabook.presentation.news.notice.adapter.NoticeAdapter
import com.daedan.festabook.presentation.news.notice.model.NoticeUiModel
import com.daedan.festabook.presentation.placeDetail.adapter.PlaceImageViewPagerAdapter
import com.daedan.festabook.presentation.placeDetail.logging.PlaceDetailImageSwipe
import com.daedan.festabook.presentation.placeDetail.model.ImageUiModel
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiState
import com.daedan.festabook.presentation.placeMap.model.PlaceUiModel
import dev.zacsweers.metro.Inject
import timber.log.Timber

class PlaceDetailActivity :
    AppCompatActivity(),
    NewsClickListener {
    @Inject
    private lateinit var viewModelFactory: PlaceDetailViewModel.Factory

    private val noticeAdapter by lazy {
        NoticeAdapter(this)
    }

    private val placeImageAdapter by lazy {
        PlaceImageViewPagerAdapter()
    }

    private lateinit var viewModel: PlaceDetailViewModel

    private val binding: ActivityPlaceDetailBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_place_detail)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        appGraph.inject(this)
        super.onCreate(savedInstanceState)

        val placeUiObject = intent?.getObject<PlaceUiModel>(KEY_PLACE_UI_MODEL)
        val placeDetailObject = intent?.getObject<PlaceDetailUiModel>(KEY_PLACE_DETAIL_UI_MODEL)
        if (placeUiObject == null && placeDetailObject == null) {
            finish()
            return
        }
        viewModel =
            ViewModelProvider(
                this,
                PlaceDetailViewModel.factory(
                    viewModelFactory,
                    placeUiObject,
                    placeDetailObject,
                ),
            )[PlaceDetailViewModel::class.java]

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpBinding()
        setUpObserver()
        Timber.d("detailActivity : ${viewModel.placeDetail.value}")
    }

    private fun setUpBinding() {
        binding.lifecycleOwner = this
        binding.rvPlaceNotice.adapter = noticeAdapter
        binding.vpPlaceImages.adapter = placeImageAdapter
        binding.tvLocation.setExpandedWhenClicked()
        binding.tvHost.setExpandedWhenClicked()
        binding.ivBackToPrevious.setOnClickListener {
            finish()
        }
    }

    private fun setUpObserver() {
        viewModel.placeDetail.observe(this) { result ->

            when (result) {
                is PlaceDetailUiState.Error -> {
                    Timber.w(result.throwable, "PlaceDetailActivity: ${result.throwable.message}")
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
            binding.clImageIndicator.setViewPager(binding.vpPlaceImages)
        }
        binding.vpPlaceImages.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                    binding.logger.log(
                        PlaceDetailImageSwipe(
                            baseLogData = binding.logger.getBaseLogData(),
                            startIndex = position,
                        ),
                    )
                }
            },
        )
        // 임시로 곰지사항을 보이지 않게 하였습니다. 추후 복구 예정입니다
//        if (placeDetail.notices.isEmpty()) {
//            binding.rvPlaceNotice.visibility = View.GONE
//            binding.tvNoNoticeDescription.visibility = View.VISIBLE
//        } else {
//            noticeAdapter.submitList(placeDetail.notices)
//        }
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

    private fun TextView.setExpandedWhenClicked(defaultMaxLines: Int = DEFAULT_MAX_LINES) {
        setOnClickListener {
            maxLines =
                if (maxLines == defaultMaxLines) {
                    Integer.MAX_VALUE
                } else {
                    defaultMaxLines
                }
        }
    }

    override fun onNoticeClick(notice: NoticeUiModel) {
        viewModel.toggleNoticeExpanded(notice)
    }

    override fun onFAQClick(faqItem: FAQItemUiModel) = Unit

    override fun onLostGuideItemClick() = Unit

    companion object {
        private const val DEFAULT_MAX_LINES = 1
        private const val KEY_PLACE_UI_MODEL = "placeUiModel"
        private const val KEY_PLACE_DETAIL_UI_MODEL = "placeDetailUiModel"

        fun newIntent(
            context: Context,
            placeDetail: PlaceDetailUiModel,
        ) = Intent(context, PlaceDetailActivity::class.java).apply {
            putExtra(KEY_PLACE_DETAIL_UI_MODEL, placeDetail)
        }
    }
}
