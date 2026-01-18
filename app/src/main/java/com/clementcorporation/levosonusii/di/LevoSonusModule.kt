package com.clementcorporation.levosonusii.di

import android.content.Context
import android.content.res.Resources
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.clementcorporation.levosonusii.data.remote.DepartmentsRepositoryImpl
import com.clementcorporation.levosonusii.data.remote.EquipmentRepositoryImpl
import com.clementcorporation.levosonusii.data.remote.LoginRepositoryImpl
import com.clementcorporation.levosonusii.data.remote.RegisterRepositoryImpl
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.DepartmentsRepository
import com.clementcorporation.levosonusii.domain.repositories.EquipmentRepository
import com.clementcorporation.levosonusii.domain.repositories.LoginRepository
import com.clementcorporation.levosonusii.domain.repositories.RegisterRepository
import com.clementcorporation.levosonusii.domain.use_cases.AuthenticateUseCase
import com.clementcorporation.levosonusii.domain.use_cases.GetBusinessesUseCase
import com.clementcorporation.levosonusii.domain.use_cases.SignInUseCase
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.clementcorporation.levosonusii.util.LSUserInfoSerializer
import com.clementcorporation.levosonusii.util.VoiceProfileSerializer
import com.google.firebase.firestore.FirebaseFirestore
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
    fun providesFirestoreDatabase(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesGetBusinessesUseCase(db: FirebaseFirestore): GetBusinessesUseCase =
        GetBusinessesUseCase(db)

    @Provides
    @Singleton
    fun providesAuthenticatetUseCase(): AuthenticateUseCase = AuthenticateUseCase()

    @Provides
    @Singleton
    fun providesSignInUseCase(db: FirebaseFirestore): SignInUseCase = SignInUseCase(db)

    @Provides
    @Singleton
    fun providesSignOutUseCase(): SignOutUseCase = SignOutUseCase

    @Provides
    @Singleton
    fun providesLoginRepository(signInUseCase: SignInUseCase): LoginRepository = LoginRepositoryImpl(signInUseCase)

    @Provides
    @Singleton
    fun providesRegisterRepository(db: FirebaseFirestore, signInUseCase: SignInUseCase
    ): RegisterRepository = RegisterRepositoryImpl(db, signInUseCase)

    @Provides
    @Singleton
    fun providesEquipmentRepository(): EquipmentRepository = EquipmentRepositoryImpl()

    @Provides
    @Singleton
    fun providesDepartmentsRepository(sessionDataStore: DataStore<LSUserInfo>, db: FirebaseFirestore
    ): DepartmentsRepository = DepartmentsRepositoryImpl(
        sessionDataStore = sessionDataStore,
        db = db
    )

    @Provides
    @Singleton
    fun providesDataStore(@ApplicationContext context: Context) = context.sessionDataStore

    @Provides
    @Singleton
    fun providesVoiceProfile(@ApplicationContext context: Context) = context.voiceProfileDataStore

    @Provides
    fun providesResources(@ApplicationContext context: Context): Resources = context.resources
}