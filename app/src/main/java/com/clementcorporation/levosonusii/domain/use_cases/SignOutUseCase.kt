package com.clementcorporation.levosonusii.domain.use_cases

import androidx.datastore.core.DataStore
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val sessionDataStore: DataStore<LSUserInfo>) {
    operator fun invoke(navigate: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            sessionDataStore.updateData { userInfo ->
                FirebaseAuth.getInstance().signOut()
                navigate()
                userInfo.copy()
            }
        }
    }
}