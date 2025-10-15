package com.daedan.festabook.di.datasource

import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.data.datasource.local.FestivalNotificationLocalDataSource
import dev.zacsweers.metro.GraphExtension

@GraphExtension(DataSourceScope::class)
interface LocalDataSourceGraph {
    val deviceLocalDataSource: DeviceLocalDataSource
    val festivalLocalDataSource: FestivalLocalDataSource
    val festivalNotificationLocalDataSource: FestivalNotificationLocalDataSource
}
