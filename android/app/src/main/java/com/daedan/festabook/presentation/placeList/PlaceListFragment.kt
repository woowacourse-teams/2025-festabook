package com.daedan.festabook.presentation.placeList

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import coil3.ImageLoader
import coil3.asImage
import coil3.request.ImageRequest
import coil3.request.ImageResult
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.placeListBottomSheetFollowBehavior
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.placeDetail.PlaceDetailActivity
import com.daedan.festabook.presentation.placeDetail.model.PlaceDetailUiModel
import com.daedan.festabook.presentation.placeList.adapter.PlaceListAdapter
import com.daedan.festabook.presentation.placeList.behavior.BottomSheetFollowCallback
import com.daedan.festabook.presentation.placeList.behavior.MoveToInitialPositionCallback
import com.daedan.festabook.presentation.placeList.behavior.PlaceListBottomSheetBehavior
import com.daedan.festabook.presentation.placeList.model.PlaceListUiState
import com.daedan.festabook.presentation.placeList.model.PlaceUiModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber

class PlaceListFragment :
    BaseFragment<FragmentPlaceListBinding>(
        R.layout.fragment_place_list,
    ),
    PlaceClickListener,
    OnMenuItemReClickListener,
    OnMapReadyCallback {
    private val viewModel by viewModels<PlaceListViewModel>({ requireParentFragment() }) { PlaceListViewModel.Factory }
    private val childViewModel by viewModels<PlaceListChildViewModel> { PlaceListChildViewModel.Factory }

    private val placeAdapter by lazy {
        PlaceListAdapter(this)
    }

    private val placeListBottomSheetBehavior by lazy {
        val params = binding.layoutPlaceList.layoutParams as? CoordinatorLayout.LayoutParams
        params?.behavior as? PlaceListBottomSheetBehavior
    }

    private lateinit var moveToInitialPositionCallback: MoveToInitialPositionCallback

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            setUpPlaceAdapter()
            setBehaviorCallback()
            setUpObserver()
            setUpBinding()
        }
    }

    override fun onPlaceClicked(place: PlaceUiModel) {
        Timber.d("onPlaceClicked: $place")
        startPlaceDetailActivity(place)
    }

    override fun onMenuItemReClick() {
        if (binding.root.isGone || !isResumed || view == null) return
        val layoutParams = binding.layoutPlaceList.layoutParams as? CoordinatorLayout.LayoutParams
        val behavior = layoutParams?.behavior as? BottomSheetBehavior
        behavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    override fun onMapReady(naverMap: NaverMap) {
        binding.lbvCurrentLocation.map = naverMap
    }

    private fun setUpPlaceAdapter() {
        binding.rvPlaces.adapter = placeAdapter
        (binding.rvPlaces.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
    }

    private fun setUpObserver() {
        childViewModel.places.observe(viewLifecycleOwner) { places ->
            when (places) {
                is PlaceListUiState.Loading -> showSkeleton()
                is PlaceListUiState.Success -> {
                    preloadImages(
                        requireContext(),
                        places.value,
                    )
                    placeAdapter.submitList(places.value) {
                        binding.rvPlaces.scrollToPosition(0)
                    }
                }

                is PlaceListUiState.Complete -> {
                    hideSkeleton()
                }

                is PlaceListUiState.Error -> {
                    hideSkeleton()
                    binding.tvErrorToLoadPlaceInfo.visibility = View.VISIBLE
                    Timber.w(places.throwable, "PlaceListFragment: ${places.throwable.message}")
                    showErrorSnackBar(places.throwable)
                }
            }
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) { selectedPlace ->
            startPlaceDetailActivity(selectedPlace)
        }

        viewModel.selectedCategories.observe(viewLifecycleOwner) { selectedCategories ->
            if (selectedCategories.isEmpty()) {
                childViewModel.clearPlacesFilter()
            } else {
                childViewModel.filterPlaces(selectedCategories)
            }
        }

        viewModel.isExceededMaxLength.observe(viewLifecycleOwner) { isExceededMaxLength ->
            moveToInitialPositionCallback.setIsExceededMaxLength(isExceededMaxLength)
            binding.chipBackToInitialPosition.visibility =
                if (isExceededMaxLength) View.VISIBLE else View.GONE
        }

        viewModel.onMapViewClick.observe(viewLifecycleOwner) {
            placeListBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setUpBinding() {
        binding.chipBackToInitialPosition.setOnClickListener {
            viewModel.onBackToInitialPositionClicked()
        }
        binding.rvPlaces.itemAnimator = null
    }

    private fun setBehaviorCallback() {
        moveToInitialPositionCallback =
            MoveToInitialPositionCallback(binding.chipBackToInitialPosition.id)

        binding.lbvCurrentLocation
            .placeListBottomSheetFollowBehavior()
            ?.setCallback(
                BottomSheetFollowCallback(binding.lbvCurrentLocation.id),
            )

        binding.chipBackToInitialPosition
            .placeListBottomSheetFollowBehavior()
            ?.setCallback(moveToInitialPositionCallback)
    }

    private fun startPlaceDetailActivity(place: PlaceUiModel) {
        viewModel.selectPlace(place.id)
    }

    private fun showSkeleton() {
        binding.tvErrorToLoadPlaceInfo.visibility = View.GONE
        binding.rvPlaces.visibility = View.GONE
        binding.sflScheduleSkeleton.visibility = View.VISIBLE
        binding.sflScheduleSkeleton.startShimmer()
    }

    private fun hideSkeleton() {
        binding.rvPlaces.visibility = View.VISIBLE
        binding.sflScheduleSkeleton.visibility = View.GONE
        binding.sflScheduleSkeleton.stopShimmer()
    }

    private fun startPlaceDetailActivity(placeDetail: PlaceDetailUiModel) {
        Timber.d("start detail activity")
        val intent = PlaceDetailActivity.newIntent(requireContext(), placeDetail)
        startActivity(intent)
    }

    // OOM 주의 !! 추후 페이징 처리 및 chunk 단위로 나눠서 로드합니다
    private fun preloadImages(
        context: Context,
        places: List<PlaceUiModel?>,
        maxSize: Int = 20,
    ) {
        val imageLoader = ImageLoader(context)
        val deferredList = mutableListOf<Deferred<ImageResult?>>()
        val defaultImage =
            ContextCompat
                .getDrawable(
                    requireContext(),
                    R.drawable.img_fallback,
                )?.asImage()

        lifecycleScope.launch(Dispatchers.IO) {
            places
                .take(maxSize)
                .filterNotNull()
                .forEach { place ->
                    val deferred =
                        async {
                            val request =
                                ImageRequest
                                    .Builder(context)
                                    .data(place.imageUrl)
                                    .error {
                                        defaultImage
                                    }.fallback {
                                        defaultImage
                                    }.build()

                            runCatching {
                                withTimeout(2000) {
                                    imageLoader.execute(request)
                                }
                            }.onFailure {
                                imageLoader.shutdown()
                            }.getOrNull()
                        }
                    deferredList.add(deferred)
                }
            deferredList.awaitAll()
            withContext(Dispatchers.Main) {
                childViewModel.setPlacesStateComplete()
            }
        }
    }
}
