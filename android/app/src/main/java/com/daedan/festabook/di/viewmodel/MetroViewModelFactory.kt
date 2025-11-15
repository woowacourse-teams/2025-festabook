package com.daedan.festabook.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

@ContributesBinding(AppScope::class)
class MetroViewModelFactory @Inject constructor(
    private val creators: Map<KClass<out ViewModel>, Provider<ViewModel>>,
) : ViewModelProvider.Factory {
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        val provider = creators[modelClass.kotlin] ?: error("$modelClass")
        return modelClass.cast(provider())
    }
}
