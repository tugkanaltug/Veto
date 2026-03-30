package veto.call.blocker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import veto.call.blocker.data.dao.BlockedContactDao
import veto.call.blocker.data.dao.CallLogDao
import veto.call.blocker.data.entity.BlockedContact
import veto.call.blocker.data.entity.CallLog

@Database(entities = [BlockedContact::class, CallLog::class], version = 1, exportSchema = false)
abstract class VetoDatabase : RoomDatabase() {
    abstract fun blockedContactDao(): BlockedContactDao
    abstract fun callLogDao(): CallLogDao

    companion object {
        @Volatile
        private var INSTANCE: VetoDatabase? = null

        fun getDatabase(context: Context): VetoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VetoDatabase::class.java,
                    "veto_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
