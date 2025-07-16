package com.daedan.festabook.presentation.placeList

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentPlaceListBinding
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.animateHideBottomNavigationView
import com.daedan.festabook.presentation.common.animateShowBottomNavigationView
import com.daedan.festabook.presentation.placeDetail.PlaceDetailFragment
import com.daedan.festabook.presentation.placeList.dummy.DummyPlace
import com.daedan.festabook.presentation.placeList.uimodel.Place
import com.daedan.festabook.presentation.placeList.uimodel.PlaceListEvent
import com.google.android.material.bottomnavigation.BottomNavigationView

class PlaceListFragment :
    BaseFragment<FragmentPlaceListBinding>(
        R.layout.fragment_place_list,
    ),
    PlaceListHandler {
    private val viewModel by viewModels<PlaceListViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPlaces.adapter =
            PlaceListAdapter(this).apply {
                submitList(DummyPlace.placeList)
            }

        setUpObservers()
    }

    override fun onPlaceClicked(place: Place) {
        viewModel.publishClickEvent()
        viewModel.setPlace(place)
    }

    private fun setUpObservers() {
        viewModel.event.observe(this) { event ->
            when (event) {
                PlaceListEvent.PLACE_CLICKED -> startPlaceDetailFragment()
                PlaceListEvent.RUNNING -> Unit
            }
        }
    }

    private fun startPlaceDetailFragment() {
        parentFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentStopped(
                    fm: FragmentManager,
                    f: Fragment,
                ) {
                    activity?.findViewById<BottomNavigationView>(R.id.bnv_menu)?.animateShowBottomNavigationView()
                    super.onFragmentStopped(fm, f)
                }

                override fun onFragmentAttached(
                    fm: FragmentManager,
                    f: Fragment,
                    context: Context,
                ) {
                    activity?.findViewById<BottomNavigationView>(R.id.bnv_menu)?.animateHideBottomNavigationView()
                    super.onFragmentAttached(fm, f, context)
                }
            },
            false,
        )
        parentFragmentManager.commit {
            setCustomAnimations(
                R.anim.anim_fade_in_left,
                R.anim.anim_fade_out,
                R.anim.anim_fade_in_right,
                R.anim.anim_fade_out,
            )
            add(
                R.id.fcv_fragment_container,
                PlaceDetailFragment.newInstance(
                    viewModel.place.value ?: return,
                ),
            )
            hide(this@PlaceListFragment)
            addToBackStack(null)
        }
    }
}
