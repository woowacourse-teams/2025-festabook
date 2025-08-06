package com.daedan.festabook.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import timber.log.Timber

abstract class BaseFragment<T : ViewBinding>(
    private val layoutId: Int,
) : Fragment() {
    //    lateinit var screenName: String
    protected lateinit var binding: T

    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: T? = null

    private var enterTime: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding = _binding!!
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        enterTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()

        val stayDuration = System.currentTimeMillis() - enterTime
        val screenName = this::class.simpleName ?: "UKnown Class"

        Timber.i("screen_stay:screen=$screenName,duration_ms=$stayDuration")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
