package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.Notice

interface NoticeRepository {
    suspend fun getNotice(): Result<List<Notice>>
}
