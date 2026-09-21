package com.example.bugtracker.data.local

import kotlinx.coroutines.flow.Flow

class IssueRepository(
    private val issueDao: IssueDao
) {

    fun getAllIssues(): Flow<List<Issue>> {
        return issueDao.getAllIssues()
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
}