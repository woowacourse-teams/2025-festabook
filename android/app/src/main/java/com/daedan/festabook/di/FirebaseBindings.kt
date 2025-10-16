package com.daedan.festabook.di

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
object FirebaseBindings {
    @Provides
    fun provideFirebaseAnalytics(application: Application): FirebaseAnalytics = FirebaseAnalytics.getInstance(application)

    @Provides
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}
