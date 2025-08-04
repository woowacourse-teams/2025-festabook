package com.daedan.festabook.presentation.news

import androidx.annotation.StringRes
import com.daedan.festabook.R

enum class NewsTab(
    @StringRes val tabNameRes: Int,
) {
    NOTICE(R.string.tab_notice),
    FAQ(R.string.tab_faq),
    LOST_ITEM(R.string.tab_lost_item),
}
