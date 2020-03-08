package com.midnightys.status

import kotlinx.coroutines.flow.*
import kotlin.experimental.ExperimentalTypeInference

/**
 * Created by Kort on 2020/3/7.
 */

suspend fun <T> Flow<Status<T>>.statusSingle(): Status<T> {
    var result: Any? = null
    takeStatusOnce().collect {
        if (it !is Loading) result = it
    }

    if (result == null) throw NoSuchElementException("Expected at least one element")
    @Suppress("UNCHECKED_CAST")
    return result as Status<T>
}

/**
 * Except loading status, rest of status [Status.Success], [Status.Failure], [Status.Cancel] just taking once
 *
 * @see Status
 */
fun <T> Flow<Status<T>>.takeStatusOnce(): Flow<Status<T>> {
    var getStatusCount = 0
    return takeWhile { value ->
        if (getStatusCount != 0) return@takeWhile false
        if (value !is Status.Loading) getStatusCount++
        return@takeWhile true
    }
}

/**
 * It will emit Loading status first when flow be collected.
 */
@UseExperimental(ExperimentalTypeInference::class)
fun <T> statusFlow(@BuilderInference block: suspend FlowCollector<Status<T>>.() -> Unit): Flow<Status<T>> =
    flow(block).onStart { emit(Loading) }

inline fun <T, R> Flow<Status<T>>.mapStatus(crossinline transform: suspend (value: T) -> R) =
    map { status -> status.map { transform(it) } }

suspend fun <T> Flow<Status<T>>.successSingle(): T {
    var result: Any? = null
    collect { value ->
        if (result != null) error("Expected only one element")
        if (result !is Loading) result = value
    }

    if (result === null) throw NoSuchElementException("Expected at least one element")
    @Suppress("UNCHECKED_CAST")
    return result as T
}

inline fun <T, R> Flow<Status<T>>.ignoreLoading() = flow {
    collect {
        if (it !is Loading) emit(it)
    }
}