package com.midnightys.toolboxapp

import com.google.android.gms.tasks.Task
import com.midnightys.status.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Kort on 2020/3/8.
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
fun <T> Task<T>.addOnTaskStatusChangeFlow() = callbackFlow<Status<Unit>> {
    offer(Loading)
    addOnSuccessListener { offer(Success(Unit)) }
    addOnFailureListener { offer(Failure(it)) }
    addOnCanceledListener { offer(Cancel) }
    addOnCompleteListener { channel.close() }
    awaitClose()
}
