package com.daedan.festabook.presentation.schedule

import android.os.Bundle
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentScheduleTabPageBinding
import com.daedan.festabook.presentation.common.BaseFragment

class ScheduleTabPageFragment : BaseFragment<FragmentScheduleTabPageBinding>(R.layout.fragment_schedule_tab_page) {
    companion object {
        private const val ARG_DATE = "arg_date"

        fun newInstance(date: String): ScheduleTabPageFragment {
            val fragment = ScheduleTabPageFragment()
            val args =
                Bundle().apply {
                    putString(ARG_DATE, date)
                }
            fragment.arguments = args
            return fragment
        }
    }
}
