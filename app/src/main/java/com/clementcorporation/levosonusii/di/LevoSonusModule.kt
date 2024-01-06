package com.clementcorporation.levosonusii.di

import android.content.Context
import android.content.res.Resources
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.clementcorporation.levosonusii.data.remote.DepartmentsRepositoryImpl
import com.clementcorporation.levosonusii.data.remote.EquipmentRepositoryImpl
import com.clementcorporation.levosonusii.data.remote.LoadingRepositoryImpl
import com.clementcorporation.levosonusii.data.remote.LoginRepositoryImpl
import com.clementcorporation.levosonusii.data.remote.RegisterRepositoryImpl
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.VoiceProfile
import com.clementcorporation.levosonusii.domain.repositories.DepartmentsRepository
import com.clementcorporation.levosonusii.domain.repositories.EquipmentRepository
import com.clementcorporation.levosonusii.domain.repositories.LoadingRepository
import com.clementcorporation.levosonusii.domain.repositories.LoginRepository
import com.clementcorporation.levosonusii.domain.repositories.RegisterRepository
import com.clementcorporation.levosonusii.domain.use_cases.GetCompanyAddressUseCase
import com.clementcorporation.levosonusii.domain.use_cases.SignInUseCase
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.clementcorporation.levosonusii.util.LSUserInfoSerializer
import com.clementcorporation.levosonusii.util.VoiceProfileSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val SESSION_FILE_NAME = "user-info.json"
private const val VOICE_PROFILE_FILE_NAME = "voice-profile.json"

@Module
@InstallIn(SingletonComponent::class)
object LevoSonusModule {

    private val Context.sessionDataStore by dataStore(fileName = SESSION_FILE_NAME, serializer = LSUserInfoSerializer)
    private val Context.voiceProfileDataStore by dataStore(fileName = VOICE_PROFILE_FILE_NAME, serializer = VoiceProfileSerializer)

    @Provides
    @Singleton
    fun providesGetCompanyAddressUseCase(): GetCompanyAddressUseCase = GetCompanyAddressUseCase()

    @Provides
    @Singleton
    fun providesSignInUseCase(): SignInUseCase = SignInUseCase()

    @Provides
    @Singleton
    fun providesSignOutUseCase(
        sessionDataStore: DataStore<LSUserInfo>,
        voiceProfileDataStore: DataStore<VoiceProfile>
    ): SignOutUseCase = SignOutUseCase(sessionDataStore, voiceProfileDataStore)

    @Provides
    @Singleton
    fun providesLoginRepository(signInUseCase: SignInUseCase): LoginRepository = LoginRepositoryImpl(signInUseCase)

    @Provides
    @Singleton
    fun providesRegisterRepository(signInUseCase: SignInUseCase): RegisterRepository = RegisterRepositoryImpl(signInUseCase)

    @Provides
    @Singleton
    fun providesLoadingRepository(getCompanyAddressUseCase: GetCompanyAddressUseCase
        ): LoadingRepository = LoadingRepositoryImpl(getCompanyAddressUseCase)

    @Provides
    @Singleton
    fun providesEquipmentRepository(): EquipmentRepository = EquipmentRepositoryImpl()

    @Provides
    @Singleton
    fun providesDepartmentsRepository(resources: Resources): DepartmentsRepository =
        DepartmentsRepositoryImpl(resources)

    @Provides
    fun providesResources(@ApplicationContext context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun providesDataStore(@ApplicationContext context: Context) = context.sessionDataStore

    @Provides
    @Singleton
    fun providesVoiceProfile(@ApplicationContext context: Context) = context.voiceProfileDataStore
}