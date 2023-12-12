package com.clementcorporation.levosonusii.domain.use_cases

import androidx.datastore.core.DataStore
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.VoiceProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignOutUseCase {
    suspend operator fun invoke(sessionDataStore: DataStore<LSUserInfo>,
                                voiceProfileDataStore: DataStore<VoiceProfile>) {
        Firebase.auth.signOut()
        sessionDataStore.updateData {
            LSUserInfo()
        }
        voiceProfileDataStore.updateData {
            VoiceProfile()
        }
    }
}