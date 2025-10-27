package com.daedan.festabook.di

import android.os.Bundle
import com.naver.maps.map.MapFragment
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
object MapBindings {
    @Provides
    fun provideMapFragment(): MapFragment =
        MapFragment().apply {
            arguments = Bundle()
        }
}
