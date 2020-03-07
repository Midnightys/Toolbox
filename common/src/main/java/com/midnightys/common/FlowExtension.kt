package com.midnightys.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * Created by Kort on 2020/3/7.
 */
fun <T> Flow<T>.throttleFirst(windowDuration: Long = 500): Flow<T> = flow {
    var windowStartTime = java.lang.System.currentTimeMillis()
    var emitted = false
    collect { value ->
        val currentTime = java.lang.System.currentTimeMillis()
        val delta = currentTime - windowStartTime
        if (delta >= windowDuration) {
            // if delta is 600,
            // delta / windowDuration would be 1 (Because of the delta and windowDuration are int)
            // and windowStartTime would be added 500.
            // value:           1 --- 2 --- 3 --- 4
            // sendSec:         0    200   200   200
            // throttleBound:                 500
            // emit:            ✔️                ✔️
            windowStartTime += delta / windowDuration * windowDuration
            emitted = false
        }
        if (!emitted) {
            emit(value)
            emitted = true
        }
    }
}

@ExperimentalCoroutinesApi
fun <T> Flow<T>.onEachInViewLifeScope(fragment: Fragment, action: suspend (T) -> Unit) {
    onEach(action).launchIn(fragment.viewLifecycleOwner.lifecycleScope)
}