package com.midnightys.toolboxapp

import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.midnightys.status.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Kort on 2020/3/7.
 */
class DocumentIsNullException : Exception("document is null")

@UseExperimental(ExperimentalCoroutinesApi::class)
inline fun <reified T> DocumentReference.toFlow() = callbackFlow {
    val listener = EventListener<DocumentSnapshot> { snapshot, exception ->
        launch {
            if (exception == null && snapshot != null) {
                send(Loading)
                val result = snapshot.toObject<T>()
                val status =
                    if (result != null) Success(result)
                    else Failure(DocumentIsNullException())
                send(status)
            } else {
                exception?.let {
                    Timber.w("DocumentReference toFlow()", exception.localizedMessage)
                    exception.printStackTrace()
                    send(Status.Failure(exception))
                }
            }
        }
    }
    val registration = addSnapshotListener(listener)
    awaitClose { registration.remove() }
}

@UseExperimental(ExperimentalCoroutinesApi::class)
inline fun <reified T : Any> Query.toFlow(): Flow<Status<List<T>>> =
    callbackFlow {
        val listener = EventListener<QuerySnapshot> { snapshot, firestoreException ->
            launch {
                send(Loading)
                val status = if (firestoreException == null) {
                    val result = snapshot?.toObjects<T>() ?: listOf()
                    Success(result)
                } else {
                    Timber.w("QueryLiveData", firestoreException.localizedMessage)
                    firestoreException.printStackTrace()
                    Failure(firestoreException)
                }
                send(status)
            }
        }
        val registration = addSnapshotListener(listener)
        awaitClose { registration.remove() }
    }

@UseExperimental(ExperimentalCoroutinesApi::class)
inline fun <reified T : Any> DocumentReference.toSingle(): Flow<Status<T>> =
    callbackFlow<Status<T>> {
        send(Loading)
        get().addOnSuccessListener { snapshot ->
            launch {
                if (snapshot != null) {
                    val result = snapshot.toObject(T::class.java)
                    if (result == null) send(Failure(DocumentIsNullException()))
                    else send(Success(result))
                }
            }
        }.addOnFailureListener { exception ->
            launch {
                Timber.w("firebaseFlow DocumentReference toSingle()", exception.localizedMessage)
                exception.printStackTrace()
                send(Status.Failure(exception))
            }
        }.addOnCanceledListener {
            launch {
                Timber.w("firebaseFlow DocumentReference toSingle() cancel")
                send(Cancel)
            }
        }
        awaitClose()
    }

@UseExperimental(ExperimentalCoroutinesApi::class)
inline fun <reified T : Any> Query.toSingle(): Flow<Status<List<T>>> =
    callbackFlow {
        send(Loading)
        get().addOnSuccessListener { snapshot ->
            launch {
                if (snapshot != null) {
                    val result = snapshot.toObjects(T::class.java)
                    send(Status.Success(result))
                } else send(Failure(DocumentIsNullException()))
            }
        }.addOnFailureListener { exception ->
            launch {
                Timber.w("firebaseFlow Query toSingle()", exception.localizedMessage)
                exception.printStackTrace()
                send(Failure(exception))
            }
        }.addOnCanceledListener {
            launch {
                Timber.w("firebaseFlow Query toSingle() cancel")
                send(Cancel)
            }
        }
        awaitClose()
    }