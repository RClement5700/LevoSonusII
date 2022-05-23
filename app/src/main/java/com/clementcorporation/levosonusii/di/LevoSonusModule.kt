package com.clementcorporation.levosonusii.di

import android.content.Context
import androidx.datastore.dataStore
import com.clementcorporation.levosonusii.model.LSUserInfoSerializer
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

    @Provides
    @Singleton
    fun providesDataStore(@ApplicationContext context: Context) = context.sessionDataStore
}