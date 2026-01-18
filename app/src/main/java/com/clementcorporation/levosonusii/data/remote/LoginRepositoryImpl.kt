package com.clementcorporation.levosonusii.data.remote

import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.LoginRepository
import com.clementcorporation.levosonusii.domain.use_cases.SignInUseCase
import com.clementcorporation.levosonusii.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val signInUseCase: SignInUseCase
): LoginRepository {
    //TODO: add VoiceProfile to LSUserInfo and a corresponding counterpart in the Firestore database
    override fun signIn(businessId: String, employeeId: String): Flow<Response<LSUserInfo>> =
        signInUseCase.invoke(businessId, employeeId)
}