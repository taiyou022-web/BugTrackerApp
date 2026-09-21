package com.example.bugtracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueDao {

    @Insert
    suspend fun insertIssue(issue: Issue)

    @Query("SELECT * FROM issues ORDER BY createdAt DESC")
    fun getAllIssues(): Flow<List<Issue>>

    @Query("SELECT * FROM issues WHERE syncPending = 1")
    fun getPendingIssues(): Flow<List<Issue>>

    @Query("UPDATE issues SET syncPending = :syncPending WHERE id = :issueId")
    suspend fun updateSyncStatus(issueId: Int, syncPending: Boolean)

    @Update
    suspend fun updateIssue(issue: Issue)

    @Delete
    suspend fun deleteIssue(issue: Issue)
}