package com.example.bugtracker.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bugtracker.data.local.BugTrackerDatabase
import com.example.bugtracker.data.local.IssueRepository
import com.example.bugtracker.data.remote.RetrofitInstance

class IssueSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        return try {

            val database =
                BugTrackerDatabase.getDatabase(applicationContext)

            val repository =
                IssueRepository(database.issueDao())

            val success =
                repository.syncPendingIssues(
                    RetrofitInstance.api
                )

            if (success) {
                Result.success()
            } else {
                Result.retry()
            }

        } catch (e: Exception) {

            Result.retry()
        }
    }
}