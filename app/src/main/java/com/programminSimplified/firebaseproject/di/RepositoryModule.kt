package com.programminSimplified.firebaseproject.di

import com.programminSimplified.firebaseproject.firebaseRealtimeDb.repository.RealtimeDbRepository
import com.programminSimplified.firebaseproject.firebaseRealtimeDb.repository.ReatimeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
   abstract fun providesRealtimeRepository(
        repo:RealtimeDbRepository
    ):ReatimeRepository

}
