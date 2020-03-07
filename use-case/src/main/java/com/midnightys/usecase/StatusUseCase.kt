package com.midnightys.usecase

import com.midnightys.status.Status
import com.midnightys.status.statusSingle
import com.midnightys.status.successSingle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking

/**
 * Created by Kort on 2020/3/7.
 */
abstract class StatusUseCase<in P, R> : NormalUseCase<P, Status<R>>() {
    abstract override fun execute(parameter: P): Flow<Status<R>>

    override fun executeNow(parameter: P): Status<R> = runBlocking {
        execute(parameter).statusSingle()
    }

    override operator fun invoke(parameter: P) = execute(parameter)
}

abstract class NormalUseCase<in P, R> {
    abstract fun execute(parameter: P): Flow<R>

    open fun executeNow(parameter: P): R = runBlocking {
        execute(parameter).single()
    }

    open operator fun invoke(parameter: P) = execute(parameter)
}