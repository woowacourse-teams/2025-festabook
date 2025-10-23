package com.daedan.festabook.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.di.viewmodel.ViewModelKey
import com.daedan.festabook.di.viewmodel.ViewModelScope
import com.daedan.festabook.presentation.common.SingleLiveData
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import timber.log.Timber

@ContributesIntoMap(AppScope::class)
@ViewModelKey(SplashViewModel::class)
class SplashViewModel @Inject constructor(
    private val festivalLocalDataSource: FestivalLocalDataSource,
) : ViewModel() {
    private val _navigationState = SingleLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _isValidationComplete = MutableLiveData(false)
    val isValidationComplete: LiveData<Boolean> = _isValidationComplete

    init {
        checkFestivalId()
    }

    private fun checkFestivalId() {
        val festivalId = festivalLocalDataSource.getFestivalId()
        Timber.d("festival ID : $festivalId")

        if (festivalId == null) {
            _navigationState.setValue(NavigationState.NavigateToExplore)
        } else {
            _navigationState.setValue(NavigationState.NavigateToMain(festivalId))
        }
        _isValidationComplete.value = true
    }
}
