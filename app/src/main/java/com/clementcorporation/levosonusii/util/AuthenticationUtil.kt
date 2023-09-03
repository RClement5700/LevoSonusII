package com.clementcorporation.levosonusii.util

import androidx.datastore.core.DataStore
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object AuthenticationUtil {
    suspend fun signOut (
        sessionDataStore: DataStore<LSUserInfo>,
        voiceProfileDataStore: DataStore<VoiceProfile>
    ) {
        Firebase.auth.signOut()
        sessionDataStore.updateData {
            LSUserInfo()
        }
        voiceProfileDataStore.updateData {
            VoiceProfile()
        }
    }
}