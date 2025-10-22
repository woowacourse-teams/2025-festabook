package com.daedan.festabook.di.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

@ContributesBinding(AppScope::class)
class MetroFragmentFactory @Inject constructor(
    private val creators: Map<KClass<out Fragment>, Provider<Fragment>>,
) : FragmentFactory() {
    override fun instantiate(
        classLoader: ClassLoader,
        className: String,
    ): Fragment {
        val fragmentClass = loadFragmentClass(classLoader, className)
        val creator =
            creators[fragmentClass.kotlin] ?: return super.instantiate(classLoader, className)

        return try {
            creator()
        } catch (e: Exception) {
            throw RuntimeException("Failed to create fragment: $className", e)
        }
    }
}
