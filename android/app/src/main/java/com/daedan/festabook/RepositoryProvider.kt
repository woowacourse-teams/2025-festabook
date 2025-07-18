package com.daedan.festabook

import com.daedan.festabook.data.datasource.remote.ApiClient
import com.daedan.festabook.data.datasource.remote.NoticeDataSourceImpl
import com.daedan.festabook.data.repository.NoticeRepositoryImpl
import com.daedan.festabook.domain.repository.NoticeRepository

object RepositoryProvider {
    private const val NOT_INITIALIZED_MESSAGE = "%s가 초기화되지 않았습니다"
    private var isInitialized = false

    private var _noticeRepository: NoticeRepository? = null
    val noticeRepository
        get() =
            requireNotNull(_noticeRepository) {
                NOT_INITIALIZED_MESSAGE.format(
                    NoticeRepository::class.simpleName,
                )
            }

    fun initialize() {
        if (isInitialized) return

        val noticeDataSource = NoticeDataSourceImpl(ApiClient.noticeService)
        _noticeRepository = NoticeRepositoryImpl(noticeDataSource)

        isInitialized = true
    }
}
