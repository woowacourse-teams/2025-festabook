package com.daedan.festabook.presentation.common

class Event<out T>(
    private val content: T,
) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? =
        if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }

    fun peekContent(): T = content
}
