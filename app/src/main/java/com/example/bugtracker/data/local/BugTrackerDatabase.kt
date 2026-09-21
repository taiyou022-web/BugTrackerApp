package com.example.bugtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Issue::class],
    version = 1,
    exportSchema = false
)
abstract class BugTrackerDatabase : RoomDatabase() {

    abstract fun issueDao(): IssueDao
}