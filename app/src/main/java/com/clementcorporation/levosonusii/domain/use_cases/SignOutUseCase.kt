package com.clementcorporation.levosonusii.domain.use_cases

import androidx.datastore.core.DataStore
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.VoiceProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val sessionDataStore: DataStore<LSUserInfo>,
    private val voiceProfileDataStore: DataStore<VoiceProfile>
) {
    suspend operator fun invoke(navigate: () -> Unit = {}) {
        Firebase.auth.signOut()
        sessionDataStore.updateData {
            LSUserInfo()
        }
        voiceProfileDataStore.updateData {
            VoiceProfile()
        }
        navigate()
    }
}