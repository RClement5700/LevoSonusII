package com.clementcorporation.levosonusii.di

import android.content.Context
import androidx.datastore.dataStore
import com.clementcorporation.levosonusii.model.LSUserInfoSerializer
import com.clementcorporation.levosonusii.model.VoiceProfileSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LevoSonusModule {

    private val Context.sessionDataStore by dataStore(fileName = "user-info.json", serializer = LSUserInfoSerializer)
    private val Context.voiceProfileDataStore by dataStore(fileName = "voice-profile.json", serializer = VoiceProfileSerializer)

    @Provides
    @Singleton
    fun providesDataStore(@ApplicationContext context: Context) = context.sessionDataStore

    @Provides
    @Singleton
    fun providesVoiceProfile(@ApplicationContext context: Context) = context.voiceProfileDataStore
}