package com.daedan.festabook.presentation.splash

sealed interface NavigationState {
    data object NavigateToExplore : NavigationState

    data class NavigateToMain(
        val festivalId: Long,
    ) : NavigationState
}
