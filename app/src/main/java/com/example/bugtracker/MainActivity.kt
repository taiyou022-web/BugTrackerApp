package com.example.bugtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bugtracker.ui.theme.BugTrackerAppTheme
import com.example.bugtracker.worker.IssueSyncScheduler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Row
import com.example.bugtracker.data.local.BugTrackerDatabase
import com.example.bugtracker.data.local.IssueRepository
import androidx.compose.runtime.rememberCoroutineScope
import com.example.bugtracker.data.local.Issue
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.Color
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
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var status by remember { mutableStateOf("Open") }

    val scope = rememberCoroutineScope()
    val issues by repository.getAllIssues().collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Issue"
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = {
                Text("Title")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = {
                Text("Description")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
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

// 👇 その下に今までのSave Issue
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


        Button(
            onClick = {
                scope.launch {
                    val issue = Issue(
                        title = title,
                        description = description,
                        priority = priority,
                        status = status
                    )

                    repository.insertIssue(issue)
                }
            }
        ) {
            Text("Save Issue")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Saved Issues")

        issues.forEach { issue ->
            Text(
                text = "${issue.title} - ${issue.priority} - ${issue.status}"
            )
        }
    }
}