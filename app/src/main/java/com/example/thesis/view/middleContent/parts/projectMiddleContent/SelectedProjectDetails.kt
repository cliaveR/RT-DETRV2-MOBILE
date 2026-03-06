package com.example.thesis.view.middleContent.parts.projectMiddleContent

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesis.viewmodel.middleContent.SelectedProjectViewModel

@Preview(showBackground = true)
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
        ProjectImageTitle()

        // Select All + Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 8.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {


            Checkbox(
                checked = images.isNotEmpty() && selectedImages.size == images.size,
                onCheckedChange = { viewModel.toggleSelectAll(it) },

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

@Composable
fun ProjectImageTitle(){
    Column (
        modifier = Modifier
            .padding(bottom = 16.dp)
            .padding(top=16.dp)


    ){
        Text(
            text = "Images",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
    }
}