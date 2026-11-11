package com.uedcl.power.di

import android.content.Context
import com.uedcl.power.data.local.UedclDatabase
import com.uedcl.power.data.remote.MockRetrofitClient
import com.uedcl.power.data.remote.UedclApiService
import com.uedcl.power.data.repository.UedclRepositoryImpl
import com.uedcl.power.domain.repository.IUedclRepository
import dagger.Module
import dagger.Provides
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUedclDatabase(@ApplicationContext context: Context): UedclDatabase {
        return UedclDatabase.getDatabase(context)
    }

    @Provides
    fun provideUserDao(db: UedclDatabase) = db.userDao()

    @Provides
    fun provideTransactionDao(db: UedclDatabase) = db.transactionDao()

    @Provides
    fun provideComplaintDao(db: UedclDatabase) = db.complaintDao()

    @Provides
    @Singleton
    fun provideApiService(): UedclApiService {
        // Return MockRetrofitClient or real Retrofit client instance here
        return MockRetrofitClient()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUedclRepository(
        repositoryImpl: UedclRepositoryImpl
    ): IUedclRepository
}
