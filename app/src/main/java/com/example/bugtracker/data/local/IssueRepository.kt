package com.example.bugtracker.data.local

import com.example.bugtracker.data.remote.IssueApiService
import com.example.bugtracker.data.remote.IssueDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class IssueRepository(
    private val issueDao: IssueDao
) {

    fun getAllIssues(): Flow<List<Issue>> {
        return issueDao.getAllIssues()
    }

    fun getPendingIssues(): Flow<List<Issue>> {
        return issueDao.getPendingIssues()
    }

    suspend fun insertIssue(issue: Issue) {
        issueDao.insertIssue(issue)
    }

    suspend fun updateIssue(issue: Issue) {
        issueDao.updateIssue(issue)
    }

    suspend fun deleteIssue(issue: Issue) {
        issueDao.deleteIssue(issue)
    }

    suspend fun updateSyncStatus(
        issueId: Int,
        syncPending: Boolean
    ) {
        issueDao.updateSyncStatus(issueId, syncPending)
    }

    suspend fun syncPendingIssues(api: IssueApiService) {
        val pendingIssues = issueDao.getPendingIssues().first()

        for (issue in pendingIssues) {
            try {
                val dto = IssueDto(
                    id = issue.id,
                    title = issue.title,
                    description = issue.description,
                    priority = issue.priority,
                    status = issue.status,
                    createdAt = issue.createdAt
                )

                api.createIssue(dto)

                issueDao.updateSyncStatus(
                    issueId = issue.id,
                    syncPending = false
                )

            } catch (e: Exception) {
                // Keep syncPending = true so it can be retried later.
            }
        }
    }
}