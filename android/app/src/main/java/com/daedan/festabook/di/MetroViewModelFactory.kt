package com.daedan.festabook.di

import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import com.daedan.festabook.FestaBookApp
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
class MetroViewModelFactory @Inject constructor(
    private val viewModelGraphFactory: ViewModelGraph,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        val provider =
            viewModelGraphFactory.viewModelProviders[modelClass.kotlin] ?: error("$modelClass")

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return modelClass.cast(provider())
    }
}

@MainThread
inline fun <reified VM : ViewModel> AppCompatActivity.metroViewModels(): Lazy<VM> =
    ViewModelLazy(
        VM::class,
        { viewModelStore },
        {
            (application as FestaBookApp)
                .festaBookGraph.viewModelGraphFactory
                .createViewModelGraph(
                    defaultViewModelCreationExtras,
                ).metroViewModelFactory
        },
        { defaultViewModelCreationExtras },
    )

@MainThread
inline fun <reified VM : ViewModel> Fragment.metroViewModels(
    crossinline viewModelStoreOwner: () -> ViewModelStoreOwner = { this },
): Lazy<VM> =
    ViewModelLazy(
        VM::class,
        { viewModelStoreOwner().viewModelStore },
        {
            (requireActivity().application as FestaBookApp)
                .festaBookGraph.viewModelGraphFactory
                .createViewModelGraph(
                    defaultViewModelCreationExtras,
                ).metroViewModelFactory
        },
        { defaultViewModelCreationExtras },
    )
