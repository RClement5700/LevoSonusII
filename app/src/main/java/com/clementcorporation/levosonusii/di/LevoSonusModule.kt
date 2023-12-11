package com.clementcorporation.levosonusii.di

import android.content.Context
import android.content.res.Resources
import androidx.datastore.dataStore
import com.clementcorporation.levosonusii.data.remote.DepartmentsRepositoryImpl
import com.clementcorporation.levosonusii.data.remote.EquipmentRepositoryImpl
import com.clementcorporation.levosonusii.data.remote.LoadingRepositoryImpl
import com.clementcorporation.levosonusii.data.remote.LoginRepositoryImpl
import com.clementcorporation.levosonusii.data.remote.RegisterRepositoryImpl
import com.clementcorporation.levosonusii.domain.repositories.DepartmentsRepository
import com.clementcorporation.levosonusii.domain.repositories.EquipmentRepository
import com.clementcorporation.levosonusii.domain.repositories.LoadingRepository
import com.clementcorporation.levosonusii.domain.repositories.LoginRepository
import com.clementcorporation.levosonusii.domain.repositories.RegisterRepository
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
    fun providesLoginRepository(): LoginRepository = LoginRepositoryImpl()

    @Provides
    @Singleton
    fun providesRegisterRepository(): RegisterRepository = RegisterRepositoryImpl()

    @Provides
    @Singleton
    fun providesLoadingRepository(): LoadingRepository = LoadingRepositoryImpl()

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