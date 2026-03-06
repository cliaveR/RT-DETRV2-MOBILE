package com.example.thesis.view.slideUp.parts

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesis.view.middleContent.parts.middleContent.Projects
import com.example.thesis.viewmodel.middleContent.ProjectViewModel

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlideUpCard(
    projectViewModel: ProjectViewModel = viewModel(),
) {
    var showFilters by remember { mutableStateOf(false) }



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .heightIn(min = 400.dp)
        ) {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showFilters = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Filters"
                    )
                }
            }

            Projects(projectViewModel = projectViewModel)
        }


    if (showFilters) {
        SlideUpCardFilter(onDismiss = { showFilters = false }
        )
    }
}