package com.daedan.festabook.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.presentation.common.SingleLiveData
import timber.log.Timber

class SplashViewModel(
    private val festivalLocalDataSource: FestivalLocalDataSource,
) : ViewModel() {
    private val _navigationState = SingleLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _isValidationComplete = MutableLiveData(false)
    val isValidationComplete: LiveData<Boolean> = _isValidationComplete

    init {
        checkFestivalId()
    }

    fun checkFestivalId() {
        val festivalId = festivalLocalDataSource.getFestivalId()
        Timber.d("festival ID : $festivalId")

        if (festivalId == null) {
            _navigationState.setValue(NavigationState.NavigateToExplore)
        } else {
            _navigationState.setValue(NavigationState.NavigateToMain(festivalId))
        }

        _isValidationComplete.value = true
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val festivalLocalDataSource =
                        (this[APPLICATION_KEY] as FestaBookApp).appContainer.festivalLocalDataSource
                    SplashViewModel(
                        festivalLocalDataSource,
                    )
                }
            }
    }
}
