package com.example.bugtracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Issue::class],
    version = 1,
    exportSchema = false
)
abstract class BugTrackerDatabase : RoomDatabase() {

    abstract fun issueDao(): IssueDao

    companion object {
        @Volatile
        private var INSTANCE: BugTrackerDatabase? = null

        fun getDatabase(context: Context): BugTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BugTrackerDatabase::class.java,
                    "bug_tracker_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}