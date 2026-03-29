package com.clementcorporation.levosonusii.domain.use_cases

import androidx.datastore.core.DataStore
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/*
    Refer to this use-case when a page's content is solely dependent on the DataStore.
    Otherwise, inject sessionDataStore into the Screen's ViewModel directly.
 */
class SessionDataStoreUseCase @Inject constructor(private val sessionDataStore: DataStore<LSUserInfo>) {
    operator fun invoke(): Flow<Response<LSUserInfo>> = callbackFlow {
        send(Response.Loading())
        sessionDataStore.data.collect { userInfo ->
            trySend(Response.Success(data = userInfo))
            close()
        }
        awaitClose {
            cancel()
        }
    }.cancellable().flowOn(Dispatchers.IO)
}