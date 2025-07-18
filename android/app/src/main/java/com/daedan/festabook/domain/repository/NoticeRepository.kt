package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.Notice

interface NoticeRepository {
    suspend fun fetchNotices(): Result<List<Notice>>
}
