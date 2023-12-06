package com.clementcorporation.levosonusii.di

import android.content.Context
import android.content.res.Resources
import androidx.datastore.dataStore
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
class LevoSonusModule {

    private val Context.sessionDataStore by dataStore(fileName = SESSION_FILE_NAME, serializer = LSUserInfoSerializer)
    private val Context.voiceProfileDataStore by dataStore(fileName = VOICE_PROFILE_FILE_NAME, serializer = VoiceProfileSerializer)

    @Provides
    fun providesResources(@ApplicationContext context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun providesDataStore(@ApplicationContext context: Context) = context.sessionDataStore

    @Provides
    @Singleton
    fun providesVoiceProfile(@ApplicationContext context: Context) = context.voiceProfileDataStore
}