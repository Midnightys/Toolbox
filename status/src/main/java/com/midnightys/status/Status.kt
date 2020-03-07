package com.midnightys.status

import kotlinx.coroutines.flow.*
import java.lang.Exception
import kotlin.experimental.ExperimentalTypeInference

/**
 * Created by Kort on 2020/3/7.
 */
typealias Loading = Status.Loading

typealias Success<T> = Status.Success<T>
typealias Failure = Status.Failure
typealias Cancel = Status.Cancel

@Suppress("UNCHECKED_CAST")
sealed class Status<out S> {
    object Loading : Status<Nothing>()
    data class Success<T>(val result: T) : Status<T>()
    data class Failure(val exception: Exception) : Status<Nothing>() {
        inline fun <reified E> getExceptionOrNull() = exception as? E
        inline fun <reified E> getExceptionOrThrow() = exception as? E
            ?: throw ClassCastException("exception: $exception can not be cast")
    }

    object Cancel : Status<Nothing>()

    inline fun <reified E, R : Exception> flatMapFailure(action: (E) -> Status<Nothing>) =
        if (this is Failure) action(getExceptionOrThrow<E>()) else this

    inline fun <reified E, R : Exception> mapFailure(action: (E) -> R) =
        flatMapFailure<E, R> { Failure(action(it)) }

    inline fun <R> flatMap(action: (S) -> Status<R>): Status<R> =
        if (this is Success) action(result)
        else this as Status<R>

    inline fun <R> map(action: (result: S) -> R): Status<R> =
        flatMap { Success(action(it)) }

    val loaded get() = this is Loading
    val succeed get() = this is Success
    val failed get() = this is Failure

    inline fun <A, reified E, R> combine(
        otherStatus: Status<A>,
        action: (Any?, A) -> R
    ): Status<R> = when (otherStatus) {
        is Success -> map { action(it, otherStatus.result) }
        else -> this as Status<R>
    }
}