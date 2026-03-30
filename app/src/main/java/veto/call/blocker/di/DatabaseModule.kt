package veto.call.blocker.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import veto.call.blocker.data.VetoDatabase
import veto.call.blocker.data.dao.BlockedContactDao
import veto.call.blocker.data.dao.CallLogDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VetoDatabase {
        return VetoDatabase.getDatabase(context)
    }

    @Provides
    fun provideBlockedContactDao(database: VetoDatabase): BlockedContactDao {
        return database.blockedContactDao()
    }

    @Provides
    fun provideCallLogDao(database: VetoDatabase): CallLogDao {
        return database.callLogDao()
    }
}
