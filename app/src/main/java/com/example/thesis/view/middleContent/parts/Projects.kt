package com.example.thesis.view.middleContent.parts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesis.viewmodel.middleContent.ProjectViewModel

@Preview
@Composable
fun Projects(
    projectViewModel: ProjectViewModel = viewModel()
) {

    val projects by projectViewModel.projects.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp)
    ) {

        Text(
            text = "Projects",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(projects) { project ->
                ProjectCard(
                    project = project,
                    onClick = {
                        projectViewModel.openProject(project.id)
                    },
                    onEditClick = {
                        // Edit
                    },
                    onDeleteClick = {
                        // Delete
                    }
                )
            }
        }
    }
}