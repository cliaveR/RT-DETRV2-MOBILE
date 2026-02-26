package com.example.thesis.view.middleContent.parts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesis.viewmodel.middleContent.SelectedProjectViewModel

@Preview
@Composable
fun SelectedProjectDetails(
    viewModel: SelectedProjectViewModel = viewModel()
) {
    val images by viewModel.images.collectAsState()
    val selectedImages by viewModel.selectedImages.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        // Select All + Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = images.isNotEmpty() && selectedImages.size == images.size,
                onCheckedChange = { viewModel.toggleSelectAll(it) }
            )

            Text(
                text = "Select All",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { viewModel.onFilterClick() }) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filter"
                )
            }
        }

        LazyColumn {
            items(images, key = { it.id }) { image ->
                SelectedProjectCard(
                    image = image,
                    isSelected = selectedImages.contains(image.id),
                    onSelectChange = {
                        viewModel.toggleImageSelection(image.id)
                    },
                    onEditClick = {
                        viewModel.editImage(image.id)
                    },
                    onDeleteClick = {
                        viewModel.deleteImage(image.id)
                    }
                )
            }
        }
    }
}