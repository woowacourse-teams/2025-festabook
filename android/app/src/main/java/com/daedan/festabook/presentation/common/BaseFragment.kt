package com.daedan.festabook.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import timber.log.Timber

abstract class BaseFragment<T : ViewBinding> : Fragment() {
    protected lateinit var binding: T
    protected abstract val layoutId: Int

    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: T? = null

    private val screenName: String = this::class.simpleName ?: "Unknown Class"
    private var enterTime: Long = 0L
    private val stayDuration get() = System.currentTimeMillis() - enterTime

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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            enterTime = System.currentTimeMillis()
        } else {
            Timber.i(formatStayLog(screenName, stayDuration))
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.i(formatStayLog(screenName, stayDuration))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private fun formatStayLog(
            screenName: String,
            stayDuration: Long,
        ): String = "screen_stay:screen=$screenName,duration_ms=$stayDuration"
    }
}
