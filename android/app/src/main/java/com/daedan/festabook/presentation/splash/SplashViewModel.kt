package com.daedan.festabook.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.festabook.presentation.common.SingleLiveData

class SplashViewModel : ViewModel() {
    private val _navigationState = SingleLiveData<NavigationState>()
    val navigationState: LiveData<NavigationState> = _navigationState

    private val _isValidationComplete = MutableLiveData(false)
    val isValidationComplete: LiveData<Boolean> = _isValidationComplete

    fun checkFestivalId() {
        // sharedPreference에서 festivalId 가져오기
        val festivalId = 1L

        if (festivalId == null) {
            _navigationState.postValue(NavigationState.NavigateToExplore)
        } else {
            // festival id을 intercepter에 넣어주기
            _navigationState.postValue(NavigationState.NavigateToMain(festivalId))
        }

        _isValidationComplete.postValue(true)
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SplashViewModel()
                }
            }
    }
}
