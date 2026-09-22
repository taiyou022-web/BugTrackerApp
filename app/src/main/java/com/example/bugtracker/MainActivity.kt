package com.example.bugtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bugtracker.data.local.BugTrackerDatabase
import com.example.bugtracker.data.local.Issue
import com.example.bugtracker.data.local.IssueRepository
import com.example.bugtracker.ui.theme.BugTrackerAppTheme
import com.example.bugtracker.worker.IssueSyncScheduler
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        IssueSyncScheduler.schedule(this)

        val database = BugTrackerDatabase.getDatabase(this)
        val repository = IssueRepository(database.issueDao())

        setContent {
            BugTrackerAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    BugTrackerScreen(
                        repository = repository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BugTrackerScreen(
    repository: IssueRepository,
    modifier: Modifier = Modifier
) {
    // rememberSaveable keeps the form state across configuration changes.
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var priority by rememberSaveable { mutableStateOf("Medium") }
    var status by rememberSaveable { mutableStateOf("Open") }

    val scope = rememberCoroutineScope()

    val issues by repository
        .getAllIssues()
        .collectAsState(initial = emptyList())

    var editingIssueId by rememberSaveable { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = if (editingIssueId == null) {
                "Create Issue"
            } else {
                "Edit Issue"
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = {
                Text("Title")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = {
                Text("Description")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Priority")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { priority = "Low" }
            ) {
                Text("Low")
            }

            Button(
                onClick = { priority = "Medium" }
            ) {
                Text("Medium")
            }

            Button(
                onClick = { priority = "High" }
            ) {
                Text("High")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Status")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { status = "Open" }
            ) {
                Text("Open")
            }

            Button(
                onClick = { status = "In Progress" }
            ) {
                Text("In Progress")
            }

            Button(
                onClick = { status = "Closed" }
            ) {
                Text("Closed")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {

                    if (editingIssueId == null) {

                        val issue = Issue(
                            title = title,
                            description = description,
                            priority = priority,
                            status = status
                        )

                        repository.insertIssue(issue)

                    } else {

                        val existingIssue = issues.find {
                            it.id == editingIssueId
                        }

                        if (existingIssue != null) {

                            val updatedIssue = existingIssue.copy(
                                title = title,
                                description = description,
                                priority = priority,
                                status = status,
                                syncPending = true
                            )

                            repository.updateIssue(updatedIssue)
                        }

                        editingIssueId = null
                    }

                    title = ""
                    description = ""
                    priority = "Medium"
                    status = "Open"
                }
            }
        ) {
            Text(
                if (editingIssueId == null) {
                    "Save Issue"
                } else {
                    "Update Issue"
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Saved Issues")

        issues.forEach { issue ->

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "${issue.title} - ${issue.priority} - ${issue.status}"
                )

                Button(
                    onClick = {
                        scope.launch {
                            repository.deleteIssue(issue)
                        }
                    }
                ) {
                    Text("Delete")
                }

                Button(
                    onClick = {
                        editingIssueId = issue.id
                        title = issue.title
                        description = issue.description
                        priority = issue.priority
                        status = issue.status
                    }
                ) {
                    Text("Edit")
                }
            }
        }
    }
}