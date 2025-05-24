package io.github.woods_marshes.base.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.woods_marshes.base.collection.CollectionRepository
import io.github.woods_marshes.base.content.ContentRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideCollectionRepository(
        @ApplicationContext context: Context,
    ): CollectionRepository {
        return CollectionRepository(context)
    }

    @Provides
    @Singleton
    fun provideContentRepository(
        @ApplicationContext context: Context,
    ): ContentRepository {
        return ContentRepository(context)
    }
}